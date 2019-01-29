#ifndef EL_STRING_H
#define EL_STRING_H

#include <string.h>
#include "eltypes.h"
#include <stdio.h>
#include <time.h>

#ifdef WIN32
#define PATH_SEP '\\'
#else
#define PATH_SEP '/'
#endif


#ifdef SPREADTRUM

#define el_sprintf  sprintf
#define el_strstr   strstr
#define el_strchr   strchr
#define el_strcmp   strcmp
#define el_strncmp  strncmp
#define el_strncpy  strncpy
#define el_strcpy   SCI_STRCPY
#define el_strlen   SCI_STRLEN
#define el_strcat   strcat
#define el_strncat   strncat
#define el_strdup   strdup
#define el_atoi     atoi


#else

#define el_sprintf  sprintf
#define el_strstr   strstr
#define el_strchr   strchr
#define el_strcmp   strcmp
#define el_strncmp  strncmp
#define el_strncpy  strncpy
#define el_strcpy   strcpy
#define el_strlen   strlen
#define el_strcat   strcat
#define el_strncat   strncat
#define el_strdup   strdup
#define el_atoi     atoi

#endif

#ifdef __cplusplus
extern "C" {
#endif

/**  用16进制字符串表示src，结果放到out. 调用者需要分配len * 2 字节的内存给out */
void  str_toHex( const char * src, int len, char * out) ;

/** 生成outlen长度的xor hash 16进制字符串结果 */
void str_xhash(const char * str,  int inlen, char * result, int outlen)  ;

/** 打印 \xXX 形式的字符串 */
void str_printu(const char *s) ;

/**  打印 \xXX 形式的字符串数组 */
void str_printua(const char * array[])  ;
/** 打印出 const char name[] = {0xaa, 0xbb} ; */
void str_printCharArray(const char * name, const char *buf, int len) ;
void  str_printx(const char * tag, const char * buffer, int len) ;
void  str_printfx(const char * tag, const char * buffer, int len) ;

/** 和标准C不同，把函数内分配内存，把两个字符串拼在一起，返回新的字符串 */
char * str_strcat(const char * s1, const char * s2);

/**
 * 字符串替换函数
 * @param char * str 原始字符串
 * @param const char * from 目标字符串
 * @param const char *to 替换的字符串
 * @return 替换好的字符串
 **/
char * str_replace(const char *str, const char *from, const char *to) ;

char * str_replaceAll(const char * s, const char * replacePairs[][2]) ;

/** 在原字符串内替换字符串，不创建字符串，如替换成功，返回替换段的下一个字符的地址，否则返回原地址。要确保src已经分配了足够的内存 */
char * str_replaceInside(char *src, const char *from, const char *to) ;


/** 去除字符串开始和结尾的空格和换行,返回新字符串 */
char * str_trim(const char * str)  ;

/** 去除字符串开始和结尾的空格和换行 */
void str_trimString(char * str)  ;

/** 判断src是否以s开头 */
Boolean str_startsWith(const char * src, const char * s) ;

/** 判断src是否以s结尾 */
Boolean str_endsWith(const char * src, const char * s) ;


/** 判断src是否包含s */
Boolean str_contains(const char * src, const char * s);

/** 判断两个字符串是否相同 */
Boolean str_equals(const char * src, const char * s);

/** 不区分大小写， 判断两个字符串是否相同 */
Boolean str_equalsIgnoreCase(const char * src, const char * s);


/** 返回字符c 在 src 中的第一个下标。不存在则返回 -1 */
int str_indexOfChr(const char * src, char c) ;


/** 返回字符串 s 在 src 中的第一个下标。不存在则返回 -1 */
int str_indexOfStr(const char * src, const char * s) ;


/** 返回字符 c 在 src 中的最后一个下标。不存在则返回 -1 */
int str_lastIndexOf(const char * src, char c) ;

/**
 * 字符串截取
 * @param str : 原字符串
 * @param form ： 截取的开始位置
 * @param to : 截取的结束位置之后的一个字节位置
 * @return 结果字符串的首地址
 */
char * str_substring(const char *src,  int from,  int to) ;

/**
 * 右边字符串截取
 * @param str : 原字符串
 * @param form ： 截取的开始位置，直至结尾
 * @return 结果字符串的首地址
 */
char * str_right(const char *src, const int from) ;

//将以splitter分隔的字符串转成字符串数组
char ** str_split(const char * str, const char splitter) ;

/**
 * 给英文句子标点符号后面加空格，原字符串会保留, 返回新的字符串
 **/
char * str_add_space(const char * str) ;

/**
 * 只要0-127范围的字符
 **/
char * str_alpha_only(const char * str)  ;

/** 全部小写，返回原指针 */
char * str_toLowerCase(char * str)  ;

// ===========================================================

/** 从包含路径的字符串中取不含文件名的目录 */
char * str_getDir(const char * path) ;
void str_dir(const char * path, char * out) ;

/** 从包含路径的字符串中取文件名 */
char * str_getFileName(const char * path) ;
void str_fileName(const char * path, char * out) ;
Boolean str_fileNameUtf(char * localPath, char * out) ;
/** 将 src 的目录换成dir*/
void str_replaceDir(const char * src, const char * dir, char * out) ;

/** 从包含路径的字符串中取不含扩展名的文件名 */
char * str_getFileNameOnly(const char * path);
void str_fileNameOnly(const char * path, char * out) ;

/** 取不含.扩展名如 jpg */
char * str_getExtension(const char * path);
void str_extension(const char * path, char * out) ;

/** 把start符号 和 end符号之间的字符串去除 */
void str_removeTag(char * s, const char start, const char end) ;

// ===========================================================

/** 找没有用[]括起来的科目, 不开辟空间 */
const char * str_getSubject(const char * s) ;

/** 找[科目] 的科目 */
char * str_getSubjectHead(const char * s);

/** [科目] 的科目对应subjects数组的index */
int str_getSubjectId(const char * s) ;

int str_getSectionId(const char * s) ;

int str_getSchoolYear(const char * s) ;


/** 取书名 */
char * str_getBookName(const char * s);

/** 取课名 */
char * str_getLesson(const char * s);

//static void str_removeExtension(char * s);

/**
 * [科目]出版社学段科目年级册别课名.txt 格式化函数，比如
 * [数学]人教版高中数学8年级上册_第一课小数的知识.txt 格式化为：
 * 人教版\n高中数学\n8年级上册\n第一课小数的知识
 */
char * str_format(const char * s ) ;

/** 判断所有字符是否是数字 */
Boolean str_isDigital(const char * s) ;

/** 判断所有字符是否是是0-127范围 */
Boolean str_isAlpha(const char * s) ;

/** 判断所有字符是否是是16进制值形式如 f1 */
Boolean str_is09af(const char * s) ;

Boolean str_is09az(const char * s) ;



/**
 * 找[科目] 的科目对应subjects数组的index
 */
int str_getSubjectId(const char * s) ;

/** 释放以NULL结束的字符串数组 */
void str_freeArray(char ** array) ;

enum{
    SHELF_SHOW_FILE ,
    SHELF_SHOW_SUBJECT
};

typedef struct {
    char * title ;
    char * contentType ;
    char ** subjects   ;
    char ** exts  ;
    Boolean group  ;
    Boolean noSubject ;
    int show  ;
    int category ;
}T_ShelfConfig ;

/** 解析书架启动的data, addStar 则给扩展名加上* */
T_ShelfConfig * str_shelfConfig(const char * data, Boolean addStar) ;

void  str_freeShelfConfig(T_ShelfConfig * config) ;

/** x.xxMB 或 x.xxGB */
void str_FileSize(int n, char * out)  ;



/** 把[aaa]bbb.ext 变成 bbb */
void str_cleanName(char * name) ;



//BXB-update-v2.3.4(u11).exe
int str_parseUpdateId(const char * name) ;
//BXB-update-v2.3.4(u11).exe
void str_parseVersion(const char * name, char * out) ;
float str_parseVersionId(const char * name);

Boolean str_urlEncode(const char* szSrc, char* pBuf, int cbBufLen);
Boolean str_urlDecode(const char* szSrc, char* pBuf, int cbBufLen);
Boolean str_isAbsolutePath(const char * path) ;

#ifdef __cplusplus
}
#endif

#endif //EL_STRING_H
