package com.dnawalletsdk.main;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.dnawalletsdk.sdk.GenerateWallet;
import com.dnawalletsdk.sdk.Account;

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
                Toast.makeText(WalletGenerate.this, getString(R.string.enter_password), Toast.LENGTH_SHORT).show();
                return;
        	}
        	account = GenerateWallet.createAccount();

        	String url = Environment.getExternalStorageDirectory().getAbsolutePath();
            String walletUrl = GenerateWallet.createWalletDb3(account,CreatePassword,url);
            
            Toast.makeText(WalletGenerate.this, getString(R.string.generate_wallet_success)+walletUrl,Toast.LENGTH_LONG).show();
          
        }
    }
	 
}