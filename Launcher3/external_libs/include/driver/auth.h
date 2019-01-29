#ifndef EL_AUTH_H
#define EL_AUTH_H

#include "rkey.h"
#include "sdkey.h"
#include "hcic.h"
#include "netkey.h"
#include "eltypes.h"
#include "stdlib.h"

#define AUTH_ERROR_NONE 0 //没有错误
#define AUTH_ERROR_DATA 1 //无效数据
#define AUTH_ERROR_ACCOUNT 2 //错误的帐户
#define AUTH_ERROR_EXPIRE 3 //过期
#define AUTH_ERROR_SERVER 4 //内部错误
#define AUTH_ERROR_EXCEED 5//超过最大允许注册次数
#define AUTH_ERROR_VERSION 6//软件版本不对
#define AUTH_ERROR_SERIAL 7

#define AUTH_ERROR_INIT 8
#define AUTH_ERROR_NO_DEVICE 9
#define AUTH_ERROR_OPEN 10
#define AUTH_ERROR_QUERY 11
#define AUTH_ERROR_READ_KEY 12
#define AUTH_ERROR_WRITE_KEY 13

#define AUTH_ERROR_NO_INFO 14 //没有文件
#define AUTH_ERROR_WRONG_INFO 15 //错误的文件
#define AUTH_ERROR_NO_WMI 16 //取不到wMI
#define AUTH_ERROR_DIR 17 //取不到dir
#define AUTH_ERROR_WRITE_FILE 18 //不能写入
#define AUTH_ERROR_CONNECT 19
#define AUTH_ERROR_WMI 20
#define AUTH_ERROR_INVALID 21 //无效的使用，比如没有注册
#define AUTH_ERROR_NEED_UPDATE 22

#define AUTH_ERROR_OLD_KEY 23
#define AUTH_ERROR_NEED_WINDOWS 24
#define AUTH_ERROR_LOG_LATER 25 //需要同步使用日志
#define AUTH_ERROR_WMI_SERVER 26 //注册的WMI已经更换


typedef struct _drm{
    unsigned char used ;
    unsigned char subject ;
    unsigned char schoolYear ;
    unsigned char semi ;
}T_DRM;


typedef struct _UserKeyData {
    char code[16] ;
    //-----------------
    int months ;
    T_DRM drms[3] ;
    //-------------
    unsigned char specialOnly ;
    unsigned char special ;
    unsigned char type ; // 0通用版 1幼儿 2小学 3幼小 4初中 8高中 12中学
    unsigned char type2 ;
    char area[4] ;
    time_t prodTime ;
    unsigned int id ;
    //------------
    time_t firstTime ;
    time_t lastTime ;
    int userId ;
    char dummy[4] ;

}T_UserKeyData ;

#ifdef __cplusplus
extern "C" {
#endif

const char * auth_string() ;
int auth_id() ;
Boolean auth_setId(int id) ;
void auth_setUserId(int userId) ;
int auth_error()  ;
Boolean auth_open() ;
Boolean auth_drm() ;

int auth_check(int section, int userId, float versionId, const char * os) ;
Boolean auth_close() ;

Boolean auth_clientCode(const char * brand, char * out) ;
Boolean auth_months(int * out) ;
void auth_monthsStr(char * out) ;
Boolean auth_daysLeft(int * out) ;
Boolean auth_verify(int subject, int schoolYear, int semi) ;
void auth_drmStr(char * months, char * subject, char * schoolYear, char * semi);
void auth_productStr(char * productId, char * version, char * areaCode);
void auth_firstTime(char * out) ;
int auth_productId() ;
const char * auth_serial() ;
/** 仅securevideo.c 使用 */
Boolean auth_do(char * buf, int len, char * out) ;
/** 如果out == NULL, 结果复制到in。 ecf_open ecf_open_code ecf_fopen 用到*/
Boolean auth_code(char * in,  const char * codeMask,  char * out) ;
Boolean auth_test() ;
int auth_productVersion() ;

Boolean auth_keyVersion() ;
Boolean auth_isValid() ;
Boolean auth_video() ;
void auth_error_text(int error,  const char ** title, const char ** text) ;
int auth_type() ;
void auth_typeVersion(int type, char * version);
Boolean auth_checkReg(int * outError) ;

#ifdef __cplusplus
}
#endif

#endif // EL_AUTH_H
