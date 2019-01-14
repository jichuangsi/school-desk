package com.hanceedu.common.segment;

import java.io.UnsupportedEncodingException;
import android.util.Log;

public class Segment {

	private static boolean mIsInited ;
	private static boolean mIsOpened ;
	
	public static void init(String path) {
		

		mIsInited = false ;
		
        try   
        {  
            System.loadLibrary("hcsegment");  
       
        } catch (UnsatisfiedLinkError ule)   
        {  
            System.err.println("WARNING: Could not load library hcsegment!"); 
            return ;
        }    
      
        mIsInited = true ;
        
        byte[] bs = null;
		try {
			bs = path.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        segment_init(bs) ;
	  
	}
	

	public static String process(String s) {
	
			
		if (mIsInited) {
			
			byte[] bs;
			try {
				bs = s.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
	    		Log.e("JNI_DEBUG", "WARNING: UnsupportedEncodingException!");
				return null ;
			}

			byte[] bs_ws = segment_do(bs) ;
			
    		if (bs_ws == null) {
    			return null ;
    		}
			try {
				String s1 = new String(bs_ws, "UTF-8") ;
				return s1 ;
			} catch (UnsupportedEncodingException e) {
	    		Log.e("JNI_DEBUG", "WARNING: UnsupportedEncodingException 2!");
				e.printStackTrace();
			}
		
		}
        return null ;
	}

	
	public static void free() {
		
		if (mIsInited) {
			segment_free() ;
			
		}
 
	}	

	
    public native static void segment_init(byte[] path) ;
    public native static byte[] segment_do(byte[] keyword) ;
    public native static void segment_free() ;
	
}

