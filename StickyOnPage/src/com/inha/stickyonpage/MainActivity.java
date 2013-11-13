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
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {

//  private Fragment[] fragments = new Fragment[Const.FRAGMENT_COUNT];
    private boolean isResumed = false;

    private UiLifecycleHelper uiHelper;
    
    DrawerLayout mDrawerLayout;
    FrameLayout mFrameLayout;
    
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
        
        
//        TextView tv = (TextView)findViewById(R.id.drawer_tv);
//        tv.setText(""+Const.LOGINSTATUS);
        
        mFrameLayout = (FrameLayout)findViewById(R.id.drawer_main);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BrowsingWebView browsingFragment = new BrowsingWebView();
        ft.add(R.id.drawer_main, browsingFragment);
        ft.commit();
        
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
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
            /*FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                showFragment(Const.SELECTION, false);
            } else if (state.isClosed()) {
                showFragment(Const.SPLASH, false);
            }*/
        }
    }
    
    public static int getLoginStatus() {
    	return Const.LOGINSTATUS;
    }
    
    /*public class GetTwitterFollowerAsyncTask extends AsyncTask<Integer, Integer, Integer>{
    	String temp;
    	public SharedPreferences pref;
    	
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
    		pref = getSharedPreferences(Const.PREFERENCE_TWITTER, MainActivity.MODE_PRIVATE);
		
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
			builder.setOAuthAccessToken(pref.getString(Const.PREF_KEY_OAUTH_TOKEN, null));
			builder.setOAuthAccessTokenSecret(pref.getString(Const.PREF_KEY_OAUTH_SECRET, null));
              
			TwitterFactory tf = new TwitterFactory(builder.build());
			Twitter twitter = tf.getInstance();
			
			long err = -1; 
			IDs ids = null;
			
			try {
				ids = twitter.getFollowersIDs(err);
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long[] list = ids.getIDs();

			for(long d : ids.getIDs())
			{	
				User user = null;
				try {
					user = twitter.showUser(d);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				temp += user.getName() + " ";
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
//			friendName.setText(temp);
		}
    }*/
    
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK && getSupportFragmentManager().getBackStackEntryCount() == 0) {
	        if(!mFlag) {
	            Toast.makeText(this, "'뒤로'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
	            mFlag = true;
	            mHandler.sendEmptyMessageDelayed(0, 2000);
	            return false;
	        } else {
	            finish();
	        }
	    }
	    return super.onKeyDown(keyCode, event);
	}
}
