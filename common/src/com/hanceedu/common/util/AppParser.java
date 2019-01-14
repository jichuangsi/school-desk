package com.hanceedu.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

import com.hanceedu.common.App;
import com.hanceedu.common.App.AppColumns;



public final class AppParser {
	
	private static final String TAG = "EL AppParser" ;

	
	public static List<App> parse(byte[] buffer, String codec) {	
		return parse(new ByteArrayInputStream(buffer), codec) ;
	}	
	
	public static List<App> parse(String s, String codec) {	
		XmlPullParser parser = null ;
		try {
			// 通过工厂模式获取xml pull解析实例
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		// 设置输入流和编码
		//parser.setInput(_context.getAssets().open("0.xml"), codec);
		try {
			parser.setInput(new StringReader(s));
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return parse(parser) ;
	}	
	
	
	/**
	 * 解析获取节点映射集合
	 * @return 节点映射集合
	 */
	public static List<App> parse(InputStream is,  String codec) {
		XmlPullParser parser = null ;
		try {
			// 通过工厂模式获取xml pull解析实例
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		// 设置输入流和编码
		//parser.setInput(_context.getAssets().open("0.xml"), codec);
		try {
			parser.setInput(is, codec) ;
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//parser.setInput(new StringReader(""));
		
		return parse(parser) ;
	}
	
	

	/**
	 * 解析获取节点映射集合
	 * @return 节点映射集合
	 */
	public static List<App> parse(XmlPullParser parser) {
		
		
		Stack<Integer> stack = new Stack<Integer>();
		List<App> appList = new ArrayList<App>();
		try {


			//Log.d("ben.upsilon.xml.debug", "开始解析");
			int eventType = parser.getEventType();
			// 初始化id,父id,深度id
			int id = -1, depth = -1;

			while (eventType != XmlPullParser.END_DOCUMENT) {
				// 标签开始
				if (eventType == XmlPullParser.START_TAG) {

					// 切换父id
					if (depth < parser.getDepth()) {
						stack.push(id);
					}
					depth = parser.getDepth();

					// 每个节点都赋予id叠加
					id++;

					String name = "", data = "", icon = "", pkg = "", cls = "", contentType = "";
					int screen = 0;
					int category = 0 ;
					int groupId = 0 ;
					int parentId = 0 ;
					// 获取属性
					int count = parser.getAttributeCount() ;
					if ( count > 0) {
						for (int i = 0 ; i < count ; i++) {
							String attr = parser.getAttributeName(i) ;
							String value = parser.getAttributeValue(i).trim() ;
							if (attr.equals(AppColumns.NAME)) {
								name = value ;
							}
							else if (attr.equals(AppColumns.SCREEN)) {
								screen = Integer.parseInt(value) ;
							}
							else if (attr.equals(AppColumns.CATEGORY) || attr.equals("type") ) {
								category = Integer.parseInt(value) ;
							}
							else if (attr.equals(AppColumns.GROUPID)) {
								groupId = Integer.parseInt(value) ;
							}							
							else if (attr.equals(AppColumns.PARENTID)) {
								parentId = Integer.parseInt(value) ;
							}
							else if (attr.equals(AppColumns.DATA) || attr.equals("file") ) {
								data = value ;
							}						
							else if (attr.equals(AppColumns.ICON)) {
								icon = value ;
							}
							else if (attr.equals(AppColumns.PKG)) {
								pkg = value ;
							}
							else if (attr.equals(AppColumns.CLS)) {
								cls = value ;
							}
							else if (attr.equals(AppColumns.CONTENT_TYPE)) {
								contentType = value ;
							}
						}
						

					}
					
					int pid = stack.peek() ;
					if (!name.equals("")) {
						App app = new App() ;
						
						if (parentId != 0) {
							app.setParentId(parentId);
						}else{
							//Log.d(TAG, "pid=" + pid) ;
							app.setParentId(pid);
						}
						app.setId(id);
						app.setName(name);
						app.setIcon(icon);
						app.setGroupId(groupId);

						app.setPkg(pkg);
						app.setCls(cls);
						app.setData(data);
						app.setContentType(contentType);
						app.setScreen(screen);
						app.setCategory(category);
						//System.out.println("add:" + name + " pkg=" + pkg);
						appList.add(app) ;
					}

				}
				// 标签结束
				if (eventType == XmlPullParser.END_TAG) {
					// 如果当前层级不是结束时候的层级
					if (depth > parser.getDepth()) {
						stack.pop();
					}
				}
				eventType = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return appList;
	}

	
}
