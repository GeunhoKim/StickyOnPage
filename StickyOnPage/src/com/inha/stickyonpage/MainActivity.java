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

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {

//  private Fragment[] fragments = new Fragment[Const.FRAGMENT_COUNT];
    private boolean isResumed = false;
        
    private MenuItem settings;
    private String verifier;
    private Twitter twitter;
    private SharedPreferences twitterPref;
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
        
        TextView tv = (TextView)findViewById(R.id.loginStatus);
        tv.setText(""+Const.LOGINSTATUS);
        
        mFrameLayout = (FrameLayout)findViewById(R.id.drawer_main);
        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        BrowsingWebView browsingFragment = new BrowsingWebView();
        ft.add(R.id.drawer_main, browsingFragment);
        ft.commit();
        
/*      Button twitterBtn = (Button)findViewById(R.id.login_button_twitter);
        twitterBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				new Thread(){				
					@Override
					public void run() {
						loginToTwitter();
					}
				}.start();
			}
		});
        
        friendName = (TextView)findViewById(R.id.user_name); 
*/        
        /*
        FragmentManager fm = getSupportFragmentManager();
        fragments[Const.SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[Const.SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragments[Const.SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
        */
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
        
        /*if(requestCode == Const.TWITTER_OAUTH_CODE && resultCode == RESULT_OK){
			verifier = data.getExtras().getString("verifier");
			//saveOauth();
//			new SaveOauthAsync().execute(null, null, null);
			new GetTwitterFollowerAsyncTask().execute((Integer)null);
		}*/
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

    /*
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        
        twitterPref = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
        String prefString = twitterPref.getString(Const.PREF_KEY_TWITTER_LOGIN, null);
		if (prefString != null && prefString.equals("true")) {
        	showFragment(Const.SELECTION, false);
		} else {
	        Session session = Session.getActiveSession();
	        if (session != null && session.isOpened()) {
	        	Const.loginStatus = Const.FACEBOOK;
	            // if the session is already open, try to show the selection fragment
	        	showFragment(Const.SELECTION, false);
	        } else {
	            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
	            showFragment(Const.SPLASH, false);
	        }
        }
    }
    */

   /* @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[Const.SELECTION].isVisible()) {
            if (menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(settings)) {
            showSettingsFragment();
            return true;
        }
        return false;
    }*/

    /*public void showSettingsFragment() {
        showFragment(Const.SETTINGS, true);
    }*/

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

   /* private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        
        if (fragmentIndex == Const.SELECTION) {
        	TextView textView = (TextView)findViewById(R.id.selection_string);
        	if (Const.loginStatus == Const.TWITTER) {
        		textView.setText("You are now logged in using Twitter.\nWelcome to Sticky on Page.");
        	} else if (Const.loginStatus == Const.FACEBOOK) {
        		textView.setText("You are now logged in using Facebook.\nWelcome to Sticky on Page.");
        	}
        }
        
        for (int i = 0; i < fragments.length; i++) {
            if (i == fragmentIndex) {
                transaction.show(fragments[i]);
            } else {
                transaction.hide(fragments[i]);
            }
        }
        if (addToBackStack) {
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }*/
    
    /*public void loginToTwitter(){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
        
        RequestToken requestToken;
        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken();
            
            Intent i = new Intent(this, SopWebview.class);
            i.putExtra("URL", requestToken.getAuthenticationURL());
            
            this.startActivityForResult(i, Const.TWITTER_OAUTH_CODE);
            
            Const.loginStatus = Const.TWITTER;
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }*/
    
    /*private class SaveOauthAsync extends AsyncTask<Object, Object, Object> {
		@Override
		protected Object doInBackground(Object... arg0) {
			AccessToken at = null;
			try {
				at = twitter.getOAuthAccessToken(verifier);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
			twitterPref = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
			SharedPreferences.Editor editor = twitterPref.edit();
			editor.putString(Const.PREF_KEY_OAUTH_TOKEN, at.getToken());
			editor.putString(Const.PREF_KEY_OAUTH_SECRET, at.getTokenSecret());
			editor.putString(Const.PREF_KEY_TWITTER_LOGIN, "true");
			editor.commit();
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			twitterPref = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
		    String prefString = twitterPref.getString(Const.PREF_KEY_TWITTER_LOGIN, null);

			if (prefString != null && prefString.equals("true")) {
//		       	showFragment(Const.SELECTION, false);
			}
		}
    }*/
    
    public static int getLoginStatus() {
    	return Const.LOGINSTATUS;
    }
    
    public class GetTwitterFollowerAsyncTask extends AsyncTask<Integer, Integer, Integer>{
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
    }
    
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
