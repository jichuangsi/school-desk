
#ifndef _HCIC_TYPES_H
#define _HCIC_TYPES_H

/*  CPU Type define ***************/
#define MCS51          1
#define OTHER          2
#define CPU         OTHER

/* data type defination */
#if (CPU==MCS51)

	#ifndef TRUE 
		#define	TRUE	1
	#endif
	#ifndef FALSE
		#define	FALSE	0
	#endif
	#ifndef NULL
		#define	NULL	0
	#endif
	#ifndef false 
		#define	false 	0
	#endif
	#ifndef true
		#define	true 	1
	#endif
	
	#ifndef BOOL 
		#define	BOOL	char
	#endif
	#ifndef PVOID
		#define	PVOID	(void*)
	#endif
	#ifndef UCHAR
		#define	UCHAR	unsigned char
	#endif
	
	#ifndef SCHAR
		#define	SCHAR	char
	#endif
	
	#ifndef  INT8
		#define	INT8	signed short
	#endif
	
	#ifndef UINT8
		#define	UINT8	unsigned short
	#endif
	#ifndef  INT16
		#define	INT16	signed short
	#endif
	
	#ifndef UINT16
		#define	UINT16	unsigned short
	#endif
	
	#ifndef PUINT16
		#define	PUINT16	UINT16*
	#endif
	
	#ifndef INT32
		#define	INT32	long
	#endif
	
	#ifndef UINT32
		#define	UINT32	unsigned long
	#endif

	#ifndef  BYTE
		#define	BYTE	unsigned char
	#endif
	

	#ifndef WORD
		#define	WORD	unsigned short
	#endif
	
	#ifndef DWORD
		#define	DWORD	unsigned long  
	#endif

#elif (CPU==OTHER)

	#ifndef BYTE
		#define	BYTE	unsigned char		/* 8 bit unsigned */	
	#endif
	#ifndef WORD
		#define	WORD	unsigned short		/* 16 bit unsigned */
	#endif
	#ifndef DWORD
		#define	DWORD	unsigned int		/* 32 bit unsigned */
	#endif
	#ifndef UINT8
		#define	UINT8	unsigned char		/* 8 bit unsigned */
	#endif
	#ifndef INT8
		#define	INT8	signed char 		/* 8 bit signed */
	#endif
	#ifndef UINT16
		#define	UINT16	unsigned short		/* 16 bit unsigned */
	#endif
	#ifndef INT16
		#define	INT16	signed short			/* 16 bit signed  */
	#endif
	#ifndef UINT32	
		#define	UINT32	unsigned int		/* 32 bit unsigned */
	#endif
	#ifndef INT32
		#define	INT32	signed int			/* 32 bit signed */
	#endif
	#ifndef uint8
		#define	uint8	unsigned char		/* 8 bit unsigned */
	#endif
	#ifndef int8	
		#define	int8	signed char			/* 8 bit signed */
	#endif
	#ifndef uint16
		#define	uint16	unsigned short		/* 16 bit unsigned */
	#endif	
	#ifndef int16
		#define	int16	signed short			/* 16 bit signed */
	#endif
	#ifndef uint32	
		#define	uint32	unsigned int		/* 32 bit unsigned */
	#endif
	#ifndef int32
		#define	int32	signed int			/* 32 bit signed */
	#endif
	#ifndef BOOL
		#define	BOOL	unsigned char		/* Boolean define */
	#endif
	/* Boolean	values */
	#ifndef TRUE
		#define	TRUE	1					/* Boolean TRUE value */
	#endif
	#ifndef FALSE
		#define	FALSE	0					/* Boolean FALSE value */
	#endif
	#ifndef NULL
		#define	NULL	0					/* used for pointer */
	#endif

#endif

#endif //_HCIC_TYPES_H

