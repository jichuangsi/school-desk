package com.hanceedu.common.data;

import java.io.UnsupportedEncodingException;


public class SearchData {

	private static boolean mIsInited ;
	private static boolean mIsOpened ;
	
	public static void init() {
        
		mIsInited = false ;
		mIsOpened = false ;		

		String lib = "hcic" ; 
        try  
        {
        	
            //System.loadLibrary("hcic_S700B_Android"); 
        	System.loadLibrary(lib);  
     
            if (jniNew() != 1) {
                System.out.println("SearchToLearnData init fail!");  
                return ;
            }
       
        } catch (UnsatisfiedLinkError ule)   
        {  
            System.out.println("WARNING: Could not load library SearchData ! " + lib);  
            return ;
        }    
        mIsInited = true ;
	}
	
	public static boolean open(String path) {
		
		if (!mIsInited) {
			System.out.println(" search data not mIsInited");  
			return false ;
		}
		
		mIsOpened = false ;
		
		byte[] bs;
		try {
			bs = path.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return false ;
		}
		if (jniOpen(bs) != 1) {
            System.out.println(" open data file " + path + " fail!");     		
			return false ;
			
		}

		mIsOpened = true ;
		return true ;
	}

	
	public static byte[] getQuestion(byte[] sId) {
		
		if (!mIsOpened) {
			return null ;
		}
		
		return  jniGetQuestion( sId) ;

	}
	
	public static byte[] getAnswer(byte[] sId) {
		
		if (!mIsOpened) {
			return null ;
		}
		
		return  jniGetAnswer( sId) ;

	}
	
	
	public static byte[] getPicture(int id) {
		
		if (!mIsOpened) {
			return null ;
		}
		
		return  jniGetPicture(id) ;

	}
	
	public static int getSize(int id) {
		
		if (!mIsOpened) {
			return 0 ;
		}
		
		return  jniGetSize(id) ;

	}
	
	

	
	public static int getOffset(int id) {
		
		if (!mIsOpened) {
			return 0 ;
		}
		
		return  jniGetOffset(id) ;

	}
	
	
	public static void close() {
		jniClose();
	}
	
    public native static int jniNew();
    public native static int jniOpen(byte[] path);  
    public native static byte[] jniGetQuestion(byte[] sId);  
    public native static byte[] jniGetAnswer(byte[] sId);   
    public native static byte[] jniGetPicture(int id);       
    public native static void jniClose();
    public native static int jniGetSize(int id);
    public native static int jniGetOffset(int id);  
    
}
