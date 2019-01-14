LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

#网络激活还是机型匹配
DEFINES += -DNET_REG 


DEFINES += -DANDROID 
LOCAL_MODULE := launcher

LOCAL_MODULE_FILENAME := liblauncher

LOCAL_SRC_FILES := launcher.c \
	launcherjni.cpp
	
LOCAL_C_INCLUDES += E:/cocosqt/libs/sqlite3/include
LOCAL_C_INCLUDES += E:/cocosqt/libs/websockets/include
#LOCAL_C_INCLUDES += E:/cocosqt/libs/scws/include
LOCAL_C_INCLUDES += E:/cocosqt/libs/zzip/include
LOCAL_C_INCLUDES += E:/cocosqt/c2d/external/curl/include/android

	LOCAL_C_INCLUDES += \
 	E:/cocosqt/libelcore/Classes/include \
 	E:/cocosqt/libelcore/Classes/include/app \
 	E:/cocosqt/libelcore/Classes/include/base \
 	E:/cocosqt/libelcore/Classes/include/common \
 	E:/cocosqt/libelcore/Classes/include/data \
 	E:/cocosqt/libelcore/Classes/include/driver \
 	E:/cocosqt/libelcore/Classes/include/ndk \
	

LOCAL_CFLAGS += -Wall -O2 $(DEFINES)
#去除cortex-a8警告
LOCAL_LDFLAGS += -fuse-ld=bfd -Wl,--fix-cortex-a8


LOCAL_LDLIBS:= -L$(SYSROOT)/usr/lib -llog -lz -landroid -LE:/cocosqt/DownloadService/proj.android/obj/local/armeabi-v7a -lelcore

LOCAL_LDLIBS += -LE:/cocosqt/libs/sqlite3/prebuilt/android -lsqlite3
LOCAL_LDLIBS += -LE:/cocosqt/libs/websockets/prebuilt/android -lwebsockets
LOCAL_LDLIBS += -LE:/cocosqt/libs/scws/prebuilt/android -lscws
LOCAL_LDLIBS += -LE:/cocosqt/libs/zziplib-0.13.59/prebuilt/android -lzzip
LOCAL_LDLIBS += -LE:/cocosqt/libs/curl/prebuilt/android -lcurl

LOCAL_LDLIBS += -LE:/cocosqt/libelcore/lib_linux_arm -lfs8836	
LOCAL_LDLIBS += -LE:/cocosqt/libs/sdkey/android/prebuilt -lsteapi-jni
LOCAL_LDLIBS += -LE:/cocosqt/libs/rkey/android/prebuilt -lrkeyintf	
LOCAL_LDLIBS += -LE:/cocosqt/libs/tfkey/android/prebuilt -ltfkey
#LOCAL_LDLIBS += -LE:/cocosqt/DownloadService/proj.android/obj/local/armeabi-v7a -lelcore

LOCAL_WHOLE_STATIC_LIBRARIES += elcore_static
LOCAL_WHOLE_STATIC_LIBRARIES += iconv

#要放在这个位置才能获得import-module输出的LOCAL_EXPORT_C_INCLUDES
include $(BUILD_SHARED_LIBRARY)

$(call import-module,libelcore/proj.android/jni)
$(call import-module,libs/libiconv-1.14)

