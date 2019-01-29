/*
 * hash.h
 *
 *  Created on: 2014年4月1日
 *      Author: mac
 */

#ifndef ELHASH_H_
#define ELHASH_H_


#ifdef __cplusplus
extern "C" {
#endif // __cplusplus


unsigned long hash_buffer_value(void *src, int size) ;

/** 不会分配新内存，返回的实际是输入参数的指针 */

char *  hash_unmask(char * mask) ;
char * hash_value_unmask(char * url, unsigned long value) ;

unsigned long hash_file_value(const char * path) ;


#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* HASH_H_ */
