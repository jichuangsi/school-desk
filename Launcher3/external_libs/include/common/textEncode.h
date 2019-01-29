/*
 * encode.h
 *
 *  Created on: 2013年9月16日
 *      Author: mac
 */

#ifndef TEXTENCODE_H_
#define TEXTENCODE_H_

#include "eltypes.h"

#ifdef __cplusplus
extern "C" {
#endif

/*UTF8转为GB2312*/
char * u2g(const char *inbuf) ;

/*GB2312转为UTF8*/
char * g2u(const char *inbuf) ;

Boolean utog(const char *inbuf, char * outbuf);

Boolean gtou(const char *inbuf, char * outbuf) ;

Boolean uctou(const char *inbuf, size_t inlen, char * outbuf, size_t outlen) ;

#ifdef __cplusplus
}
#endif

#endif /* TEXTENCODE_H_ */
