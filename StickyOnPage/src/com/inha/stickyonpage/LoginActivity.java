package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.facebook.AppEventsLogger;
import com.facebook.Request;
import com.facebook.Request.GraphUserCallback;
import com.facebook.Request.GraphUserListCallback;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.inha.stickyonpage.db.DBConnectionModule;

public class LoginActivity extends Activity {
	
	ProgressDialog mDialog;
	
	// Facebook variable
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
    
    private boolean isResumed = false;
    
	// Twitter variable
	Twitter twitter;
	ConfigurationBuilder builder;
	RequestToken requestToken;
	TwitterFactory factory;
	String verifier;
	SharedPreferences mSharedPref;
	SharedPreferences.Editor editor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		// App must use UiLifecycleHelper for Facebook SDK
		uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
		
        Button twitterBtn = (Button)findViewById(R.id.login_button_twitter);
        twitterBtn.setOnClickListener(new OnClickListener() {	
			@Override
			public void onClick(View v) {
				loginToTwitter();
			}
        });
	}
	
	public void loginToTwitter(){
        builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
        builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
        
        factory = new TwitterFactory(builder.build());
        twitter = factory.getInstance();

    	// Server communicate must be do background in Android
    	new Thread(){
    		public void run() {
    			try {
					requestToken = twitter.getOAuthRequestToken();	// get unAuthorized requestToken
					
					Intent i = new Intent(LoginActivity.this, LoginWebView.class);
			        i.putExtra("URL", requestToken.getAuthenticationURL());
			        LoginActivity.this.startActivityForResult(i, Const.TWITTER_OAUTH_CODE);	// start webView
				} catch (TwitterException e) {
					e.printStackTrace();
				}
    		};
    	}.start();   
    }
	
	private class SaveOauthAsync extends AsyncTask<Object, Object, Object> {
		ProgressDialog mDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			showProgress();
		}
		
		@Override
		protected Object doInBackground(Object... arg0) {
			
			AccessToken at = null;
			try {
				at = twitter.getOAuthAccessToken(verifier);
			} catch (TwitterException e) {
				e.printStackTrace();
			}
			
			HashSet<String> friendsList = new HashSet<String>();
			try {
				long[] ids = twitter.getFriendsIDs(twitter.getId(), -1).getIDs();
				for(int i=0; i<ids.length; i++) {
					friendsList.add(ids[i]+"");
				}
			} catch (IllegalStateException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (TwitterException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			
			mSharedPref = getSharedPreferences(Const.PREFERENCE, MODE_PRIVATE);
			editor = mSharedPref.edit();
			editor.putString(Const.PREF_KEY_OAUTH_TOKEN, at.getToken());
			editor.putString(Const.PREF_KEY_OAUTH_SECRET, at.getTokenSecret());
			editor.putString(Const.PREF_LOGINSTATUS, "Twitter");
			try {
				editor.putString(Const.PREF_LOGINID, "T"+Long.toString(twitter.getId()));
				editor.putString(Const.PREF_LOGINNAME, (twitter.showUser(twitter.getId())).getName());
				editor.putStringSet(Const.PREF_LOGINFRIENDS, friendsList);
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			editor.commit();
			
			
			try {
				Connection conn = DBConnectionModule.getConnection();
				DBConnectionModule.getInstance().insertUser("T"+twitter.getId(), (twitter.showUser(twitter.getId())).getName(), conn);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object result) {
			hideProgress();
			Intent i = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(i);
			finish();
		}
		
		private void showProgress() {
			// TODO Auto-generated method stub
			mDialog = new ProgressDialog(LoginActivity.this);
			mDialog.setTitle("Sticky On Page");
			mDialog.setMessage("잠시만 기다려주세요.");
			mDialog.setCancelable(false);
			mDialog.show();
		}
		
		private void hideProgress(){
			mDialog.cancel();
		}
    }
	
	@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
        
        if(requestCode == Const.TWITTER_OAUTH_CODE && resultCode == RESULT_OK){
			verifier = data.getExtras().getString("verifier");
			new SaveOauthAsync().execute((Object)null);
		}
    }
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		if(isResumed){
			if (state.equals(SessionState.OPENED)) {
				
				mDialog = new ProgressDialog(LoginActivity.this);
				mDialog.setTitle("Sticky On Page");
				mDialog.setMessage("잠시만 기다려주세요.");
				mDialog.setCancelable(false);
				mDialog.show();
				
				Request.newMeRequest(session, new GraphUserCallback() {
					
					@Override
					public void onCompleted(GraphUser user, Response response) {
						final GraphUser mUser = user; 
						// TODO Auto-generated method stub
						mSharedPref = getSharedPreferences(Const.PREFERENCE, MODE_PRIVATE);
						editor = mSharedPref.edit();
						editor.putString(Const.PREF_LOGINSTATUS, "Facebook");
						editor.putString(Const.PREF_LOGINID, "F"+user.getId());
						editor.putString(Const.PREF_LOGINNAME, user.getUsername());
						editor.commit();
					
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								try {
									Connection conn = DBConnectionModule.getConnection();
									DBConnectionModule.getInstance().insertUser("F"+mUser.getId(), mUser.getUsername(), conn);
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}).start();
						
						
					}
				}).executeAsync();
				
				Request.newMyFriendsRequest(session, new GraphUserListCallback() {
					
					@Override
					public void onCompleted(List<GraphUser> users, Response response) {
						// TODO Auto-generated method stub
						HashSet<String> friendsList = new HashSet<String>();
						
						for(int i=0; i<users.size(); i++) {
							friendsList.add(users.get(i).getId());
						}
						
						mSharedPref = getSharedPreferences(Const.PREFERENCE, MODE_PRIVATE);
						editor = mSharedPref.edit();
						editor.putStringSet(Const.PREF_LOGINFRIENDS, friendsList);
						editor.commit();
						
						mDialog.cancel();
						
						Intent i = new Intent(LoginActivity.this, MainActivity.class);
						startActivity(i);
						finish();
					}
				}).executeAsync();
            }
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
}
