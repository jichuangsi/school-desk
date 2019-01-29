#ifndef EL_RAND_H
#define EL_RAND_H


#ifdef __cplusplus
extern "C" {
#endif

    void el_srand(void) ;
    int el_rand(int max);
    /**返回16个字节的随机字符串*/
    char * el_randStr() ;
    char * el_randStrX8() ;

#ifdef __cplusplus
}
#endif

#endif // EL_RAND_H

