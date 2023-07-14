#ifndef __HW_DECODER_H__
#define __HW_DECODER_H__

#include <semaphore>
#include "PowerDevice.h"
#include "SerialPort.h"

class DecoderListener {
public:
    virtual void onEvent(int msg, const char *const data = nullptr, jsize len = 0) = 0;
};

class HwDecoder {
private:
    SerialPort *serialPort;
    PowerDevice *powerDevice;

    char *decodeData;

    uint16_t decodeTimeout;

    pthread_t thread_pid{};

    sem_t decode_sem{};

    timer_t timer{};
    DecoderListener *decoderListener;

    pthread_mutex_t decodeLock{};

    bool supportAIM;

    bool supportLight;

    bool isOpened;

public:
    bool threadExit;
    bool decode;
    bool decodingTimeout;

private:
    static void *decode_thread_routine(void *arg);

    void *decode_thread();

    void start_timer(long timeout_ms);

    void stop_timer();

    int get_decode_result();

    static void decoding_timeout_route(sigval_t sig);

public:
    HwDecoder(DecoderListener *listener);

    virtual ~HwDecoder();

    bool open();

    void close();

    int start_decode();

    void stop_decode();

    void decode_timeout(uint16_t timeout);

    void light(bool enable);

    inline bool isSupportLight() { return  supportLight; }

    inline bool isSupportAIM() { return  supportAIM; }
};

class HwDecoderContext : public DecoderListener {
private:
    HwDecoder *hwDecoder;

public:
    HwDecoderContext();

    ~HwDecoderContext();

    inline HwDecoder *getDecoder() { return hwDecoder; }

    void onEvent(int msg, const char *const data, jsize len) override;

public:
    jobject object{};
    jmethodID method_postEvent{};
};

#endif //__HW_DECODER_H__
