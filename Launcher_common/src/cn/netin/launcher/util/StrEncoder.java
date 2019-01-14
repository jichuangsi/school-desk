package cn.netin.launcher.util;

import java.nio.charset.Charset;

public class StrEncoder {  
	  
    private static final String key0 = "FECOI()*&<MNCXZPKL";  
    private static final Charset charset = Charset.forName("UTF-8");  
    private static byte[] keyBytes = key0.getBytes(charset);  
      
    public static byte[] encode(String enc){  
        byte[] bs = enc.getBytes(charset);  
        for(int i=0,size=bs.length;i<size;i++){  
            for(byte keyBytes0:keyBytes){  
                bs[i] = (byte) (bs[i]^keyBytes0);  
            }  
        }  
        return bs ;
    }  
      
    public static String decode(byte[] e){  
        byte[] dee = e;  
        for(int i=0,size=e.length;i<size;i++){  
            for(byte keyBytes0:keyBytes){  
                e[i] = (byte) (dee[i]^keyBytes0);  
            }  
        }  
        return new String(e);  
    }  
    
    public static String bytes2HexStr(byte[] bs) {
        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
    
        int bit;
        sb.append("byte[] bs = {") ;
        for (int i = 0; i < bs.length; i++) {
        	sb.append("(byte)0x") ;
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            if (i < bs.length -1) {
            	sb.append(", ");
            }
        }
        sb.append(" } ;") ;
        
        return sb.toString();
    }
    
	public static String toHexString(byte[] b)
    {
	 	if (b == null || b.length == 0) {
	 		return "*null or empty*" ;
	 	}
        StringBuffer sb = new StringBuffer() ;
        for (int i = 0; i < b.length; i++)
        {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            sb.append(hex.toUpperCase() + " ") ;
        }
        return sb.toString() ;
    }
  
}  