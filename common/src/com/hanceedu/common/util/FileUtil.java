package com.hanceedu.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.util.Log;

/**
 * 文件工具类
 * @author QiJintang
 *
 */
public class FileUtil {
	
	private static final String TAG = "EL Common FileUtil";
	private static final int BUFFER_SIZE = 1024 * 64;
	
	public static  boolean unzip(String zip, String targetDir){

		InputStream fis;
		try {
			fis = new FileInputStream(zip);
		} catch (FileNotFoundException e1) {
			Log.e(TAG, "unzip FileNotFoundException " + zip) ;
			return false ;
		}
		File dir = new File(targetDir) ;
		if (!dir.exists()) {
			dir.mkdirs() ;
		}
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis,BUFFER_SIZE));

		ZipEntry ze;
		try {
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
					while ((pos = zis.read(buf, 0, BUFFER_SIZE)) != -1) {
						bos.write(buf, 0, pos);
					}
					bos.flush();
					bos.close();

					Log.d(TAG, "unzip: " + file.getAbsolutePath());
				}
			}
		} catch (FileNotFoundException e) {
			Log.e(TAG, "unzip FileNotFoundException") ;
			return false ;
		} catch (IOException e) {
			Log.e(TAG, "unzip IOException") ;
			return false ;
		}

		try {
			zis.close();
			fis.close();
		} catch (IOException e) {

		}

		return true ;

	}


	/**
	 * 把字节数据保存为文件
	 * @param mp3SoundByteArray
	 * @return
	 */
	public static boolean CreateFile(String path, byte[] mp3SoundByteArray) {

		File tempMp3 = null;
		FileOutputStream fos = null;
		//path = path + "/temp.mp3" ;
		try {
			tempMp3 = new File(path);
			if(tempMp3.exists()){
				tempMp3.delete();
			}
			fos = new FileOutputStream(tempMp3);
			fos.write(mp3SoundByteArray);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
			try {
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}

		return true ;

	}
	
	/**
	 * 删除 目录path下面所有文件和目录
	 */
	public static boolean removeDir(String path) {
		File f = new File(path) ;

		return removeDir(f) ;
	}
	
	/**
	 * 删除 目录f下面所有文件和目录
	 */
	public static boolean removeDir(File f) {
		if (!f.exists()) {
			return false ;
		}
		if (!f.isDirectory()){
			return false;
		}
		File[] files = f.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				removeDir(file);
			if (file.isFile())
				file.delete();
		}
		//再删除一次空目录
		files = f.listFiles();
		for (File file : files) {
			if (file.isDirectory())
				file.delete();
		}
		
		return true ;
	}

	/**
	 * 目录复制.包括子目录和文件 注意的是.如果设置覆盖为false.则为不处理而跳过.
	 * 
	 * @param src
	 *            源目录
	 * @param target
	 *            目标目录
	 * @param overWrite
	 *            是否覆盖
	 */
	public static void copyDir(File src, File target, boolean overWrite) {
		if (!src.isDirectory())
			return;// 不是目录的话,直接跳出
		File[] files = src.listFiles();
		for (File file : files) {
			if (file.isDirectory())// 是目录就递归
				copyDir(file, target, overWrite);
			if (file.isFile()) {
				if (overWrite)
					copyFileWithDir(file, target);
			}
		}
	}

	/**
	 * @category 复制文件.包括目录文件夹
	 * @param file
	 * @param target
	 */
	private static boolean copyFileWithDir(File file, File target) {
		String targetPath = target.getPath();
		if (!file.getParentFile().getName().equals("temp")) {
			targetPath += "/" + file.getParentFile().getName();
		}
		targetPath += "/" + file.getName();
		Log.d(TAG, file.toString() + " >>>>> " + targetPath);
		makeParentDirIfNeeded(targetPath);
		try {
			FileInputStream in = new FileInputStream(file);
			FileOutputStream out = new FileOutputStream(targetPath);
			return copyByFileChannel(in, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean copyFile(String src, String target) {
		makeParentDirIfNeeded(target);
		try {
			FileInputStream in = new FileInputStream(src);
			FileOutputStream out = new FileOutputStream(target);
			return copyByFileChannel(in, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	private final static long BUF_SIZE = 1024 * 1024;

	/**
	 * 基于文件流的通道复制
	 * 
	 * @param in
	 *            文件输入流
	 * @param out
	 *            文件输出流
	 * @return 如果返回true则复制完成,否则失败
	 */
	public static boolean copyByFileChannel(FileInputStream in,
			FileOutputStream out) {
		long length = BUF_SIZE;
		try {
			FileChannel inC = in.getChannel();
			FileChannel outC = out.getChannel();
			while (true) {
				if (inC.position() == inC.size()) {
					inC.close();
					outC.close();
					return true;
				}
				if ((inC.size() - inC.position()) < BUF_SIZE)
					length = (int) (inC.size() - inC.position());
				else
					length = BUF_SIZE;
				inC.transferTo(inC.position(), length, outC);
				inC.position(inC.position() + length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 检查目标目录文件夹
	 * 
	 * @param targetPath
	 */
	public static void makeParentDirIfNeeded(String path) {
		File f = new File(path).getParentFile();
		if (!f.exists())
			f.mkdirs();
	}

}
