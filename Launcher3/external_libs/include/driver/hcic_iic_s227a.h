/********************************************************************************
*                            Module: HCIC Drvier
*                 Copyright(c) 2012-2015 Hance Software Inc,
*                            All Rights Reserved. 
*
* History:         
*      <author>    <time>           <version >             <desc>
*       lbkang   2012-09-28     			1.0             build this file 
********************************************************************************/
/*!
 * \file   hcic.h
 * \brief  describle the command to access the Hcic driver and the data struct 
 *			used in the driver
 * \author lbkang
 * \par GENERAL DESCRIPTION:
 *       In This File 
 *       1)Define some Commands which are used to interact with the driver 
 *       2)Define some data type which will be used in driver and Hance application    
 * \par EXTERNALIZED FUNCTIONS:
 *     Copyright(c) 2012-2015 Hance Software, All Rights Reserved.
 *
 *  \version 1.0
 *  \date  2012/09/28
 *******************************************************************************/
#ifndef __HCIC_H__
#define __HCIC_H__

#define HCIC_SEND_BYTE			0x01
#define HCIC_RECEIVE_BYTE 	0x02
#define HCIC_SEND_STRING		0x03
#define HCIC_RECEIVE_STRING 0x04

/*!
 *  \brief
 *  	定义HCIC消息类型结构体
 */
typedef struct _i2c_msg_t
{
	u8 addr;
	u16 len;
	u8 *buf;
}i2c_msg_t;

#endif //end __HCIC_H__
