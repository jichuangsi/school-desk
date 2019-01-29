
#ifndef _ELLIST_h
#define _ELLIST_h


#include "eltypes.h"

struct _ElList {
    int _current_capacity;
    void ** _data;
    int size;
    Boolean (*_equals)();
} ;

typedef struct _ElList ElList ;


#ifdef __cplusplus
extern "C" {
#endif
/*
  function declarations
*/
/** 仅释放ElList结构体的内存，保存的数据不会收到影响 */
void ellist_destroy(ElList * list);

/** free list的数据及自身 */
void ellist_free_data_and_destroy( ElList *list) ;
void ellist_free_data( ElList *list) ;

ElList * ellist_create(const Boolean (*equals)(const void *  object_1, const void *  object_2));
Boolean ellist_add(ElList *list, const void *  object);
Boolean ellist_insert(ElList *list, const void *  object, const int index) ;
void * ellist_removeAt(ElList *list, const int index);
Boolean ellist_remove(ElList *list, const void *  object);
Boolean ellist_contains(ElList *list, const void *  object);
int ellist_indexOf(ElList *list, const void *  object);
Boolean ellist_isEmpty(ElList *list);
int ellist_size(ElList *list);
void *  ellist_get(ElList *list, const int index);
void ellist_clear(ElList *list);
void ellist_sort(ElList *list, const int (*compare)(const void *  object_1, const void *  object_2));


#ifdef __cplusplus
}
#endif


#endif /* __defined_arraylist_h */


