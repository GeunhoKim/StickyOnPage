package com.inha.stickyonpage;

public class Const {
	
	// Twitter Consumer Info
	public static final String TWITTER_CONSUMER_KEY = "ZtFcJ9xwNf4aXUcQKMZng";
    public static final String TWITTER_CONSUMER_SECRET = "QdODmq6cQlUX0d1wrrz9yRKO3Z5ILVt0SwJ95kB5os";
    public static final String TWITTER_CALLBACK_URL = "http://www.facebook.com/stickyOnPage";
    
    public static final int TWITTER_OAUTH_CODE = 777;
    
    // Preference Constants
    public static final String PREFERENCE_TWITTER = "twitter_oauth";
    public static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    public static final String PREF_KEY_OAUTH_SECRET = "oauth_secret";
    public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    
    // Twitter Oauth 
    public static final String URL_TWITTER_AUTH = "auth_url";
    public static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
    public static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
 
    
    // Main Activity
    public static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";

    /*public static final int SPLASH = 0;
    public static final int SELECTION = 1;
    public static final int SETTINGS = 2;
    public static final int FRAGMENT_COUNT = SETTINGS +1;*/
    
    public static final int LOGGEDOUT = 10;
    public static final int TWITTER = 11;
    public static final int FACEBOOK = 12;
    public static int LOGINSTATUS = LOGGEDOUT;
}
