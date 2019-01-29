#ifndef ELBIN_H
#define ELBIN_H

#include "eltypes.h"

#ifdef __cplusplus
extern "C" {
#endif


void * bin_open(const char * path, int blockSize);
char bin_reopen(void * h) ;
int bin_remove(void * h, const char * name) ;
char * bin_read(void * h, const char * name, int * size) ;
int bin_write(void * h, const char * name, char * data, int dataSize) ;
int bin_clean(void * h) ;
void bin_close(void * h) ;
Boolean bin_exists(void * h, const char * name) ;
char ** bin_find(void * h, const char * prefix) ;

#ifdef __cplusplus
}
#endif



#endif // ELBIN_H
