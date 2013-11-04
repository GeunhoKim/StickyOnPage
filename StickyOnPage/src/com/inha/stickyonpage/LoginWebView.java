package com.inha.stickyonpage;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class LoginWebView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
	
		String url = (String)getIntent().getExtras().get("URL");
		WebView wb = (WebView)findViewById(R.id.webView1);
		wb.setWebViewClient( new WebViewClient(){
	            @Override
	            public boolean shouldOverrideUrlLoading(WebView view, final String url)
	            {
	            	if(url.contains(Const.TWITTER_CALLBACK_URL))
	                {
		            	Uri uri = Uri.parse(url);
		            	String verifier = uri.getQueryParameter(Const.URL_TWITTER_OAUTH_VERIFIER);
		            	
		            	Intent i = new Intent();
		            	i.putExtra("verifier", verifier);
		      
		            	setResult(RESULT_OK, i);
		            	
		            	finish();
		            	return true;
	                }
	            	return false;
	            }
	        });
		wb.loadUrl(url);
	}
}
