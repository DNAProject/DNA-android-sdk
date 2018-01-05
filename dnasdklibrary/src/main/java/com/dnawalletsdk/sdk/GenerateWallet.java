package com.dnawalletsdk.sdk;

import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import com.dnawalletsdk.Cryptography.AES;
import com.dnawalletsdk.Cryptography.Digest;
import com.dnawalletsdk.Cryptography.ECC;
import com.dnawalletsdk.Data.DataUtil;
import com.dnawalletsdk.sdk.Account;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Base64;

public class GenerateWallet {	
	
    /**
     * 账户/合约
     */
    public static Account createAccount() {
        byte[] privateKey = ECC.generateKey();
        Account account = createAccount(privateKey);
        Arrays.fill(privateKey, (byte) 0);
        return account;
    }

    public static Account createAccount(byte[] privateKey) {
        Account account = new Account(privateKey);
        return account;
    }
    
	public static String decryptWallet(SQLiteDatabase wallet, String Password) {
		try {
			byte[] PasswordHash1 = Digest.sha256(Password.getBytes());
			byte[] PasswordHash2 = Digest.sha256(PasswordHash1);
			byte[] PasswordHash3 = Digest.sha256(PasswordHash2);
			byte[] PassWord_db;
		    byte[] MasterKey = null;
		    byte[] IV = null;
		    Cursor cursor = wallet.rawQuery("select * from Key where Name = ?", new String[]{"PasswordHash"});
		    if(cursor.moveToFirst()){
		        PassWord_db= cursor.getBlob(cursor.getColumnIndex("Value"));
			    if(!DataUtil.bytesToHexString(PasswordHash3).equals(DataUtil.bytesToHexString(PassWord_db))){
			    	return "-1";
			    }		        
		    }
		    cursor.close();
	
		    cursor = wallet.rawQuery("select * from Key where Name = ?", new String[]{"MasterKey"});
		    if(cursor.moveToFirst()){
		        MasterKey= cursor.getBlob(cursor.getColumnIndex("Value"));    
		    }
		    cursor.close();
	
		    cursor = wallet.rawQuery("select * from Key where Name = ?", new String[]{"IV"});
		    if(cursor.moveToFirst()){
		        IV= cursor.getBlob(cursor.getColumnIndex("Value"));    
		    }
		    cursor.close();
	
		    byte[] key = PasswordHash2;
		    byte[] plainMasterKey = null;
	
			plainMasterKey = AES.decrypt(MasterKey, key, IV);
	
			byte[] privateKeyEncrypted = null;
		    cursor = wallet.rawQuery("select * from Account", null);
		    if(cursor.moveToFirst()){
		    	privateKeyEncrypted= cursor.getBlob(cursor.getColumnIndex("PrivateKeyEncrypted"));    
		    }
		    cursor.close();
	
		    byte[] plainPrivateKey = null;
			plainPrivateKey = AES.decrypt(privateKeyEncrypted, plainMasterKey,  IV);
	
		    String privateKeyHexString = DataUtil.bytesToHexString(plainPrivateKey).substring(128, 192);   
			return privateKeyHexString;
		
		}catch (Exception e) {
			 System.out.println(e.toString());
			e.printStackTrace();
			return "-2";
		}
	}

	
	public static String createWalletDb3(Account account , String Password) {
        
		byte[] publicKeyEncode = account.publicKeyEncoded;
		byte[] passwordKey = Digest.hash256(Password.getBytes());
		
		byte[] passwordHash = Digest.sha256(passwordKey);
		byte[] iv = AES.generateIV();
		byte[] masterKey = AES.generateKey();
		byte[] masterKeyEncrypt = AES.encrypt(masterKey, passwordKey, iv);
		
		String privateKeyData = account.publicKey.toString().substring(1, 65)+account.publicKey.toString().substring(66,130)+DataUtil.bytesToHexString(account.privateKey);
		byte[] privateKeyDataPlain = DataUtil.HexStringToByteArray(privateKeyData);
		byte[] privateKeyDataEncrypted = AES.encrypt(privateKeyDataPlain,masterKey , iv);

		byte[] publicKeyHash = account.publicKeyHash;
		byte[] scriptHash =account.programHash;
		String scriptCode = account.script;
		

		String walletName = DataUtil.bytesToHexString(Digest.hash256(account.publicKeyEncoded)).substring(0,32);
		//String walletUrl = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/Wallet/wallet--"+walletName+".db3";
		String walletUrl = Environment.getExternalStorageDirectory().getAbsolutePath().toString()+"/wallet--"+walletName+".db3";
		SQLiteDatabase wallet =SQLiteDatabase.openOrCreateDatabase(walletUrl, null);
		
	    wallet.execSQL("CREATE TABLE Account ( PublicKeyHash BINARY NOT NULL CONSTRAINT PK_Account PRIMARY KEY, PrivateKeyEncrypted VARBINARY NOT NULL );");  
	    wallet.execSQL("CREATE TABLE Address ( ScriptHash BINARY NOT NULL CONSTRAINT PK_Address PRIMARY KEY );");  
	    wallet.execSQL("CREATE TABLE Coin ( TxId BINARY  NOT NULL, [Index] INTEGER NOT NULL, AssetId BINARY NOT NULL, ScriptHash BINARY  NOT NULL, State INTEGER NOT NULL, Value INTEGER NOT NULL, CONSTRAINT PK_Coin PRIMARY KEY ( TxId, [Index] ), CONSTRAINT FK_Coin_Address_ScriptHash FOREIGN KEY ( ScriptHash ) REFERENCES Address (ScriptHash) ON DELETE CASCADE );");  
	    wallet.execSQL("CREATE TABLE Contract ( ScriptHash BINARY NOT NULL CONSTRAINT PK_Contract PRIMARY KEY, PublicKeyHash BINARY NOT NULL, RawData VARBINARY NOT NULL, CONSTRAINT FK_Contract_Account_PublicKeyHash FOREIGN KEY ( PublicKeyHash ) REFERENCES Account (PublicKeyHash) ON DELETE CASCADE, CONSTRAINT FK_Contract_Address_ScriptHash FOREIGN KEY ( ScriptHash ) REFERENCES Address (ScriptHash) ON DELETE CASCADE );");  
	    wallet.execSQL("CREATE TABLE [Key] ( Name VARCHAR NOT NULL CONSTRAINT PK_Key PRIMARY KEY, Value VARBINARY NOT NULL );");  
	    wallet.execSQL("CREATE TABLE [Transaction] ( Hash BINARY NOT NULL CONSTRAINT PK_Transaction PRIMARY KEY, Height INTEGER, RawData VARBINARY NOT NULL, Time TEXT NOT NULL, Type INTEGER NOT NULL );");  
	
	    //Account table
	    wallet.execSQL("INSERT INTO Account(PublicKeyHash,PrivateKeyEncrypted) VALUES (?,?)", new Object[]{publicKeyHash,privateKeyDataEncrypted});  
	    //Address table
	   wallet.execSQL("INSERT INTO Address(ScriptHash) VALUES (?)", new Object[]{scriptHash});  
	   //Contract table
	   byte[] RawData = DataUtil.HexStringToByteArray(DataUtil.bytesToHexString(publicKeyHash)+"010023"+scriptCode);
	   wallet.execSQL("INSERT INTO Contract(ScriptHash,PublicKeyHash,RawData) VALUES (?,?,?)",new Object[] {scriptHash,publicKeyHash,RawData});
	   //Key table
	   wallet.execSQL("INSERT INTO Key(Name,Value) VALUES (?,?)",new Object[] {"PasswordHash",passwordHash});
	   wallet.execSQL("INSERT INTO Key(Name,Value) VALUES (?,?)",new Object[] {"IV",iv});
	   wallet.execSQL("INSERT INTO Key(Name,Value) VALUES (?,?)",new Object[] {"MasterKey",masterKeyEncrypt});
	   wallet.execSQL("INSERT INTO Key(Name,Value) VALUES (?,?)",new Object[] {"Version",DataUtil.HexStringToByteArray("01000000060000000000000000000000")});
	   wallet.execSQL("INSERT INTO Key(Name,Value) VALUES (?,?)",new Object[] {"Height", DataUtil.HexStringToByteArray("00000000")});
	   
	   return walletUrl;
	   
	}
	
}
