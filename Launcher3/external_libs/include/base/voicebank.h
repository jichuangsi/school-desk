#ifndef VOICEBANK_H
#define VOICEBANK_H

#include "elcommon.h"

#ifdef __cplusplus
extern "C" {
#endif


/* 服务开始时调用，初始化，传入 voice.wts文件路径 */
char voicebank_open(const char* path ) ;

/* 取单词word的wav文件，保存到path
如果path == NULL，仅查询，不保存
*/
char voicebank_voice(const char * word, const char * path) ;
/* 服务退出时调用*/
void voicebank_close(void) ;


/*以下函数不使用*/
//char voicebank_info(const char * word, int *info) ;
void voicebank_build(const char * path) ;


#ifdef __cplusplus
}
#endif

#endif // VOICEBANK_H
