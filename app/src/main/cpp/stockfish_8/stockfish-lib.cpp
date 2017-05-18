#include <jni.h>
#include <iostream>
#include <android/log.h>
#include <sstream>

#include "bitboard.h"
#include "position.h"
#include "search.h"
#include "thread.h"
#include "tt.h"
#include "misc.h"
#include "uci.h"
#include "syzygy/tbprobe.h"

using namespace std;

namespace PSQT {
    void init();
}

struct CommandAnswerCallbackWrapper {
    jmethodID methodId;
    jobject callbackPtr;
    JavaVM *javaVm;
};
CommandAnswerCallbackWrapper callbackWrapper;

void uciCommandCallback(const string &answer){
    JNIEnv *env;
    jint assignRes = callbackWrapper.javaVm->AttachCurrentThread(&env, NULL);
    if (assignRes != JNI_OK){
        __android_log_print(ANDROID_LOG_DEBUG, "BasicEndgamesTraining", "Failed to attach current thread !");
        return;
    }
    jstring nativeString = env->NewStringUTF(answer.c_str());
    env->ExceptionClear();
    env->CallVoidMethod(callbackWrapper.callbackPtr, callbackWrapper.methodId, nativeString);

    if (env->ExceptionOccurred()){
        __android_log_print(ANDROID_LOG_DEBUG, "BasicEndgamesTraining", "Failed to execute the callback !");
        env->ExceptionClear();
    }

    jint detachSuccess = callbackWrapper.javaVm->DetachCurrentThread();
    if (detachSuccess != JNI_OK){
        __android_log_print(ANDROID_LOG_DEBUG, "BasicEndgamesTraining", "Failed to detach current thread !");
    }
}

extern "C"{
JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciStart(JNIEnv *env, jobject obj);

JNIEXPORT jstring JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciInteract(JNIEnv *env, jobject obj, jstring uciCmd);

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciSetAnswerCallback(JNIEnv *env, jobject obj, jobject callback);

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciEnd(JNIEnv *env, jobject obj);
}

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciStart(JNIEnv *env, jobject obj) {
    UCI::init(Options);
    PSQT::init();
    Bitboards::init();
    Position::init();
    Bitbases::init();
    Search::init();
    Pawns::init();
    Threads.init();
    Tablebases::init(Options["SyzygyPath"]);
    TT.resize(Options["Hash"]);

    UCI::startCommandReader();
    return;
}

JNIEXPORT jstring JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciInteract(JNIEnv *env, jobject obj, jstring uciCmd){
    const char *uciCmdStr = env->GetStringUTFChars(uciCmd, JNI_FALSE);
    string uciCmdString(uciCmdStr);

    string output = UCI::readCommand(uciCmdString);
    jstring resultToReturn = env->NewStringUTF(output.c_str());

    env->ReleaseStringUTFChars(uciCmd, uciCmdStr);

    return resultToReturn;
}

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciSetAnswerCallback(JNIEnv *env, jobject obj, jobject callback){
    if (callbackWrapper.callbackPtr != nullptr) env->DeleteGlobalRef(callbackWrapper.callbackPtr);

    jclass objclass = env->GetObjectClass(callback);
    jmethodID methodId = env->GetMethodID(objclass, "execute", "(Ljava/lang/String;)V");
    if (methodId == 0) {
        __android_log_print(ANDROID_LOG_DEBUG, "BasicEndgamesTraining", "Failed to get callback method id !");
        return;
    }

    callbackWrapper.methodId = methodId;
    jint assignRes = env->GetJavaVM(&callbackWrapper.javaVm);
    if (assignRes != JNI_OK){
        __android_log_print(ANDROID_LOG_DEBUG, "BasicEndgamesTraining", "Failed to get java vm pointer !");
        return;
    }
    if (callback != nullptr) callbackWrapper.callbackPtr = env->NewGlobalRef(callback);
    else callbackWrapper.callbackPtr = nullptr;

    setCommandAnswerCallback(uciCommandCallback);
}

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_MyApplication_uciEnd(JNIEnv *env, jobject obj){
    UCI::endCommandReader();
    if (callbackWrapper.callbackPtr != nullptr) env->DeleteGlobalRef(callbackWrapper.callbackPtr);

    Threads.exit();
}

