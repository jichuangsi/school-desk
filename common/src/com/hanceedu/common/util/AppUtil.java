package com.hanceedu.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.hanceedu.common.App;

import android.util.Log;


public class AppUtil {

	private List<App>  mAppList = null ;
	private static final String TAG = "EL AppUtil" ;

	public AppUtil() {
		
	}
	
	public void setAppList(List<App> appList){
		mAppList = appList ;
	}

	public List<App> getAppList() {
		return mAppList ;
	}

	/**
	 * 获取parentId等于父id的app列表
	 */
	public List<App> getAppList(int parentId) {
		//System.out.println("getList pid =" + pid);
		if (parentId == -1) {
			return mAppList ;
		}

		List<App> list = new ArrayList<App>();

		for (int i = 0 ; i < mAppList.size(); i++) {
			App app = mAppList.get(i) ;
			//System.out.println("app.getParentID()=" + app.getParentID());
			if (app.getParentId() ==  parentId) {
				list.add(app) ;
			}
		}

		return list;
	}

	public App getApp(int id) {
		if (mAppList == null) {
			return null ;
		}
		for (int i = 0 ; i < mAppList.size(); i++) {
			App app = mAppList.get(i) ;
			if (app.getId() ==  id) {
				return app ;
			}
		}

		return null;
	}

	public App getParent(int id) {

		for (int i = 0 ; i < mAppList.size(); i++) {
			App app = mAppList.get(i) ;
			if (app.getId() ==  id) {
				return getApp(app.getParentId())  ;
			}
		}

		return null;
	}

	public boolean hasChild(int id) {

		for (int i = 0 ; i < mAppList.size(); i++) {
			App app = mAppList.get(i) ;
			if (app.getParentId() ==  id) {
				return true  ;
			}
		}

		return false;
	}

	public String getTitle(int id) {
		String headerTitle = "" ;

		Stack<String> st = new Stack<String>() ;

		int parentId = id;
		App app ;
		while (true) {
			//System.out.println(parentId);
			if (parentId != -1) {
				app = getApp(parentId) ;
				if (app != null) {
					st.push(app.getName()) ;
				}else{
					break ;
				}
			}else{
				break ;
			}
			parentId = app.getParentId() ;
		}

		for(int i = 0;i < st.size();i++){
			//System.out.println("==" +mTitleStack.get(i));
			headerTitle += st.get(i) + " : " ;
		}
		return headerTitle ;
	}
	

	
	public void release() {
		if (mAppList != null) {
			mAppList.clear() ;
			mAppList = null ;
		}
	}
}
