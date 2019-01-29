#ifndef _SDKEY_HELPER_H
#define _SDKEY_HELPER_H

#ifdef __cplusplus
extern "C" {
#endif 

int sd_open() ;
void sd_close() ;
int sd_do(char * buf, int len) ;
int sd_en(char * buf, int len) ;
int sd_read_serial( char * szSerial ) ;
int sd_read(char * buf, int offset , int len)  ;
int sd_write(const char * buf, int offset , int len)  ;
int sd_error() ;

char sd_drive() ;

#ifdef __cplusplus
}
#endif 


#endif //_SDKEY_HELPER_H
