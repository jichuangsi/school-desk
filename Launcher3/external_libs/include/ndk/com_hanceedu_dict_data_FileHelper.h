/* DO NOT EDIT THIS FILE - it is machine generated */
#include <jni.h>
/* Header for class com_hanceedu_dict_data_FileHelper */

#ifndef _Included_com_hanceedu_dict_data_FileHelper
#define _Included_com_hanceedu_dict_data_FileHelper
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_init
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1init
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_find
 * Signature: (Ljava/lang/String;I)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1find
  (JNIEnv *, jclass, jstring, jint);

/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_getMeaning
 * Signature: (Ljava/lang/String;)[Ljava/lang/String;
 */
JNIEXPORT jobjectArray JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1getMeaning
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_writeCDI
 * Signature: (Ljava/lang/String;Z)Z
 */
JNIEXPORT jboolean JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1writeCDI
  (JNIEnv *, jclass, jstring, jboolean);

/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_writeSynCDI
 * Signature: (Ljava/lang/String;)Z
 */
JNIEXPORT jboolean JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1writeSynCDI
  (JNIEnv *, jclass, jstring);

/*
 * Class:     com_hanceedu_dict_data_FileHelper
 * Method:    hcdict_close
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_com_hanceedu_dict_data_FileHelper_hcdict_1close
  (JNIEnv *, jclass);

#ifdef __cplusplus
}
#endif
#endif