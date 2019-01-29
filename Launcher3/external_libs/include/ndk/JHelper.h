#ifndef __ANDROID_J_HELPER_H__
#define __ANDROID_J_HELPER_H__

#include <jni.h>
#include <string>

#undef JMethodInfo
typedef struct JMethodInfo_
{
    JNIEnv *    env;
    jclass      classID;
    jmethodID   methodID;
} J_MethodInfo;

class  JHelper
{
public:
	static void setEnv(JNIEnv *env) ;
    static JavaVM* getJavaVM();
    static void setJavaVM(JavaVM *javaVM);
    static jclass getClassID(const char *className, JNIEnv *env=0);
    static bool getStaticMethodInfo(J_MethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode);
    static bool getMethodInfo(J_MethodInfo &methodinfo, const char *className, const char *methodName, const char *paramCode);
    static std::string jstring2string(jstring str);

private:
    static JavaVM *m_psJavaVM;
};


#endif // __ANDROID_J_HELPER_H__
