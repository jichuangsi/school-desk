package cn.netin.launcher.installer;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AXMLResource;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.widget.Toast;
import cn.netin.launcher.R;
import cn.netin.launcher.data.LauncherState;
import cn.netin.launcher.service.AppStat;

public class InstallerActivity extends Activity {

	private static final String TAG = "EL InstallerActivity" ;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_installer);

		handelIntent(this.getIntent()) ;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		handelIntent(intent) ;
	}

	@SuppressWarnings("unchecked")
	private void handelIntent(Intent intent) {

		if (LauncherState.isAlive()){

			Uri uri = intent.getData() ;
			if (uri == null) {
				Toast.makeText(this, "URI无效", Toast.LENGTH_LONG).show(); ;
				this.finish();
				return ;
			}
			//Log.d(TAG, "path=" + uri.getPath()) ;
			//Log.d(TAG, "uri=" +  uri.toString()) ;
			String uriStr = uri.toString() ;
			if (uriStr.endsWith("INSTALLER_TEST.apk")) {
				this.finish();
				return ;
			}

			String scheme = uri.getScheme() ;
			if (scheme == null) {
				Toast.makeText(this, "scheme数据无效", Toast.LENGTH_LONG).show(); ;
				this.finish();
				return ;
			}
			if ("package".equals(scheme)) {
				Toast.makeText(this, "不支持package多安装包", Toast.LENGTH_LONG).show(); ;
				finish();
				return;
			}
			if ( !"file".equals(scheme) && !"content".equals(scheme)) {
				Toast.makeText(this, "不支持此scheme：" + scheme, Toast.LENGTH_LONG).show(); ;
				finish();
				return;
			}
			if (!uri.toString().endsWith(".apk")) {
				Toast.makeText(this, "只支持apk文件安装", Toast.LENGTH_LONG).show(); ;
				finish();
				return;
			}

			InputStream ins = null ;

			if ("file".equals(scheme)) {
				File file = new File(uri.getPath());
				try {
					ins = new FileInputStream(file) ;
				} catch (FileNotFoundException e) {
					Toast.makeText(this, "找不到文件", Toast.LENGTH_LONG).show(); ;
					finish();
					return ;
				}


			}else{
				ParcelFileDescriptor pfd;
				try {
					//need to close
					pfd = getContentResolver().openFileDescriptor(uri, "r");
				} catch (FileNotFoundException e) {
					Toast.makeText(this, "找不到文件", Toast.LENGTH_LONG).show(); 
					finish();
					return ;
				}	
				FileDescriptor fd = pfd.getFileDescriptor();
				ins = new FileInputStream(fd);			
			}

			String pkg = getPkgFromApkStream(ins) ;
			if (ins != null){
				try {
					ins.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (pkg == null) {
				Toast.makeText(this, "解析APK包错误", Toast.LENGTH_LONG).show(); 
				finish();
				return ;
			}
			Log.d(TAG, "pkg=" + pkg) ;
			boolean allowed = AppStat.isAllowed(pkg, "") ;
			if (!allowed) {
				Toast.makeText(this, "禁止安装此应用", Toast.LENGTH_LONG).show(); 
				AppStat.setInstallerAllowed(false) ;
				finish();
				return ;
			}
			AppStat.setInstallerAllowed(true) ;
		}
		
		intent.setClassName("com.android.packageinstaller", "com.android.packageinstaller.PackageInstallerActivity") ;
		//startActivityForResult(intent, -1) ;
		startActivity(intent) ;
		this.finish();

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		Log.d(TAG, "onActivityResult") ;
		AppStat.setInstallerAllowed(false) ;
		finish();
	}  


	private static String getPkgFromApkStream(InputStream ins)  {  

		String pkg =  null ;
		try {
			ZipInputStream zis = new ZipInputStream(new BufferedInputStream(ins));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				String name = entry.getName() ;
				Log.d(TAG, "entry: " + name);
				if (entry.isDirectory()) {
					continue ;
				} 
				if (name.toLowerCase(Locale.ENGLISH).equals("androidmanifest.xml")) {

					//byte[] b=new byte[(int) entry.getSize()];//新建一个字节数组  
					//zis.read(b);//将文件中的内容读取到字节数组中  
					//String str2=new String(b);//再将字节数组中的内容转化成字符串形式输出  
					//Log.d(TAG, str2);  
					//saveFile(zis, "/storage/emulated/0/Download/a.xml") ;  

					AXMLResource axml =	new AXMLResource();
					axml.readZip(zis, entry.getSize()) ;

					String xml = axml.toXML();
					try {
						Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
						Node manifestNode = document.getFirstChild();
						NamedNodeMap manifestNodeAttributes = manifestNode.getAttributes();
						pkg = manifestNodeAttributes.getNamedItem("package").getNodeValue() ;

					} catch (SAXException e) {
						Log.e(TAG, "parse xml failed") ;
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						Log.e(TAG, "parse xml failed") ;
						e.printStackTrace();
					}

					break ;
				}				

			}

			zis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return pkg ;
	}

	public static void saveFile(InputStream ins, String path) {
		try {
			File file = new File(path) ;
			boolean ret = file.createNewFile() ;
			Log.d(TAG, "ret=" + ret) ;
			OutputStream os = new FileOutputStream(file);
			int bytesRead = 0;
			byte[] buffer = new byte[8192];
			while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
				os.write(buffer, 0, bytesRead);
			}
			os.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}



