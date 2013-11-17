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

	WebView mWebView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.webview, container, false);

		mWebView = (WebView)view.findViewById(R.id.webView1);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setFocusable(true);
		mWebView.setWebViewClient(new WebViewClient(){

		    @Override
		    public boolean shouldOverrideUrlLoading(WebView view, String url){
		    	mWebView.loadUrl(url);
		    	Const.URL = url;
		    	((MemoLinearLayout) getActivity().findViewById(R.id.drawer_left)).getMemoList();
		      return true;
		    }
		});
		
		WebSettings mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		
		mWebView.loadUrl(Const.URL);
		Const.URL = "http://m.daum.net";
		
		return view;
	}
	

}
