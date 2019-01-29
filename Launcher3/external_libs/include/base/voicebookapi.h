#ifndef   _VOICEBOOKAPI_H_
#define		_VOICEBOOKAPI_H_

#include "elcommon.h"
#include "voicebookstruct.h"
#include "ecf.h"

typedef struct{
	TextBook * book ;
	int readWholeId  ;
	int onWhichPage ;
	int state  ;
	int playstate ;
	/**************************/
	//test
	int currentTestId  ;
	char hasTest  ;
	int testCount  ;
	int testPage  ; // testing which page
	int errorCount ;
	int scores ;
	int testId ;
	Test *selectedTest  ;
	/*********************/
	int 	currentPageId ; //=
	int  	playingTurn  ;
	int	pageSide  ;// 0 = 左 ; 1= 右
	char   	reset0 ;
	Page * page0;//左页
	Page * page1;//右页
	Page	 * currentPage ;//上一次点击的页
	Page * selectedPage ; //选择的页
	Block * selectedBlock ; //选择的页
	char 	* pText ;
	char  * pVoice  ;
	int voiceLength ;
	char reset;
	char 	actionPrev ; //允许上一页
	char 	actionNext ; // 允许下一页
	char 	page0IsBlank ; //page0是否为空
	char 	page1IsBlank ; //page1是否为空
	char *buffer0  ;
	char *buffer1  ;
	Boolean isEmptyBlock ;
	void * handle ;
}T_VBAPI;

#ifdef __cplusplus
extern "C" {
#endif

T_VBAPI * vbapi_openBook(const char *filepath) ;

    /***************/

    /** 打开某一页，把数据放到内存备用 */
    Boolean vbapi_openPage(T_VBAPI * vb, int pageId);
    /** 双页显示模式下使用，会打开左右两页 */
    Boolean vbapi_openTwoPages(T_VBAPI * vb, int gotoPageId);
    /**
     * 点击处理
     * @param whichPage : 0=左 , 1=右, -1=当前页
     * @return < 0 : 错误;  0: 没有; > 0 : 第几遍
     */
    int vbapi_click(T_VBAPI * vb, int whichPage, int  x , int y);
    /**  获得所点击位置的block. turn为第几遍 */
    Block * vbapi_clickBlock(T_VBAPI * vb, int whichPage, int  x , int y, int * turn) ;
    char * vbapi_getText(T_VBAPI * vb) ;
    char * vbapi_getVoice(T_VBAPI * vb) ;
    int vbapi_getVoiceLength(T_VBAPI * vb) ;
    void vbapi_closeBook(T_VBAPI * vb);
    int vbapi_getState(T_VBAPI * vb);
    void vbapi_onSoundStop(T_VBAPI * vb) ;
    int vbapi_readWholeStart(T_VBAPI * vb, int whichPage);
    Block * vbapi_readWholeContinue(T_VBAPI * vb );

    //TEST
    int vbapi_testStart(T_VBAPI * vb, int whichPage);
    int vbapi_testContinue(T_VBAPI * vb) ;
    int vbapi_testClick(T_VBAPI * vb, int whichPage, int x, int y) ;
    int vbapi_getCurrentTestId(T_VBAPI * vb) ;
    /***************/

    void vbapi_stopAll(T_VBAPI * vb) ;
    Page * vbapi_getPage(T_VBAPI * vb, int pageId) ;
    void vbapi_freePage( Page *page) ;
    char * vbapi_getFileData(T_VBAPI * vb, const char * fileName, int * size) ;
    char * vbapi_getPictureData(T_VBAPI * vb, int id, int * size)  ;
    char * vbapi_getVoiceData(T_VBAPI * vb, int id, int * size)  ;

    T_ECF_INFO * vbapi_getEcfInfo(T_VBAPI * vb) ;
    void * vbapi_getEcfHandle(T_VBAPI * vb) ;

#ifdef __cplusplus
}
#endif


#endif
