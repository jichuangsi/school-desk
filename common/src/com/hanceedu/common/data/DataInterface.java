package com.hanceedu.common.data;

public interface DataInterface {

	public static final int ONSTART = 0 ;
	public static final int ONPROGRESS = 1 ;
	public static final int ONEND = 2 ;	

	public class Param {
		public int msgId ;
		public int status ;
		public String key ;
		public int ret ; //执行返回结果，是否成功
		public int i0 = 0 ; 
		public int i1 = 0 ; 
		public int i2 = 0 ;
		public String s0 = null ;
		public String s1 = null ; 
		public String s2  = null ;
		
		public Param() {
		}
		
		public Param(int msgId) {
			this.msgId = msgId ;
		}
	}
	
	public interface DataListener {
		void onData(Param param) ;
	}

	public Param execute(Param param) ;

	public boolean prepare(Param param) ;
	public void setListener(DataListener dataListener) ;
	public void release() ;

}
