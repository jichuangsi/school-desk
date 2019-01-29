#ifndef El_SEGMENT_H
#define El_SEGMENT_H

#include "elcommon.h"

#ifdef __cplusplus
extern "C" {
#endif

/** 没用，仅为解决链接问题 */
Boolean segment_prepare()  ;
Boolean segment_open() ;
void segment_do(const char * keyword,  char * keyword_ws) ;
ElList * segment_list(const char * keyword) ;
void segment_close() ;

#ifdef __cplusplus
}
#endif

#endif //El_SEGMENT_H
