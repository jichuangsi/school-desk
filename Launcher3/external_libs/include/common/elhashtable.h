#ifndef _HC_HASHTABLE_H_
#define _HC_HASHTABLE_H_


#ifdef __cplusplus
extern "C" {
#endif

/**
 * 如果开启会出现莫名奇妙的编译错误： multiple definition of 'std::__deque_buf_size(unsigned int)'
#ifndef __USE_ISOC99
#define inline
#endif
*/


#define create_hashtable(hsize) \
    hash_create(hash_strhash, equal_str, hsize)
	//这个会和其他的库重名
    unsigned int hash_strhash(void *src);
    int equal_str(void *k1, void *k2);

    struct hashentry;
    struct _hashtable;
    typedef struct _hashtable   Hashtable;


    Hashtable *hash_create(unsigned int (*keyfunc)(void *),
                           int (*comparefunc)(void *,void *),
                           int size);
    void hash_free(Hashtable *tab);
    void hash_insert(void *key, void *data, Hashtable *tab);
    void hash_remove(void *key, Hashtable *tab);
    void *hash_value(void *key, Hashtable *tab);
    void hash_for_each_do(Hashtable *tab, int (cb)(void *, void *));
    int hash_count(Hashtable *tab);


#ifdef __cplusplus
}
#endif

#endif

