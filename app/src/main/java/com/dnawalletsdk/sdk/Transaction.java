package com.dnawalletsdk.sdk;

import java.io.IOException;
import java.net.URLEncoder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dnawalletsdk.Cryptography.Base58;
import com.dnawalletsdk.Cryptography.Digest;
import com.dnawalletsdk.Data.DataUtil;
import com.dnawalletsdk.info.AssetInfo;
import com.dnawalletsdk.info.TransferInputData;
import com.dnawalletsdk.info.TransferLengthData;
import com.dnawalletsdk.main.MainActivity;

import android.app.Activity;
import android.provider.ContactsContract.Contacts.Data;
import android.widget.Toast;

public class Transaction {

	/**
	 * Make transfer transaction and get transaction unsigned data.
	 * 发起一个转账交易和获取交易数据（十六进制）。
	 *
	 * 数据格式：
	 * 字节            内容
	 * 1              type ： 80
	 * 1              version  ： 00
	 * 1              交易属性个数：01
	 * 1              交易属性中的用法
	 * 8              交易属性中的数据长度
	 * 数据实际长度     交易属性中的数据
	 * 1              引用交易的输入个数：个数为0时，则无
	 * 32             引用交易的hash：个数为0时，则无
	 * 2              引用交易输出的索引：个数为0时，则无
	 * 1              交易输出类型: 01为全部转账；02位有找零
	 * 32             转账资产ID
	 * 8              转账资产数量
	 * 20             转账资产ProgramHash
	 * 32             找零转账资产ID，仅在交易输出类型为02时有
	 * 8              找零转账资产数量，仅在交易输出类型为02时有
	 * 20             找零转账资产ProgramHash，仅在交易输出类型为02时有
	 * 1              Program长度：0x01
	 * 1              参数长度 parameter
	 * 参数实际长度 	  参数：签名
	 * 1			  代码长度 code
	 * 代码实际长度     代码：公钥
	 *
	 * @param $coin
	 * @param $publicKeyEncoded
	 * @param $toAddress
	 * @param $Amount
	 *
	 * @returns {*} : TxUnsignedData
	 */
	
	public static String makeTransferTransaction(AssetInfo Asset, byte[] publicKeyEncoded, String toAddress, Double transferAssetAmount){
		
		byte[] ProgramHash = Base58.decode(toAddress);
		try {
			byte[] ProgramHashBuffer = new byte[21];		
			for (int i = 0 ; i < 21; i ++) {
				ProgramHashBuffer[i] = ProgramHash[i];
			}
			byte[] ProgramSha256Buffer = Digest.hash256(ProgramHashBuffer);
			
			byte[] ProgramSha256Buffer_part = new byte[4];
			byte[] ProgramHash_part = new byte[4];
			for(int i = 0 ; i < 4 ; i ++) {
				ProgramSha256Buffer_part[i] = ProgramSha256Buffer[i];
			}
			for(int i = 0 ; i < 4 ; i ++) {
				ProgramHash_part[i] = ProgramHash[i+21];
			}
			if(!DataUtil.bytesToHexString(ProgramSha256Buffer_part).equals(DataUtil.bytesToHexString(ProgramHash_part))) {
				//address verify failed.
				return "-1";
			}
		} catch (Exception e) {  
			//address verify failed.
            e.printStackTrace();  
            return "-1";
		}
		
		byte[] programHash = new byte[20];
		for(int i = 0 ; i < 20 ; i ++) {
			programHash[i] = ProgramHash[i+1];
		}
		
		String SignatureScript = Account.createSignatureScript(publicKeyEncoded);
		byte[] myProgramHash = Digest.hash160(DataUtil.HexStringToByteArray(SignatureScript));

		//Input Construct
		TransferInputData inputData = makeTransferInputData(Asset,transferAssetAmount);
		if(inputData == null) {
			return "-2";
		}
		
		Double inputAmount = inputData.getCoin_amount();
		
		//Adjust the accuracy. (调整精度之后的数据）
		int accuracyVal = 100000000;
		double newOutputAmount = transferAssetAmount*accuracyVal ;
		double newInputAmount = inputAmount*accuracyVal - newOutputAmount;

		/**
	     * data
	     * @type {string}
	     */
		String type = "80";
		String version = "00";
		//自定义属性，Attributes
		String transactionAttrNum = "01";
		String transactionAttrUsage = "00";
		String transactionAttrData =  DataUtil.bytesToHexString(Integer.toString((int)(Math.random()*99999999)).getBytes());
		String transactionAttrDataLen = DataUtil.prefixInteger(Integer.toHexString(transactionAttrData.length()/2), 2);
		String referenceTransactionData = DataUtil.bytesToHexString(inputData.getData());
		
		String data = type + version + transactionAttrNum + transactionAttrUsage + transactionAttrDataLen+ transactionAttrData + referenceTransactionData ;
		
		//OUTPUT
		String transactionOutputNum = "01";//无找零
		String transactionOutputAssetID = DataUtil.bytesToHexString(DataUtil.reverseArray(DataUtil.HexStringToByteArray(Asset.getAssetId())));
		String transactionOutputValue = DataUtil.numStoreInMemory(Long.toHexString((long)newOutputAmount),16);
		String transactionOutputProgramHash = DataUtil.bytesToHexString(programHash);
		
		if(inputAmount == transferAssetAmount ) {
			data =data + transactionOutputNum +  transactionOutputAssetID + transactionOutputValue + transactionOutputProgramHash;
			//System.out.println(data);
		}else {
			transactionOutputNum = "02" ; //有找零
			//Transfer to someone 发给他人
			data =data + transactionOutputNum +  transactionOutputAssetID + transactionOutputValue + transactionOutputProgramHash;

			//Change to yourself 找零给自己
			String transactionOutputValue_me  =DataUtil.numStoreInMemory(Long.toHexString((long)newInputAmount),16);
			String transactionOutputProgramHash_me = DataUtil.bytesToHexString(myProgramHash);
			data = data + transactionOutputAssetID + transactionOutputValue_me + transactionOutputProgramHash_me;
			
		}
		
		//System.out.println(data);
		return data;
	}
	
