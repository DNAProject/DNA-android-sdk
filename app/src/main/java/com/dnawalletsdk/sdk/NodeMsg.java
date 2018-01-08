package com.dnawalletsdk.sdk;

import android.os.Message;

import com.dnawalletsdk.Http.MyHandler;
import com.dnawalletsdk.main.setting.HandlerFlag;
import com.dnawalletsdk.main.MainActivity;
import com.dnawalletsdk.main.MainActivity.MainHandler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NodeMsg {

	public String hostName;
	public String hostProvider;
	public String restapi_host;
	public String restapi_port;
	public String webapi_host;
	public String webapi_port;
	public String node_type;

	private static MyHandler mAPP = null;

	private static MainHandler mHandler = null;
	
	public static String getNodeHeight(final String nodeAPI ) {
		new Thread(new Runnable() {
			public void run() {  
			        HttpURLConnection connection = null;  
			        try {
						mAPP = (MyHandler) MainActivity.Main.getApplication();
						mHandler = mAPP.getHandler();

						URL url = new URL(nodeAPI+"/api/v1/block/height?auth_type=getblockheight");
			            connection = (HttpURLConnection) url.openConnection();  
			            connection.setRequestMethod("GET");  
			            connection.setConnectTimeout(8000);  
			            connection.setReadTimeout(8000);  
			  
			            InputStream in = connection.getInputStream();
			            BufferedReader reader = new BufferedReader(new  InputStreamReader(in));  
			            StringBuilder response = new StringBuilder();  
			            String line;  
			            while ((line = reader.readLine()) != null) {  
			                response.append(line);  
			            }  
						String nodeMsg = response.toString();

						Message msg = Message.obtain();
						msg.obj = nodeMsg;
						msg.what = HandlerFlag.GET_NODEHIGHT;
						mHandler.sendMessage(msg);
			            	
			        } catch (Exception e) {  
			            e.printStackTrace();

						int error = 1;
						Message msg = Message.obtain();
						msg.obj = error;
						msg.what = HandlerFlag.GET_NODEHIGHT_FALSE;
						mHandler.sendMessage(msg);

			        } finally {  
			                if (connection != null) {
			                    connection.disconnect();  
			                }  
			        }  
			    }  
			}).start();
		return null;
	}
}
