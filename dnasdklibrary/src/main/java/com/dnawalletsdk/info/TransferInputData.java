package com.dnawalletsdk.info;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 转账数据
 * 
 * @author 12146
 *
 */
public class TransferInputData {
	
		private Double coin_amount ;		
		private byte[] data;	

		public TransferInputData(){
			super();
		}

		public TransferInputData(Double coin_amount , byte[] data) {
			super();
			this.coin_amount = coin_amount;
			this.data = data;
		}
		
		public void setCoin_amount(Double coin_amount) {
			this.coin_amount = coin_amount;
		}
		
		public void setData(byte[] data) {
			this.data =data;
		}
		
		public Double getCoin_amount() {
			return this.coin_amount;
		}
		
		public byte[] getData() {
			return this.data;
		}
		


}