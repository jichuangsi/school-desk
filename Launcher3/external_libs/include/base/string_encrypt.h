#ifndef STRING_ENCRYPT
#define STRING_ENCRYPT

#ifdef __cplusplus
extern "C" {
#endif

char* se_en(const char* szSource, const char* szPassWord); // 加密，返回加密结果
char* se_de(const char* szSource, const char* szPassWord); // 解密，返回解密结果

int HCF_RUN(const unsigned char* data, int data_len, const unsigned char* key, int key_len, unsigned char* out, int* out_len) ;

#ifdef __cplusplus
}
#endif

#endif 
