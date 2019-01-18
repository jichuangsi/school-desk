package cn.netin.launcher;

import java.util.ArrayList;
import java.util.List;

//import cn.netin.launcher.data.Constants;
import cn.netin.launcher.util.StrEncoder;

public class PasswordCreator {

	public static void main(String[] args) {
		
		create() ;
	}
	
	private static void create() {
		
		String pass = "jcs930";//"xly424" ;
		byte[] ens = StrEncoder.encode(pass) ;
		System.out.println(StrEncoder.bytes2HexStr(ens));
		
		
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
