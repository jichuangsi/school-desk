
/**
 * @Title: WifiAdmin.java
 * @author: Xiho
 * @data: 2016年1月11日 下午3:34:17 <创建时间>
 * @history：<以下是历史记录>
 * @modifier: <修改人>
 * @modify date: 2016年1月11日 下午3:34:17 <修改时间>
 * @log: <修改内容>
 * @modifier: <修改人>
 * @modify date: 2016年1月11日 下午3:34:17 <修改时间>
 * @log: <修改内容>
 */
package cn.netin.wifisetting;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.Inet4Address;
import java.util.List;

/**
 * Function:Wifi连接管理工具类
 * Created by Xiho on 2016/2/1.
 */
public class WifiUtils {
	private static final String TAG = "EL WifiUtils" ;

	// 定义一个WifiManager对象
	private WifiManager mWifiManager;
	// 定义一个WifiInfo对象
	private WifiInfo mWifiInfo;
	// 扫描出的网络连接列表
	private List<ScanResult> mScanWifiList;
	// 网络连接列表
	private List<WifiConfiguration> mWifiConfigurations;

	private WifiManager.WifiLock mWifiLock;

	public static final int ERROR_NONE = 0 ;
	public static final int ERROR_OPEN = 1 ;
	public static final int ERROR_CONFIG = 2 ;
	public static final int ERROR_ENABLE = 3 ;
	//public static final int ERROR_CONNECT = 4 ;

	private static final String[] ERRORS = {
			"连接成功",
			"打开wifi失败",
			"找不到配置信息",
			"连接成功",
			"连接成功",
	};

	public WifiUtils(Context mContext) {
		//取得WifiManager对象
		mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
		//取得WifiInfo对象
		mWifiInfo = mWifiManager.getConnectionInfo();

	}

	public String errorText(int error){
		return ERRORS[error] ;
	}

