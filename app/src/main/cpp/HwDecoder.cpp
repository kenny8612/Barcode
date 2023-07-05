#include <jni.h>
#include <android/log.h>
#include <unistd.h>
#include <pthread.h>
#include "HwDecoder.h"

#define LOG_TAG "HW_DECODER"
#define DBG(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define ERR(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

#define MSG_DECODE_COMPLETE (0x000001)
#define MSG_DECODE_TIMEOUT  (0x000002)
#define MSG_DECODE_CANCEL   (0x000004)
#define MSG_DECODE_ERROR    (0x000008)

#define MAX_DECODE_BUFFER (4095)
#define DEFAULT_DECODE_TIMEOUT (5000)

extern JNIEnv *getJniEnv();

static void instantiate(JNIEnv *env, jobject instance, jlong handler) {
    jclass clazz = env->GetObjectClass(instance);
    jfieldID serviceId = env->GetFieldID(clazz, "handler", "J");
    env->SetLongField(instance, serviceId, handler);
}

static HwDecoderContext *getInstance(JNIEnv *env, jobject instance) {
    jclass clazz = env->GetObjectClass(instance);
    jfieldID serviceId = env->GetFieldID(clazz, "handler", "J");
    jlong service = env->GetLongField(instance, serviceId);
    return reinterpret_cast<HwDecoderContext *>(service);
}

static jboolean native_open(JNIEnv *env, jobject thiz) {
    HwDecoderContext *pContext = getInstance(env, thiz);
    if (pContext == nullptr) {
        pContext = new HwDecoderContext();
        pContext->object = env->NewGlobalRef(thiz);
        jclass clazz = env->GetObjectClass(thiz);
        pContext->method_postEvent = env->GetMethodID(clazz, "postEventFromNative",
                                                      "(ILjava/lang/Object;)V");
        instantiate(env, thiz, (jlong) pContext);
    }
    return pContext->getDecoder()->open();
}

static void native_close(JNIEnv *env, jobject thiz) {
    HwDecoderContext *pContext = getInstance(env, thiz);
    pContext->getDecoder()->close();
}

static void native_start_decode(JNIEnv *env, jobject thiz) {
    HwDecoderContext *pContext = getInstance(env, thiz);
    pContext->getDecoder()->start_decode();
}

static void native_cancel_decode(JNIEnv *env, jobject thiz) {
    HwDecoderContext *pContext = getInstance(env, thiz);
    pContext->getDecoder()->stop_decode();
}

static void native_decode_timeout(JNIEnv *env, jobject thiz, jint timeout) {
    HwDecoderContext *pContext = getInstance(env, thiz);
    pContext->getDecoder()->decode_timeout(timeout);
}

static const JNINativeMethod method_table[] = {
        {"nativeOpen",          "()Z",  reinterpret_cast<void *>(native_open)},
        {"nativeClose",         "()V",  reinterpret_cast<void *>(native_close)},
        {"nativeStartDecode",   "()V",  reinterpret_cast<void *>(native_start_decode)},
        {"nativeCancelDecode",  "()V",  reinterpret_cast<void *>(native_cancel_decode)},
        {"nativeDecodeTimeout", "(I)V", reinterpret_cast<void *>(native_decode_timeout)}
};

void registerModule(JNIEnv *env) {
    jclass clazz = env->FindClass("org/k/barcode/decoder/HardDecoder");
    env->RegisterNatives(clazz, method_table,
                         sizeof(method_table) / sizeof(method_table[0]));
}

HwDecoder::HwDecoder(DecoderListener *listener) :
        decoderListener(listener),
        threadExit(true),
        decode(false),
        decodeTimeout(DEFAULT_DECODE_TIMEOUT),
        decodingTimeout(false) {
    serialPort = new SerialPort();
    powerDevice = new PowerDevice();
    decodeData = new char[MAX_DECODE_BUFFER + 1];
    sem_init(&decode_sem, 0, 0);
    pthread_mutex_init(&decodeLock, nullptr);
}

HwDecoder::~HwDecoder() {
    delete serialPort;
    delete powerDevice;
    delete decodeData;
    sem_destroy(&decode_sem);
    pthread_mutex_destroy(&decodeLock);
}

bool HwDecoder::open() {
    bool result;

    if (!threadExit)
        return true;

    result = serialPort->open("/dev/ttyWCH0", 9600);
    if (!result)
        return false;

    powerDevice->scannerTrig(1);
    powerDevice->scannerPowerDwn(1);
    powerDevice->scannerWakeup(1);
    powerDevice->scannerPower(1);
    usleep(1000 * 1000);

    decode = false;
    threadExit = false;
    pthread_create(&thread_pid, nullptr, decode_thread_routine, this);

    return true;
}

