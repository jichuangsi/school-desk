#ifndef EL_WSTRING_H
#define EL_WSTRING_H

#include <string.h>

#include "eltypes.h"


#ifdef SPREADTRUM
#include "mmk_app.h"

#define el_wstrcpy   MMIAPICOM_Wstrcpy
#define el_wstrncpy  MMIAPICOM_Wstrncpy
#define el_wstrcat  MMIAPICOM_Wstrcat
#define el_wstrncat MMIAPICOM_Wstrncat
#define el_wstrcmp   MMIAPICOM_Wstrcmp
#define el_wstrncmp  MMIAPICOM_Wstrncmp
#define el_wstrlen   MMIAPICOM_Wstrlen
#define el_wstrdup   wstr_dup
#define el_strtowstr   MMIAPICOM_StrToWstr
#define el_wstrtostr   MMIAPICOM_WstrToStr
#define el_wstrlower MMIAPICOM_Wstrlower

/*
extern wchar_t* MMIAPICOM_StrToWstr(
                                  const char* src,
                                  wchar_t* dst
                                  );
extern char* MMIAPICOM_WstrToStr( 
                                 const wchar_t* src,
                                 char* dst
                                 );


    sprintf((char *)time_str," %02d:%02d ",sys_time.hour,sys_time.min);
    MMIAPICOM_StrToWstr(time_str, time_wstr); 
	string.wstr_ptr = time_wstr;
	string.wstr_len = MMIAPICOM_Wstrlen(time_wstr);


*/




#else

#define el_wstrcpy   wcscpy
#define el_wstrncpy  wcsncpy
#define el_wstrcat  wcscat
#define el_wstrncat wcsncat
#define el_wstrcmp   wcscmp
#define el_wstrncmp  wcsncmp
#define el_wstrlen   wcslen
#define el_wstrdup   wstr_dup
#define el_strtowstr win32_c2w
#define el_wstrtostr win32_w2c
//#define el_wstrlower wstrlower

#endif


#ifdef __cplusplus
extern "C" {
#endif

#include "string.h"


    wchar_t * wstr_dup(const wchar_t *src) ;

    /**
     * 字符串替换函数
     * @param wchar_t * str 原始字符串
     * @param const wchar_t * from 目标字符串
     * @param const wchar_t *to 替换的字符串
     * @return 替换好的字符串
     **/
    wchar_t * wstr_replace(wchar_t *str, const wchar_t *from, const wchar_t *to) ;

    /** 去除字符串开始和结尾的空格 */
    wchar_t * wstr_trim(const wchar_t * str)  ;



    /** 判断src是否以s开头 */
    Boolean wstr_startsWith(const wchar_t * src, const wchar_t * s) ;

    /** 判断src是否以s结尾 */
    Boolean wstr_endsWith(const wchar_t * src, const wchar_t * s) ;


    /** 判断src是否包含s */
    Boolean wstr_contains(const wchar_t * src, const wchar_t * s);

    /** 判断两个字符串是否相同 */
    Boolean wstr_equals(const wchar_t * src, const wchar_t * s);



    /** 返回字符c 在 src 中的第一个下标。不存在则返回 -1 */
    int wstr_indexOfChr(const wchar_t * src, wchar_t c) ;


    /** 返回字符串 s 在 src 中的第一个下标。不存在则返回 -1 */
    int wstr_indexOfStr(const wchar_t * src, const wchar_t * s) ;


    /** 返回字符 c 在 src 中的最后一个下标。不存在则返回 -1 */
    int wstr_lastIndexOf(const wchar_t * src, wchar_t c) ;

    /**
    * 返回 c 在 src(0, len) 中的最后一个下标。不存在则返回 -1
    *
    */
    int wstr_lastIndexOfLen(const wchar_t * src, int len, wchar_t c ) ;

/**
* 字符串截取
* @param str : 原字符串
* @param form ： 截取的开始位置
* @param to : 截取的结束位置之后的一个字节位置
* @return 结果字符串的首地址
*/
    wchar_t * wstr_substring(const wchar_t *src,  int from,  int to) ;

/**
* 右边字符串截取
* @param str : 原字符串
* @param form ： 截取的开始位置，直至结尾
* @return 结果字符串的首地址
*/
    wchar_t * wstr_right(const wchar_t *src, const int from) ;

    //将以splitter分隔的字符串转成字符串数组
    wchar_t ** wstr_split(const wchar_t * str, const wchar_t splitter) ;


    /** 从包含路径的字符串中取文件名 */
    wchar_t * wstr_getFileName(const wchar_t * path) ;

    /** 从包含路径的字符串中取不含扩展名的文件名 */
    wchar_t * wstr_getFileNameOnly(const wchar_t * path);

    /** 取扩展名 */
    const wchar_t * wstr_getExtension(const wchar_t * path);


    // ===========================================================

    /** 找没有用[]括起来的科目, 不开辟空间 */
    const wchar_t * wstr_getSubject(const wchar_t * s) ;

    /** 找[科目] 的科目 */
    wchar_t * wstr_getSubjectHead(const wchar_t * s);

    /** [科目] 的科目对应subjects数组的index */
    int wstr_getSubjectId(const wchar_t * s) ;


    /** 找学段（小学初中高中）的的index*/
   // static int str_indexOfSection(const char * s);

    /** 取书名 */
    wchar_t * wstr_getBookName(const wchar_t * s);

    /** 取课名 */
    wchar_t * wstr_getLesson(const wchar_t * s);

    //static void str_removeExtension(char * s);

/**
 * [科目]出版社学段科目年级册别课名.txt 格式化函数，比如
 * [数学]人教版高中数学8年级上册_第一课小数的知识.txt 格式化为：
 * 人教版\n高中数学\n8年级上册\n第一课小数的知识
 */
    wchar_t * wstr_format(const wchar_t * s ) ;



#ifdef __cplusplus
}
#endif

#endif //EL_STRING_H
