#ifndef _RKEY_HELPER_H
#define _RKEY_HELPER_H

#ifdef __cplusplus
extern "C" {
#endif 

int rk_open() ;
void rk_close() ;
int rk_do(char * buf, int len) ;
int rk_read_serial( char * szSerial ) ;
int rk_read(char * buf, int offset , int len)  ;
int rk_write(const char * buf, int offset , int len)  ;
int rk_error() ;

void * rk_handle() ;

#ifdef __cplusplus
}
#endif 

#endif //_RKEY_HELPER_H
