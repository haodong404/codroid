#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>

#define APP_NAME "Zac"

extern "C"
JNIEXPORT jint JNICALL
Java_org_codroid_body_Codroid_stringFromJNI(JNIEnv *env, jobject thiz) {
    int forked = fork();
    if (forked) {
        __android_log_print(ANDROID_LOG_DEBUG, APP_NAME, "Father");
    } else {
        __android_log_print(ANDROID_LOG_DEBUG, APP_NAME, "Child");

        FILE *file = fopen("/sdcard/output.txt", "w");
        fprintf(file, "I'm Running.");
    }
    return forked;
}