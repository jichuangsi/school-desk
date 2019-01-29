#ifndef EL_COMMON_LOG_H
#define EL_COMMON_LOG_H


#ifdef ANDROID

#ifdef __cplusplus
extern "C" {
#endif
void LOGE(const char * pszFormat, ...) ;
void LOGI(const char * pszFormat, ...) ;
void LOGD(const char * pszFormat, ...) ;
#ifdef __cplusplus
}
#endif

#define LOGC printf

#elif SPREADTRUM

#include "os_api.h"
#define LOGE SCI_TRACE_HIGH
#define LOGI SCI_TRACE_MID
#define LOGD SCI_TRACE_LOW
#define LOGC SCI_TRACE_LOW

#else

#include <stdio.h>
#define LOGE printf
#define LOGI printf
#define LOGD printf
#define LOGC printf
#endif



#endif // EL_COMMON_LOG_H
