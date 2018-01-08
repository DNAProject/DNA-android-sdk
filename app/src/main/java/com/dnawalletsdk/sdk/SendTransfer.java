package com.dnawalletsdk.sdk;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import com.dnawalletsdk.Http.MyHandler;
import com.dnawalletsdk.main.setting.HandlerFlag;
import com.dnawalletsdk.main.MainActivity;

import android.os.Message;

import com.dnawalletsdk.main.MainActivity.MainHandler;

public class SendTransfer {

	private static MyHandler mAPP = null;
	private static MainHandler mHandler = null;
	
	public static void SignTxAndSend (String txData,byte[] publicKeyEncoded,byte[] privateKey) {
		
		byte[] sign = Account.signatureData(txData,privateKey);
		String txRawData = Transaction.AddContract(txData , sign , publicKeyEncoded);

		SendTransactionData(txRawData);

	}

	public static void SendTransactionData(final String txRawData) {
		new Thread(new Runnable() {  
			@Override  
			public void run() {  
			    HttpURLConnection connection = null;
			    try {
		        	mAPP = (MyHandler) MainActivity.Main.getApplication();
		    		mHandler = mAPP.getHandler();
		            URL url = new URL("https://srv1.iptchain.net:10443/api/v1/transaction");
		            connection = (HttpURLConnection) url.openConnection();
		            connection.setRequestMethod("POST");
		            connection.setRequestProperty("Content-type", "application/json");
		            connection.setReadTimeout(5000);
		            connection.setConnectTimeout(5000);  
		            connection.setDoOutput(true);
		            connection.setDoInput(true); 
		            
		            JSONObject jsonObject = new JSONObject();
		            jsonObject.put("Action", "sendrawtransaction");
		            jsonObject.put("Version", "1.0.0");
		            jsonObject.put("Type", ""); 
		            jsonObject.put("Data", txRawData);

		            OutputStream os = connection.getOutputStream();  
		            os.write(jsonObject.toString().getBytes());  
		            os.flush();  
		            
		            if (connection.getResponseCode() == 200) {  
		                InputStream is = connection.getInputStream();  
		                ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		                int len = 0;  
		                byte buffer[] = new byte[1024];  
		                while ((len = is.read(buffer)) != -1) {  
		                    baos.write(buffer, 0, len);  
		                }  
		                is.close();  
		                baos.close();  
		                  
		                final String result = new String(baos.toByteArray());  
		                //System.out.println("result:"+result);
		                
		                analyzeReturnMsg(result);
		  
		            } else {  

		            }  
		            
		        } catch (IOException e) {
			        e.printStackTrace();
			    } catch (JSONException e) {
					e.printStackTrace();
				}
			}  
		}).start();  
	}

	protected static void analyzeReturnMsg(String result) throws JSONException {
		JSONObject object = new JSONObject(result);
		int  ErrorResult = object.getInt("Error");

		if(ErrorResult == 0) {
             Message msg = Message.obtain();
             msg.what = HandlerFlag.SEND_TRANSACTION_SUCCESS ;
             mHandler.sendMessage(msg);
		}else {
            Message msg = Message.obtain();
            msg.obj = ErrorResult;
            msg.what = HandlerFlag.SEND_TRANSACTION_FALSE ;
            mHandler.sendMessage(msg);
		}
	}
}
