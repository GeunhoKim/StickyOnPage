package com.inha.stickyonpage;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class BrowsingWebView extends Fragment {

	WebView mView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.webview, container, false);

		mView = (WebView)view.findViewById(R.id.webView1);
		mView.setHorizontalScrollBarEnabled(false);
		mView.setVerticalScrollBarEnabled(false);
		mView.setFocusable(true);
		mView.setWebViewClient(new WebViewClient(){

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		      mView.loadUrl(url);
		      return true;
		    }
		});
		
		WebSettings mSettings = mView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		
		mView.loadUrl("http://m.naver.com");	
		return view;
	}
}
