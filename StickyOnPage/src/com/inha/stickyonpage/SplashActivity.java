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
	private String twitterLogin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);
		
		SharedPreferences twitterPref = getSharedPreferences(Const.PREFERENCE_TWITTER, MODE_PRIVATE);
		twitterLogin = twitterPref.getString(Const.PREF_KEY_TWITTER_LOGIN, "false");
		
		context = this;
		
		Handler hdl = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				//Check Login
				if(!twitterLogin.equals("true") && Const.LOGINSTATUS == Const.LOGGEDOUT){
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