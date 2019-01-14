package cn.netin.elui;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;
//import android.os.storage.StorageVolume;
import android.util.Log;

public class Storage {
	private static final String TAG = "EL Removables" ;
	
	public String path = "" ;
	public boolean isRemovable = false ;
	public boolean isMounted = false ;

	public static List<Storage> getStorageList(Context context) {

		List<Storage> storages = new ArrayList<Storage>();
		StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
		Class<?> storageVolumeClazz = null;
		try {
			storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
			Method getVolumeList = storageManager.getClass().getMethod("getVolumeList");			
			Method getPath = storageVolumeClazz.getMethod("getPath");
			//Method getUserLabel = storageVolumeClazz.getMethod("getUserLabel");
			Method isRemovable = storageVolumeClazz.getMethod("isRemovable");  
			Method getState = null ;
			try {
				getState = storageVolumeClazz.getMethod("getState");  
			}catch(Exception e){

			}

			Object result = getVolumeList.invoke(storageManager);
			final int length = Array.getLength(result);
			for (int i = 0; i < length; i++) {
				Object storageVolumeElement = Array.get(result, i);
				//String userLabel = (String) getUserLabel.invoke(storageVolumeElement);
				String path = (String) getPath.invoke(storageVolumeElement);
				String state = "" ;
				if (getState != null){
					state = (String) getState.invoke(storageVolumeElement);
				}else{
					Method getVolumeState = storageManager.getClass().getMethod("getVolumeState", String.class);
					state = (String) getVolumeState.invoke(storageManager, path);
				}
				//String state = (String)getVolumeState.invoke(mStorageManager, path) ;
				boolean removableFlag = (Boolean) isRemovable.invoke(storageVolumeElement);//是否可移除
				Storage storage = new Storage() ;
				storage.path = path ;
				storage.isRemovable = removableFlag ;
				storage.isMounted = (Environment.MEDIA_MOUNTED.equals(state)) ;
				storages.add( storage ) ;
				// paths.add( path + " " + removableFlag  ) ;

			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return storages ;
	}

	

	public static String storages(Context context) {
		List<Storage> storages = getStorageList(context)	;
		StringBuffer sb  = new StringBuffer();
		for(Storage s : storages) {
			sb.append(s.path + " removable:" + s.isRemovable + " mounted:" + s.isMounted + "\n") ;
		}
		return sb.toString() ;

	}

}
