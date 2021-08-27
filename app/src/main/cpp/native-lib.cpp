#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_org_codroid_editor_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello C++";
    return env->NewStringUTF(hello.c_str());
}