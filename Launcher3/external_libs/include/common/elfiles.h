#ifndef EL_FILES_H
#define EL_FILES_H

#include "eltypes.h"
#include "ellist.h"


#ifdef __cplusplus
extern "C" {
#endif


/**
    * 搜索多个目录下符合条件的文件
    * @param dirs 目录字符串数组
    * @param filters 文件名适配条件。仅支持通配符*
    *   支持 *, *.*, *something, *something*, something*, something
    *   也支持排除条件-， 比*.hgv 和-*.exp.hgv组合，表示查找*.hgv,但不包括*.exp.hgv
    * @param isRecursive 是否遍历子目录
    * @return 符合条件的文件名list， 包含路径
    */
ElList * files_search(const char ** dirs, const char ** filters, Boolean isRecursive) ;

/** 在各驱动器及学习内容文件夹查找一个符合filter的文件 */
char * files_find(const char * filter, const char * fourCC) ;

void files_test(void) ;


/** 在各驱动器及学习内容文件夹取符合filters条件的文件list */
ElList * files_contentList(const char ** filters) ;
/** 在各驱动器及学习内容文件夹中取符合bookId文件list，文件名为exceptFileName的除外 */
ElList * files_friends(int bookId, int unitId, int lessonId, const char ** pFilters, const char * exceptFileName) ;

/** 列出dir下直接的目录和文件，返回 T_File * list。 orderBy 0: 不排序， 1: 按时间 2: 按类型（未实现） */
ElList * files_listFilesT(const char * dir, const char ** filters, int orderBy) ;


#ifdef __cplusplus
}
#endif

#endif //EL_FILES_H
