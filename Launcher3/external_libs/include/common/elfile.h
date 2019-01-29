#ifndef EL_FILE_H
#define EL_FILE_H

#include "eltypes.h"
#include <stdio.h>

#ifdef SPREADTRUM
#include "sfs.h"
typedef SFS_HANDLE ElFile ;
#else
typedef FILE * ElFile ;
#endif

#ifdef __cplusplus
extern "C" {
#endif


ElFile  el_fopen (const void *path, const char *mode)  ;
int  el_fclose (ElFile stream)  ;
int  el_feof (ElFile stream) ;
size_t  el_fread(void *ptr, size_t size, size_t nmemb, ElFile fd)  ;
size_t  el_fwrite (const void *ptr, size_t size, size_t nmemb, ElFile fd) ;
int  el_fseek (ElFile fd, size_t offset, int whence) ;
size_t  el_ftell (ElFile fd) ;

/** 把二进制数据保存到文件 */
Boolean el_bufferToFile(const void * buffer, int len, const char * filePath) ;
/** 把文本文件的数据全部读进缓冲区，并返回首地址 */
char * el_fileToString(const char * path) ;
/** 把二进制文件的数据全部读进缓冲区，并返回首地址, outSize 是输出参数，存放数据长度 */
char * el_fileToBuffer(const char * path, int * outSize) ;

Boolean el_copyFile(const char * srcPath, const char * dstPath) ;

unsigned char createDirIfNecessary(const char * path) ;

#ifdef __cplusplus
}
#endif

#endif // ElFile _H
