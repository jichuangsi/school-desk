#ifndef ECF_STRUCT_H
#define ECF_STRUCT_H

#include "elcommon.h"


#define EXTS_COUNT 30
#define INDEX_COUNT (EXTS_COUNT + 1)

typedef struct {
    int serial ;
    char name[128] ;
    int offset ;
    int size ;
}T_NameIndex;


typedef struct {
    int serial ;
    int offset ;
    int size ;
}T_IdIndex;

typedef struct {
    char format[4] ; //格式标识 ECF0: 数字文件 ECF1 : 普通文件,  ECF2: 6组key数字文件 ECF3 : 6组key普通文件
    char brand[4] ; //
    int headerSize ;
    int size ;
    int module ; //模块
    int subject ; //
    int section ; //
    int bookId ; //
    int unitId ; //
    int lessonId ; //
    int id;
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
    int schoolYear ;
    int semi ;
    int single ; // 0: 非单个文件 1: avi 2: mp4
    int category ;
    char tag[16] ;
    char foo1[24] ; //
    char dummy[128] ; //
    int hash ;
    // (16 * 4) + 64  + 128 + 128 + 64 + (32 + 32) + 128 = 640
    char data1[16] ; //codeA
    char data2[16] ; //codeR
    char data3[16] ; //codeSD
    char data4[16] ; //
    char data5[16] ; //
    char data6[16] ; //
    int maxCount[INDEX_COUNT] ;
    T_NameIndex * nameIndice  ; //保存非数字文件名的文件的索引
    T_IdIndex * IdIndice[INDEX_COUNT] ; //保存数字文件名的文件的索引，分扩展名, 0下标留空
    ElFile fp;
}T_ECF;






#endif // ECF_STRUCT_H
