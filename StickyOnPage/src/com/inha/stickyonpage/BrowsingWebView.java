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

	WebView wv;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		View view = inflater.inflate(R.layout.webview, container, false);

		wv = (WebView)view.findViewById(R.id.webView1);
		wv.setHorizontalScrollBarEnabled(false);
		wv.setVerticalScrollBarEnabled(false);
		wv.setFocusable(true);
		wv.setWebViewClient(new WebViewClient(){

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		      wv.loadUrl(url);
		      return true;
		    }
		});
		
		WebSettings ws = wv.getSettings();
		ws.setJavaScriptEnabled(true);
		
		wv.loadUrl("http://m.naver.com");
		
		return view;
	}
}
