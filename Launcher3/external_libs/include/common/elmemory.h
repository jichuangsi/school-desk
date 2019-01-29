#ifndef EL_MEMORY_H
#define EL_MEMORY_H

#include <stdlib.h>
#include <string.h>

#ifdef SPREADTRUM
#include "os_api.h"
#define el_malloc   SCI_ALLOC_APP
#define el_memcmp   memcmp
#define el_memcpy   SCI_MEMCPY
#define el_memmove  SCI_MEMMOVE
#define el_memset   SCI_MEMSET
#define el_free     SCI_FREE

#else

#define el_malloc   malloc
#define el_memcmp   memcmp
#define el_memcpy   memcpy
#define el_memmove  memmove
#define el_memset   memset
#define el_free     free

#endif

#ifdef __cplusplus
extern "C" {
#endif

#ifdef SPREADTRUM

void SCI_MEMMOVE (void *s1, const void *s2, size_t n) ;

#endif



#ifdef __cplusplus
}
#endif

#endif // EL_MEMORY_H
