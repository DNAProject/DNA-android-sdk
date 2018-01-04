package com.dnawalletsdk.Http;




import com.dnawalletsdk.main.MainActivity.MainHandler;

import android.app.Application;

/**
 * 自己实现Application，实现数据共享
 * 
 * @author mark
 *
 */
public class MyHandler extends Application {
	// 共享变量
	private MainHandler handler = null;
	
	// set方法
	public void setHandler(MainHandler handler) {
		this.handler = handler;
	}
	
	// get方法
	public MainHandler getHandler() {
		return handler;
	}
}
