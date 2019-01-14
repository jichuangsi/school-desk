package com.hanceedu.common.util;

import java.io.IOException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException; 
import org.xmlpull.v1.XmlPullParserFactory;

import com.hanceedu.common.Item;

import android.content.Context;
import android.util.Log;

/**
 * 多层级xml解析工具类
 * 
 * @author ben.upsilon@gmail.com
 * 
 */
public final class ItemParserMap {

	public static final Map<Integer, List<Item>> itemMap = new LinkedHashMap<Integer, List<Item>>();

	// 保存上下文引用
	private Context _context;

	// 保证只有一个解析实例
	private static XmlPullParser parser = null;
	private static Stack<Integer> stack = new Stack<Integer>();

	/**
	 * 构造时候传递Context对象 同时获取解析实例
	 * 
	 * @param context
	 *            Context对象
	 */
	public ItemParserMap(Context context) {
		_context = context;
		try {
			// 通过工厂模式获取xml pull解析实例
			parser = XmlPullParserFactory.newInstance().newPullParser();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 解析获取节点映射集合
	 * 
	 * @return 节点映射集合
	 */
	public Map<Integer, List<Item>> getItemMap() {
		itemMap.clear();
		try {
			// 设置输入流和编码
			parser.setInput(_context.getAssets().open("0.xml"), "gbk");
			Log.d("ben.upsilon.xml.debug", "开始解析");
			int type = parser.getEventType();
			// 初始化id,父id,深度id
			int id = -1, depth = -1;
			List<Item> list = null;

			while (type != XmlPullParser.END_DOCUMENT) {
				// 标签开始
				if (type == XmlPullParser.START_TAG) {

					// 切换父id
					if (depth < parser.getDepth()) {
						stack.push(id);
					}
					depth = parser.getDepth();

					// 每个节点都赋予id叠加
					id++;
					list = getItemList(stack.peek());

					String name = "", data = "";
					// 获取属性
					if (parser.getAttributeCount() > 0) {
						name = parser.getAttributeValue(0).trim();
						if (parser.getAttributeCount() > 1)
							data = parser.getAttributeValue(1).trim();
					}
					list.add(new Item(id, stack.peek(), name, data));

				}
				// 标签结束
				if (type == XmlPullParser.END_TAG) {
					// 如果当前层级不是结束时候的层级
					if (depth > parser.getDepth()) {
						stack.pop();
					}
				}
				type = parser.next();
			}

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Log.d("ben.upsilon.xml.debug", "完成解析");
		return itemMap;
	}

	/**
	 * 获取key等于父id的item列表
	 * 
	 * @param pid
	 *            父id
	 * @return 返回对应父id的item列表
	 */
	private List<Item> getItemList(int pid) {

		if (itemMap.containsKey(pid)) {
			// 如果有对应的父id节点列表
			return itemMap.get(pid);
		} else {
			// 没有的就new一个.并且加入映射集合
			List<Item> list = new LinkedList<Item>();
			itemMap.put(pid, list);
			return list;
		}
	}
}
