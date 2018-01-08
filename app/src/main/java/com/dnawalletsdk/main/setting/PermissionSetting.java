package com.dnawalletsdk.main.setting;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;


@TargetApi(23)
public abstract  class PermissionSetting extends Activity {

	public static final int QUEST_CODE_ALL  = 1;
	public static final  String[] permArray = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
	
}
