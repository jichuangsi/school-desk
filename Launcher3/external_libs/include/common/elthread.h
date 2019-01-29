#ifndef EL_THREAD_H
#define EL_THREAD_H

#ifdef SPREADTRUM
#include "os_api.h"
//#include "std_header.h"
//#include "mmk_type.h"
#include "sci_types.h"
#include "mmk_timer.h"
typedef BLOCK_ID ElThread ;
typedef void ElMutex ;
typedef SCI_TIMER_PTR ElTimer ;
#else

#include <pthread.h>

#endif

typedef pthread_t ElThread ;
typedef pthread_mutex_t ElMutex ;
typedef int ElTimer ;


#include "eltypes.h"

#ifdef __cplusplus
extern "C" {
#endif



void el_sleep(unsigned int ms) ;

int elthread_create (ElThread *tid, void *(*start)(void *), void *arg)  ;
//int elthread_createST (ElThread *tid, const char  *thread_name, const char  *queue_name, void (*start)(uint32, void *), void *arg) ;

int elthread_createST (ElThread *tid, const char  *thread_name, const char  *queue_name, void (*start)(unsigned int, void *), void *arg) ;

//int elthread_createST (ElThread *tid, void (*start)(uint32, void *), uint32 argc, void *arg)  ;
int elthread_destroy(ElThread *tidp );

ElMutex *  elmutex_create(const char * name);
int elmutex_lock(ElMutex * mutex);
int elmutex_unlock(ElMutex * mutex);
int elmutex_destroy (ElMutex * mutex);

ElTimer eltimer_create(const char * name, int timeout, Boolean isLoop, void (*start)(void *), void * arg) ;
int eltimer_destroy(ElTimer tid) ;

#ifdef __cplusplus
}
#endif

#endif // EL_THREAD_H