void HwDecoder::close() {
    if (threadExit) return;

    powerDevice->scannerPower(0);
    powerDevice->scannerTrig(0);
    powerDevice->scannerPowerDwn(0);
    powerDevice->scannerWakeup(0);
    serialPort->close();

    decode = false;
    threadExit = true;
    sem_post(&decode_sem);
    pthread_join(thread_pid, nullptr);
    thread_pid = 0L;
}

void *HwDecoder::decode_thread_routine(void *arg) {
    auto *decoder = (HwDecoder *) arg;
    return decoder->decode_thread();
}

int HwDecoder::get_decode_result() {
    ssize_t len;

    len = serialPort->read_nb(decodeData, MAX_DECODE_BUFFER, 100, '\n');
    if (len > 0) {
        //(CR, ASCII 13, \r)
        //(LF, ASCII 10, \n)
        //filter CR LF
        if (len > 2) {
            if (*(decodeData + len - 1) == '\n') {
                ERR("end contains LF");
                len -= 1;
                if (*(decodeData + len - 1) == '\r')
                    len -= 1;
            }
        }
    }
    return (int) len;
}


void *HwDecoder::decode_thread() {
    int length;

    ERR("decoder thread start.");
    while (!threadExit) {
        sem_wait(&decode_sem);
        if (threadExit)
            break;

        length = 0;
        decodingTimeout = false;
        pthread_mutex_lock(&decodeLock);

        if (decode) {
            start_timer(decodeTimeout);
            powerDevice->scannerTrig(0);
            while (decode && !decodingTimeout) {
                length = get_decode_result();
                pthread_mutex_unlock(&decodeLock);
                if (length > 0)
                    break;
            }
            stop_timer();
            powerDevice->scannerTrig(1);
        } else {
            pthread_mutex_unlock(&decodeLock);
        }

        if (!threadExit && decoderListener != nullptr) {
            if (length > 0) {
                decoderListener->onEvent(MSG_DECODE_COMPLETE, decodeData, length);
            } else if (!decode) {
                decoderListener->onEvent(MSG_DECODE_CANCEL);
            } else if (decodingTimeout) {
                decoderListener->onEvent(MSG_DECODE_TIMEOUT);
            } else {
                decoderListener->onEvent(MSG_DECODE_ERROR);
            }
        }
        decode = false;
    }
    ERR("decoder thread exit.");
    pthread_exit(nullptr);
}

int HwDecoder::start_decode() {
    if (!decode) {
        decode = true;
        sem_post(&decode_sem);
    }
    return 0;
}

void HwDecoder::stop_decode() {
    pthread_mutex_lock(&decodeLock);
    decode = false;
    pthread_mutex_unlock(&decodeLock);
}

void HwDecoder::decoding_timeout_route(sigval_t sig) {
    auto *decoder = (HwDecoder *) sig.sival_ptr;
    decoder->decodingTimeout = true;
}

void HwDecoder::start_timer(long timeout_ms) {
    struct sigevent evp{};

    memset(&evp, 0, sizeof(struct sigevent));
    evp.sigev_value.sival_int = 100;
    evp.sigev_value.sival_ptr = this;
    evp.sigev_notify = SIGEV_THREAD;
    evp.sigev_notify_function = decoding_timeout_route;
    timer_create(CLOCK_MONOTONIC, &evp, &timer);

    struct itimerspec it{};
    it.it_interval.tv_sec = 0;
    it.it_interval.tv_nsec = 0;
    it.it_value.tv_sec = timeout_ms / 1000;
    it.it_value.tv_nsec = timeout_ms % 1000 * 1000000;
    timer_settime(timer, 0, &it, nullptr);
}

void HwDecoder::stop_timer() {
    if (timer != nullptr) {
        timer_delete(timer);
        timer = nullptr;
    }
}

void HwDecoder::decode_timeout(uint16_t timeout) {
    decodeTimeout = timeout;
}

HwDecoderContext::HwDecoderContext() {
    hwDecoder = new HwDecoder(this);
}

HwDecoderContext::~HwDecoderContext() {
    delete hwDecoder;
}

void HwDecoderContext::onEvent(int msg, const char *const data, jsize len) {
    JNIEnv *env = getJniEnv();

    jbyteArray array;
    if (len == 0) {
        array = env->NewByteArray(1);
    } else {
        array = env->NewByteArray(len);
        env->SetByteArrayRegion(array, 0, len, reinterpret_cast<const jbyte *>(data));
    }
    env->CallVoidMethod(object, method_postEvent, msg, array);
    env->DeleteLocalRef(array);
}
