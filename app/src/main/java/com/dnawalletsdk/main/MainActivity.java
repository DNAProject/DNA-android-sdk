package com.dnawalletsdk.main;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.dnawalletsdk.Data.DataUtil;
import com.dnawalletsdk.Http.MyHandler;
import com.dnawalletsdk.info.AssetInfo;
import com.dnawalletsdk.main.setting.HandlerFlag;
import com.dnawalletsdk.main.setting.PermissionSetting;
import com.dnawalletsdk.sdk.Account;
import com.dnawalletsdk.sdk.AccountAsset;
import com.dnawalletsdk.sdk.GenerateWallet;
import com.dnawalletsdk.sdk.NodeMsg;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends Activity  {

	private Button openWallet;
	private Button generateWallet;
	private EditText passwordEditText;
	private Button chooseWallet;
	private Button connectNode;

	private String Password;
	
	SQLiteDatabase wallet ;
	private String walletAddressUrl ;
	
	public static Account account;
	public static AssetInfo assets[];
	public static NodeMsg Node ;

	private MainHandler handler = null;
	private MyHandler mAPP = null;
	
	public static Activity Main = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mAPP = (MyHandler) getApplication();
		handler = new MainHandler();
		mAPP.setHandler(handler);
		
		Main = this;
		
		openWallet = findViewById(R.id.openWallet);
		generateWallet = findViewById(R.id.generateWallet);
		chooseWallet = findViewById(R.id.chooseWallet);
        passwordEditText = findViewById(R.id.password);
		connectNode = findViewById(R.id.connectNode);
         
		openWallet.setOnClickListener(new openWallet_OnClickListener());
		generateWallet.setOnClickListener(new generateWallet_OnClickListener());
		chooseWallet.setOnClickListener(new chooseWallet_OnClickListener());
		connectNode.setOnClickListener(new connectNode_OnClickListener());
		
		permissionApplication(); 
	}

    @TargetApi(23)
	private void permissionApplication() {
		if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
			return;
		}

		List<String> deniedPerms = new ArrayList<String>();
		for(int i=0;PermissionSetting.permArray!=null&&i<PermissionSetting.permArray.length;i++){
			if(PackageManager.PERMISSION_GRANTED != checkSelfPermission(PermissionSetting.permArray[i])){
				deniedPerms.add(PermissionSetting.permArray[i]);
			}
		}

		int denyPermNum = deniedPerms.size();
		if(denyPermNum != 0){
			requestPermissions(deniedPerms.toArray(new String[denyPermNum]),PermissionSetting.QUEST_CODE_ALL);
		}
	}

	class openWallet_OnClickListener implements OnClickListener  {  
        public void onClick(View v)  {  

        	wallet =SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getAbsolutePath()+"/DNAWallet/wallet.db3", null);
	       	if (walletAddressUrl == null) {
                Toast.makeText(MainActivity.this, getString(R.string.choose_file), Toast.LENGTH_SHORT).show();
                return;
        	} else if (!walletAddressUrl.substring(walletAddressUrl.length()-4).equals(".db3")) {
                Toast.makeText(MainActivity.this, getString(R.string.choose_correct_file), Toast.LENGTH_SHORT).show();
                return;
        	}

           if(walletAddressUrl.substring(0, 18).equals("/document/primary:")) {
            	walletAddressUrl = Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+walletAddressUrl.substring(18);
            }
			System.out.println("address："+walletAddressUrl);
        	
        	wallet = SQLiteDatabase.openOrCreateDatabase(walletAddressUrl, null);
        	
       		Password = passwordEditText.getText().toString();
        	if (Password.isEmpty()) {
				Toast.makeText(MainActivity.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
				return;
			}

        	String privateKeyHexString = GenerateWallet.decryptWallet(wallet,Password);
        	
        	if(privateKeyHexString.equals("-1")) {
                Toast.makeText(MainActivity.this, getString(R.string.password_wrong), Toast.LENGTH_SHORT).show();
                return;
        	}else if (privateKeyHexString.equals("-2")) {
                Toast.makeText(MainActivity.this, getString(R.string.open_file_false), Toast.LENGTH_SHORT).show();
                return;
        	}

        	if(Node == null) {
				Toast.makeText(MainActivity.this, getString(R.string.please_connect_node), Toast.LENGTH_SHORT).show();
				return;
			}
			String nodeAPI = Node.restapi_host + ":" + Node.restapi_port;

        	//Get account
        	account =GenerateWallet.createAccount(DataUtil.HexStringToByteArray(privateKeyHexString));

        	//Get balance
        	AccountAsset.getUpspent(nodeAPI,account);
        	
        	Intent intent = new Intent();  
        	intent.setClass(MainActivity.this,OpenWallet.class);
        	startActivity(intent);
        }  
    }  
    
    class generateWallet_OnClickListener implements OnClickListener  {  
        public void onClick(View v)  {  

        	Intent intent = new Intent();  
        	intent.setClass(MainActivity.this,WalletGenerate.class);
        	startActivity(intent);
        }  
    }
    
    class chooseWallet_OnClickListener implements OnClickListener  {
		public void onClick(View v)  {

			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setType("*/*");
			intent.addCategory(Intent.CATEGORY_OPENABLE);
			startActivityForResult(intent,1);
		}
	}

	class connectNode_OnClickListener implements OnClickListener  {
		public void onClick(View v)  {
			//Get node hight
			Node = chooseNode();
			NodeMsg.getNodeHeight(Node.restapi_host+":"+Node.restapi_port);
		}
	}

	private NodeMsg chooseNode(){
		NodeMsg node = new NodeMsg();
		try {
			InputStreamReader isr = new InputStreamReader(getAssets().open("wallet-conf.json"),"UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line;
			StringBuilder builder = new StringBuilder();
			while((line = br.readLine()) != null){
				builder.append(line);
			}
			br.close();
			isr.close();
			JSONObject json = new JSONObject(builder.toString());
			JSONArray array = json.getJSONArray("host_info");
			JSONObject nodeMsg = array.getJSONObject((int)(Math.random()*(10-1+1)));
			node.hostName = nodeMsg.getString("hostName");
			node.hostProvider = nodeMsg.getString("hostProvider");
			node.restapi_host = nodeMsg.getString("restapi_host");
			node.restapi_port = nodeMsg.getString("restapi_port");
			node.webapi_host = nodeMsg.getString("webapi_host");
			node.webapi_port = nodeMsg.getString("webapi_port");
			node.node_type = nodeMsg.getString("node_type");

		} catch (Exception e) {
			e.printStackTrace();
		}
		return node;
	}

    //Get the selected .db3
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == Activity.RESULT_OK) {  
            if (requestCode == 1) {  
                Uri uri = data.getData();  
                Toast.makeText(this, getString(R.string.you_select_certificate)+uri.getPath(), Toast.LENGTH_SHORT).show();
                walletAddressUrl = uri.getPath();
                chooseWallet.setText(R.string.selected_wallet);
            }  
        }  
    }  

	public final class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
            switch (msg.what) {   
            case HandlerFlag.GET_ASSET_SUCCESS :
            	assets = (AssetInfo[])msg.obj;
                Toast.makeText(MainActivity.this,getString(R.string.get_asset_msg_success), Toast.LENGTH_LONG).show();
                break;  
                
            case HandlerFlag.SEND_TRANSACTION_SUCCESS  :
            	Toast.makeText(OpenWallet.OpenWalletActivity,getString(R.string.transfer_success) ,Toast.LENGTH_SHORT).show();
            	break;
            	
            case HandlerFlag.SEND_TRANSACTION_FALSE :
            	int ErrorCode = (Integer) msg.obj;
            	Toast.makeText(OpenWallet.OpenWalletActivity, getString(R.string.transfer_false)+ ErrorCode,Toast.LENGTH_SHORT).show();
            	break;

			case HandlerFlag.GET_NODEHIGHT :
				String nodeHightMsg = (String)msg.obj;
				try {
					JSONObject nodeMsgObj = new JSONObject(nodeHightMsg);
					int error = nodeMsgObj.getInt("Error");
					if (error == 0) {
						Toast.makeText(MainActivity.this, getString(R.string.get_node_hight_success), Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(MainActivity.this, getString(R.string.get_node_hight_false), Toast.LENGTH_SHORT).show();
					}
				}catch (Exception e) {
					e.printStackTrace();
				}
				break;

			case HandlerFlag.GET_NODEHIGHT_FALSE :
				Toast.makeText(MainActivity.this, getString(R.string.get_node_hight_false), Toast.LENGTH_SHORT).show();
				break;
            }
		}
	}
}