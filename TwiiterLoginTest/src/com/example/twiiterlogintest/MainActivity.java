package com.example.twiiterlogintest;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

	Twitter twitter;
	RequestToken requestToken;
	String verifier;
	SharedPreferences pref;
	TextView textview;
	String temp = "";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		textview = (TextView)findViewById(R.id.textview1);
		
		Button twitterBtn = (Button)findViewById(R.id.button1);
		twitterBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new Thread(){
				
					@Override
					public void run() {
						// TODO Auto-generated method stub
						loginToTwitter();
					}
				}.start();
			}
		});
		
		Button twitBtn = (Button)findViewById(R.id.button2);
		twitBtn.setOnClickListener(new OnClickListener() {
					
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pref = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
				
				if(pref.getString(Const.PREF_KEY_TWITTER_LOGIN, null).equals("true")){
					new Thread(){
						@Override
						public void run() {
							// TODO Auto-generated method stub
						
							try {
								ConfigurationBuilder builder = new ConfigurationBuilder();
								builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
								builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
								builder.setOAuthAccessToken(pref.getString(Const.PREF_KEY_OAUTH_TOKEN, null));
								builder.setOAuthAccessTokenSecret(pref.getString(Const.PREF_KEY_OAUTH_SECRET, null));
			                      
								TwitterFactory tf = new TwitterFactory(builder.build());
								twitter = tf.getInstance();
								
								twitter.updateStatus("Testing145");
								
								long err = -1; 
								IDs ids = twitter.getFollowersIDs(err);
								long[] list = ids.getIDs();
			
								for(long d : ids.getIDs())
								{	
									User user = twitter.showUser(d);
									temp = user.getName();
								}
								
							} catch (TwitterException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
						}}.start();
						textview.setText(temp);
				}else{
					Toast.makeText(MainActivity.this, "Twitter Login을 해주세요", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void loginToTwitter(){
            ConfigurationBuilder builder = new ConfigurationBuilder();
            builder.setOAuthConsumerKey(Const.TWITTER_CONSUMER_KEY);
            builder.setOAuthConsumerSecret(Const.TWITTER_CONSUMER_SECRET);
            
            TwitterFactory factory = new TwitterFactory(builder.build());
            twitter = factory.getInstance();
 
            try {
                requestToken = twitter.getOAuthRequestToken();
                
                Intent i = new Intent(this, SopWebview.class);
                i.putExtra("URL", requestToken.getAuthenticationURL());
                
                this.startActivityForResult(i, Const.TWITTER_OAUTH_CODE);
                
            } catch (TwitterException e) {
                e.printStackTrace();
            }
	}

	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode == Const.TWITTER_OAUTH_CODE && resultCode == RESULT_OK){
			verifier = data.getExtras().getString("verifier");
			saveOauth();
		}
	}
	
	private void saveOauth(){
		new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				AccessToken at = null;
				try {
					at = twitter.getOAuthAccessToken(verifier);
				} catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				pref = getSharedPreferences(Const.PREFERENCE_NAME, MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(Const.PREF_KEY_OAUTH_TOKEN, at.getToken());
				editor.putString(Const.PREF_KEY_OAUTH_SECRET, at.getTokenSecret());
				editor.putString(Const.PREF_KEY_TWITTER_LOGIN, "true");
				editor.commit();
			}
		}.start();
	}
}

