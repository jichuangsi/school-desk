#ifndef _HCIC_IIC_H
#define _HCIC_IIC_H

#ifdef WIN32
#include <windows.h>
#else
//#include "hcic_types.h"
#endif

#include "eltypes.h"

#ifdef __cplusplus
extern "C" {
#endif 
 
void I2c_Init(BYTE SlvAdr);
Boolean I2cSendByte(BYTE c);
BYTE I2c_SendStr(BYTE *s, WORD Len);
BYTE I2c_RcvStr(BYTE *s, WORD Len);
void I2cRemove(void) ;
void Delay_1ms(BYTE cnt);

#ifdef __cplusplus
}
#endif 
#endif