	@SuppressWarnings("null")
	private static TransferInputData makeTransferInputData(AssetInfo Asset , Double transferAssetAmount) {
	
		JSONArray Utxo = Asset.getUtxo();
		Double[] coin_value = new Double[Utxo.length()] ;
		String[] coin_txid = new String[Utxo.length()];
		int[] coin_index = new int[Utxo.length()];
		try {
			for (int i = 0 ; i < Utxo.length() ; i ++) {
				String utxo = Utxo.getString(i);
				JSONObject utxoObj = new JSONObject(utxo);
				coin_value[i] = utxoObj.getDouble("Value");
				coin_txid[i] = utxoObj.getString("Txid");
				coin_index[i] = utxoObj.getInt("Index");
			}
		} catch (Exception e) {  
            e.printStackTrace();  				  
        }
		
	    for (int i = 0 ; i < coin_value.length - 1 ; i++) {
	        for (int j = 0 ; j < coin_value.length - 1 - i ; j++) {
	            if (coin_value[j]<coin_value[j + 1]) {
	            	Double temp = coin_value[j];
	            	coin_value[j] = coin_value[j + 1];
	            	coin_value[j + 1] = temp;
	            	
	            	String temp2 = coin_txid[j];
	            	coin_txid[j] = coin_txid[j+1];
	            	coin_txid[j+1] = temp2;
	            	
	            	int temp3 = coin_index[j];
	            	coin_index[j] = coin_index[j+1];
	            	coin_index[j+1] = temp3;
	            }
	        }
	    }
	    
	    Double sum = 0.0;
	    for(int i = 0 ; i < coin_value.length ; i ++) {
	    	sum = sum + coin_value[i];
	    }
		
	    if(sum<transferAssetAmount) {
	    	return null;
	    }
	    
	    double amount = transferAssetAmount;
	    int k = 0;
	    while(coin_value[k]<=amount) {
	    	amount = amount - coin_value[k];
	    	if (amount == 0){
	    		break;
	    	}
	    	k = k+1 ;
	    }    
	    
	    TransferLengthData lengthData  = InputDataLength(k);
	    //coin[0] - coin[k]
	    byte[] data = new byte[lengthData.getlen()+34*(k+1)];
	    //input num
	    int m = 0;
	    if(lengthData.getlen() ==1) {
	    	byte[] inputNum = DataUtil.HexStringToByteArray(lengthData.getInputNum());
	    	for( int i = 0 ; i<inputNum.length ; i++) {
	    		data[i] = inputNum[i];
	    	}
	    	m = inputNum.length;
	    }else {
	    	byte[] firstVal = DataUtil.HexStringToByteArray(lengthData.getfirstVal());
	    	byte[] inputNum = DataUtil.HexStringToByteArray(lengthData.getInputNum());
	    	for(int i = 0 ; i < firstVal.length ; i++) {
	    		data[i] = firstVal[i];
	    	}
	    	m = inputNum.length;
	    	for(int i = 0 ; i<inputNum.length ; i++) {
	    		data[m + i] = inputNum[i];
	    	}
	    	m = m + inputNum.length;
	    }
	    
	    //input coins
	    for( int x = 0 ; x < k+1 ; x++) {
	    	//txid
	    	int pos = m + (x * 34);
	    	byte[] txid = DataUtil.reverseArray(DataUtil.HexStringToByteArray(coin_txid[x]));
	    	for(int i = 0 ; i < txid.length ; i++) {
	    		data[pos+i] = txid[i];
	    	}
	    	
	    	//index
	    	pos = pos + 32;
	    	byte[] index = DataUtil.HexStringToByteArray(DataUtil.numStoreInMemory(Integer.toHexString(coin_index[x]), 4));
	    	for(int i = 0 ; i < index.length ; i++) {
	    		data[pos+i] = index[i];
	    	}
	    }
	    
    	//calc coin_amount
    	Double balance = 0.0;
    	for(int i = 0 ; i < k+1 ; i ++) {
    		balance = balance + coin_value[i];
    	}
	    
    	TransferInputData inputData = new TransferInputData();
    	inputData.setCoin_amount(balance);
    	inputData.setData(data);
    
    	
		return inputData;
		
	}

