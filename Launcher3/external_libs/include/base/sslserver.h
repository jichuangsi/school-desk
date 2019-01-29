/*
 * sslserver.h
 *
 *  Created on: 2013年11月18日
 *      Author: mac
 */

#ifndef SSLSERVER_H_
#define SSLSERVER_H_


#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

void * ssl_start(void)  ;
void * ssl_start_ecf(void * ecf, const char * path, char * url, int magic, const char * certPath) ;
void ssl_stop(void * handle) ;

void * http_start(const char * root)  ;
void http_stop(void * handle) ;

#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* SSLSERVER_H_ */
