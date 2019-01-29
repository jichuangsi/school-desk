#ifndef _HCIC_BASE64_H_
#define _HCIC_BASE64_H_

#ifdef __cplusplus
extern "C" {
#endif

 char isbase64 (char ch);

 void base64_encode (const char *in, unsigned int inlen, char *out, unsigned int outlen);

 unsigned int base64_encode_alloc (const char *in, unsigned int inlen, char **out);

 char base64_decode (const char *in, unsigned int inlen,
                           char *out, unsigned int *outlen);

 char base64_decode_alloc (const char *in, unsigned int inlen,
                                 char **out, unsigned int *outlen);
				 
#define BASE64_LENGTH(inlen) ((((inlen) + 2) / 3) * 4)


#ifdef __cplusplus
}
#endif 

#endif /* _HCIC_BASE64_H_ */
