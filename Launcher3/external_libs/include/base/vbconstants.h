
#ifndef EL_VBCONSTANTS_H
#define EL_VBCONSTANTS_H

typedef enum EL_VB_MEDIA_MSG_E {
        MEDIA_PLAY = 0xF000 ,
        MEDIA_STOP ,
        MEDIA_END ,
        MEDIA_RECORD ,
}EL_VB_MEDIA_MSG_E ;

typedef enum EL_VB_MSG_E {
        EL_VB_MSG_START = 1000,

        MSG_CLICK ,
        MSG_PLAY_MEDIA ,
        MSG_RECORD_MEDIA ,
        MSG_STOP_MEDIA ,
        MSG_REPEAT  ,
        MSG_READWHOLE  ,
        MSG_FOLLOW  ,
        MSG_DATAREADY  ,
        MSG_GOTOPAGE  ,
        MSG_UPDATEDISPLAY  , //1010
        MSG_OPEN_BOOK  ,
        MSG_UPDATE_CONTROL  ,
        MSG_MEDIA_END  ,
        MSG_TOGGLE_TOOLBAR  ,
        MSG_SHOW_TOAST  ,
        MSG_STOP  ,
        MSG_HIDE_TOOLBAR  ,
        MSG_SHOW_TOOLBAR  ,
        MSG_SET_SCALE ,
        MSG_EXIT , //1020
        MSG_LONG_PRESS,
        MSG_BOOK_OPENED,
        MSG_BOOK_THUMBS_READY,
        MSG_BOOK_FIRST_CACHES_READY,
        MSG_BOOK_CACHES_READY,
        MSG_BOOK_PAGE_READY,
        MSG_BOOK_SCROLL_END,
        MSG_BOOK_ADD_QUEUE ,
        MSG_BOOK_INVALID_DRAG ,
        MSG_BOOK_SHOW_BLOCK ,
        MSG_BOOK_GOTO_PAGE,
        MSG_BOOK_REMOVE_DATA,
        EL_VB_MSG_END ,

} EL_VB_MSG_E;



//------------ state -----------------------
/**空闲模式*/
#define STATE_NONE  0
/**点读有效模式*/
#define STATE_READ  1
/**整读模式*/
#define STATE_READWHOLE  2
/**复读模式*/
#define STATE_REPEAT  3
/**跟读模式*/
#define STATE_FOLLOW  4
/**正在播放模式*/
#define STATE_READING  5

//-----------------------跟读状态常量--------------------------
//跟读流程 播放->录音->播放原音->播放录音
/**无状态*/
#define FOLLOWSTATE_NONE  0
/**播放状态*/
#define FOLLOWSTATE_PLAY  1
/**录音状态*/
#define FOLLOWSTATE_RECORD  2
/**播放原音状态*/
#define FOLLOWSTATE_PLAYORIGINAL  3
/**播放录音状态*/
#define FOLLOWSTATE_PLAYRECORD  4
/**完成状态*/
#define FOLLOWSTATE_FINISH  5




#endif
