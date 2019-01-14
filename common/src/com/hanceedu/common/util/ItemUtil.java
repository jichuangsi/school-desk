package com.hanceedu.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Stack;

import android.util.Log;

import com.hanceedu.common.Item;


public class ItemUtil {
	private static final String TAG = "EL ItemUtil" ;
	private List<Item>  mItemList = null ;

	public ItemUtil( List<Item> itemList ) {
		mItemList = itemList ;
	}

	public List<Item> getItemList() {
		return mItemList ;
	}

	/**
	 * 获取parentId等于父id的item列表
	 */
	public List<Item> getItemList(int parentId) {
		//System.out.println("getList pid =" + pid);
		if (parentId == -1) {
			return mItemList ;
		}

		List<Item> list = new ArrayList<Item>();

		for (int i = 0 ; i < mItemList.size(); i++) {
			Item item = mItemList.get(i) ;
			//System.out.println("item.getParentID()=" + item.getParentID());
			if (item.getParentId() ==  parentId) {
				list.add(item) ;
			}
		}

		return list;
	}

	public Item getItem(int id) {
		if (mItemList == null) {
			return null ;
		}
		for (int i = 0 ; i < mItemList.size(); i++) {
			Item item = mItemList.get(i) ;
			if (item.getId() ==  id) {
				return item ;
			}
		}

		return null;
	}

	public Item getParent(int id) {

		for (int i = 0 ; i < mItemList.size(); i++) {
			Item item = mItemList.get(i) ;
			if (item.getId() ==  id) {
				return getItem(item.getParentId())  ;
			}
		}

		return null;
	}

	public boolean hasChild(int id) {

		for (int i = 0 ; i < mItemList.size(); i++) {
			Item item = mItemList.get(i) ;
			if (item.getParentId() ==  id) {
				return true  ;
			}
		}

		return false;
	}
	
	public boolean childIsContent(int id) {

		for (int i = 0 ; i < mItemList.size(); i++) {
			Item item = mItemList.get(i) ;
			if (item.getParentId() ==  id) {
				if (item.getData() != null && item.getData().length() > 0) {
					return true ;
				}else{
					return false  ;
				}
				
			}
		}

		return false;
	}



	public String getTitle(int id) {
		String headerTitle = "" ;

		Stack<String> st = new Stack<String>() ;

		int parentId = id;
		Item item ;
		while (true) {
			//System.out.println(parentId);
			if (parentId != -1) {
				item = getItem(parentId) ;
				if (item != null) {
					st.push(item.getName()) ;
				}else{
					break ;
				}
			}else{
				break ;
			}
			parentId = item.getParentId() ;
		}
/*
		for(int i = 0;i < st.size();i++){
			//System.out.println("==" +mTitleStack.get(i));
			headerTitle += st.get(i) ;
			if (i != st.size() - 1) {
				headerTitle += " > " ;
			}
		}
		*/
		while(st.size() > 0 ) {
			headerTitle += st.pop() ;
			if (st.size() > 0) {
				headerTitle += " > " ;
			}
		}
		
		return headerTitle ;
	}

	private boolean containsSubject(int[] subjects, int subject) {
		if (subjects == null) {
			return false ;
		}
		for (int i = 0 ; i < subjects.length; i++) {
			if (subject == subjects[i]) {
				return true ;
			}
		}
		return false ;
	}

	private boolean containsSubject(String[] subjects, String subject) {
		if (subjects == null) {
			//Log.e(TAG, "subjects == null")  ;
			return false ;
		}
		for (int i = 0 ; i < subjects.length; i++) {
			//Log.d(TAG, "containsSubject subject=" + subject + " subjects[i]=" + subjects[i])  ;
			if (subject.equals(subjects[i]) ) {
				return true ;
			}
		}
		return false ;
	}

