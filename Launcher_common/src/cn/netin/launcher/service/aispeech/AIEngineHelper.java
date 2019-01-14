package cn.netin.launcher.service.aispeech;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import com.aispeech.AIEngine;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

public class AIEngineHelper {

	private static String TAG = "EL AIEngineHelper";
	private static int BUFFER_SIZE = 4096;
	private static Context mContext ;
	private static boolean mIsBusy ; 

	public static void init(Context context) {
		mContext = context ;
		mIsBusy = false ;
	}

	synchronized public static long getEngine() {

		Log.d(TAG, "getEngine ... ") ;
		mIsBusy = true ;
		String filesDirStr = null ;

		byte buf[] = new byte[1024];
		AIEngine.aiengine_get_device_id(buf, mContext);
		String deviceId = new String(buf).trim();
		Log.d(TAG, "deviceId: " + deviceId);
		String serialNumber = registerDeviceOnce(AIConstants.APP_KEY, AIConstants.SECRECT_KEY, deviceId, AIConstants.USERID);
		if (serialNumber == null) {
			return 0 ;
		}
		Log.d(TAG, "serialNumber: " + serialNumber);

		if (AIConstants.NEED_EXTRACT) {
			//获取APP内部存储或外部存储的默认目录，外部存储优先
			File filesDir = getFilesDir(mContext);
			filesDirStr = filesDir.getAbsolutePath() ;	
			extractResourceOnce(filesDir, "aiengine.resource_en.zip");
			extractProvisionOnce(filesDir, "aiengine.provision");
			extractResourceOnce(filesDir, "vad.zip");
		}else{
			filesDirStr = getPrepairedFilesDir() ;
		}
		if (filesDirStr == null) {
			Log.e(TAG, "getEngine filesDirStr == null") ;
			mIsBusy = false ;
			return 0 ;
		}		
		//filesDirStr = "/mnt/sdcard/Android/data/com.aispeech.sample/files" ;
		String cfg = String.format("{\"appKey\": \"%s\", \"secretKey\": \"%s\", \"serialNumber\": \"%s\",\"provision\": \"%s\", \"vad\": {\"enable\": 1, \"res\": \"%s\", \"speechLowSeek\": 200, \"sampleRate\": 16000}, \"native\": {\"en.word.score\":{\"res\": \"%s\"},\"en.sent.score\":{\"res\": \"%s\"}}}",
				AIConstants.APP_KEY, AIConstants.SECRECT_KEY, 
				serialNumber,
				filesDirStr + "/aiengine.provision",
				filesDirStr + "/vad/vad.0.8.bin" ,
				filesDirStr + "/aiengine.resource_en/en/bin/eng.wrd.strap.1.6" ,
				filesDirStr + "/aiengine.resource_en/en/bin/eng.snt.robust.splp.0.9"
				);
		Log.d(TAG, "cfg: " + cfg);	

		long engine = AIEngine.aiengine_new(cfg, mContext);
		if (engine == 0) {
			Log.e(TAG, "aiengine_new fail! ");	
		}
		Log.e(TAG, "aiengine_new engine=" + engine);	

		mIsBusy = false ;
		return engine ;
	}

	public static boolean isBusy() {
		return mIsBusy ;
	}
	private static  String getPrepairedFilesDir() {
		String filesDirStr = "/mnt/sdcard/" + AIConstants.FILES_DIR ;
		String f1 = filesDirStr + "/aiengine.provision" ;
		String f2 = filesDirStr + "/vad/vad.0.8.bin" ;
		String f3 = filesDirStr +  "/aiengine.resource_en/en/bin/eng.wrd.strap.1.6/eng.wrd.strap.1.6.bin" ;
		String f4 = filesDirStr + "/aiengine.resource_en/en/bin/eng.snt.robust.splp.0.9/eng.snt.robust.splp.0.9.bin" ;

		if (
				new File(f1).exists() 
				&& new File(f2).exists() 
				&& new File(f3).exists() 
				&& new File(f4).exists() 
				) {
			return filesDirStr ;
		}else{
			Log.d(TAG, "getPrepairedFilesDir internal fail.") ;
			Log.d(TAG, f1) ;
			Log.d(TAG, f2) ;
			Log.d(TAG, f3) ;
			Log.d(TAG, f4) ;
			Log.d(TAG, f1 + " " + new File(f1).exists()) ;
		}

		if(! Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			Log.d(TAG, "getPrepairedFilesDir external fail. NOT MEDIA_MOUNTED") ;
			return null ;
		}
		filesDirStr =  Environment.getExternalStorageDirectory().getAbsolutePath() + AIConstants.FILES_DIR ;
		f1 = filesDirStr + "/aiengine.provision" ;
		f2 = filesDirStr + "/vad/vad.0.8.bin" ;
		f3 = filesDirStr +  "/aiengine.resource_en/en/bin/eng.wrd.strap.1.6/eng.wrd.strap.1.6.bin" ;
		f4 = filesDirStr + "/aiengine.resource_en/en/bin/eng.snt.robust.splp.0.9/eng.snt.robust.splp.0.9.bin" ;

		Log.d(TAG, filesDirStr) ;
		if (
				new File(f1).exists() 
				&& new File(f2).exists() 
				&& new File(f3).exists() 
				&& new File(f4).exists() 
				) {
			return filesDirStr ;
		}

		Log.d(TAG, "getPrepairedFilesDir external fail.") ;

		return null ;
	}

