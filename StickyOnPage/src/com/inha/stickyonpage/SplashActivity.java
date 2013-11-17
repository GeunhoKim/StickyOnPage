package com.inha.stickyonpage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class SplashActivity extends Activity {
	private Context context;
	private String loginStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		SharedPreferences twitterPref = getSharedPreferences(Const.PREFERENCE, MODE_PRIVATE);
		loginStatus = twitterPref.getString(Const.PREF_LOGINSTATUS, "");
		
		context = this;
		
		Handler hdl = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Check Login
				if(!loginStatus.equals("Facebook") && !loginStatus.equals("Twitter")){
					startActivity(new Intent(context, LoginActivity.class));
				}else{
					startActivity(new Intent(context, MainActivity.class));
				}
				finish();
			}
		};
		hdl.sendEmptyMessageDelayed(0, 1200); // Do after 1.2s 
	}
}