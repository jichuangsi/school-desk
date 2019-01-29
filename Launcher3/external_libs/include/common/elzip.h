
#ifndef EL_ZIP_H
#define EL_ZIP_H

#include "eltypes.h"



#ifdef __cplusplus
extern "C" {
#endif

typedef  Boolean (*unzipCB) (const char *, int, void *)  ;

/** 读取 zip_path压缩包中文件名为path的数据 */
char * zip_read(const char * zip_path, const char * path, int * size) ;

Boolean zip_unzip(const char * zip_path, const char * dest_dir, char * errorOut, unzipCB cb, void * param) ;

#ifdef __cplusplus
}
#endif

#endif
