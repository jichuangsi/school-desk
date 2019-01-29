#ifndef ITEMPARSER_H
#define ITEMPARSER_H

#include "elcommon.h"

#define TAG_NODE 0
#define CLOSE_NODE 1
#define TEXT_NODE 2

#ifdef __cplusplus
extern "C" {
#endif

    //解析文本，获得ITEM ElList
    ElList * item_parse(char * source, Boolean * hasError) ;

    //解析文件，获得ITEM ElList
    ElList * item_parseFile(const char * path, Boolean * hasError) ;

    //获取该ID下所有直接的子ITEM
    ElList * item_child(ElList * pItems, int id) ;

    //释放ITEM内存
    void item_freeArray(ElItem ** pItems) ;

    void item_toString(ElItem * item, char * out) ;


#ifdef __cplusplus
}
#endif

#endif // ITEMPARSER_H
