#ifndef _HCIC_API_H
#define _HCIC_API_H

#ifdef __cplusplus
extern "C" {
#endif 

int hcic_error() ;
int hcic_init(void);
int hcic_do(char* buf, int length) ;
int hcic_read(unsigned char* buf, unsigned int ofs, unsigned int len) ;
void hcic_close() ;

#ifdef __cplusplus
}
#endif 

#endif
