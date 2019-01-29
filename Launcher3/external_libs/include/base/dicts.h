#ifndef DICTS_H
#define DICTS_H

#include "eltypes.h"
#include "ellist.h"

typedef struct{
    int dictId ;
    char word[128] ;
    char * text ;
}T_Explanation ;

#ifdef __cplusplus
extern "C" {
#endif

Boolean dicts_open() ;

ElList * dicts_hints(const char * word, int max) ;

ElList * dicts_explanations(const char * word) ;

void dicts_close() ;

#ifdef __cplusplus
}
#endif

#endif // DICTS_H
