package com.dnawalletsdk.sdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dnawalletsdk.Http.HttpUtils;
import com.dnawalletsdk.Http.MyHandler;
import com.dnawalletsdk.info.AssetInfo;
import com.dnawalletsdk.main.MainActivity;
import com.dnawalletsdk.main.MainActivity.MainHandler;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

/**
 * 账户资产
 * 
 * @author 12146
 *
 */
public class AccountAsset {
	
	private static AssetInfo[] assetInfo;

	private static MyHandler mAPP = null;
	
	private static MainHandler mHandler = null;
	
	public static void getUpspent(final String nodeAPI ,final Account account) {		
		
		new Thread(new Runnable() {  
			@Override  
			public void run() {  
			        HttpURLConnection connection = null;  
			        try {  
			        	mAPP = (MyHandler) MainActivity.Main.getApplication();
			    		mHandler = mAPP.getHandler();
			    		
			    		URL url = new URL(nodeAPI+"/api/v1/asset/utxos/"+account.address);
			    		//System.out.println(nodeAPI+"/api/v1/asset/utxos/"+account.address);
			            connection = (HttpURLConnection) url.openConnection();  
			            connection.setRequestMethod("GET");  
			            connection.setConnectTimeout(8000);  
			            connection.setReadTimeout(8000);  
			  		            	
		        		int statusCode = connection.getResponseCode();

                        if (statusCode == 200) {  
                            //如果获取的code为200，则证明数据获取是正确的。 
                            InputStream is = connection.getInputStream();  
                            String result = HttpUtils.readMyInputStream(is);  
                            
                            JSONObject object = new JSONObject(result);
   			            	assetInfo = AnalyzeCoins(object);

	   			             Message msg = Message.obtain();
	   			             msg.obj = assetInfo;
	   			             msg.what = MainActivity.GET_ASSET_SUCCESS;
	
	   			             mHandler.sendMessage(msg);
  			            
                        }else {  
                        }  

			        } catch (Exception e) {  
			            e.printStackTrace();  
			  
			        } finally {  
			                if (connection != null) {  
			                    connection.disconnect();  
			                }  
			        }
			    }  
			}).start();  
	}

	protected static AssetInfo[] AnalyzeCoins(JSONObject response) throws JSONException {
		
		JSONArray AssetResult = response.getJSONArray("Result");
		AssetInfo []assetInfo = new AssetInfo[AssetResult.length()];
		
		for (int i = 0 ; i < AssetResult.length() ; i ++) {
			String assetResult =  AssetResult.getString(i);
			JSONObject assetResultObj =  new JSONObject(assetResult);
			
			JSONArray Utxo = assetResultObj.getJSONArray("Utxo");
			Double amount = 0.0;
			for (int j = 0 ; j < Utxo.length() ; j ++) {
				String utxo = Utxo.getString(j);
				JSONObject utxoObj = new JSONObject(utxo);
				amount = amount + utxoObj.getDouble("Value");
			}
			
			String AssetId = assetResultObj.getString("AssetId");
			String AssetName = assetResultObj.getString("AssetName");
			AssetInfo Asset = new AssetInfo(AssetId,AssetName,amount,Utxo);
			assetInfo[i] = Asset;
//			System.out.println(assetInfo[i].assetId);
//			System.out.println(assetInfo[i].assetName);
//			System.out.println(assetInfo[i].amount);
		}
		return assetInfo;

		
		
	}
		
}
	