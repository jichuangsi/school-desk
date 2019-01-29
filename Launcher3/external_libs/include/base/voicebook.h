
#ifndef  _VOICEBOOK_H_
#define  _VOICEBOOK_H_

#include "elcommon.h"
#include "voicebookstruct.h"
#include "ecf.h"

#ifdef __cplusplus
extern "C" {
#endif

void * vb_new(void) ;
TextBook * vb_openBook(void * handle, const char *filepath) ;
/** 取得一页的数据 */
Page * vb_openPage(void * handle, int pageId) ;

Boolean  vb_page(void * handle, int pageId, Page *page) ;

char * vb_voice(void * handle, int id, int * size) ;
char * vb_picture(void * handle, int id, int * size) ;
int vb_save_voice(char * pVoice, int len) ;
char * vb_file(void * handle, const char * fileName, int * size) ;
int vb_save_file(char * buffer, int len, const char * filePath) ;

int vb_seek(void * handle, int cate, int id) ;
int vb_tell(void * handle );
void vb_close(void * handle ) ;
Boolean vb_eof (void * handle );
void vb_freePage(Page *page) ;

T_ECF_INFO * vb_getEcfInfo(void * handle ) ;
void * vb_getEcfHandle(void * handle ) ;

#ifdef __cplusplus
}
#endif

#endif //_VOICEBOOK_H_
