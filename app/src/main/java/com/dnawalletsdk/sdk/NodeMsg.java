package com.dnawalletsdk.sdk;

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
	
	
	public static String getNodeHeight(final String nodeAPI) {		
		new Thread(new Runnable() {  
			@Override  
			public void run() {  
			        HttpURLConnection connection = null;  
			        try {  
			    		
			    		URL url = new URL(nodeAPI+"/api/v1/block/height?auth_type=getblockheight");
			            connection = (HttpURLConnection) url.openConnection();  
			            connection.setRequestMethod("GET");  
			            connection.setConnectTimeout(8000);  
			            connection.setReadTimeout(8000);  
			  
			            InputStream in = connection.getInputStream();  
			            // 下面对获取到的输入流进行读取  
			            BufferedReader reader = new BufferedReader(new  InputStreamReader(in));  
			            StringBuilder response = new StringBuilder();  
			            String line;  
			            while ((line = reader.readLine()) != null) {  
			                response.append(line);  
			            }  
			            	String msg = response.toString();  
			            	//System.out.println("nodeMsg:"+msg);
			            	
			        } catch (Exception e) {  
			            e.printStackTrace();  
			  
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
