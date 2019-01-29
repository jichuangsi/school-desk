#ifndef ECF_H
#define ECF_H

#include <stdio.h>

#ifdef __cplusplus
extern "C" {
#endif

typedef struct _T_ECF_INFO{
    char format[4] ; //格式标识 HCF0: 数字文件 HCF1 : 普通文件
    char brand[4] ; //
    int headerSize ;
    int size ;
    int module ; //模块
    int subject ; //
    int section ; //
    int bookId ; //
    int unitId ; //
    int lessonId ; //
    int id; //注意和bookId区分
    int sum;
    int density ;
    int thumbOffset ;
    int thumbSize ;
    int coverOffset ;
    // 16 * 4
    int coverSize ; //
    char name[128] ; //名称
    char name1[128] ; //名称
    char publisher[64] ; //
    char version[32] ; //
    char author[32] ; //

}T_ECF_INFO;

void * ecf_open(const char * ecfPath); //传入数据文件名
void * ecf_fast_open(const char * ecfPath); //不解密code
int ecf_open_code(void * h) ; //用ecf_fast_open获得handle, 调用这个函数来获得code
char * ecf_read(void * h, const char * path, int * outSize) ;  //取数据块。数据的长度返回到outSize
char * ecf_thumb(void * h, int * outSize) ;
char * ecf_cover(void * h, int * outSize) ;
void ecf_close(void * h) ; //应用退出时执行

void * ecf_fopen(void * h, const char * path) ;
size_t ecf_fseek (void * f, size_t offset, int whence) ;
size_t  ecf_fread( void *ptr, size_t size, size_t nmemb, void * f) ;
int ecf_feof(void * f) ;
size_t ecf_ftell(void * f) ;
void ecf_fclose(void * f) ;
size_t ecf_fsize(void * f) ;

const char * ecf_name(void * h) ;
int ecf_subject(void *h) ;
/* 是否是单文件 0: 非单个文件 1: avi 2: mp4 */
int ecf_single(void * h) ;
int ecf_category(void * h) ;

#ifdef __cplusplus
}
#endif 


#endif // ECF_H
