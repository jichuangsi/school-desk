#include "itemparser.h"
#include <stdio.h>
#include "elcommon.h"
#include "coffee.h"
#include "netutils.h"
#include "reg.h"
#include "auth.h"


#define ISIZE sizeof(ElItem)

static ElList * sAppList = NULL;
static int sNextId = 0 ;
static char sDeskPath[128] ;
static Boolean sInstallerAllowed = FALSE ;
static int sWaitCount = 0 ;


static const unsigned char header[16] = {'d', 'e', 's', 'k', 0, 0, 0, 0,
		0, 0, 0, 0, 0, 0, 0, 0} ;


static char sHash[16] ;

static const char * sWhiteList[] = {
		"com.adobe.flashplayer",
		"com.android.wallpaper.livepicker",
		"com.android.launcher",
		"com.android.launcher2",
		"com.android.launcher3",
		"com.android.documentsui",
		"com.google.android.inputmethod.pinyin",
		"org.videolan.vlc",
		"com.gensee.webcast",
		"com.cyanogenport.trebuchet",

		"com.mobisystems.office",
		"cn.wps.moffice_eng",
		"com.android.dialer",
		"com.android.mms",

		NULL
};

static void printList() {
	int i;
	ElItem *item ;
	int count ;
	char temp[1024] ;

	if (!sAppList){
		LOGE("sAppList is null \n") ;
		return ;
	}

	count = sAppList->size ;
	for ( i = 0 ; i < count ; i ++ ) {
		item = (ElItem *)ellist_get(sAppList, i) ;
		item_toString(item, temp);
		LOGD("\n=================\n%s",temp) ;
	}

}


static Boolean readDesk() {
	int i;
	ElItem *item ;
	int count ;
	char * p ;
	int size ;
	char * buf = el_fileToBuffer(sDeskPath, &size) ;
	if (!buf || size < 16) {
		LOGE("readDesk  fail \n") ;
		return FALSE ;
	}
	coffee(buf, size, sHash) ;
	if (memcmp(buf, "desk", 4) != 0) {
		LOGE("wrong header \n") ;
		return FALSE ;
	}
	memcpy(&count, buf + 4, 4) ;
	if (count < 1 || count > 500) {
		LOGE("wrong app count: %d \n", count) ;
		return FALSE ;
	}
	memcpy(&sNextId, buf + 8, 4) ;
	if (count < 1 || count > 1000) {
		LOGE("wrong app nextId: %d \n", sNextId) ;
		return FALSE ;
	}
	if (sAppList){
		item_freeList(sAppList) ;
		ellist_destroy(sAppList) ;
	}
	sAppList = ellist_create(NULL) ;
	p = buf + 16 ;
	for ( i = 0 ; i < count ; i ++ ) {
		item = (ElItem *)malloc(ISIZE) ;
		memcpy(item, p, ISIZE) ;
		ellist_add(sAppList, item) ;
		p += ISIZE ;
	}

	free(buf) ;


	return TRUE ;
}


static Boolean saveDesk(){
	int i;
	ElItem *item ;
	int count ;
	void * buf ;
	void * p ;
	int size ;
	Boolean ret ;

	count = sAppList->size ;

	size = 16 + ISIZE * count ;
	buf = malloc(size) ;
	memcpy(buf, header, 16) ;
	memcpy(buf + 4, &count, 4) ;
	memcpy(buf + 8, &sNextId, 4) ;
	p = buf + 16 ;

	for ( i = 0 ; i < count ; i ++ ) {
		item = (ElItem *)ellist_get(sAppList, i) ;
		memcpy(p, item, ISIZE) ;
		p += ISIZE ;
	}
	coffee_bean(buf, size, sHash) ;
	ret = el_bufferToFile(buf, size, sDeskPath) ;
	if (!ret) {
		LOGE("saveDesk fail") ;
	}
	return ret ;
}

int launcher_addItem(ElItem * item){

	if (!sAppList){
		LOGE("launcher_addItem sAppList is null \n") ;
		return ;
	}

	item->id = sNextId ;
	sNextId++ ;
	ellist_add(sAppList, item) ;
	if (saveDesk()){
		return item->id ;
	}
	return -1 ;
}
/*
int launcher_add( const char * name, int category, int groupId, int parentId,
             const char * icon, const char * pkg, const char * cls, const char * extra){

    ElItem * item = (ElItem *)malloc(ISIZE) ;
    memset(item, 0, ISIZE) ;
    strcpy(item->name, name) ;
    item->category = category;
    item->groupId = groupId;
    item->parentId = parentId;
    strcpy(item->icon, icon) ;
    strcpy(item->pkg, pkg) ;
    strcpy(item->cls, cls) ;
    strcpy(item->extra, extra) ;

    return addItemAndSave(item) ;
}
 */


