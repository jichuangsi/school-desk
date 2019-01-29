#ifndef NODEPARSER_H
#define NODEPARSER_H

#include <stdlib.h>
#include "elstack.h"
#include "ellist.h"
//#ifdef QTGUI
//#include <QString>
//#endif


#define TAG_NODE  0
#define CLOSE_NODE 1
#define TEXT_NODE  2

//#define Boolean char ;


typedef struct {
	int size ;
	int color ;
	int align ;
	int style ;
}T_Paint;


typedef struct _ElAttr{
	char *name;
	char *value ;
} ElAttr;

/** 排版的行信息*/
typedef struct{
	int id ;
	int y ; //代表起始Y位置
	int h  ; //本行的高度
	int contentWidth ; //本行的内容宽度，用于对齐中间或右边情况下的计算
}T_Line;

/** 图文区块 */
typedef struct{
	int lineId ; //行号
	int x ;
	int y ; 
	int w ; 
	int h ; 
	char * text ;
//#ifdef QTGUI
    void * s ;
//#endif
}T_Block;



typedef struct _ElNode{
	char type ;
	int id;
	int parentId ;
	char *tag;
    ElAttr attrs[16] ;
	int attrCount ;
	char *text ;
	struct _ElNode *next ;
	char * data ;
	int len ;

	void * paint ;
	void * font ;
	void * pen ;
	ElList * blockList ;
} T_ElNode;


typedef struct _NodeLinkedList{
	int total ;
	T_ElNode * first ;
	T_ElNode * current ;
	ElStack * stack ;

}NodeLinkedList ;


#ifdef __cplusplus
extern "C" {
#endif

/** 把NodeLinkedList 转换成 ElList, 并销毁原来的NodeLinkedList数据结构 */
ElList * node_toList(NodeLinkedList *list) ;

//解析文本，获得所有节点的ellist
ElList * node_list(const char * source,  Boolean * hasError) ;

//解析文本文件，获得所有节点的ellist
ElList  * node_list_file(const char * path, Boolean * hasError) ;

//释放所有节点内存
void node_free_list(ElList * list) ;

//解析文件，获得所有节点的链表
NodeLinkedList * node_parseFile(const char * path, Boolean * hasError) ;

//解析文本，获得所有节点的链表
NodeLinkedList * node_parse(const char * source, Boolean * hasError)  ;

//释放所有节点内存
void node_free_linkedlist(NodeLinkedList * list) ;

//取某个节点Id下所有第一层子节点(不含CLOSE_NODE)，返回指针数组
T_ElNode ** node_child(NodeLinkedList * list ,int id) ;

//根据ID取节点
T_ElNode * node_get(NodeLinkedList * list, int id) ;

//根据id取上级节点
T_ElNode * node_parent(NodeLinkedList * list, int id) ;

//取与此ID相邻的下一个同级节点
T_ElNode * node_sibling(NodeLinkedList * list, int id)  ;

//根据属性名取值
const char * node_value(T_ElNode *node, const char * name) ;

/** 设置属性的值，属性必须存在 */

Boolean node_setValue(T_ElNode *node, const char * name, char * value) ;

//获取节点的总数
int node_total(NodeLinkedList * list) ;

#ifdef __cplusplus
}
#endif

#endif // HTMLPARSE_H
