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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;

import com.facebook.AppEventsLogger;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;

public class MainActivity extends FragmentActivity {

    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";

    private static final int SPLASH = 0;
    private static final int SELECTION = 1;
    private static final int SETTINGS = 2;
    private static final int FRAGMENT_COUNT = SETTINGS +1;

    private Fragment[] fragments = new Fragment[FRAGMENT_COUNT];
    private boolean isResumed = false;
    private MenuItem settings;
    private String verifier;
    private Twitter twitter;
    private SharedPreferences twitterPref;
    private UiLifecycleHelper uiHelper;
    
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

        Button twitterBtn = (Button)findViewById(R.id.login_button_twitter);
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
        
        // initialize fragments
        FragmentManager fm = getSupportFragmentManager();
        fragments[SPLASH] = fm.findFragmentById(R.id.splashFragment);
        fragments[SELECTION] = fm.findFragmentById(R.id.selectionFragment);
        fragments[SETTINGS] = fm.findFragmentById(R.id.userSettingsFragment);

        FragmentTransaction transaction = fm.beginTransaction();
        for(int i = 0; i < fragments.length; i++) {
            transaction.hide(fragments[i]);
        }
        transaction.commit();
    }

    @Override
    public void onResume() {
    	Log.i("sop", "onResume");
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
    	Log.i("sop", "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == TwitterConst.TWITTER_OAUTH_CODE && resultCode == RESULT_OK){
			verifier = data.getExtras().getString("verifier");
			saveOauth();
		}
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

    @Override
    protected void onResumeFragments() {
    	Log.i("sop", "onResumeFragments");
        super.onResumeFragments();
        
        twitterPref = getSharedPreferences(TwitterConst.PREFERENCE_NAME, MODE_PRIVATE);
        String prefString = twitterPref.getString(TwitterConst.PREF_KEY_TWITTER_LOGIN, null);
        Log.i("sop", "*" + prefString);
		if (prefString != null && prefString.equals("true")) {
        	showFragment(SELECTION, false);
		}
        else {
	        Session session = Session.getActiveSession();
	        if (session != null && session.isOpened()) {
	            // if the session is already open, try to show the selection fragment
	        	showFragment(SELECTION, false);
	        } else {
	            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
	            showFragment(SPLASH, false);
	        }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // only add the menu when the selection fragment is showing
        if (fragments[SELECTION].isVisible()) {
            if (menu.size() == 0) {
                settings = menu.add(R.string.settings);
            }
            return true;
        } else {
            menu.clear();
            settings = null;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.equals(settings)) {
            showSettingsFragment();
            return true;
        }
        return false;
    }

    public void showSettingsFragment() {
        showFragment(SETTINGS, true);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
    	Log.i("sop", "onSessionStateChange");
    	if (isResumed) {
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
                showFragment(SELECTION, false);
            } else if (state.isClosed()) {
                showFragment(SPLASH, false);
            }
        }
    }

    private void showFragment(int fragmentIndex, boolean addToBackStack) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
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
    }
    
    public void loginToTwitter(){
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(TwitterConst.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(TwitterConst.TWITTER_CONSUMER_SECRET);
        
        RequestToken requestToken;
        TwitterFactory factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

        try {
            requestToken = twitter.getOAuthRequestToken();
            
            Intent i = new Intent(this, SopWebview.class);
            i.putExtra("URL", requestToken.getAuthenticationURL());
            
            this.startActivityForResult(i, TwitterConst.TWITTER_OAUTH_CODE);
            
        } catch (TwitterException e) {
            e.printStackTrace();
        }
    }
    
	private void saveOauth(){
		new Thread(){
			@Override
			public void run() {
				AccessToken at = null;
				try {
					at = twitter.getOAuthAccessToken(verifier);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				
				twitterPref = getSharedPreferences(TwitterConst.PREFERENCE_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = twitterPref.edit();
				editor.putString(TwitterConst.PREF_KEY_OAUTH_TOKEN, at.getToken());
				editor.putString(TwitterConst.PREF_KEY_OAUTH_SECRET, at.getTokenSecret());
				editor.putString(TwitterConst.PREF_KEY_TWITTER_LOGIN, "true");
				editor.commit();
			}
		}.start();
	}
}
