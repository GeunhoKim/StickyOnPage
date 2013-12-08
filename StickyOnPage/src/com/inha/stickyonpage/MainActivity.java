/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.inha.stickyonpage;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {
    
    DrawerLayout mDrawerLayout;
    FrameLayout mFrameLayout;
    MemoLinearLayout mMemoLinearLayout;
    private ActionBar mActionBar;
    private Intent mIntent;

    private boolean isResumed = false;
    private UiLifecycleHelper uiHelper;
    private boolean mFlag = false;
    private Handler mHandler = new Handler() {

	    public void handleMessage(Message msg) {
	        if(msg.what == 0) {
	            mFlag = false;
	        }
	    }
	};
    
   private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        
        mDrawerLayout.openDrawer(Gravity.END);
        mDrawerLayout.closeDrawer(Gravity.END);
        mFrameLayout = (FrameLayout)findViewById(R.id.drawer_main);
        mMemoLinearLayout = (MemoLinearLayout)findViewById(R.id.drawer_left);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		mIntent = getIntent();
//		if (mIntent.hasExtra(Const.MEMO_URL_FROM_MEMO_READ)) { // when MainActivity is called from MemoCRUDActivity
//			String url = mIntent.getStringExtra(Const.MEMO_URL_FROM_MEMO_READ);
//			Bundle bundle = new Bundle();
//			bundle.putString(Const.MEMO_URL_FROM_MEMO_READ, url);
//			
//			BrowsingWebView browsingFragment = new BrowsingWebView();
//			browsingFragment.setArguments(bundle);
//			ft.replace(R.id.drawer_main, browsingFragment, "BrowsingWebView");
//		} else {
		RecentStickyView stickyFragment = new RecentStickyView();
		ft.add(R.id.drawer_main, stickyFragment, "RecentStickyView");
//		}
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == Const.MEMO_REFRESH_CODE && resultCode == RESULT_OK){
        	mMemoLinearLayout.getMemoList(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	if (isResumed) {
    		// ?
        }
    }
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(getSupportFragmentManager().findFragmentByTag("BrowsingWebView") != null){
    			WebView mWebView = (WebView)findViewById(R.id.webView1);
    			if(mWebView.canGoBack()) {
    				mWebView.goBack();
    			} else {
        	        	 FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        	             RecentStickyView stickyFragment = new RecentStickyView();
        	             stickyFragment.getRecentStickyAsyncTask();
        	             ft.replace(R.id.drawer_main, stickyFragment, "RecentStickyView");
        	             ft.commit();        	   
    			}
    			return false;
    		} else if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
    	        if(!mFlag) {
    	            Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
    	            mFlag = true;
    	            mHandler.sendEmptyMessageDelayed(0, 2000);
    	            return false;
    	        } else {
    	            finish();
    	        }
    	    }
    	}
	    return super.onKeyDown(keyCode, event);
	}
}
