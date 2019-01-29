//#include "hcic_types.h"
#include "eltypes.h"

#ifndef HCIC_V3_H
#define HCIC_V3_H

#ifdef __cplusplus
extern "C" {
#endif 

#if (CPU==MCS51)
	#define 	code 	code
	#define 	data 	data
#elif (CPU==OTHER)
  	#define	code 
  	#define	data 
  	#define	xdata
#endif


/*Error Nubmer *******/
#define  NO_ERROR                       0x00
#define ERROR_READ_CODE			0x04
#define ERROR_I2C_SENDERR		0x20
#define ERROR_ACK_Detect		0x20
#define ERROR_CHIP_OTHER		0xFF
#define	ERROR_Simulateing		0x49		// Arvin, 20100516
#define	ERROR_Detect_Simulate_parameter	0x50		// Arvin, 20100516
/********/

/* below content for use define application function by dfli 20090410*/
/*Select the key which is needed to update for ukey_update command*/
#define	FS8836_NO_SELECT		0x00
#define	FS8836_SELECT_KEY1		0x01
#define	FS8836_SELECT_KEY2		0x02
#define	FS8836_SELECT_KEY3		0x03

/*Select which algorithm is used, DES or TDES for data_encry or data_decry */
#define	FS8836_USING_DES		0x00
#define	FS8836_USING_TDES		0x04

/*Select plain or secret mode for data_decry*/
#define	FS8836_SECRET_MODE		0x00
#define	FS8836_PLAIN_MODE		0x08



/************************************************************************
*  
*        FS8836 lib funcion interface 
*
************************************************************************/
UINT8 InitFS8836Lib(UINT8 I2cSlvAdr, UINT8 *key, UINT8 Rand);
UINT8 Validation(UINT8 *key);
UINT8 SoftResetOpr(void);
UINT8 PowerDownChipOpr(void);
UINT8 Authentication(UINT8 *MessageAdd, UINT8 *isComplete, UINT8 *flagMAC);
//UINT8 Authentication(DWORD *MessageAdd, UINT8 *isComplete, UINT8 *flagMAC);
 

void CreateRN(UINT8 index, UINT8 curval, UINT8 rand);	// Arvin, 20100516
UINT8 UpdateUserKeyOpr(UINT8 sel_key,UINT8 *update_key);
UINT8 EncryptUserDataOpr(UINT8 para, UINT8 *idat, UINT8 *odat);
UINT8 DecryptUserDataOpr(UINT8 para, UINT8 *idat, UINT8 *odat);
UINT8 DecryptBootloadOpr(UINT8 *encry_pkt_id, UINT8 *idat, UINT8 *odat);

UINT8 WriteNVMOpr(UINT16 Nvmoffset, UINT16 len, UINT8 *buffer);
UINT8 ReadNVMOpr (UINT16 Nvmoffset, UINT16 len, UINT8 *buffer);
UINT8 UpdateUserKeyOpr(UINT8 sel_key,UINT8 *update_key);
UINT8 EncryptUserDataOpr(UINT8 para, UINT8 *idat, UINT8 *odat);
UINT8 DecryptUserDataOpr(UINT8 para,UINT8 *idat,UINT8 *odat);
UINT8 DecryptBootloadOpr(UINT8 *encry_pkt_id, UINT8 *idat, UINT8 *odat);

 
//BOOL FS8836_API_ComnunicationTest(void);
//BOOL FS8836_API_Init(void);
//BYTE FS8836_API_Authen(void);

//void ReadCode(BYTE *pBuff, BYTE *StAdd, DWORD Len);

#ifdef __cplusplus
}
#endif 

#endif

