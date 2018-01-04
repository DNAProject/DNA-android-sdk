package com.dnawalletsdk.Data;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class DataUtil {

	 /**
	  * 类标记
	  */
	 private static final String TAG = "DataUtil";

	 
    /** 
     * 10进制byte[]转换成16进制字符串 
     *  
     * @param src 
     * @return 
     */  
    public static String bytesToHexString(byte[] src) {  
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    } 
    
    /**
     * 16进制的字符串表示转成10进制字节数组
     *
     * @param hexString 16进制格式的字符串            
     * @return 转换后的字节数组
     **/
    public static byte[] HexStringToByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {//因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }
    

	 public static String numStoreInMemory(String num, int length) {
		    if (num.length() % 2 == 1) {
		        num = '0' + num;
		    }

		    for (int i = num.length() ; i < length ; i++) {
		        num = '0' + num;
		    }

		    byte[] data = reverseArray(HexStringToByteArray(num));

		    return bytesToHexString(data);
		}
	 
	 public static byte[] reverseArray(byte[] arr) {
		    byte[] result = new byte[arr.length];
		    for (int i = 0 ; i < arr.length ; i++) {
		        result[i] = arr[arr.length - 1 - i];
		    }

		    return result;
		}
	 
	 /**
	  * 补全数字串前的0
	  *
	  * @param num 数字串
	  * @param length 需要多长
	  * @return {string}
	  */
	 public static String prefixInteger(String num,int length) {
	        String result = "";
	        // 0 代表前面补充0       
	        // d 代表参数为正数型 
	        result = String.format("%0" + length + "d", Integer.parseInt(num) );

	        return result;
	    }


    
}
