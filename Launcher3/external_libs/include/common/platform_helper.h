/*
 * platform_helper.h
 *
 *  Created on: 2014年4月2日
 *      Author: Brent wu
 */

#ifndef PLATFORM_HELPER_H_
#define PLATFORM_HELPER_H_

#ifdef WIN32
#include <windows.h>
#endif

#include "eltypes.h"

enum{
    AUTH_NETKEY,
    AUTH_RKEY,
    AUTH_SDKEY,
    AUTH_HCIC,
    AUTH_DEMO
} ;


typedef struct {
    int type;        // GetDriveType返回码类型
    char name[256];
    char path[512];
    char ext[16] ;
    int id ;
    unsigned int size ;
    int time ;
    char foo[4] ;
}T_File ;


enum {
    FT_UNKNOWN,
    FT_NO_ROOT_DIR,
    FT_REMOVABLE,
    FT_FIXED,
    FT_REMOTE,
    FT_CDROM,
    FT_RAMDISK,
    FT_DESKTOP ,
    FT_DOCUMENT ,
    FT_MUSIC ,
    FT_VIDEO ,
    FT_PICTURE ,
    FT_DOWNLOAD ,
    FT_FOLDER ,
    FT_FILE ,
    FT_URL ,
    FT_CONTENT,
    FT_RECORD
};


#ifdef __cplusplus
extern "C" {
#endif

int platform_auth() ;

#ifdef WIN32
wchar_t * win32_c2w(const char *src, Boolean isUtf8) ;
char * win32_w2c(const wchar_t *pwstr) ;
void win32_hideTaskBar(Boolean flag) ;
Boolean win32_getProcessName(int pid, char * out) ;
#endif

void platform_open(const char * path) ;
Boolean platform_exec(const char * cmd, Boolean isUtf8, Boolean show) ;
Boolean platform_myPath(char * out) ;
Boolean platform_myDir(char * out) ;
Boolean platform_myPathUTF8(char * out) ;
Boolean platform_myDirUTF8(char * out) ;
Boolean platform_dataDir(char * out) ;
Boolean platform_dataDirUTF8(char * out) ;
Boolean platform_sharedDir(char * out) ;
Boolean platform_sharedDirUTF8(char * out) ;
Boolean platform_info(char * info) ;
Boolean platform_UGCDir(char * out) ;
Boolean platform_UGCDirUTF8(char * out) ;
Boolean platform_deviceId(char * out) ;

/** 创建目录， dir为utf8编码 */
Boolean platform_mkdir(const char * dir) ;

char ** platform_drives() ;
char ** platform_storages() ;
char ** platform_removables() ;
T_File * platform_drivesT();
/** 特殊文件夹， 如我的文档等 */
T_File * platform_locationsT();

/** 返回字符串数组，应该释放本身及每个字符串 */
char ** platform_contentDirs(Boolean utf8) ;
char ** platform_UGCDirs(Boolean utf8) ;
char ** platform_writableContentDirs(Boolean utf8) ;
char ** platform_writableUGCDirs(Boolean utf8) ;

Boolean platform_extractAsset(const char * fileName, char* outPath) ;
char * platform_readAsset(const char * fileName, int * outSize) ;

Boolean platform_excute(const char * path, const char * param) ;
Boolean platform_killProcessByName(const char * name) ;

Boolean platform_startDownloadService(const char * path) ;
void platform_stopDownloadService() ;
Boolean platform_startInteractiveService() ;
void platform_stopInteractiveService() ;

void platform_hideInputMethod() ;
/** in MB */
int platform_totalMem() ;

Boolean platform_getActive(char * outName, char * outLabel) ;

typedef struct tagIPInfo
{
    char ip[30];
}IPInfo;

Boolean platform_getIp(char ips[][32],int maxCnt,int* cnt) ;

int platform_sslValue() ;

#ifdef __cplusplus
}
#endif

#endif /* PLATFORM_HELPER_H_ */
