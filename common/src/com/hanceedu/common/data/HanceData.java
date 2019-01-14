package com.hanceedu.common.data;

import java.io.UnsupportedEncodingException;

public class HanceData {
	
	private static boolean mIsInited ;
	private static boolean mIsOpened ;

	public static void init() {
		mIsInited = false ;
		mIsOpened = false ;
		
		
		String lib = "hcic" ; 
        try   
        {  
            System.loadLibrary(lib); 
     
            if (rsnew() != 1) {
                System.out.println("HCData init fail!");  
                return ;
            }
       
        } catch (UnsatisfiedLinkError ule)   
        {  
            System.out.println("ERROR: Could not load hc library: " + lib);  
            return ;
        }    
        mIsInited = true ;
	}
	
	public static boolean open(String path) {
		
		if (!mIsInited) {
			System.out.println(" hance data not mIsInited");  
			return false ;
		}
		
		mIsOpened = false ;
		
		if (path == null) {
			System.out.println(" hance data path is null");  
			return false ;
		}
		
		byte[] bs;
		try {
			bs = path.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false ;
		}
		if (rsopen(bs) != 1) {
            System.out.println(" open data file " + path + " fail!");     		
			return false ;
			
		}

		mIsOpened = true ;
		return true ;
	}

	
	public static byte[] get(int cate, int id) {
		
		if (!mIsOpened) {
			return null ;
		}
		
		return  rsdata(  cate,  id) ;

	}
	
	
	public static void close() {
		 rsclose();
	}
	
    public native static int rsnew();
    public native static int rsopen(byte[] path);  
    public native static byte[] rsdata(int  cate, int id);  
    public native static void rsclose();
}
