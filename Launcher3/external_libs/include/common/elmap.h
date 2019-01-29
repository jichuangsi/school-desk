
#ifndef __defined_elmap_h
#define __defined_elmap_h

#include "eltypes.h"

/*
  constants
*/
#define SIMPLEMAP_CAPACITY 300

typedef struct _ElMap {
    char * _key[SIMPLEMAP_CAPACITY] ;
    void * _data[SIMPLEMAP_CAPACITY] ;
    int _size;
    int _capacity;
}ElMap;


#ifdef __cplusplus
extern "C" {
#endif
/*
  function declarations
*/
/** 释放map, 不处理map中保存的数据 */
void elmap_free(ElMap * map);
ElMap * elmap_create(void);

/** 调用本函数之前，应该用elmap_remove来获得本key已经存在的对象，如有，应进行释放处理 */
Boolean elmap_put(ElMap * map, const char * key, void * object) ;
void * elmap_remove(ElMap * map, const char * key);
Boolean elmap_contains(ElMap * map, const char * key);
int elmap_index(ElMap * map, const char * key);
Boolean elmap_is_empty(ElMap * map);
int elmap_size(ElMap * map);
void * elmap_at(ElMap * map, const int index);
void * elmap_get(ElMap * map, const char * key);
void elmap_clear(ElMap * map);
void elmap_sort(ElMap * map);

#ifdef __cplusplus
}
#endif

#endif /* __defined_elmap_h */
