#include "launcher.h"
#include "jni.h"
#include "jnihelper.h"
#include "auth.h"

#define ISIZE sizeof(ElItem)

static void pushList(JNIEnv *env, jclass clazz){
	ElList * appList = launcher_list() ;
	int count  = appList->size ;
	if (count == 0) {
		return ;
	}

	//jclass jclazz = env->GetObjectClass(clazz);
	jclass jclazz = env->FindClass("cn/netin/launcher/data/LauncherData");
	if (!jclazz) {
		LOGE("GetObjectClass fail \n");
		return;
	}

	jmethodID  jmid = env->GetStaticMethodID(jclazz,"setApp","(ILjava/lang/String;IIILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V");
	if (!jmid) {
		LOGE("method setApp not found! \n") ;
		return ;
	}

	ElItem * item ;
	int i ;
	jstring jname ;
	jstring jicon ;
	jstring jpkg ;
	jstring jcls ;
	jstring jextra ;

	for (i = 0 ; i < count; i++){
		item = (ElItem *)ellist_get(appList, i) ;
		jname = env->NewStringUTF(item->name);
		jicon = env->NewStringUTF(item->icon);
		jpkg = env->NewStringUTF(item->pkg);
		jcls = env->NewStringUTF(item->cls);
		jextra = env->NewStringUTF(item->extra);
		//(int id, String name, int category, int groupId, int parentId,
		//String icon, String pkg, String cls, String extra)
		env->CallStaticVoidMethod(jclazz,jmid, item->id, jname, item->category, item->groupId, item->parentId,
				jicon, jpkg, jcls, jextra);

		env->DeleteLocalRef(jname) ;
		env->DeleteLocalRef(jicon) ;
		env->DeleteLocalRef(jpkg) ;
		env->DeleteLocalRef(jcls) ;
		env->DeleteLocalRef(jextra) ;
	}
}


#ifdef __cplusplus
extern "C" {
#endif

jint JNI_OnLoad(JavaVM *vm, void *reserved)
{

    jni_setJavaVM(vm);

    return JNI_VERSION_1_4;
}

JNIEXPORT int JNICALL Java_cn_netin_launcher_data_LauncherData_initData
(JNIEnv *env, jclass clazz){

	int error = launcher_init() ;
	if (error != AUTH_ERROR_NONE) {
		return error;
	}

	pushList(env, clazz) ;
	return AUTH_ERROR_NONE ;
}

static void j2c(JNIEnv * env, jstring js, char * s){
	if (!js){
		return ;
	}
	const char * p = env->GetStringUTFChars(js, 0) ;
	strcpy(s, p) ;
	env->ReleaseStringUTFChars(js, p);
	env->DeleteLocalRef(js) ;
}

JNIEXPORT jint JNICALL Java_cn_netin_launcher_data_LauncherData_addItem
(JNIEnv * env, jclass clazz, jstring jname, jint category, jint groupId, jint parentId,
		jstring jicon, jstring jpkg, jstring jcls){

	ElItem * item = (ElItem * )malloc(ISIZE) ;
	memset(item, 0, ISIZE) ;
	j2c(env, jname, item->name) ;
	j2c(env, jicon, item->icon) ;
	j2c(env, jpkg, item->pkg) ;
	j2c(env, jcls, item->cls) ;
	item->category =  category ;
	item->groupId = groupId ;
	item->parentId = parentId ;

	int ret = launcher_addItem(item) ;
	pushList(env, clazz) ;

	return ret ;
}

JNIEXPORT void JNICALL Java_cn_netin_launcher_data_LauncherData_removeItem
(JNIEnv * env, jclass clazz, jint id){
	launcher_removeItem(id) ;
	pushList(env, clazz) ;
}

JNIEXPORT int JNICALL Java_cn_netin_launcher_data_LauncherData_activate
(JNIEnv * env, jclass clazz, jstring jcode){
	char code[16] ;
	j2c(env, jcode, code) ;
	return launcher_activate(code) ;
}

JNIEXPORT jboolean JNICALL Java_cn_netin_launcher_service_AppStat_nativeIsAllowed
(JNIEnv * env, jclass clazz, jstring jpkg, jstring jcls, jboolean banInstaller){
	char pkg[128] ;
	char cls[128] ;
	j2c(env, jpkg, pkg) ;
	j2c(env, jcls, cls) ;

	return launcher_isAllowed(pkg, cls, banInstaller) ;
}

JNIEXPORT void JNICALL Java_cn_netin_launcher_service_AppStat_nativeSetInstallerAllowed
(JNIEnv * env, jclass clazz, jboolean allowed){

	launcher_setInstallerAllowed(allowed) ;
}


#ifdef __cplusplus
}
#endif

