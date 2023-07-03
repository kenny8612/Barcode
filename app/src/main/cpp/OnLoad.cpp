#include <jni.h>
#include <memory>

JavaVM *sJvm;

extern void registerModule(JNIEnv *env);

class ScopedJniThreadAttach {
public:
    ScopedJniThreadAttach() {
        sJvm->AttachCurrentThread(&mEnv, nullptr);
    }

    ~ScopedJniThreadAttach() {
        sJvm->DetachCurrentThread();
    }

    JNIEnv *getEnv() {
        return mEnv;
    }

private:
    JNIEnv *mEnv = nullptr;
};

thread_local std::unique_ptr<ScopedJniThreadAttach> tJniThreadAttacher;

JNIEnv *getJniEnv() {
    JNIEnv *env;

    sJvm->GetEnv((void **) &env, JNI_VERSION_1_4);
    if (env == nullptr) {
        tJniThreadAttacher = std::make_unique<ScopedJniThreadAttach>();
        env = tJniThreadAttacher->getEnv();
    }

    return env;
}

jint JNI_OnLoad(JavaVM *vm, void */*reserved*/) {
    JNIEnv *env = nullptr;

    if (vm->GetEnv((void **) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    env->GetJavaVM(&sJvm);

    registerModule(env);

    return JNI_VERSION_1_4;
}
