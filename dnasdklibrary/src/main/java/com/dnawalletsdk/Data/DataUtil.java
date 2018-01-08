package com.dnawalletsdk.Data;

import android.annotation.SuppressLint;
import android.text.TextUtils;

@SuppressLint("NewApi")
public class DataUtil {

	 /**
	  * class tag
	  */
	 private static final String TAG = "DataUtil";

	 
    /** 
     * The decimal byte[] is converted into a hexadecimal string
     *
     * @param src The decimal byte[]
     * @return {string} The hexadecimal string
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
     * The hexadecimal string is converted into a decimal byte[]
     *
     * @param hexString The hexadecimal string
     * @return {byte[]} The decimal byte[]
     **/
    public static byte[] HexStringToByteArray(String hexString) {
        if (TextUtils.isEmpty(hexString))
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) {
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
	  * completing the total number by "0"
	  *
	  * @param num
	  * @param length
	  * @return {string}
	  */
	 public static String prefixInteger(String num,int length) {
	        String result = "";
	        // 0 stands for completing the total number by "0"
	        // d stands for positive number
	        result = String.format("%0" + length + "d", Integer.parseInt(num) );

	        return result;
	    }
    
}
