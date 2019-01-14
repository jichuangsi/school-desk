package com.hanceedu.common.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;



public class NetworkHelper {

	private static final String TAG = "EL NetworkHelper" ;

	private static Context mContext = null ;

	public static void setContext(Context context) {
		mContext = context ;
	}

	public static Context getContext() {
		return mContext ;
	}	

	//根据Wifi信息获取本地Mac
	public static String macFromWifiInfo(){
		if (mContext == null) {
			return null ;
		}
		WifiManager wifi = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);  
		WifiInfo info = wifi.getConnectionInfo();  

		Log.d(TAG, "macFromWifiInfo :" + info.getMacAddress()); 
		return info.getMacAddress(); 
	}
	
	static public  String macFromWifiInfo2(){  
		if (mContext == null) {
			return null ;
		}
		WifiManager wifi_service = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);  
		DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();  
		WifiInfo wifiinfo = wifi_service.getConnectionInfo();  
		
		Log.d(TAG, "macFromWifiInfo2 getIpAddress = " + wifiinfo.getIpAddress()); 
		Log.d(TAG, "macFromWifiInfo2 getIpAddress format = " + Formatter.formatIpAddress(dhcpInfo.ipAddress));  
		//System.out.println("DHCP info gateway----->"+Formatter.formatIpAddress(dhcpInfo.gateway));  
		//System.out.println("DHCP info netmask----->"+Formatter.formatIpAddress(dhcpInfo.netmask));  
		//DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址  
		return Formatter.formatIpAddress(dhcpInfo.ipAddress);  
	}  
	

	//获取本地IP
	static public String macFromNetworkInterface() {  
		String ipv4;
		byte[] mac ;
		String macString = null;
		try {  
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces() ;
			ArrayList<NetworkInterface>  nilist = Collections.list(en); 

			for (NetworkInterface ni: nilist)   
			{  			
				ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses()); 
				for (InetAddress address: ialist){  
					ipv4 = address.getHostAddress() ;
					Log.d(TAG, "macFromNetworkInterface ipv4=" + ipv4); 
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4))   
					{   
						mac = ni.getHardwareAddress();
						if (mac != null) {
							macString = byte2hex(mac);
							Log.d(TAG, "macFromNetworkInterface=" + macString); 
							return macString;  
						}else{
							Log.e(TAG, "macFromNetworkInterface getHardwareAddress fail") ;
						}

					}  
				}  
			}  

		} catch (SocketException ex) { 
			Log.e(TAG, "macFromNetworkInterface fail");  
			//ex.printStackTrace();

		}  
		Log.e(TAG, "macFromNetworkInterface return null");  
		return null;  
	} 


	//根据IP获取本地Mac
	public static String macFromIp() {
		if (mContext == null) {
			return null ;
		}

		String mac_s= "";
		try {
			byte[] mac;
			String ip = getIp() ;
			if (ip == null) {
				Log.e(TAG, "macFromIp getIp fail") ;
				return null ;
			}
			Log.d(TAG, "macFromIp getIp=" + ip); 
			NetworkInterface ne=NetworkInterface.getByInetAddress(InetAddress.getByName(ip));
			mac = ne.getHardwareAddress();
			mac_s = byte2hex(mac);
		} catch (Exception e) {
			Log.e(TAG, "macFromIp fail"); 
			//e.printStackTrace();
		}
		Log.d(TAG, "macFromIp :" + mac_s); 
		return mac_s;
	}

	public static  String byte2hex(byte[] b) {
		StringBuffer hs = new StringBuffer(b.length);
		String stmp = "";
		int len = b.length;
		for (int n = 0; n < len; n++) {
			stmp = Integer.toHexString(b[n] & 0xFF);
			if (stmp.length() == 1)
				hs = hs.append("0").append(stmp);
			else {
				hs = hs.append(stmp);
			}
		}
		return String.valueOf(hs);
	}


	//获取本地IP
	static public String getIp() {  
		try {  
			String ipv4;  
			ArrayList<NetworkInterface>  nilist = Collections.list(NetworkInterface.getNetworkInterfaces());  
			for (NetworkInterface ni: nilist)   
			{  
				ArrayList<InetAddress>  ialist = Collections.list(ni.getInetAddresses());  
				for (InetAddress address: ialist){  
					if (!address.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ipv4=address.getHostAddress()))   
					{   
						Log.e(TAG, "getIp=" + ipv4); 
						return ipv4;  
					}  
				}  
			}  

		} catch (SocketException ex) { 
			Log.e(TAG, "getIp fail");  
			//ex.printStackTrace();

		}  
		
		Log.e(TAG, "getIp return null");  
		return null;  
	} 

	public static String getIp2() {  
		try {  
			for (Enumeration<NetworkInterface> en = NetworkInterface  
					.getNetworkInterfaces(); en.hasMoreElements();) {  
				NetworkInterface intf = en.nextElement();  
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); 
						enumIpAddr.hasMoreElements();) {  
					InetAddress inetAddress = enumIpAddr.nextElement();  
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {  
						Log.e(TAG, "getIp2=" +  inetAddress.getHostAddress().toString()); 	
						return inetAddress.getHostAddress().toString();  
					}  
				}  
			}  
		} catch (SocketException ex) {  
			Log.e(TAG, "getIp2 fail");  
			//ex.printStackTrace();
		}  

		Log.e(TAG, "getIp2 return null");  
		return null;  
	} 



	static public void test() {
		String mac ;
		mac = macFromWifiInfo() ;
		Log.d(TAG, "test macFromWifiInfo=" + mac) ;
		
		mac = macFromWifiInfo2() ;
		Log.d(TAG, "test macFromWifiInfo2=" + mac) ;			
		
		mac = macFromNetworkInterface() ;
		Log.d(TAG, "test macFromNetworkInterface=" + mac) ;		
		
		mac = macFromIp() ;
		Log.d(TAG, "test macFromIp=" + mac) ;	
		
		
	}

}
