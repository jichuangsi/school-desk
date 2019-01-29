 #ifndef _MD5UTIL_H
#define _MD5UTIL_H

#ifdef __cplusplus
extern "C" {
#endif

void EncryptMD5(unsigned char *output, unsigned char *input, int len);
void EncryptMD5str(char *output, unsigned char *input, int len);

#ifdef __cplusplus
}
#endif

#endif