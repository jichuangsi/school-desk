#ifndef _EL_NETUTILS_H
#define _EL_NETUTILS_H

#ifdef __cplusplus
extern "C" {
#endif

void net_coffee(unsigned long *input, unsigned long k[4]);
char * net_fool(char * buf) ;
char * net_trans(char * data, int size)  ;
char * net_xxoo(char * xx, const char * oo, int size) ;
unsigned char net_propHash(unsigned char * hash) ;
unsigned char net_wmiHash(unsigned char * hash, const char * wmi) ;
int net_request(const char * url, const char * in, int in_len, char * out, int * out_len, int max_out_len) ;
unsigned char net_serverFail(int error) ;
int net_readUserInfo(void * userinfo) ;
int net_writeUserInfo(void * userinfo) ;

#ifdef __cplusplus
}
#endif

#endif
