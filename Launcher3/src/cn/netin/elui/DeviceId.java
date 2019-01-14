package cn.netin.elui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.util.Log;


public class DeviceId {

	private static final String TAG = "EL DeviceId" ;

	private static Context mContext = null;

	//Android 6.0 : Access to mac address from WifiManager forbidden
	private static final String marshmallowMacAddress = "02:00:00:00:00:00";
	private static final String fileAddressMac = "/sys/class/net/wlan0/address";    


	public static void setContext(Context context) {
		mContext = context ;
	}

	public static String getAndroidId() {
		if (mContext == null) {
			return null ;
		}
		return Secure.getString(mContext.getContentResolver(),Secure.ANDROID_ID); 
	}

	//尝试读取MAC地址
	private  static String getMacFromWifiInfo(WifiManager wifiManager)
	{

		int maxTimes = 3 ;
		String mac= null ;
		boolean forcedOpen = false ;

		mac = getMacAddressFromWifi(wifiManager);
		if( mac != null)
		{
			return mac;
		}

		//获取失败，尝试打开wifi获取
		forcedOpen = openWifi(wifiManager);

		for(int index=0;index<maxTimes;index++)
		{

			mac = getMacAddressFromWifi(wifiManager);
			if(mac != null)
			{
				break;
			}
			Log.d(TAG, "try getMacAddressFromWifi fail " + index) ;
			if (forcedOpen) {
				try
				{
					Thread.sleep(2000);
				}
				catch (InterruptedException e)
				{

				}	
			}



		}


		//尝试关闭wifi
		if(forcedOpen)
		{
			closeWifi(wifiManager);
		}


		return mac;

	}

	//尝试打开wifi, 如果之前未打开，返回true，代表强制打开
	private static boolean openWifi(WifiManager manager)
	{
		boolean forcedOpen = false;
		int state = manager.getWifiState();
		if (state != WifiManager.WIFI_STATE_ENABLED && state != WifiManager.WIFI_STATE_ENABLING)
		{
			manager.setWifiEnabled(true);
			forcedOpen = true;
			Log.d(TAG, "openWifi force open" ) ;
			try
			{
				Thread.sleep(1000);
			}
			catch (InterruptedException e)
			{

			}	
		}

		return forcedOpen;
	}

	//尝试关闭MAC
	private static void closeWifi(WifiManager manager)
	{
		manager.setWifiEnabled(false);
	}

	//尝试获取MAC地址
	private static String getMacAddressFromWifi(WifiManager wifiManager)
	{
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		if (wifiInfo == null || wifiInfo.getMacAddress() == null)
		{
			//Log.e(TAG, "getMacAddressFromWifi getMacAddress is null") ;
			return null;
		}
		//String mac = wifiInfo.getMacAddress().replaceAll(":", "").trim().toUpperCase();
		String mac =  wifiInfo.getMacAddress() ;
		return mac ;

	}


	public static String getMac() {

		WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
		String mac = getMacFromWifiInfo(wifiManager) ;
		if(mac == null || mac.equals(marshmallowMacAddress)){
			String ret = null ;
			ret= getAdressMacByInterface();
			if (ret != null){
				return ret;
			} else {
				ret = getAddressMacByFile(wifiManager);
				return ret;
			}

		} else{
			return mac;
		}

	}

	private static String getAdressMacByInterface(){
		try {
			List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
			for (NetworkInterface nif : all) {
				if (nif.getName().equalsIgnoreCase("wlan0")) {
					byte[] macBytes = nif.getHardwareAddress();
					if (macBytes == null) {
						return "";
					}

					StringBuilder res1 = new StringBuilder();
					for (byte b : macBytes) {
						res1.append(String.format("%02X:",b));
					}

					if (res1.length() > 0) {
						res1.deleteCharAt(res1.length() - 1);
					}
					return res1.toString();
				}
			}
			
			for (NetworkInterface nif : all) {
				if (nif.getName().equalsIgnoreCase("eth0")) {
					byte[] macBytes = nif.getHardwareAddress();
					if (macBytes == null) {
						return "";
					}

					StringBuilder res1 = new StringBuilder();
					for (byte b : macBytes) {
						res1.append(String.format("%02X:",b));
					}

					if (res1.length() > 0) {
						res1.deleteCharAt(res1.length() - 1);
					}
					return res1.toString();
				}
			}

		} catch (Exception e) {
			Log.e(TAG, "getAdressMacByInterface fail ");
		}
		return null;
	}

