#include <jni.h>
#include <iostream>

#include "bitboard.h"
#include "position.h"
#include "search.h"
#include "thread.h"
#include "tt.h"
#include "uci.h"
#include "syzygy/tbprobe.h"

using namespace std;

namespace PSQT {
    void init();
}

extern "C"{
JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_00024Companion_uciStart(JNIEnv *env, jobject obj);

JNIEXPORT jstring JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_uciInteract(JNIEnv *env, jobject obj, jstring uciCmd);

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_uciEnd(JNIEnv *env, jobject obj);
}

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_00024Companion_uciStart(JNIEnv *env, jobject obj) {
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
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_uciInteract(JNIEnv *env, jobject obj, jstring uciCmd){
    const char *uciCmdStr = env->GetStringUTFChars(uciCmd, JNI_FALSE);
    string uciCmdString(uciCmdStr);

    string output = UCI::readCommand(uciCmdString);
    jstring resultToReturn = env->NewStringUTF(output.c_str());

    env->ReleaseStringUTFChars(uciCmd, uciCmdStr);

    return resultToReturn;
}

JNIEXPORT void JNICALL
Java_com_loloof64_android_basicchessendgamestrainer_ExerciseChooserActivity_uciEnd(JNIEnv *env, jobject obj){
    UCI::endCommandReader();

    Threads.exit();
}