	/**
	 * Function:关闭wifi<br>
	 * @return<br>
	 */
	public boolean closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			return mWifiManager.setWifiEnabled(false);
		}
		return false;
	}

	/**
	 * Gets the Wi-Fi enabled state.检查当前wifi状态
	 */
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.acquire();
		}
	}

	// 创建一个wifiLock
	public void createWifiLock() {
		mWifiLock = mWifiManager.createWifiLock("test");
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		mWifiConfigurations = mWifiManager.getConfiguredNetworks();
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public void connetionConfiguration(int index) {
		getConfiguration() ;
		if (index > mWifiConfigurations.size()) {
			return;
		}
		// 连接配置好指定ID的网络
		mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,true);
	}

	public void startScan() {
		// 开启wifi
		//openWifi();
		// 开始扫描
		mWifiManager.startScan();
		mScanWifiList = mWifiManager.getScanResults();
		//mScanWifiList可能为空
		if (mScanWifiList != null){
			//Log.d(TAG, "getScanResults " + mScanWifiList.size()) ;
		}

	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		mScanWifiList = mWifiManager.getScanResults();
		return mScanWifiList;
	}

	public List<ScanResult>  scanWifiList() {

		mWifiManager.startScan();
		mScanWifiList = mWifiManager.getScanResults();
		if (mScanWifiList != null){
			Log.d(TAG, "scanWifiList size=" + mScanWifiList.size()) ;
		}
		return mScanWifiList ;
	}

	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mScanWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mScanWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	/**
	 * Return the basic service set identifier (BSSID) of the current access
	 * point. The BSSID may be {@code null} if there is no network currently
	 * connected.
	 *
	 * @return the BSSID, in the form of a six-byte MAC address:
	 *         {@code XX:XX:XX:XX:XX:XX}
	 */
	public String getBSSID() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}

	public int getIpAddress() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	/**
	 * the network ID, or -1 if there is no currently connected network
	 */
	public int getNetWordId() {
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	/**
	 * Function: 得到wifiInfo的所有信息
	 */
	public String getWifiInfoString() {
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		mWifiManager.enableNetwork(wcgId, true);
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int networkId) {
		mWifiManager.disableNetwork(networkId);
		mWifiManager.disconnect();
	}

	// 忘记指定ID的网络
	public void forgetWifi(int networkId) {
		Log.d(TAG, "forgetWifi " + networkId) ;
		disConnectionWifi(networkId) ;
		mWifiManager.removeNetwork(networkId);
		mWifiManager.saveConfiguration(); 
	
	}

	public void forgetWifiBySsid(String ssid){
		String s = ssid.replace("\"", "") ;
		WifiConfiguration wifiConfig = this.isExsits(s);
		if (wifiConfig == null) {
			Log.e(TAG, "wifiConfig is null for " + s);
			return ;
		}
		forgetWifi(wifiConfig.networkId);
	
	}

	/**
	 * Function: 打开wifi功能<br>
	 * @return true:打开成功；false:打开失败<br>
	 */
	public boolean openWifi() {
		boolean bRet = true;
		if (!mWifiManager.isWifiEnabled()) {
			bRet = mWifiManager.setWifiEnabled(true);
		}
		return bRet;
	}

	/**
	 * 给外部提供一个借口，连接无线网络
	 *
	 * @param SSID
	 * @param Password
	 * @param Type
	 * @return <br>
	 * @return true:连接成功；false:连接失败<br>
	 *
	 */
	public boolean connect(String SSID, String Password, WifiConnectUtils.WifiCipherType Type) {
		if (SSID == null || Password == null || SSID.equals("")) {
			Log.e(TAG, "connect() ssid or password is empty.");
			return false;
		}
		
		if (!this.openWifi()) {
			return false;
		}
		// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒在检测……
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}
		
		WifiConfiguration wifiConfig = createConfig(SSID, Password, Type);
		if (wifiConfig == null) {
			Log.d(TAG, "connect wifiConfig is fail");
			return false;
		}
		// 断开连接
		mWifiManager.disconnect();

		// 添加一个新的网络描述为一组配置的网络。
		int networkId = mWifiManager.addNetwork(wifiConfig);
		if (networkId == -1) {
			Log.e(TAG, "connect addNetwork fail.");
			return false ;
		}
		mWifiManager.saveConfiguration();
		// 设置为true,使其他的连接断开
		boolean ret = mWifiManager.enableNetwork(networkId, true);
		if (!ret) {
			Log.d(TAG, "connect enableNetwork fail.");
			return false ;
		}
		return ret;
	}


	public int connectIfExist(String SSID) {

		if (!this.openWifi()) {
			Log.e(TAG, "connectIfExist openWifi fail") ;
			return ERROR_OPEN;
		}
		// 状态变成WIFI_STATE_ENABLED的时候才能执行下面的语句
		while (mWifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLED) {
			try {
				// 为了避免程序一直while循环，让它睡个100毫秒在检测……
				Thread.currentThread();
				Thread.sleep(100);
			} catch (InterruptedException ie) {
			}
		}

		// 查看以前是否也配置过这个网络
		WifiConfiguration wifiConfig = this.isExsits(SSID);
		if (wifiConfig == null) {
			Log.e(TAG, "connectIfExist wifiConfig does not exist:" + SSID);
			return ERROR_CONFIG;
		}

		// 断开连接
		mWifiManager.disconnect();
		// 重新连接
		//Log.d(TAG, "connectIfExist 准备连接到：" + wifiConfig.networkId );
		// 设置为true,使其他的连接断开
		boolean ret = mWifiManager.enableNetwork(wifiConfig.networkId, true);  
		if (!ret) {
			Log.e(TAG, "connectIfExist enableNetwork failed");
			return ERROR_ENABLE;
		}
		/*
		ret = mWifiManager.reconnect();
		if (!ret) {
			Log.e(TAG, "connectIfExist reconnect failed");
			return ERROR_CONNECT ;
		}
		*/
		
		return ERROR_NONE;
	}
	

	// 查看以前是否也配置过这个网络
	private WifiConfiguration isExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		if (existingConfigs == null || existingConfigs.size() == 0) {
			Log.e(TAG, "getConfiguredNetworks is empty") ;
		}
		for (WifiConfiguration existingConfig : existingConfigs) {
			Log.d(TAG, SSID + " -> " + existingConfig.SSID) ;
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiConfiguration createConfig(String SSID, String Password,
			WifiConnectUtils.WifiCipherType cipherType) {

		WifiConfiguration config = isExsits(SSID);
		/* 6.0以上不能移除
    	 if (config != null) {
             mWifiManager.removeNetwork(tempConfig.networkId);
         }
		 */
		if (config == null) {
			config = new WifiConfiguration();
		}
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";
		if (cipherType == WifiConnectUtils.WifiCipherType.WIFICIPHER_NOPASS) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (cipherType == WifiConnectUtils.WifiCipherType.WIFICIPHER_WEP) {
			config.preSharedKey = "\"" + Password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (cipherType == WifiConnectUtils.WifiCipherType.WIFICIPHER_WPA) {
			config.preSharedKey = "\"" + Password + "\"";
			//也有用这个的
			//config.wepKeys[0]= "\""+Password+"\"";   
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

		} else {
			Log.e(TAG, "createConfig invalid cipherType:" + cipherType) ;
			return null;
		}
		return config;
	}

	/**
	 * Function:判断扫描结果是否连接上<br>
	 * @param result
	 * @return<br>
	 */
	public boolean isConnected(ScanResult result) {
		if (result == null) {
			Log.e(TAG, "isConnected ScanResult is NULL") ;
			return false;
		}

		mWifiInfo = mWifiManager.getConnectionInfo();
		String ssid = "\"" + result.SSID + "\"";
		if (mWifiInfo.getSSID() == null ) {
			Log.e(TAG, "isConnected getSSID is NULL") ;
			return false;
		}
		if (mWifiInfo.getSSID().equals(ssid)) {
			return true;
		}
		Log.e(TAG, "isConnected getSSID:" + mWifiInfo.getSSID() + " != " + ssid) ;
		return false;
	}

	/**
	 * Function: 将int类型的IP转换成字符串形式的IP<br>
	 * @param ip
	 * @return<br>
	 */
	public String ipIntToString(int ip) {
		try {
			byte[] bytes = new byte[4];
			bytes[0] = (byte) (0xff & ip);
			bytes[1] = (byte) ((0xff00 & ip) >> 8);
			bytes[2] = (byte) ((0xff0000 & ip) >> 16);
			bytes[3] = (byte) ((0xff000000 & ip) >> 24);
			return Inet4Address.getByAddress(bytes).getHostAddress();
		} catch (Exception e) {
			return "";
		}
	}

	public WifiInfo getWifiInfo() {
		mWifiInfo = mWifiManager.getConnectionInfo();
		return mWifiInfo;
	}

	public int getNetworkId() {
		// result.SSID;
		mWifiInfo = mWifiManager.getConnectionInfo();
		return mWifiInfo.getNetworkId();
	}

	/**
	 * Function:信号强度转换为字符串
	 *
	 * @author Xiho
	 * @param level
	 */
	public static String singlLevToStr(int level) {

		String resuString = "无信号";

		if (Math.abs(level) > 100) {
		} else if (Math.abs(level) > 80) {
			resuString = "弱";
		} else if (Math.abs(level) > 70) {
			resuString = "强";
		} else if (Math.abs(level) > 60) {
			resuString = "强";
		} else if (Math.abs(level) > 50) {
			resuString = "较强";
		} else {
			resuString = "极强";
		}
		return resuString;
	}

	/**
	 * 添加到网络
	 *
	 * @param conf
	 */
	public boolean addNetwork(WifiConfiguration conf) {
		if (conf == null) {
			return false;
		}
		// receiverDhcp = new ReceiverDhcp(ctx, mWifiManager, this,
		// wlanHandler);
		// ctx.registerReceiver(receiverDhcp, new
		// IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
		int networkId = mWifiManager.addNetwork(conf);
		if (networkId == -1) {
			Log.d(TAG, "addNetwork failed") ;
			return false ;
		}
		boolean ret = mWifiManager.enableNetwork(networkId, true);
		if (!ret){
			Log.d(TAG, "addNetwork enableNetwork failed") ;
			return false ;
		}
		ret = mWifiManager.saveConfiguration();
		if (!ret){
			Log.d(TAG, "addNetwork saveConfiguration failed") ;
			return false ;
		}

		return true;
	}

	public boolean connectSpecificAP(ScanResult scan) {
		List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
		boolean networkInSupplicant = false;
		boolean connectResult = false;
		// 重新连接指定AP
		mWifiManager.disconnect();
		for (WifiConfiguration w : list) {
			// 将指定AP 名字转化
			// String str = convertToQuotedString(info.ssid);
			if (w.BSSID != null && w.BSSID.equals(scan.BSSID)) {
				connectResult = mWifiManager.enableNetwork(w.networkId, true);
				// mWifiManager.saveConfiguration();
				networkInSupplicant = true;
				break;
			}
		}
		if (!networkInSupplicant) {
			WifiConfiguration config = CreateWifiInfo(scan, "");
			connectResult = addNetwork(config);
		}

		return connectResult;
	}

	// 然后是一个实际应用方法，只验证过没有密码的情况：
	public WifiConfiguration CreateWifiInfo(ScanResult scan, String Password) {
		WifiConfiguration config = new WifiConfiguration();
		config.hiddenSSID = false;
		config.status = WifiConfiguration.Status.ENABLED;

		if (scan.capabilities.contains("WEP")) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.allowedAuthAlgorithms
			.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers
			.set(WifiConfiguration.GroupCipher.WEP104);

			config.SSID = "\"" + scan.SSID + "\"";

			config.wepTxKeyIndex = 0;
			config.wepKeys[0] = Password;
			// config.preSharedKey = "\"" + SHARED_KEY + "\"";
		} else if (scan.capabilities.contains("PSK")) {
			//
			config.SSID = "\"" + scan.SSID + "\"";
			config.preSharedKey = "\"" + Password + "\"";
		} else if (scan.capabilities.contains("EAP")) {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedAuthAlgorithms
			.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedPairwiseCiphers
			.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.SSID = "\"" + scan.SSID + "\"";
			config.preSharedKey = "\"" + Password + "\"";
		} else {
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

			config.SSID = "\"" + scan.SSID + "\"";
			// config.BSSID = info.mac;
			config.preSharedKey = null;
			//
		}

		return config;
	}


}

