#include <stdio.h>
#include <jni.h>
#ifdef __linux__
#include "nanotunes_pulse.h"
#endif

JNIEXPORT jlong JNICALL Java_io_github_rowak_nanotunes_NativeAudioUtil_open
  (JNIEnv *env, jclass class) {
    return (jlong)pulse_open();
}

JNIEXPORT void JNICALL Java_io_github_rowak_nanotunes_NativeAudioUtil_close
  (JNIEnv *env, jclass class, jlong pulse) {
    pulse_close((pa_simple *)pulse);
}

JNIEXPORT jint JNICALL Java_io_github_rowak_nanotunes_NativeAudioUtil_getMaxAmplitude
  (JNIEnv *env, jclass class, jlong pulse) {
    return (jint)get_max_amplitude((pa_simple *)pulse);
}