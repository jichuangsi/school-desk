#ifndef EL_TIME_H
#define EL_TIME_H

#include <time.h>

#ifdef __cplusplus
extern "C" {
#endif

/** yyyy-mm-dd hh:mm */
void time_YMDHM(time_t t, char * out) ;

/** yyyy-mm-dd hh:mm:ss */
void time_YMDHMS(time_t t, char * out) ;

/** yyyymmddhhmmss */
void time_secondSerial(char * out) ;

/** 秒数转字符串 天小时分秒 */
void time_secondToString(int seconds, char * out) ;

/** 2000年1月1日 **/
void time_ChineseYMD(char * out) ;
/** 2000年1月1日 **/
void time_toChineseYMD(time_t t, char * out) ;

/** 秒数换算成天数*/
int time_toDays(time_t t) ;

/** 今天天数*/
int time_days() ;

#ifdef __cplusplus
}
#endif

#endif // EL_TIME_H

