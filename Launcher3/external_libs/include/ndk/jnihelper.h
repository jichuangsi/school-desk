#ifndef __EL_JNI_HELPER_H__
#define __EL_JNI_HELPER_H__

#include <jni.h>
//#include <string>
#include "eltypes.h"

typedef struct jniMethodInfo_
{
	JNIEnv *    env;
	jclass      classID;
	jmethodID   methodID;
} JMethodInfo;


#ifdef __cplusplus
extern "C" {
#endif

int jni_getSdkInt() ;
const char * jni_getPackageName();
const char * jni_getApkPath();
const char * jni_getDataDir();
const char * jni_getExtStorageDir();
const char * jni_getRemovableSDDir() ;
const char * jni_getUsbStorageDir() ;

int jni_getAndroidVersionInt(void) ;

void jni_setEnv(JNIEnv * env) ;
JavaVM* jni_getJavaVM();
void jni_setJavaVM(JavaVM *javaVM);
jclass jni_getClassID(const char *className);
Boolean jni_getStaticMethodInfo(JMethodInfo * methodinfo, const char *className, const char *methodName, const char *paramCode);
Boolean jni_getMethodInfo(JMethodInfo * methodinfo, const char *className, const char *methodName, const char *paramCode);
//std::string jni_jstring2string(jstring str);

char * jni_readAsset(const char * fileName, int * outSize) ;

Boolean jni_startWebActivity(const char * url, const char * title) ;
Boolean jni_startXbwAbook(const char * url, const char * title) ;
Boolean jni_startXbwExp(const char * url, const char * title) ;
Boolean jni_loadUrl(const char * url, const char * title)  ;
Boolean jni_startVideoPlayerActivity() ;
Boolean jni_playVideo(const char * url, int flag) ;
Boolean jni_openFile(const char * url) ;
Boolean jni_browse(const char * url) ;
Boolean jni_startBrowser(const char * url) ;
Boolean jni_startSimpleBrowser(const char * url) ;
Boolean jni_startKidsBrowser(const char * url) ;
Boolean jni_startActivity(const char * pkg, const char * clazz)  ;
Boolean jni_startPackage(const char * pkg)  ;
Boolean jni_installApk(const char * path)  ;
Boolean jni_setAuthFlag(int flag)  ;

Boolean jni_startSystemLauncher() ;
Boolean jni_home() ;
unsigned long jni_ssl_value();

Boolean jni_lockWifi() ;
Boolean jni_unlockWifi() ;
Boolean jni_lockWake() ;
Boolean jni_unlockWake() ;

Boolean jni_startWirelessSettings() ;
Boolean jni_startWifiSettings() ;

Boolean jni_isScreenOff() ;
Boolean jni_isScreenLocked() ;
Boolean jni_getActive(char * outPkg, char * outLabel) ;
Boolean jni_getIp( char * out) ;

Boolean jni_getClipboard( char * out, int max) ;
Boolean jni_setClipboard( const char * text) ;


#ifdef __cplusplus
}
#endif

#endif // __EL_JNI_HELPER_H__
