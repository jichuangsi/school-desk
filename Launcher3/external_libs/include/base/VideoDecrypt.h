#ifndef RK_VIDEODECRYPT_H
#define RK_VIDEODECRYPT_H

//不能用extern "C" ，这样会用c的不带前后缀的函数符号。c++用的是带前后缀的函数符号，用于区分重载的函数
#ifdef __cplusplus
//extern "C" {
#endif 

typedef signed long long int64;

#define DecryptKeysize 1024*16

enum EncryptProvider{
	RK_VIDEO_NOENCRYPT,
	RK_VIDEO_ENCRYPT,
} ;

enum EncryptType {
	NONEENCRYPT,
	VIDEOONLY,
	AUDIOONLY,
	ALLENCRYPT,
};

extern int sEncryptProvider;
extern int sEncryptType;
extern int64 VideoHeaderOffset;
extern char mDecryptKey[DecryptKeysize];



unsigned char FileHasAcceptableExtension(const char *extension);
void  VideoDecrypted_Init(const char * filename, int64 filesize);
void dec_video_data(char *videodata, int64 size, int64 offset, int provider, const char * decryptkey);

#ifdef __cplusplus
//}
#endif 

#endif 