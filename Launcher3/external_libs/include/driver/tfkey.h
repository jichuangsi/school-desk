#ifndef _TFKEY_HELPER_H
#define _TFKEY_HELPER_H

#ifdef __cplusplus
extern "C" {
#endif 

int tf_open() ;
void tf_close() ;
int tf_do(char * buf, int len) ;
int tf_doTest(char * buf, int len) ;
int tf_en(char * buf, int len) ;
int tf_read_serial( char * szSerial ) ;
int tf_read(char * buf, int offset , int len)  ;
int tf_write(const char * buf, int offset , int len)  ;
int tf_error() ;

#ifdef __cplusplus
}
#endif 


#endif //_TFKEY_HELPER_H
