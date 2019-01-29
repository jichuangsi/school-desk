#ifndef HC_STACK_H
#define HC_STACK_H

typedef struct _ElStack {
    void **stack ;
    int size ;
    int capacity ;
}ElStack;

;

#ifdef __cplusplus
extern "C" {
#endif

ElStack * stack_create(void) ;
void stack_push(ElStack *st, void *) ;
void * stack_pop(ElStack *st) ;
void * stack_peek(ElStack *st) ;
void * stack_get(ElStack * st, int index) ;
/** 将元素个数设为0，不释放原来各个元素的内存 */
void stack_clear(ElStack * st) ;
/** 将元素个数设为0，释放stack指向的数据的内存 */
void stack_free(ElStack * st) ;
/** 释放stack内存，但不处理stack指向的数据 */
void stack_destroy(ElStack *st) ;
int stack_size(ElStack *st) ;

#ifdef __cplusplus
}
#endif

#endif // STACK_H
