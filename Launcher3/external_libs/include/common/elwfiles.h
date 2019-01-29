#ifndef EL_WFILES_H
#define EL_WFILES_H

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
    ElList * wfiles_search(const uint16 ** dirs, const uint16 ** filters, Boolean isRecursive) ;


#ifdef __cplusplus
}
#endif

#endif //EL_FILES_H
