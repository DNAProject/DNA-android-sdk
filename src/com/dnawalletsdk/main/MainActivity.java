package com.dnawalletsdk.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.dnawalletsdk.Cryptography.Digest;
import com.dnawalletsdk.Data.DataUtil;
import com.dnawalletsdk.Http.MyHandler;
import com.dnawalletsdk.info.AssetInfo;
import com.dnawalletsdk.main.setting.PermissionSetting;
import com.dnawalletsdk.sdk.Account;
import com.dnawalletsdk.sdk.AccountAsset;
import com.dnawalletsdk.sdk.GenerateWallet;
import com.dnawalletsdk.sdk.NodeMsg;
import com.example.dnawalletsdk.R;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.SyncStateContract.Constants;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity  {

	private Button openWallet;
	private Button generateWallet;
	private EditText passwordEditText;
	private Button chooseWallet;

	
	private String Password;
	
	SQLiteDatabase wallet ;
	private String walletAddressUrl ;
	
	public static Account account;
	public static AssetInfo assets[];
	
	public static final int GET_ASSET_SUCCESS = 1;
	public static final int SEND_TRANSACTION_SUCCESS = 40000;
	public static final int SEND_TRANSACTION_FALSE = 50000;
	

	
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
		
		openWallet = (Button) findViewById(R.id.openWallet);
		generateWallet = (Button) findViewById(R.id.generateWallet);
		chooseWallet = (Button) findViewById(R.id.chooseWallet);
        passwordEditText = (EditText) findViewById(R.id.password);
       
         
		openWallet.setOnClickListener(new openWallet_OnClickListener());
		generateWallet.setOnClickListener(new generateWallet_OnClickListener());
		chooseWallet.setOnClickListener(new chooseWallet_OnClickListener());
		
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

        	//wallet =SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Wallet/wallet--9f7fe470-67bb-491a-87d0-692ac6c8bc76.db3", null);
        	if (walletAddressUrl == null) {
                Toast.makeText(MainActivity.this, "请选择钱包文件", Toast.LENGTH_SHORT).show();  
                return;
        	} else if (!walletAddressUrl.substring(walletAddressUrl.length()-4).equals(".db3")) {
                Toast.makeText(MainActivity.this, "请选择正确的钱包文件", Toast.LENGTH_SHORT).show();  
                return;
        	}
        	
        	System.out.println("address："+walletAddressUrl);
        	
            if(walletAddressUrl.substring(0, 18).equals("/document/primary:")) {
            	walletAddressUrl = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/"+walletAddressUrl.substring(18);
            }
        	System.out.println(walletAddressUrl);
        
        	
        	wallet = SQLiteDatabase.openOrCreateDatabase(walletAddressUrl, null);
        	
        	Password = passwordEditText.getText().toString();
        	if (Password.isEmpty()) {
                Toast.makeText(MainActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();  
                return;
        	}

        	String privateKeyHexString = GenerateWallet.decryptWallet(wallet,Password);
        	
        	if(privateKeyHexString.equals("-1")) {
                Toast.makeText(MainActivity.this, "密码错误，请重新输入", Toast.LENGTH_SHORT).show();  
                return;
        	}else if (privateKeyHexString.equals("-2")) {
                Toast.makeText(MainActivity.this, "打开钱包失败，原因：私钥解密失败", Toast.LENGTH_SHORT).show();  
                return;
        	}

        	String nodeAPI ="https://srv1.iptchain.net:10443";
        	//获取账户
        	
        	account =GenerateWallet.createAccount(DataUtil.HexStringToByteArray(privateKeyHexString));
        	
        	//获取节点高度
        	NodeMsg.getNodeHeight(nodeAPI);
        	//获取余额
        	AccountAsset.getUpspent(nodeAPI,account);
        	
        	System.out.println("privateKey:"+DataUtil.bytesToHexString(account.privateKey));
        	
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
            intent.setType("*/*");//设置类型，这里是任意类型，任意后缀的可以这样写。  
            intent.addCategory(Intent.CATEGORY_OPENABLE);  
            startActivityForResult(intent,1);  
        }  
    }
    //获得选择的.db3
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        if (resultCode == Activity.RESULT_OK) {  
            if (requestCode == 1) {  
                Uri uri = data.getData();  
                Toast.makeText(this, "您选择的钱包为："+uri.getPath().toString(), Toast.LENGTH_SHORT).show();  
                walletAddressUrl = uri.getPath().toString();
                chooseWallet.setText("已选择钱包");
            }  
        }  
    }  

	public final class MainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
            switch (msg.what) {   
            case GET_ASSET_SUCCESS :   
            	assets = (AssetInfo[])msg.obj;

            	Double IPTassetBalance = null;
            	for(int i = 0 ; i <assets.length ; i++) {
            		if (assets[i].getAssetName().equals("IPT")) {
            		    IPTassetBalance = assets[i].getbalance();
            		}
            	}
                Toast.makeText(MainActivity.this, "获取资产信息成功！IPT余额为"+IPTassetBalance, Toast.LENGTH_LONG).show();
                break;  
                
            case SEND_TRANSACTION_SUCCESS  :   
            	Toast.makeText(OpenWallet.OpenWalletActivity, "转账成功" ,Toast.LENGTH_SHORT).show();
            	break;
            	
            case SEND_TRANSACTION_FALSE :   
            	int ErrorCode = (Integer) msg.obj;
            	Toast.makeText(OpenWallet.OpenWalletActivity, "转账失败，错误代码为："+ ErrorCode,Toast.LENGTH_SHORT).show();
            	break;
            }
		}
	}
	
	
	
}