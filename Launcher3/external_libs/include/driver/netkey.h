#ifndef _EL_NET_H
#define _EL_NET_H

#ifdef __cplusplus
extern "C" {
#endif

void net_setUrl(const char * url) ;
int server_process(char * input, int size, char * output, int * out_size)  ;
/** input: key[64], output : plain[16] */
int net_do(const char * input, char * output) ;
int net_reg(int cardNO, const char * pass) ;
int net_open() ;
int net_error() ;
int net_isValid() ;
int net_isAccessible() ;
int net_read(void * buf, int offset, int len) ;
int net_read_serial(char * out) ;

int net_log(const void * keyData, const char * serial, int keyVersion, int authId, int userId, float versionId, const char * os, void * outData) ;

unsigned char net_isRegistered(const char * serial, int * outError) ;

#ifdef __cplusplus
}
#endif

#endif
