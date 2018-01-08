package com.dnawalletsdk.Http;




import com.dnawalletsdk.main.MainActivity.MainHandler;

import android.app.Application;

/**
 * Make Application，Implement data sharing
 */
public class MyHandler extends Application {

	// Shared variable
	private MainHandler handler = null;
	
	// set function
	public void setHandler(MainHandler handler) {
		this.handler = handler;
	}
	
	// get function
	public MainHandler getHandler() {
		return handler;
	}
}
