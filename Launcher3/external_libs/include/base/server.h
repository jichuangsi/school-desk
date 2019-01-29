#ifndef _EL_NET_H
#define _EL_NET_H


#ifdef __cplusplus
extern "C" {
#endif


int server_process(char * input, int size, char * output, int * out_size)  ;
/** input: key[64], output : plain[16] */
int net_data(const char * input, char * output) ;
int net_reg(int cardNO, const char * pass) ;

#ifdef __cplusplus
}
#endif

#endif
