/*
 * html.h
 *
 *  Created on: 2014年4月6日
 *      Author: mac
 */

#ifndef EL_HTML_HELPER_H_
#define EL_HTML_HELPER_H_

#include "nodeparser.h"


#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

/** 解析标签中的 Width Height 值，如无，设为defaultValue */
void html_parseWidthHeight(T_ElNode * tag, int * width, int * height, int defaultValue) ;

/** 设置颜色和字体大小 */
void html_setPaintFontStyle(T_ElNode * tag, T_Paint * paint) ;

/** 解析颜色，返回RGBA值 */
int html_parseColor(const char * s) ;

/** 解析字体大小， 支持加减号*/
int html_parseSize (const char * s, int defaultValue)  ;

/** 支持解析以下格式 ff 0xff #ff */
int html_parseHex(const char * s) ;

void html_filter(char * buf,  const char * filterPairs[][2], const char * replacePairs[][3]) ;


#ifdef __cplusplus
}
#endif // __cplusplus

#endif /* EL_HTML_HELPER_H_ */
