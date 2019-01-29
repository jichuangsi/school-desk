#ifndef EL_ITEM_H
#define EL_ITEM_H

#include "ellist.h"

typedef struct _ElItem{
    int id;
    int parentId;
    int dataId;
    int subdataId;
    int type;
    int category ;
    int groupId ;
    int screen ;
    int unit ;
    int lesson ;
    int pageId ;
    int startId ;
    int endId ;
    char *content;
    void *data;
    int dataSize ;
    char name[256];
    char subname[256];
    char extra[256];
    char icon[256];
    char file[256];
    char subfile[256];
    char pkg[64];
    char cls[64];
    char contentType[64] ;

}ElItem;

#ifdef __cplusplus
extern "C" {
#endif

    void item_destroyList(ElList * list)  ;
    void item_freeList(ElList * list ) ;
    void item_free(ElItem * item) ;

    /** 创建一个ElItem, 各个成员变量初始化为0 */
    ElItem * item_create(void) ;


    /** 从itemList中获取该id对应的ITEM */
    ElItem * item_getItem(ElList * itemList, int id) ;

    /** 从itemList中获取该id对应的父ITEM */
    ElItem * item_getParent(ElList * itemList, int id) ;

    /** 从itemList中获取该id对应的父ID*/
    int item_getParentId(ElList * itemList, int id) ;

    /**从itemList中查询此id所属的子ITEM数量*/
    int item_getChildCount(ElList * itemList, int id)  ;

    /** 从itemList中查询是否存在此id的ITEM */
    Boolean item_hasChild(ElList * itemList, int id) ;

    /** 从itemList中查询此id的路径 */
    char * item_getPath(ElList * itemList, int id) ;


    /** 从itemList中获取该id下所有直接的子ITEM */
    ElList * item_getChildList(ElList * itemList, int id) ;

    /** 取第一层及第二层子目录 */
    ElList * item_getNestedList(ElList *itemList, int rootId);

    /** 根据含有pageId的list, 生成新的页码目录。原itemList会被销毁，原数据保留在新的里面 */
    ElList *  item_makePageList(ElList *itemList) ;

    /** 把数据文件名按科目分组，返回各个科目及文件数量, 返回的ElList 是新的item, 最后需要item_freeList */
    ElList *  item_getSubjectList(ElList * itemList) ;

    /** 把数据文件名按书本分组，返回各个书本及文件数量, 返回的ElList 是新的item, 最后需要item_freeList */
    ElList *  item_getBookList(ElList * itemList) ;


    /** 返回某科目下的ITEM。 返回的ElList只是引用itemList的item, 不应该用item_freeList */
    ElList * item_getListBySubject(ElList * itemList, char * subject) ;

    /** 返回某书名下的ITEM, 返回的ElList只是引用itemList的item, 不应该用item_freeList  */
    ElList * item_getListByBookName(ElList * itemList, char * bookName) ;

    void item_setIconAndFile(ElList *itemList)  ;

#ifdef __cplusplus
}
#endif

#endif // EL_ITEM_H
