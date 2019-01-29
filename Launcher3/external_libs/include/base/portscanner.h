#ifndef _PORTSCANNER_H
#define _PORTSCANNER_H

#include "platform_helper.h"

#ifdef __cplusplus
extern "C" {
#endif


int port_scan(const char * ip, int start_port, int max_port) ;
const IPInfo * ip_scan(const char * prefix, int port, int * count) ;

#ifdef __cplusplus
}
#endif

#endif
