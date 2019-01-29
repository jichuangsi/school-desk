#ifndef READSTREAM_H
#define READSTREAM_H

#include "elcommon.h"

#ifdef __cplusplus
extern "C" {
#endif 


int rs_new(); // 每个应用只执行一次
int rs_open(const char * datafilename) ; //传入数据文件名
char * rs_data(int cate, int id, int * outSize) ;  //取数据块。数据的长度返回到outSize
void rs_close() ; //应用退出时执行
int rs_size(int id) ;
int rs_offset(int id) ;
void rs_crack(const char * path, Boolean convertToUtf8) ;

#ifdef __cplusplus
}
#endif 


#endif // READSTREAM_H
