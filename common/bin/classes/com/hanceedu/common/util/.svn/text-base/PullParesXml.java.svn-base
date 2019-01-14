package com.hanceedu.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Xml;

/**
 * 
 * 工具类:解析得到xml数据 功能：读取assets文件夹中的keyboard_resource.xml文件，
 * 得到相应的数据以供其他类使用,该数据对应着数字键盘上各个key的坐标以及id 实现过程：采用了pull的方法解析xml文件
 * 
 * @author wjx
<<<<<<< .mine
 * @date 2011-9-21
 * @~ com.hanceedu.common.widget.util//PullParesXml
=======
 * @date 2011-9-23
 * @~ com.hanceedu.common.util//PullParesXml 
 *    工具类:解析得到xml数据
 *    功能：读取assets文件夹中的keyboard_resource.xml文件，
 *                得到相应的数据以供其他类使用,该数据对应着数字键盘上各个key的坐标以及id 
 *    实现过程：采用了pull的方法解析xml文件
>>>>>>> .r49
 */
public class PullParesXml {
	Context context;

	public PullParesXml(Context context) {
		this.context = context;
	}

	/**
	 * 读取xml文件得到一个InputStream对象
	 * 
	 * @return InputStream
	 */
	public InputStream xmlToStream() {
		InputStream is = null;
		try {
			/* 通过getAssets()方法得到assets文件夹的路径 */
			AssetManager assetManager = context.getAssets();

			/* 通过open()方法在该路径打开指定的xml文件 */
			is = assetManager.open("keyboard_resource.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return is;
	}

	/**
	 * 
	 * 将其解析出来的每个key标签中的6个属性值存到KeyInfo对象中， 再将每个存有数据的KeyInfo对象存到List对象
	 * 
	 * @return List
	 */
	public List<KeyInfo> parse() {

		/* 调用xmlToStream方法得到一个输入流 */
		InputStream is = xmlToStream();

		/* 用来存储KeyInfo对象 */
		List<KeyInfo> infos = null;

		/* 用来存储xml中每个key中的6个属性值 */
		KeyInfo keyInfo = null;
		try {

			/* 通过newPullParser方法得到一个XmlPullParser对象 */
			XmlPullParser parser = Xml.newPullParser();

			/* 设置以UTF-8编码读取文件 */
			parser.setInput(is, "UTF-8");

			/* 得到xml标签的整型id值 */
			int event = parser.getEventType();

			/* 直到xml的结束的父标签时跳出循环 */
			while (event != XmlPullParser.END_DOCUMENT) {
				switch (event) {

				/* xml开始的父标签 */
				case XmlPullParser.START_DOCUMENT:

					infos = new ArrayList<KeyInfo>();
					break;

				/* xml父标签下的子标签 */
				case XmlPullParser.START_TAG:

					/* key子标签 */
					if ("key".equals(parser.getName())) {

						/* 每一个key标签就先得到一个KeyInfo对象 */
						keyInfo = new KeyInfo();

						/* 得到key标签下的属性总数 */
						int count = parser.getAttributeCount();
						for (int i = 0; i < count; i++) {

							/*
							 * 依次判断为哪个属性名，并将其对应的属性值存到KeyInfo对象中，
							 * getAttributeName方法为得到属性名
							 * ，getAttributeValue方法为得到属性值
							 */
							/* 每个key标签的id */
							if ("id".equals(parser.getAttributeName(i))) {
								keyInfo.setKeyId(parser.getAttributeValue(i));
							}

							/* 数字键盘上key的左上角的x坐标 */
							else if ("x".equals(parser.getAttributeName(i))) {

								/* parseFloat方法将其方法内的参数强制转化为float型 */
								keyInfo.setLeft(Float.parseFloat(parser
										.getAttributeValue(i)));
							}

							/* 数字键盘上key的左上角的y坐标 */
							else if ("y".equals(parser.getAttributeName(i))) {
								keyInfo.setTop(Float.parseFloat(parser
										.getAttributeValue(i)));
							}

							/* 数字键盘上key的右下角的x坐标 */
							else if ("w".equals(parser.getAttributeName(i))) {
								keyInfo.setRight(Float.parseFloat(parser
										.getAttributeValue(i)));
							}

							/* 数字键盘上key的右下角的y坐标 */
							else if ("h".equals(parser.getAttributeName(i))) {
								keyInfo.setBottom(Float.parseFloat(parser
										.getAttributeValue(i)));
							}

							/* 数字键盘上key对应图片id */
							else if ("sourceId".equals(parser
									.getAttributeName(i))) {
								keyInfo.setSourceId(parser.getAttributeValue(i));
							}
						}
					}
					break;
				/* 结束的key标签 */
				case XmlPullParser.END_TAG:
					if ("key".equals(parser.getName())) {

						/* 将存有key标签的属性值的KeyInfo对象存到List对象中 */
						infos.add(keyInfo);
						keyInfo = null;
					}
					break;
				}

				/* 下一个key标签 */
				event = parser.next();
			}
		} catch (XmlPullParserException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
		return infos;
	}

	/**
	 * 
	 * @param keyX
	 *            数字键盘上的x坐标
	 * @param keyY
	 *            数字键盘上的y坐标
	 * @return 如果参数坐标在对应的xml数据范围内则返回KeyInfo 否则返回null
	 */
	public KeyInfo byXY(float keyX, float keyY) {

		/* 调用parse方法得到存有xml数据的List对象 */
		List<KeyInfo> list = parse();
		KeyInfo info = null;
		for (int i = 0; i < list.size(); i++) {
			info = (KeyInfo) list.get(i);

			/*
			 * 将参数keyX，keyY分别与相对应的KeyInfo对象中的左上角的x坐标和右下角的x坐标， 左上角的y坐标和右下角的y坐标比较
			 */
			if ((info.getLeft() <= keyX & info.getRight() >= keyX)
					& (info.getTop() <= keyY & info.getBottom() >= keyY)) {
				return info;
			}
		}
		return null;
	}

}