	private static TransferLengthData InputDataLength(int orderNum) {
		int firstVal = orderNum+1;
		int len = 0 ;
		int inputNum = orderNum+1;
		String inputNumString;
		
		if(orderNum < 253) { //0xFD
			len = 1;
			 inputNumString = DataUtil.numStoreInMemory(Integer.toHexString(inputNum),2);
		} else if (orderNum < 65535) { //0xFFFF
			firstVal = 253;
			len = 3;
			inputNumString = DataUtil.numStoreInMemory(Integer.toHexString(inputNum),4);
		} else if (orderNum < 4294967295L) { //0xFFFFFFFF
			firstVal = 254;
			len = 5;
			inputNumString = DataUtil.numStoreInMemory(Integer.toHexString(inputNum),8);
		} else {
			firstVal = 255;
			len = 9;
			inputNumString = DataUtil.numStoreInMemory(Integer.toHexString(inputNum),16);
		}
		String firstValString = DataUtil.numStoreInMemory(Integer.toHexString(firstVal),2);
		
		TransferLengthData lengthData = new TransferLengthData( firstValString,len,inputNumString);
		
		return lengthData;

		
	}
	


	/**构成签名结构
	 *
	 *  * 数据格式：
	 * 字节            内容
	 * 文本数据长度    文本数据
	 * 1              标识 ： 01
	 * 1              结构长度  ： 41
	 * 1              数据长度  ：40
	 * 40             数据内容
	 * 1              协议数据长度
	 * 脚本数据长度   签名脚本数据
	 *
	 * @param $txData
	 * @param $sign
	 * @param $publicKeyEncoded
	 * @return {string}
	 * @constructor
	 */
	public static String AddContract(String txData, byte[] sign, byte[] publicKeyEncoded) {

		//sign num
		String Num = "01";
		//sign struct len
		String structLen = "41";
		//sign data len
		String dataLen = "40";
		//sign data
		String data = DataUtil.bytesToHexString(sign);
		//Contract data len
		String contractDataLen = "23";
		//script data
		String signatureScript = Account.createSignatureScript(publicKeyEncoded);
		
		return txData + Num + structLen + dataLen + data + contractDataLen + signatureScript;
	}
	
}
