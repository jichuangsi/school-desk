package com.hanceedu.common.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;


/**
 * 遍历目录 寻找指定后缀名文件
 * 
 * 
 */
public class FileSearcher {
	
	public FileSearcher() {
		
	}

	public List<String> search(String[] paths, String[] extensions, boolean isRecursive) {
		
		 List<String> resultPathList  = new ArrayList<String>();

		FileExtensionFilter filter ;
		if (extensions != null) {
			filter = new FileExtensionFilter(extensions);
		}else{
			filter = null ;
		}
		for (int i = 0; i < paths.length; i++) {
			find(paths[i], filter, isRecursive, resultPathList) ;
		}
		return resultPathList;
	}


	/**
	 * 寻找指定目录下指定后缀名文件 封装
	 */
	private void find(String path, FileExtensionFilter filter, boolean isRecursive,  List<String> resultPathList) {

		File file = new File(path);
		// 文件或目录是否存在
		if (!file.exists() || !file.isDirectory()) {
			return ;
		}

		File[] files = file.listFiles(filter);
		if (files != null && files.length > 0) {

			for (int i = 0; i < files.length; i++) {
				//System.out.println("found: " + files[i].getAbsolutePath());
				resultPathList.add(files[i].getAbsolutePath());
			}
		}
		if (isRecursive) {
			String[] subFiles = file.list();
			if (subFiles != null && subFiles.length > 0) {
				for (int i = 0; i < subFiles.length; i++) {
					find(path + "/" + subFiles[i], filter, true, resultPathList );
				}
			}
		}

	}



	/**
	 * 文件过滤器扩展名过滤器
	 * 
	 * 
	 */
	 class FileExtensionFilter implements FilenameFilter {

		private String[] mExtension;

		public FileExtensionFilter(String[] extension) {
			mExtension = extension;

		}

		public FileExtensionFilter(String extension) {
			mExtension = new String[] { extension };
		}

		@Override
		public boolean accept(File file, String filename) {
			boolean fileOK = false;

			if (mExtension != null) {
				for (int i = 0; i < mExtension.length; i++) {
					//fileOK |= (filename.endsWith('.' + extension[i]));
					fileOK |= (filename.indexOf(mExtension[i]) != -1);
				}
				if (fileOK) {
					return true;
				}
			}
			return fileOK;
		}

	}


}
