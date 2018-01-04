package com.dnawalletsdk.main;

import java.io.File;

import com.dnawalletsdk.Data.DataUtil;
import com.dnawalletsdk.sdk.Account;
import com.dnawalletsdk.sdk.GenerateWallet;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WalletGenerate extends Activity  {

	private Button generateWalleFromRandom;
	private EditText createPasswordEditText;
	
	private String CreatePassword;
	private String privateKey;
	
	SQLiteDatabase wallet ;
	
	Account account;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.generate_wallet);
		
		generateWalleFromRandom = findViewById(R.id.generateWalletFromRandom);
        createPasswordEditText = findViewById(R.id.createPassword);
		
        generateWalleFromRandom.setOnClickListener(new generateWalleFromRandom_OnClickListener());
	}

    
    class generateWalleFromRandom_OnClickListener implements OnClickListener  {  
        public void onClick(View v)  {  

        	CreatePassword =  createPasswordEditText.getText().toString();
        	if (CreatePassword.isEmpty()) {
                Toast.makeText(WalletGenerate.this, "请输入密码", Toast.LENGTH_SHORT).show();  
                return;
        	}
        	account = GenerateWallet.createAccount();
        	
            String walletUrl = GenerateWallet.createWalletDb3(account,CreatePassword);
            
            Toast.makeText(WalletGenerate.this, "生成钱包成功，钱包路径为："+walletUrl,Toast.LENGTH_LONG).show();
          
        }


    }
    

	 
}