package com.hanceedu.common;

import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.BaseColumns;

public class App extends Item {
	public final static String TABLE_APP = "App";
	
	public static final class AppColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String CATEGORY = "category";
		public final static String GROUPID = "groupId";		
		public final static String PARENTID = "parentId";
		public final static String SCREEN = "screen";
		public final static String NAME = "name";
		public final static String ICON = "icon";	
		public final static String PKG = "pkg" ; 
		public final static String CLS = "cls" ;
		public final static String DATA = "data";
		public final static String CONTENT_TYPE = "contentType";
	}
	
	
	private int id ;
	private int category = 0 ;
	private int groupId = 0 ;
	private int parentId = 0 ;
	private int screen = 0;
	private String name = null ;
	private String icon = null ;
	private String pkg = null ;
	private String cls = null ;
	private String data = null ;
	private String contentType = null ;
	private int sum = 0 ;
	private Drawable drawable = null ;
	private Drawable smallDrawable = null ;
	
	public Drawable getSmallDrawable() {
		return smallDrawable;
	}
	public void setSmallDrawable(Drawable smallDrawable) {
		this.smallDrawable = smallDrawable;
	}
	public Drawable getDrawable() {
		return drawable;
	}
	public void setDrawable(Drawable drawable) {
		this.drawable = drawable;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	public int getGroupId() {
		return groupId;
	}
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public int getScreen() {
		return screen;
	}
	public void setScreen(int screen) {
		this.screen = screen;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getPkg() {
		return pkg;
	}
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}
	public String getCls() {
		return cls;
	}
	public void setCls(String cls) {
		this.cls = cls;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public int getSum() {
		return sum;
	}
	public void setSum(int sum) {
		this.sum = sum;
	}
	
	
}
