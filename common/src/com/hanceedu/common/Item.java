package com.hanceedu.common;

import android.provider.BaseColumns;

/**
 * Item对象
 * 
 */
public class Item {
	// 当前节点id
	private int id = -1;
	// 父节点id
	private int parentId = -1 ;
	// 当前节点的名字
	private String name = "";
	// 当前节点的数据
	private String data = "";	
	private String type = "" ;
	private String icon = "" ;
	private byte[] bytes = null ;

	public final static String TABLE_ITEM = "ITEM";
	public static final class ItemColumns implements BaseColumns {
		public final static String ID = "_id";	
		public final static String PARENT = "parentId";
		public final static String type = "type" ; 		
		public final static String NAME = "name";
		public final static String ICON = "icon";	
		public final static String DATA = "data";
	}
	
	
	public Item() {
		
	}
	public Item(String name, String data) {
		this.name = name;
		this.data = data;
	}
	
	public Item(int id, int parentId, String name, String data) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.data = data;
	}
	
	public Item(int id, int parentId, String name, String data, String icon) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.data = data;
		this.icon = icon ;
	}
	
	public Item(int id, int parentId, String name, String data, String icon, String type) {
		this.id = id;
		this.parentId = parentId;
		this.name = name;
		this.data = data;
		this.icon = icon ;
		this.type = type ;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getParentId() {
		return parentId;
	}
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public byte[] getBytes() {
		return bytes;
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

    @Override
	public String toString() {
		return name ;
	}

}