	private static String getAddressMacByFile(WifiManager wifiManager)  {
		String ret = null;

		//尝试打开wifi获取
		boolean forcedOpen = openWifi(wifiManager);

		if (forcedOpen) {
			try
			{
				Thread.sleep(2000);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}	
		}
		File fl = new File(fileAddressMac);
		FileInputStream fin;
		try {
			fin = new FileInputStream(fl);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "getAddressMacByFile fail to open file") ;
			return null ;
		}
		StringBuilder builder = new StringBuilder();
		int ch;
		try {
			while((ch = fin.read()) != -1){
				builder.append((char)ch);
			}
		} catch (IOException e) {
			Log.e(TAG, "getAddressMacByFile fail to read file") ;
			return null ;
		}

		ret = builder.toString();
		try {
			fin.close();
		} catch (IOException e) {

		}


		//尝试关闭wifi
		if(forcedOpen)
		{
			closeWifi(wifiManager);
		}

		return ret;
	}



	public static String getIMEI() {
		if (mContext == null) {
			return null ;
		}
		try {

			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = tm.getDeviceId();
			List<String> IMEIS = new ArrayList<String>();
			if (checkimei(imei.trim())) {
				IMEIS.add(imei.trim());
			}


			try{
				TelephonyManager telephonyManager1 = (TelephonyManager)mContext.getSystemService("phone1");
				String imeiphone1=   telephonyManager1.getDeviceId();
				if (imeiphone1 != null && checkimei(imeiphone1)) {
					if (!IMEIS.contains(imeiphone1)) {
						IMEIS.add(imeiphone1);
					}
				}
			}  catch (Exception e) {

			}
			try{
				TelephonyManager telephonyManager2 = (TelephonyManager)mContext.getSystemService("phone2");
				String imeiphone2=   telephonyManager2.getDeviceId();
				if (imeiphone2 != null && checkimei(imeiphone2)) {
					if (!IMEIS.contains(imeiphone2)) {
						IMEIS.add(imeiphone2);
					}
				}
			}  catch (Exception e) {

			}

			List<String> imeis = IMEI_initQualcommDoubleSim();
			if (imeis != null && imeis.size() > 0) {
				for (String item : imeis) {
					if (!IMEIS.contains(item)) {
						IMEIS.add(item);
					}
				}
			}

			imeis = IMEI_initMtkSecondDoubleSim();
			if (imeis != null && imeis.size() > 0) {
				for (String item : imeis) {
					if (!IMEIS.contains(item)) {
						IMEIS.add(item);
					}
				}
			}
			imeis = IMEI_initMtkDoubleSim();
			if (imeis != null && imeis.size() > 0) {
				for (String item : imeis) {
					if (!IMEIS.contains(item)) {
						IMEIS.add(item);
					}
				}
			}
			imeis = IMEI_initSpreadDoubleSim();
			if (imeis != null && imeis.size() > 0) {
				for (String item : imeis) {
					if (!IMEIS.contains(item)) {
						IMEIS.add(item);
					}
				}
			}

			StringBuffer IMEI_SB = new StringBuffer();

			Integer TIMES_TEMP = 1;
			for (String item : IMEIS) {
				if (TIMES_TEMP > 1) {
					IMEI_SB.append('|');
				}
				IMEI_SB.append(item);
				// params.put("IMEI" + TIMES_TEMP, item);
				TIMES_TEMP++;
			}

			String imeis_tmp = IMEI_SB.toString().trim();


			if ("".equals(imeis_tmp)) {
				Log.e(TAG, "no_imei_1") ;
				imeis_tmp = null ;
			}
			return imeis_tmp;
			//
			// TextView_imei.setText(IMEI_SB.toString());
			//
			// WriteFile("imei", IMEI_SB.toString());

		} catch (Exception e) {
			//Log.e(TAG, "no_imei_2") ;
			return null;
		}

	}

	private static Boolean checkimeisame(String imei) {
		char firstchar = '0';
		if (imei.length() > 0) {
			firstchar = imei.charAt(0);
		}
		Boolean issame = true;
		for (int i = 0; i < imei.length(); i++) {
			char ch = imei.charAt(i);
			if (firstchar != ch) {
				issame = false;
				break;
			}
		}
		return issame;
		// if (issame) {
		// // 全是相同字符;
		// } else {
		// // 包含不同字符
		// }

	}

	private static Boolean checkimei(String IMEI) {
		Integer LEN = IMEI.length();
		if (LEN > 10 && LEN < 20 && !checkimeisame(IMEI.trim())) {
			return true;
		}
		return false;
	}

	private static List<String> IMEI_initMtkDoubleSim() {
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");
			Integer simId_1, simId_2;
			try {
				java.lang.reflect.Field fields1 = c.getField("GEMINI_SIM_1");
				fields1.setAccessible(true);
				simId_1 = (Integer) fields1.get(null);
				java.lang.reflect.Field fields2 = c.getField("GEMINI_SIM_2");
				fields2.setAccessible(true);
				simId_2 = (Integer) fields2.get(null);
			} catch (Exception ex) {
				simId_1 = 0;
				simId_2 = 1;
			}

			// java.lang.reflect.Method m = TelephonyManager.class
			// .getDeclaredMethod("getSubscriberIdGemini", int.class);
			// String imsi_1 = (String) m.invoke(tm, simId_1);
			// String imsi_2 = (String) m.invoke(tm, simId_2);

			java.lang.reflect.Method m1 = TelephonyManager.class
					.getDeclaredMethod("getDeviceIdGemini", int.class);
			String imei_1 = ((String) m1.invoke(tm, simId_1)).trim();
			String imei_2 = ((String) m1.invoke(tm, simId_2)).trim();

			// java.lang.reflect.Method mx = TelephonyManager.class
			// .getDeclaredMethod("getPhoneTypeGemini", int.class);
			// Integer phoneType_1 = (Integer) mx.invoke(tm, simId_1);
			// Integer phoneType_2 = (Integer) mx.invoke(tm, simId_2);
			// String defaultImsi = "";
			// if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
			// defaultImsi = imsi_2;
			// }
			// if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
			// defaultImsi = imsi_1;
			// }

			List<String> imeis = new ArrayList<String>();
			if (checkimei(imei_1)) {
				imeis.add(imei_1);
			}
			if (checkimei(imei_2)) {
				imeis.add(imei_2);
			}
			return imeis;
		} catch (Exception e) {
			return null;
		}

	}

	private static List<String> IMEI_initMtkSecondDoubleSim() {
		try {
			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> c = Class.forName("com.android.internal.telephony.Phone");

			Integer simId_1, simId_2;
			try {
				java.lang.reflect.Field fields1 = c.getField("GEMINI_SIM_1");
				fields1.setAccessible(true);
				simId_1 = (Integer) fields1.get(null);
				java.lang.reflect.Field fields2 = c.getField("GEMINI_SIM_2");
				fields2.setAccessible(true);
				simId_2 = (Integer) fields2.get(null);
			} catch (Exception ex) {
				simId_1 = 0;
				simId_2 = 1;
			}

			java.lang.reflect.Method mx = TelephonyManager.class.getMethod(
					"getDefault", int.class);
			TelephonyManager tm1 = (TelephonyManager) mx.invoke(tm, simId_1);
			TelephonyManager tm2 = (TelephonyManager) mx.invoke(tm, simId_2);

			// String imsi_1 = tm1.getSubscriberId();
			// String imsi_2 = tm2.getSubscriberId();

			String imei_1 = (tm1.getDeviceId()).trim();
			String imei_2 = (tm2.getDeviceId()).trim();
			//
			// Integer phoneType_1 = tm1.getPhoneType();
			// Integer phoneType_2 = tm2.getPhoneType();
			// String defaultImsi = "";
			// if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
			// defaultImsi = imsi_2;
			// }
			// if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
			// defaultImsi = imsi_1;
			// }

			List<String> imeis = new ArrayList<String>();
			if (checkimei(imei_1)) {
				imeis.add(imei_1);
			}
			if (checkimei(imei_2)) {
				imeis.add(imei_2);
			}
			return imeis;

		} catch (Exception e) {
			return null;
		}
	}



	private static List<String> IMEI_initSpreadDoubleSim() {
		try {
			Class<?> c = Class
					.forName("com.android.internal.telephony.PhoneFactory");
			java.lang.reflect.Method m = c.getMethod("getServiceName",
					String.class, int.class);
			String spreadTmService = (String) m.invoke(c,
					Context.TELEPHONY_SERVICE, 1);

			TelephonyManager tm = (TelephonyManager) mContext
					.getSystemService(Context.TELEPHONY_SERVICE);
			// String imsi_1 = tm.getSubscriberId();
			String imei_1 = (tm.getDeviceId()).trim();
			// Integer phoneType_1 = tm.getPhoneType();
			TelephonyManager tm1 = (TelephonyManager) mContext
					.getSystemService(spreadTmService);
			// String imsi_2 = tm1.getSubscriberId();
			String imei_2 = (tm1.getDeviceId()).trim();
			// Integer phoneType_2 = tm1.getPhoneType();
			// String defaultImsi = "";
			// if (TextUtils.isEmpty(imsi_1) && (!TextUtils.isEmpty(imsi_2))) {
			// defaultImsi = imsi_2;
			// }
			// if (TextUtils.isEmpty(imsi_2) && (!TextUtils.isEmpty(imsi_1))) {
			// defaultImsi = imsi_1;
			// }

			List<String> imeis = new ArrayList<String>();
			if (checkimei(imei_1)) {
				imeis.add(imei_1);
			}
			if (checkimei(imei_2)) {
				imeis.add(imei_2);
			}
			return imeis;

		} catch (Exception e) {
			return null;
		}
	}

	public static List<String> IMEI_initQualcommDoubleSim() {
		try {
			//TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
			Class<?> cx = Class
					.forName("android.telephony.MSimTelephonyManager");
			Object obj = mContext.getSystemService("phone_msim");
			Integer simId_1 = 0;
			Integer simId_2 = 1;
			//
			// java.lang.reflect.Method mx = cx.getMethod("getDataState");
			// // int stateimei_1 = (Integer) mx.invoke(cx.newInstance());
			// int stateimei_2 = tm.getDataState();
			// java.lang.reflect.Method mde = cx.getMethod("getDefault");
			java.lang.reflect.Method md = cx
					.getMethod("getDeviceId", int.class);
			// java.lang.reflect.Method ms = cx.getMethod("getSubscriberId",
			// int.class);
			// java.lang.reflect.Method mp = cx.getMethod("getPhoneType");

			// Object obj = mde.invoke(cx);

			String imei_1 = ((String) md.invoke(obj, simId_1)).trim();
			String imei_2 = ((String) md.invoke(obj, simId_2)).trim();

			// String imsi_1 = (String) ms.invoke(obj, simId_1);
			// String imsi_2 = (String) ms.invoke(obj, simId_2);

			// int statephoneType_1 = tm.getDataState();
			// int statephoneType_2 = (Integer) mx.invoke(obj);

			List<String> imeis = new ArrayList<String>();
			if (checkimei(imei_1)) {
				imeis.add(imei_1);
			}
			if (checkimei(imei_2)) {
				imeis.add(imei_2);
			}
			return imeis;

			// Log.e("tag", statephoneType_1 + "---" + statephoneType_2);

			// Class<?> msc = Class.forName("android.telephony.MSimSmsManager");
			// for (Method m : msc.getMethods()) {
			// if (m.getName().equals("sendTextMessage")) {
			// m.getParameterTypes();
			// }
			// Log.e("tag", m.getName());
			// }

		} catch (Exception e) {
			return null;
		}
	}

	public static final int CONNECTIVITY_UNAVAILABLE = 0 ;
	public static final int CONNECTIVITY_AVAILABLE = 1 ;
	public static final int CONNECTIVITY_CONNECTED = 2 ;

	public static int getNetState() {
		int ret = CONNECTIVITY_UNAVAILABLE ;
		try {
			ConnectivityManager connectivity = (ConnectivityManager) mContext
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivity == null) {
				Log.e(TAG, "CONNECTIVITY_SERVICE not available") ;
				return ret ;
			}
			NetworkInfo networkinfo = connectivity.getActiveNetworkInfo();
			if (networkinfo == null) {
				Log.e(TAG, "getActiveNetworkInfo fail") ;
				return ret ;
			}

			if (networkinfo.isAvailable()) {
				ret = CONNECTIVITY_AVAILABLE ;
				Log.d(TAG, "CONNECTIVITY_AVAILABLE") ;
			}

			if (networkinfo.isConnected()) {
				ret = CONNECTIVITY_CONNECTED ;
				Log.d(TAG, "CONNECTIVITY_CONNECTED") ;
			}else if(networkinfo.isConnectedOrConnecting()) {
				ret = CONNECTIVITY_AVAILABLE ;
				Log.d(TAG, "isConnectedOrConnecting") ;
			}

			if (networkinfo.getState() == NetworkInfo.State.CONNECTED) {
				ret = CONNECTIVITY_CONNECTED ;
			}else{
				Log.d(TAG, "State not connected") ;
			}
		} catch (Exception e) {

			e.printStackTrace();
		}
		return ret;
	}


	public static void release() {
		mContext = null ;
	}

}

