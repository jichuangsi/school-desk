#ifndef _HCTEA_H
#define _HCTEA_H


//#include <stdio.h>



/*************************************************************************
 * 
 *    NIST High Level C API, with some IBM additions
 *
 ************************************************************************/

//#define TRUE            1
//#define FALSE           0

#define DIR_ENCRYPT     0    /*  Are we encrypting?                         */
#define DIR_DECRYPT     1    /*  Are we decrypting?                         */
#define MODE_ECB        1    /*  Are we ciphering in ECB mode?              */
#define MODE_CBC        2    /*  Are we ciphering in CBC mode?              */
#define MODE_CFB1       3    /*  Are we ciphering in 1-bit CFB mode?        */

#define BAD_KEY_DIR      -1  /*  Key direction is invalid                   */
#define BAD_KEY_MAT      -2  /*  Key material not of correct length         */
#define BAD_KEY_INSTANCE -3  /*  Key passed is not valid                    */
#define BAD_CIPHER_MODE  -4  /*  Params struct passed to cipherInit invalid */
#define BAD_CIPHER_STATE -5  /*  Cipher in wrong state                      */

/* IBM Addition - a unsigned long must be 32 bits for this implementation */
//typedef unsigned long unsigned long;

/* IBM specific defines: these parameters can be changed               */
#define NUM_MIX 8           /* number of mixing rounds per stage       */
#define NUM_ROUNDS 16       /* number of full core rounds              */
#define NUM_SETUP 7         /* number of key setup mixing rounds       */

/* IBM specific defines: these parameters are fixed for this implementation */
#define W          32       /* number of bits in a unsigned long                     */
#define NUM_DATA    4       /* data block size in unsigned longs                     */
#define EKEY_WORDS (2*(NUM_DATA+NUM_ROUNDS))    /* number of subkey unsigned longs   */

/* IBM modified values                                                      */
#define MAX_KEY_SIZE (EKEY_WORDS*8)  /* max ASCII char's needed for a key   */
#define MAX_IV_SIZE (NUM_DATA*4)     /* max bytes's needed for an IV        */

/*  The structure for key information */
typedef struct {
    unsigned char  direction;	   /*  Key used for encrypting or decrypting?       */
    int   keyLen;	   /*  Length of the key in BITS                    */
    char  keyMaterial[MAX_KEY_SIZE+1];  /*  Raw key data in ASCII           */
    unsigned long  E[EKEY_WORDS];   /* IBM addition for mars expanded key            */
} keyInstance;

/*  The structure for cipher information */
typedef struct {
    unsigned char mode;             /*  MODE_ECB, MODE_CBC, or MODE_CFB1             */
    unsigned char IV[MAX_IV_SIZE];  /*  initial binary IV unsigned char for chaining          */
    unsigned long CIV[NUM_DATA];    /*  IBM addition: current IV in binary unsigned longs     */
} cipherInstance;


#ifdef __cplusplus
extern "C" {
#endif 

/*  NIST High level function protoypes  */
int makeKey(keyInstance *key, unsigned char direction, int keyLen, char *keyMaterial);

int cipherInit(cipherInstance *cipher, unsigned char mode, char *IV);

int blockEncrypt(cipherInstance *cipher, keyInstance *key, unsigned char *input,
                 int inputLen, unsigned char *outBuffer);

int blockDecrypt(cipherInstance *cipher, keyInstance *key, unsigned char *input,
                 int inputLen, unsigned char *outBuffer);

/*************************************************************************
 * 
 *    IBM Low Level (unsigned long Oriented) API
 *
 ************************************************************************/

/* setup a mars expanded key 
 *
 * k  (input) is the number of unsigned longs in the key
 * kp (input) is a pointer to the array of k key unsigned longs
 * ep (output) is a pointer to an array of EKEY_WORDS expanded subkey unsigned longs
 */
int tea_setup(int k, unsigned long *kp, unsigned long *ep);

/* The basic mars encryption of one block (of NUM_DATA WORDS) */
void tea_encrypt(unsigned long *in, unsigned long *out, unsigned long *ep);

/* mars decryption is simply encryption in reverse */
void tea_decrypt(unsigned long *in, unsigned long *out, unsigned long *ep);

#ifdef __cplusplus
}
#endif 
#endif 