	private static String readFileAsString(File file) throws IOException {
		String line;
		StringBuffer sb = new StringBuffer();
		BufferedReader br = new BufferedReader(new FileReader(file));
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		br.close();
		return sb.toString();
	}

	private static void writeFileAsString(File file, String str) throws IOException {
		FileWriter fw = new FileWriter(file);
		fw.write(str);
		fw.close();
	}

	private static void removeDirectory(File directory) {
		if (directory.isDirectory()) {
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					removeDirectory(files[i]);
				}
				files[i].delete();
			}
			directory.delete();
		}
	}

	/**
	 * extract resource once, the resource should in zip formatting
	 * 
	 * @param context
	 * @param name
	 * @return return resource directory contains resources for native aiengine cores on success, otherwise return null
	 */
	public static File extractResourceOnce(File filesDir, String name) {

		try {
			String pureName = name.replaceAll("\\.[^.]*$", "");

			//在filesDir目录建子目录
			File targetDir = new File(filesDir, pureName);
			String md5sum = md5sum(mContext.getAssets().open(name));
			File md5sumFile = new File(targetDir, ".md5sum");
			//如果已经存在目录，且存在md5文件
			if (targetDir.isDirectory()) {
				//且存在md5文件
				if (md5sumFile.isFile()) {
					String md5sum2 = readFileAsString(md5sumFile);
					//如果md5值和要解压的文件相同
					if (md5sum2.equals(md5sum)) {
						//直接返回目标
						return targetDir; /* already extracted */
					}
				}
				//否则删除原来的目录
				removeDirectory(targetDir); /* remove old dirty resource */
			}
			//注意，不一定跑到这里的。解压
			unzip(mContext.getAssets().open(name), targetDir);
			//写md5值
			writeFileAsString(md5sumFile, md5sum);

			return targetDir;
		} catch (Exception e) {
			Log.e(TAG, "failed to extract resource", e);
		}

		return null;
	}

	/**
	 * extract provision profile once
	 * 
	 * @param context
	 * @param name
	 * @return return provision profile file on success, otherwise return null
	 */
	public static File extractProvisionOnce(File filesDir, String name) {

		/* TODO extract once */
		try {
			File targetFile = new File(filesDir, name);
			copyInputStreamToFile(mContext.getAssets().open(name), targetFile);
			return targetFile;
		} catch (Exception e) {
			Log.e(TAG, "failed to extract provision profile", e);
		}

		return null;
	}
	public static File externalFile(Context context, String name) {

		/* TODO extract once */
		try {
			File targetFile = new File(getFilesDir(context), name);			
			return targetFile;
		} catch (Exception e) {
			Log.e(TAG, "failed to open extenal file", e);
		}

		return null;
	}

	/**
	 * register device once
	 * @param appKey
	 * @param secretKey
	 * @param deviceId
	 * @param userId
	 * @return return serialNumber on success, otherwise return null
	 */
	public static String registerDeviceOnce(String appKey, String secretKey,
			String deviceId, String userId) {

		File filesDir ;
		if (AIConstants.NEED_EXTRACT) {
			//获取APP内部存储或外部存储的默认目录，外部存储优先
			filesDir = getFilesDir(mContext);
		}else{
			String dir = getPrepairedFilesDir() ;
			if (dir == null) {
				return null ;
			}
			filesDir = new File(dir) ;
		}

		File serialNumberFile = new File(filesDir, "aiengine.serial");
		String serialNumber;
		if (serialNumberFile.isFile()) {
			try {
				serialNumber = readFileAsString(serialNumberFile);
				return serialNumber;
			} catch (IOException e) {
				/* ignore */
			}
		}

		Log.d(TAG, "Online register") ;
		String timestamp = System.currentTimeMillis() + "";
		String sig = sha1(appKey + timestamp + secretKey + deviceId);

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("appKey", appKey));
		params.add(new BasicNameValuePair("timestamp", timestamp));
		params.add(new BasicNameValuePair("deviceId", deviceId));
		params.add(new BasicNameValuePair("sig", sig));
		params.add(new BasicNameValuePair("userId", userId));

		try {
			HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 5000);

			HttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpPost httpPost = new HttpPost("http://auth.api.aispeech.com/device");
			httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
			HttpResponse response = httpClient.execute(httpPost);
			String content = EntityUtils.toString(response.getEntity());
			Log.d(TAG, "register response:" + content) ;
			serialNumber = (String) new JSONObject(content).get("serialNumber");

			try {
				writeFileAsString(serialNumberFile, serialNumber);
			} catch (Exception e) {
				/* ignore */
			}

			return serialNumber;
		} catch (Exception e) {
			//e.printStackTrace();
			/* ignore */
			Log.e(TAG, "failed to register serialNumber", e);
		}

		return "";
	}
	

	////获取APP内部存储或外部存储的默认目录，外部存储优先
	public static File getFilesDir(Context context) {

		File targetDir = null;

		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			// targetDir = context.getExternalFilesDir(null); // not support android 2.1
			targetDir = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getApplicationInfo().packageName + "/files");
			if (!targetDir.exists()) {
				targetDir.mkdirs();
			}
		}

		if (targetDir == null || !targetDir.exists()) {
			targetDir = context.getFilesDir();
		}

		Log.d(TAG, " context.getFilesDir()=" +  context.getFilesDir().getAbsolutePath()) ;
		return targetDir;
	}

	private static void unzip(InputStream is, File targetDir)
			throws IOException {

		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is,
				BUFFER_SIZE));

		ZipEntry ze;
		while ((ze = zis.getNextEntry()) != null) {
			if (ze.isDirectory()) {
				new File(targetDir, ze.getName()).mkdirs();
			} else {

				File file = new File(targetDir, ze.getName());
				File parentdir = file.getParentFile();
				if (parentdir != null && (!parentdir.exists())) {
					parentdir.mkdirs();
				}

				int pos;
				byte[] buf = new byte[BUFFER_SIZE];
				OutputStream bos = new FileOutputStream(file);
				while ((pos = zis.read(buf, 0, BUFFER_SIZE)) > 0) {
					bos.write(buf, 0, pos);
				}
				bos.flush();
				bos.close();

				Log.d(TAG, file.getAbsolutePath());
			}
		}

		zis.close();
		is.close();
	}

	private static void copyInputStreamToFile(InputStream is, File file)
			throws Exception {
		int bytes;
		byte[] buf = new byte[BUFFER_SIZE];

		FileOutputStream fos = new FileOutputStream(file);
		while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) {
			fos.write(buf, 0, bytes);
		}

		is.close();
		fos.close();
	};

	private static String sha1(String message) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(message.getBytes(), 0, message.length());
			return bytes2hex(md.digest());
		} catch (Exception e) {
			/* ignore */
		}
		return null;
	}

	private static String bytes2hex(byte[] bytes) {
		StringBuffer sb = new StringBuffer(bytes.length * 2);
		for (int i = 0; i < bytes.length; i++) {
			int v = bytes[i] & 0xff;
			if (v < 16) {
				sb.append('0');
			}
			sb.append(Integer.toHexString(v));
		}
		return sb.toString();
	}

	private static String md5sum(InputStream is) {
		int bytes;
		byte buf[] = new byte[BUFFER_SIZE];
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			while ((bytes = is.read(buf, 0, BUFFER_SIZE)) > 0) {
				md.update(buf, 0, bytes);
			}
			is.close();
			return bytes2hex(md.digest());
		} catch (Exception e) {
			/* ignore */
		}
		return null;
	}
}
