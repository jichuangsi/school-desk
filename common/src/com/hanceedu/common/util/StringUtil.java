package com.hanceedu.common.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class StringUtil {
	
	private static final char LESSON_SIGN = '@' ;

	public static final String[] SUBJECTS = {"其它", "语文", "数学", "英语", "物理", "化学", "生物", "历史", "地理", "政治", 
		"思品", "科学",  "作文", "文科", "理科", "综合"} ;
	public static final String[] sections = {"幼儿园", "小学", "初中", "高中", "小升初", "中考", "高考",
		"高一", "高二","高三"} ;

	public static final String[] volumns = {"上册", "下册", 
		"第1册", "第2册", "第3册", "第4册", "第5册", "第6册" ,
		"第一册", "第二册", "第三册", "第四册", "第五册", "第六册" ,
		"一上", "一下", "二上", "二下", "三上", "三下", "四上", "四下", "五上", "五下", "六上", "六下",
		"选修一", "选修二", "选修三", "选修四", "选修五", "选修六" ,
		"选修1", "选修2", "选修3", "选修4", "选修5", "选修6" ,
		"文科综合", "理科综合", "文科总复习", "理科总复习",  "总复习",
	} ;

	/** 从包含路径的字符串中取文件名 */
	public static String getFileName(String path) {

		int pos0 = path.lastIndexOf("/") ;

		String s ;
		if (pos0 == -1) {
			s = path ;
		}else{
			s = path.substring(pos0 + 1) ;
		}

		return s ;
	}
	
	/** 从包含路径的字符串中取目录名，包含最后一个目录分割符  */
	public static String getDir(String path) {

		int pos0 = path.lastIndexOf("/") ;

		String s ;
		if (pos0 == -1) {
			s = "" ;
		}else{
			s = path.substring(0, pos0 + 1)  ;
		}

		return s ;
	}

	/** 从包含路径的字符串中取不含扩展名的文件名 */
	public static String getFileNameOnly(String path) {

		if (path == null) {
			return "" ;
		}
		int pos0 = path.lastIndexOf("/") ;

		String s ;
		if (pos0 == -1) {
			s = path ;
		}else{
			s = path.substring(pos0 + 1) ;
		}
		int pos1 = s.lastIndexOf(".") ;

		// 以点开头 的文件名不处理 
		if (pos1 > 0) {
			s = s.substring(0, pos1 ) ;
		}

		return s ;
	}

	/** 取扩展名 */
	public static String getExtension(String path) {
		int pos = path.lastIndexOf(".") ;
		if (pos == -1) {
			return "" ;
		}
		return path.substring(pos + 1) ;
	}

	/**
	 * 找[科目的] 的科目
	 */
	private static String getSubjectHead(String s) {
		int start = s.indexOf('[');
		int end = s.indexOf(']');
		if (start == -1 || end == -1) {
			return  getSubject(s) ; 
		}
		return s.substring(start + 1, end);
	}


	/**
	 * 找[科目的] 的科目对应subjects数组的index
	 */
	public static int getSubjectId(String s) {
		String subject ;
		int index ;
		int i ;
		for ( i = 0 ; i < 15; i++) {
			subject = SUBJECTS[i] ;
			index = s.indexOf(subject) ;
			if (index != -1) {
				return i  ;
			}
		}
		return 0 ;
	}

	/**
	 * 找[科目的]以外的科目的index
	 */
	private static int indexOfSubject(String s) {
		String subject ;
		int index ;
		int i ;
		for ( i = 0 ; i < 15; i++) {
			subject = SUBJECTS[i] ;
			index = s.indexOf(subject) ;
			if (index != -1) {
				return index ;
			}
		}
		return -1 ;
	}

	/**
	 * 找没有用[]括起来的科目
	 */
	public static String getSubject(String s) {
		String subject ;
		int index ;
		int i ;
		for ( i = 0 ; i < 15; i++) {
			subject = SUBJECTS[i] ;
			index = s.indexOf(subject) ;
			if (index != -1) {
				return subject ;
			}
		}
		return "未知" ;
	}

	/**
	 * 找小学初中高中的的index
	 */
	private static int indexOfSection(String s) {
		int index ;
		int i ;
		for ( i = 0 ; i < 4; i++) {
			index =  s.indexOf(sections[i]) ;
			if (index != -1) {
				return index ;
			}
		}
		return -1;
	}
	/**
	 * 找小学初中高中
	 */
	private static String getSection(String s) {
		String section ;
		int index ;
		int i ;
		for ( i = 0 ; i < 4; i++) {
			section = sections[i] ;
			index = s.indexOf(section) ;
			if (index != -1) {
				return section ;
			}
		}
		return "" ;
	}

	public static String getBookName(String s) {
		//看有没有课名
		int indexOfLesson = s.indexOf(LESSON_SIGN) ;
		if (indexOfLesson == -1) {
			return null ;
		}
		return s.substring(0, indexOfLesson) ;
	}
	
	public static String getLesson(String s) {
		//看有没有课名
		int indexOfLesson = s.indexOf(LESSON_SIGN) ;
		if (indexOfLesson == -1) {
			return null ;
		}
		return s.substring(indexOfLesson + 1) ;
	}

	/**
	 * [科目]出版社学段科目年级册别课名.txt
	 * 格式化函数，比如
	 * [数学]人教版高中数学8年级上册_第一课小数的知识.txt 格式化为：
	 * 人教版\n高中数学\n8年级上册\n第一课小数的知识 
	 */
	public static String formatBookName(String name) {

		StringBuffer buffer = new StringBuffer();	
		if (name == null || name.equals("")) {
			return "" ;
		}
		int indexAfterHead = name.indexOf(']') + 1 ;

		//去掉[科目]和扩展名
		String s = name.substring(indexAfterHead) ;
		int indexOfDot = name.lastIndexOf('.') ;
		if (indexOfDot != -1) {
			s = name.substring(0, indexOfDot) ;
		}
		//学段的位置
		int indexOfSection = indexOfSection(s) ;
		if (indexOfSection == -1) {
			//System.out.println("formatBookName indexOfSection == -1 s=" + s);
			return s ;
		}
		//学段之前是出版社
		if (indexOfSection > 0) {
			String publisher = s.substring(0, indexOfSection) ;
			buffer.append(publisher) ;
			buffer.append('\n') ;
		}
		if(s.length() - indexOfSection < 4) {
			return s ;
		}
		//学段和科目 = 学段开始的位置  + 4
		int posAfterSubject = indexOfSection + 4 ;
		String sectionAndSubject = s.substring(indexOfSection, posAfterSubject) ;
		buffer.append(sectionAndSubject) ;
		buffer.append('\n') ;
		//看有没有课名
		int indexOfLesson = s.indexOf(LESSON_SIGN) ;
		String bookName = "" ;
		//如果没有课名或者课名符号在在科目之前，后面就是书名了
		if (indexOfLesson == -1 || indexOfLesson < posAfterSubject) {
			bookName = s.substring(posAfterSubject) ;
			buffer.append(bookName) ;
			return buffer.toString() ;
		}
		//书名= 科目之后 ---> 课名符号
		bookName = s.substring(posAfterSubject, indexOfLesson) ;
		buffer.append(bookName) ;
		buffer.append('\n') ;
		//课名 = 课名符号--->尾巴 
		String lesson = s.substring(indexOfLesson + 1) ;
		buffer.append(lesson) ;

		return buffer.toString() ;
	}

	/**
	 * 把数据文件名按科目分组，返回各个科目及文件数量
	 * */
	public static List<String> getSubjectList(List<String> list) {
		List<String> subjectList = new ArrayList<String>() ;
		//和subjects对应的数量数组
		int[] subjectCounts = new int[15] ;
		for (int i = 0 ; i < 15; i++) {
			subjectCounts[i] = 0 ;
		}
		//计算各个科目的数量
		for (String s : list) {
			int subjectId = getSubjectId(s) ;
			if (subjectId != -1) {
				subjectCounts[subjectId]++ ;
			}				
		}
		for (int i = 0 ; i < 15; i++) {
			int count = subjectCounts[i] ;
			//只加入有的科目
			if (count > 0) {
				subjectList.add(SUBJECTS[i] + " (" + count + ")") ;
			}
		}

		return subjectList ;
	}


	/**
	 * 把数据文件名按书本分组，返回各个书本及文件数量
	 * 
	 * */
	public static List<String> getBookList(List<String> list) {
		List<String> bookList = new ArrayList<String>() ;

		HashMap<String, Integer> map = new HashMap<String, Integer>(100) ;
		int count = 0 ;
		//把书名和数量放入map
		for (String s : list) {
			String bookName = getBookName(s) ;
			if (map.containsKey(bookName)) {
				count = (Integer)map.get(bookName) + 1 ;
			}
			map.put(bookName, count) ;			
		}
		Iterator<Entry<String, Integer>> iterator =  map.entrySet().iterator() ;
		while(iterator.hasNext()) {
			Entry<String, Integer> entry = iterator.next() ;
			bookList.add(entry.getKey() + " (" + entry.getValue() + ")" ) ;
		}

		return bookList ;
	}

	/**
	 * 按科目返回相应的文件名
	 * */
	public static List<String> getListBySubject(List<String> list, String subject) {
		List<String> resultList = new ArrayList<String>() ;

		for (String s : list) {
			String tempSubject = getSubject(s) ;
			if (tempSubject.equals(subject)) {
				resultList.add(s) ;
			}				
		}		
		return resultList ;
	}

	/**
	 * 按书名返回相应的文件名
	 * */
	public static List<String> getListByBookName(List<String> list, String bookName) {
		List<String> resultList = new ArrayList<String>() ;
		for (String s : list) {
			if (s.indexOf(bookName) != -1) {
				resultList.add(s) ;
			}				
		}		
		return resultList ;
	}

}
