#ifndef _SECUREVIDEO_H
#define _SECUREVIDEO_H

#include "tea.h"

typedef struct _SecureVideoHandle
{
    keyInstance keyin ;
    cipherInstance cipher ;
} SecureVideoHandle;



#ifdef __cplusplus
extern "C" {
#endif 

void * securevideo_init(char * buf) ;
int securevideo_dec(void * handle, char *video_data, int video_length) ;
//void * securevideo_init_enc(char * buf) ;
//int securevideo_enc(void * handle, char *video_data, int video_length) ;

#ifdef __cplusplus
}
#endif 
#endif
