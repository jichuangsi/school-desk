#ifndef EL_APPDATA_H
#define EL_APPDATA_H

#include "eltypes.h"
#include "elmap.h"

static const int ONSTART = 0 ;
static const int ONPROGRESS = 1 ;
static const int ONEND = 2 ;


typedef struct _EL_PARAM {
    int id ;
    int status ;
    int ret ;
    char key[32] ;
    int i0 ;
    int i1 ;
    int i2 ;
    int i3 ;
    char s0[128]  ;
    char s1[128]  ;
    char s2[128]  ;
    void * data ;
} ElParam;



typedef struct _ElApp
{
    void (*handleMessage)(ElParam * param);
    void (*handleData)(ElParam * param);
    struct _ElMap * elmap ;
} ElApp;



#ifdef __cplusplus
extern "C" {
#endif

    /** 创建一个指定ID的ElParam，其余成员变量设为0*/
    ElParam * createParam(int id) ;
    void destroyParam(ElParam * param) ;
    void destroyParamKeepData(ElParam * param) ;
    ElApp * elapp_create( void (*handleMessage)(ElParam * param), void (*handleData)(ElParam * param)) ;
    void elapp_release(ElApp * elApp) ;
    Boolean elapp_sendMessage(ElApp * elApp, ElParam *param) ;
    ElMap * elapp_getMap(ElApp * elApp) ;
    void * elapp_getData(ElApp * elApp, const char * key) ;
    void elapp_putData(ElApp * elApp, const char * key, void * data) ;
    /**在map中去除对key-data的引用，返回数据*/
    void * elapp_removeData(ElApp * elApp, const char * key) ;

#ifdef __cplusplus
}
#endif

#endif // APPDATA_H
