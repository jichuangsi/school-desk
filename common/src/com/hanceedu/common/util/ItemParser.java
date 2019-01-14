package com.hanceedu.common.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.hanceedu.common.Item;


public final class ItemParser {

	
	public static List<Item> parse(byte[] buffer, String codec) {	
		return parse(new ByteArrayInputStream(buffer), codec) ;
	}

	/**
	 * 解析获取节点映射集合
	 * @return 节点映射集合
	 */
	public static List<Item> parse(InputStream is,  String codec) {
		XmlPullParser parser = null ;
		try {
			// 通过工厂模式获取xml pull解析实例
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		Stack<Integer> stack = new Stack<Integer>();
		List<Item> itemList = new ArrayList<Item>();
		try {
			// 设置输入流和编码
			//parser.setInput(_context.getAssets().open("0.xml"), codec);
			parser.setInput(is, codec) ;

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

					String name = "", data = "", icon = "", type = "" ;
					// 获取属性
					int count = parser.getAttributeCount() ;
					if ( count > 0) {
						for (int i = 0 ; i < count ; i++) {
							String attr = parser.getAttributeName(i) ;
							String value = parser.getAttributeValue(i).trim() ;
							if (attr.equals("name" )|| attr.equals("term")) {
								name = value ;
							}						
							else if (attr.equals("data") || attr.equals("file")) {
								data = value ;
							}						
							else if (attr.equals("icon")) {
								icon = value ;
							}
							else if (attr.equals("type")) {
								type = value ;
							}
						}
						

					}
					
					int pid = stack.peek() ;
					if (!name.equals("")) {
						itemList.add(new Item(id, pid, name, data, icon, type));
						//System.out.println("add:" + name + " data=" + data);
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

		return itemList;
	}

	
}