	/**
	 * 把数据文件名按科目分组，返回各个科目及文件数量
	 * */
	public List<Item> getSubjectList(boolean hideZero, String [] subjects) {
		final int COUNT = 12 ;
		List<Item> subjectList = new ArrayList<Item>() ;
		//和subjects对应的数量数组
		int[] subjectCounts = new int[COUNT] ;
		for (int i = 0 ; i < COUNT; i++) {
			subjectCounts[i] = 0 ;
		}
		//计算各个科目的数量
		for (Item item : mItemList) {
			String s = item.getName() ;
			int subjectId = StringUtil.getSubjectId(s) ;
			//Log.d(TAG, "getSubjectList subjectId=" + subjectId) ;
			subjectCounts[subjectId]++ ;			
		}
		for (int i = 1 ; i < COUNT ; i++) {
			int count = subjectCounts[i] ;
			//只加入有的科目
			if (!hideZero || count > 0) {
				String subject = StringUtil.SUBJECTS[i] ;
				//Log.d(TAG, "getSubjectList subject=" + subject) ;
				if (subjects == null || containsSubject(subjects, subject)) {
					Item item = new Item() ;
					//item.setName(StringUtil.SUBJECTS[i] + "\n(" + count + ")") ;
					item.setName(subject + "(" + count + ")") ;
					item.setId(-1) ; // -1 代表科目
					item.setData(subject) ;
					//借parentId 来存subjectId
					item.setParentId(i);
					subjectList.add(item) ;
					//Log.d(TAG, "getSubjectList add=" + item.getName()) ;
				}
			}
		}
		//最后才加其他
		int count = subjectCounts[0] ;
		if (!hideZero || count > 0) {
			if (subjects == null || containsSubject(subjects, "其它")) {
				Item item = new Item() ;
				//item.setName(StringUtil.SUBJECTS[i] + "\n(" + count + ")") ;
				item.setName(StringUtil.SUBJECTS[0] + "(" + count + ")") ;
				item.setId(-1) ; // -1 代表科目
				item.setData(StringUtil.SUBJECTS[0]) ;
				//借parentId 来存subjectId
				item.setParentId(0);
				subjectList.add(item) ;
			}
		}


		return subjectList ;
	}


	/**
	 * 把数据文件名按书本分组，返回各个书本及文件数量
	 * */
	public  List<Item> getBookList() {
		List<Item> bookList = new ArrayList<Item>() ;

		HashMap<String, Integer> map = new HashMap<String, Integer>(100) ;
		int count = 1 ;
		//把书名和数量放入map
		for (Item item : mItemList) {
			String s = item.getName() ;
			String bookName = StringUtil.getBookName(s) ;
			if (bookName == null) {
				continue ;
			}
			if (map.containsKey(bookName)) {
				count = (Integer)map.get(bookName) + 1 ;
			}
			map.put(bookName, count) ;			
		}
		Iterator<Entry<String, Integer>> iterator =  map.entrySet().iterator() ;
		while(iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next() ;
			Item item = new Item() ;
			item.setName(entry.getKey() + "\n(" + entry.getValue() + ")") ;
			item.setId(-2) ; // -2 代表书名
			item.setData(entry.getKey()) ;
			bookList.add(item) ;
		}

		return bookList ;
	}

	private boolean isOtherSubject(String subject) {
		if (subject.endsWith("其他")) {
			return true ;
		}
		int count = StringUtil.SUBJECTS.length ;
		for (int i = 0 ; i < count; i++) {
			if (subject.equals(StringUtil.SUBJECTS[i])) {
				return false ;
			}
		}

		return true ;

	}

	/**
	 * 返回某科目下的ITEM
	 * */
	public List<Item> getListBySubject(String subject) {
		List<Item> resultList = new ArrayList<Item>() ;
		for (Item item : mItemList) {
			String s = item.getName() ;
			String tempSubject = StringUtil.getSubject(s) ;
			if (subject.equals("其它")) {
				if (isOtherSubject(tempSubject)) {
					resultList.add(item) ;
				}
			}else{
				
				if (tempSubject.equals(subject)) {
					//System.out.println("getListBySubject name=" + s + " tempSubject=" + tempSubject);
					resultList.add(item) ;
				}	
			}

		}		
		return resultList ;
	}

	/**
	 * 返回某科目下的ITEM
	 * */
	public List<Item> getListByBookName(String bookName) {
		List<Item> resultList = new ArrayList<Item>() ;
		for (Item item : mItemList) {
			String s = item.getName() ;
			if (s.indexOf(bookName) != -1) {
				resultList.add(item) ;
			}				
		}		
		return resultList ;
	}

	public void release() {
		if (mItemList != null) {
			mItemList.clear() ;
			mItemList = null ;
		}
	}
}
