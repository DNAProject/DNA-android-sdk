package com.dnawalletsdk.info;

import org.json.JSONArray;

/**
 * 资产信息
 * 
 * @author 12146
 *
 */
public class AssetInfo {
	
		private String assetId ;		// 资产编号
		private String assetName;	// 资产名称
		private Double balance;		// 资产余额
		private JSONArray Utxo;  //资产区块json

		public AssetInfo(){
			super();
		}

		public AssetInfo(String assetId ,String assetName ,Double balance,JSONArray Utxo) {
			super();
			this.assetId = assetId;
			this.assetName = assetName;
			this.balance = balance;
			this.Utxo = Utxo;
		}
		
		public void setAssetId(String assetId) {
			this.assetId = assetId;
		}
		
		public void setAssetName(String assetName) {
			this.assetName =assetName;
		}
		
		public void setbalance(Double balance) {
			this.balance = balance;
		}
		
		public void setUtxo(JSONArray Utxo) {
			this.Utxo = Utxo;
		}
		
		public String getAssetId() {
			return this.assetId;
		}
		
		public String getAssetName() {
			return this.assetName;
		}

		public Double getbalance() {
			return this.balance;
		}
		
		public JSONArray getUtxo() {
			return this.Utxo;
		}

}