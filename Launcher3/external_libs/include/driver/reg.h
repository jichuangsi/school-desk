#ifndef EL_REG_H
#define EL_REG_H

#include "eltypes.h"
#include "stdlib.h"
#include "auth.h"


#ifdef __cplusplus
extern "C" {
#endif

int reg_check() ;
void  reg_code(char * code) ;
int reg_reg(const char * code) ;
void reg_error_text(int error,  const char ** title, const char ** text) ;

#ifdef __cplusplus
}
#endif

#endif // EL_AUTH_H
