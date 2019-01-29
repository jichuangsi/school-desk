/*
 * ACTIVITYEHELPER_H_
 *
 *  Created on: 2013年10月27日
 *      Author: mac
 */

#ifndef ACTIVITYEHELPER_H_
#define ACTIVITYEHELPER_H_


class ActivityHelper {
public:
	//AIServiceHelper();
	virtual ~ActivityHelper();

	static bool startWebActivity() ;
	static bool loadUrl(const char * url, const char * title) ;
	static bool browse(const char * url) ;
	/** clazz 必须实现 	public static Context getContext() ，传入Activity类的名，以获得Context */
	static unsigned long ssl_value(const char * clazz) ;
	static unsigned long ssl_value() ;
	static bool startVideoPlayerActivity() ;
	static bool playVideo(const char * url) ;
	static bool openFile(const char * url) ;
	static unsigned long hash(void *src, int size) ;

	static bool startXbwAbook() ;
	static bool startXbwExp() ;
	static bool hideInputMethod() ;
	static bool startDownloadService()  ;
	static void stopDownloadService()  ;
	static bool installApk(const char * path) ;
public :


private :




};

#endif /* ACTIVITYEHELPER_H_ */