void launcher_removeItem(int id){
	int i ;
	ElItem *item ;
	int size = sAppList->size;
	for ( i = 0 ; i < size ; i ++ ) {
		item = (ElItem *)ellist_get(sAppList, i) ;
		if (item->id == id){
			ellist_removeAt(sAppList, i) ;
			item_free(item) ;
			break ;
		}
	}
	saveDesk() ;
}

ElList * launcher_list(){
	return sAppList ;
}


int launcher_init(void){
	Boolean ret ;
	int error ;
	//const char hash[] = {0xd0, 0xa0, 0xdc, 0x09, 0x8c, 0x7d, 0x18, 0xc3, 0x1b, 0x93, 0x17, 0xc1, 0xb7, 0xfe, 0x71, 0xc2} ;

	ret = platform_extractAsset("desk", sDeskPath)  ;
	if (!ret) {
		return AUTH_ERROR_DATA ;
	}
	//LOGD("sDeskPath=%s \n", sDeskPath) ;
#ifdef NET_REG
	error = reg_check();
	if (error != AUTH_ERROR_NONE) {
		return error ;
	}
	reg_code(sHash) ;
#else
	//不网络激活，就通过取build.prop的hash来取得密钥。
	//以上方法需要绑定硬件。现在不绑定硬件，直接给密钥
	#if 0
		ret = net_propHash(sHash) ;
		if (!ret) {
			return AUTH_ERROR_DATA ;
		}
	#else
		memcpy(sHash, hash, 16) ;
	#endif
#endif
	ret = readDesk() ;
	if (!ret ){
		return AUTH_ERROR_DATA;
	}

	return AUTH_ERROR_NONE ;
}


int launcher_activate(const char * code){
	return reg_reg(code) ;
}

void launcher_setInstallerAllowed(Boolean allowed){
	//LOGD("setInstallerAllowed=%d", allowed) ;
	sInstallerAllowed = allowed ;
	if (allowed) {
		sWaitCount = 0 ;
	}
}

Boolean launcher_isAllowed(const char * pkg, const char * cls, Boolean banInstaller){
	const char * s ;
	int i ;
	ElItem *item ;
	int size ;

	if (str_startsWith(pkg, "cn.netin.")
			|| str_startsWith(pkg, "net.xuele.")
			|| (str_equals(pkg, "android") && str_equals(cls, "android"))
			|| (str_equals(pkg, "android") && str_equals(cls, "com.android.internal.app.ResolverActivity"))
			|| (str_equals(pkg, "android") && str_equals(cls, "com.android.internal.app.ChooserActivity"))
			|| (str_equals(pkg, "com.huawei.android.internal.app") && str_equals(cls, "com.huawei.android.internal.app"))

	) {
		return TRUE ;
	}

	if (sInstallerAllowed ) {
		if (str_equals(pkg, "com.android.packageinstaller")){
			//LOGD("isAllowed: sInstallerAllowed is true \n") ;
			return TRUE ;
		}
		if (sWaitCount > 4){
			sWaitCount = 0 ;
			sInstallerAllowed = FALSE ;
			//LOGD("set sInstallerAllowed = false \n") ;
		}else{
			sWaitCount++ ;
		}
	}

	if (!banInstaller) {
		if (str_equals(pkg, "com.android.packageinstaller")){
			//Log.d(TAG, "isAllowed: BAN_INSTALLER is false") ;
			return TRUE ;
		}
	}

	if (str_equals(pkg, "cn.netin.flashbrowser") && str_equals(cls, "org.mozilla.search.SearchActivity")) {
		return FALSE ;
	}
	if (str_equals(pkg, "com.android.systemui") && str_equals(cls, "com.android.systemui.recent.RecentsActivity")) {
		return FALSE ;
	}



	if (sAppList == NULL) {
		return FALSE ;
	}
	size = sAppList->size ;
	if (size == 0) {
		return FALSE ;
	}

	i = 0 ;
	while (1){
		s = sWhiteList[i] ;
		if (s == NULL) {
			break ;
		}
		if (str_equals(pkg, s)){
			return TRUE ;
		}
		i++;
	}

	for ( i = 0 ; i < size ; i ++ ) {
		item = (ElItem *)ellist_get(sAppList, i) ;
		if (str_equals(item->pkg,pkg)){
			return TRUE ;
		}
	}

	return FALSE ;
}
