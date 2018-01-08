package com.dnawalletsdk.info;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 转账数据
 * 
 * @author 12146
 *
 */
public class TransferLengthData {
	
		private String firstVal ;		
		private int len = 0;
		private String inputNum;

		public TransferLengthData(){
			super();
		}

		public TransferLengthData(String firstVal , int len ,String inputNum){
			super();
			this.firstVal = firstVal;
			this.len = len;
			this.inputNum = inputNum;
		}
		
		public void setfirstVal(String firstVal) {
			this.firstVal = firstVal;
		}
		
		public void setlen(int len) {
			this.len =len;
		}
		
		public void setInputNum(String inputNum) {
			this.inputNum = inputNum;
		}
		
		public String getfirstVal() {
			return this.firstVal;
		}
		
		public int getlen() {
			return this.len;
		}
		
		public String getInputNum() {
			return this.inputNum;
		}
		


}