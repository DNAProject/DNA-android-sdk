package com.dnawalletsdk.main;

import com.dnawalletsdk.info.AssetInfo;
import com.dnawalletsdk.sdk.SendTransfer;
import com.dnawalletsdk.sdk.Transaction;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OpenWallet extends Activity  {

	private Button chooseAsset;
	private Button transferSend;
	private EditText transferAddressEditText;
	private EditText transferAssetAmountEditText;
	
	public static Activity OpenWalletActivity = null;
	
	private int assetNameChooseNum = 0;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_wallet);
		
		OpenWalletActivity = this;
		
		chooseAsset = findViewById(R.id.chooseAsset);
		transferSend = findViewById(R.id.transferSend);
		transferAddressEditText = findViewById(R.id.transferAddress);
		transferAssetAmountEditText = findViewById(R.id.transferAssetAmount);
		
		transferAddressEditText.setText("ARi5hSjiKtNN9xhchHK9maC58973bcTaX3");
		
		
		chooseAsset.setOnClickListener(new chooseAsset_OnClickListener());
		transferSend.setOnClickListener(new transferSend_OnClickListener());
	}

    
    class chooseAsset_OnClickListener implements OnClickListener  {  
        public void onClick(View v)  {  
    		AssetInfo assets[] = MainActivity.assets;
    		String[] assetName = new String[assets.length];
        	for(int i = 0 ; i <assets.length ; i++) {
        		assetName[i] = assets[i].getAssetName();
        	}
        	
        	showChooseAssetDialog(assetName);
        }
    }
    
    class transferSend_OnClickListener implements OnClickListener  {  
        public void onClick(View v)  {  

            String transferAddress = transferAddressEditText.getText().toString();
            System.out.println("Address:"+transferAddress);
            Double transferAssetAmount = Double.valueOf(transferAssetAmountEditText.getText().toString());
            String coin = chooseAsset.getText().toString();
            
    		AssetInfo assets[] = MainActivity.assets;
    		AssetInfo asset = null ;
        	for(int i = 0 ; i <assets.length ; i++) {
        		if (assets[i].getAssetName().equals(coin)) {
        		   asset = assets[i];
        		}
        	}
            
            String txData = Transaction.makeTransferTransaction(asset,MainActivity.account.publicKeyEncoded,transferAddress,transferAssetAmount);
            if(txData.equals("-1")) {
            	Toast.makeText(OpenWallet.this, "地址验证失败。", Toast.LENGTH_LONG).show();
            }else if(txData.equals("-2")) {
            	Toast.makeText(OpenWalletActivity, "没有足够的余额进行转账", Toast.LENGTH_LONG).show();
            }
            
            SendTransfer.SignTxAndSend(txData,MainActivity.account.publicKeyEncoded,MainActivity.account.privateKey);
        }
    }

	private void showChooseAssetDialog(String[] assetName) {
	    final String[] AssetName =assetName;
	    AlertDialog.Builder chooseAssetDialog = new AlertDialog.Builder(OpenWallet.this);
	    chooseAssetDialog.setTitle(R.string.transfer_asset_name);
	    chooseAssetDialog.setCancelable(false);
	    chooseAssetDialog.setSingleChoiceItems(AssetName, assetNameChooseNum, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        	assetNameChooseNum = which;
	        }
	    });
	    chooseAssetDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	        		chooseAsset.setText(AssetName[assetNameChooseNum]);
	        }
	    });
	    chooseAssetDialog.setNegativeButton(R.string.cancle, new DialogInterface.OnClickListener() { 
           @Override 
           public void onClick(DialogInterface dialog, int which) { 
           } 
       });
	    chooseAssetDialog.show();
	}
    
	 
}