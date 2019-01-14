package cn.netin.launcher.installer;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

public class PackageParser {

	private static final String TAG = "EL PackageParser" ;


	public static String getPackageName(File file, String apkPath)  {  

		Object pkgParserPkg;
		try {
			pkgParserPkg = getPackage(file, apkPath);
		} catch (Exception e) {

			e.printStackTrace();
			return null ;
		}
		// pkgParserPkg 为Package对象
		if (pkgParserPkg == null) {
			Log.e(TAG, "pkgParserPkg = null") ;
			return null;
		}
		Field appInfoFld;
		try {
			appInfoFld = pkgParserPkg.getClass().getDeclaredField("applicationInfo");
		} catch (NoSuchFieldException e) {

			e.printStackTrace();
			return null;
		}
		// 从对象Package对象得到applicationInfo
		try {
			ApplicationInfo info = (ApplicationInfo) appInfoFld.get(pkgParserPkg);
			if (info == null) {
				return null;
			}
			
			return info.packageName ;

		} catch (Exception e) {
			
			return null;
		}


	}

	// >= 5.0 用file, 否则 apkPath 
	private static Object getPackage(File file, String apkPath) {
		String PATH_PackageParser = "android.content.pm.PackageParser";

		Constructor<?> packageParserConstructor = null;
		Method parsePackageMethod = null;
		Object packageParser = null;
		Class<?>[] parsePackageTypeArgs = null;
		Object[] parsePackageValueArgs = null;

		Class<?> pkgParserCls;
		try {
			pkgParserCls = Class.forName(PATH_PackageParser);
		} catch (ClassNotFoundException e) {
			Log.e(TAG, "class not found: " + PATH_PackageParser) ;
			return null ;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			try {
				packageParserConstructor = pkgParserCls.getConstructor();
			} catch (Exception e) {
				Log.e(TAG, "packageParserConstructor not found " ) ;

			}//PackageParser构造器

			try {
				packageParser = packageParserConstructor.newInstance();
			} catch (Exception e) {
				Log.e(TAG, "packageParserConstructor newInstance failed " ) ;

			}


			//PackageParser对象实例
			parsePackageTypeArgs = new Class<?>[]{File.class, int.class};
			parsePackageValueArgs = new Object[]{file, 0};//parsePackage方法参数
		} else {
			Class<?>[] paserTypeArgs = {String.class};
			try {
				packageParserConstructor = pkgParserCls.getConstructor(paserTypeArgs);
			} catch (Exception e) {
				Log.e(TAG, "packageParserConstructor not found " ) ;
			}//PackageParser构造器

			Object[] paserValueArgs = {apkPath};
			try {
				packageParser = packageParserConstructor.newInstance(paserValueArgs);
			} catch (Exception e) {
				Log.e(TAG, "packageParserConstructor not found " ) ;
			}//PackageParser对象实例

			parsePackageTypeArgs = new Class<?>[]{File.class, String.class,
				DisplayMetrics.class, int.class};
				DisplayMetrics metrics = new DisplayMetrics();
				metrics.setToDefaults();
				parsePackageValueArgs = new Object[]{new File(apkPath), apkPath, metrics, 0};//parsePackage方法参数

		}
		try {
			parsePackageMethod = pkgParserCls.getDeclaredMethod("parsePackage", parsePackageTypeArgs);
		} catch (Exception e) {
			Log.e(TAG, "parsePackage method not found " ) ;
		}
		// 执行pkgParser_parsePackageMtd方法并返回
		try {
			return parsePackageMethod.invoke(packageParser, parsePackageValueArgs);
		} catch (Exception e) {
			Log.e(TAG, "invoke parsePackage method failed " ) ;
			e.printStackTrace();
			return null ;
		}
	}



}



