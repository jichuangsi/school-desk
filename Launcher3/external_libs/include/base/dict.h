#ifndef _HCIC_DICT_H
#define _HCIC_DICT_H

#include "elcommon.h"

#ifdef __cplusplus
extern "C" {
#endif 
 
char hcdict_init(const char * dictPath) ;
char** hcdict_getMeaning(const char * word) ;
char** hcdict_find(const char *word, int max) ;
char hcdict_writeCDI(const char * dictPath, char hasOffsetBits) ;
char hcdict_writeSyn(const char * dictPath) ;
void hcdict_close() ;
#ifdef __cplusplus
}
#endif
 
#endif
