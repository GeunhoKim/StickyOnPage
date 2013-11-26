package com.inha.stickyonpage;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class BrowsingWebView extends Fragment {

	ActionBar mActionBar;
	WebView mWebView;
	Button mButton;
	EditText mText;
	Activity mActivity;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.webview, container, false);
		mActivity = getActivity();
		
		mText = (EditText)view.findViewById(R.id.browser_url);
		mButton = (Button)view.findViewById(R.id.browser_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = mText.getText().toString();
				if (!url.equals("")) {
					if (!url.startsWith("http://")) {
						url = "http://" + url;
					}
				} else {
					url = Const.URL;
				}
				
				mWebView.loadUrl(url);
			}
		});
		
		mWebView = (WebView)view.findViewById(R.id.webView1);
		mWebView.setHorizontalScrollBarEnabled(false);
		mWebView.setVerticalScrollBarEnabled(false);
		mWebView.setFocusable(true);
		mWebView.setWebViewClient(new WebViewClient() {
		    @Override
		    public void onPageStarted(WebView view, String url, Bitmap favicon) {
		    	Const.URL = url;
		    	((MemoLinearLayout) getActivity().findViewById(R.id.drawer_left)).getMemoList();
		    	mText.setText(url);
		    }
		    
		    @Override
		    public void onPageFinished(WebView view, String url) {
		    	Const.URL = url;
		    	if(getActivity() != null)
		    		((UrlLinearLayout) getActivity().findViewById(R.id.drawer_right)).getUrlList();
		    }
		});
		
		WebSettings mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		
		// Set up URL to load
		String url = Const.URL;
		Bundle bundle = this.getArguments();

		if (bundle != null) {
			if (bundle.containsKey(Const.MEMO_URL_FROM_MEMO_LIST)) {
				url = bundle.getString(Const.MEMO_URL_FROM_MEMO_LIST);
			} else if (bundle.containsKey(Const.MEMO_URL_FROM_ACTIONBAR)) {
				url = bundle.getString(Const.MEMO_URL_FROM_ACTIONBAR);
				System.out.println("url = " + url);
			} 
		}
		mWebView.loadUrl(url);
		
		// Action Bar
		setHasOptionsMenu(true);
		mActionBar = mActivity.getActionBar();
		View mActionBarView = inflater.inflate(R.layout.action_bar, null);
		mActionBar.setCustomView(mActionBarView);
		mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		
		final ImageButton prevButton = (ImageButton)mActivity.findViewById(R.id.browser_prev);
		prevButton.setBackgroundResource(R.drawable.browser_prev_select);
		prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				DrawerLayout mDrawerLayout = (DrawerLayout)mActivity.findViewById(R.id.drawer_layout);
		   		
		   		if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
		   			mDrawerLayout.closeDrawer(Gravity.RIGHT);
		   			mDrawerLayout.openDrawer(Gravity.LEFT);
		   		} else if (mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
		   			mDrawerLayout.closeDrawer(Gravity.LEFT);
		   		}
			}
		});
		
		// Recommend URL
		ListView mListView = (ListView) getActivity().findViewById(R.id.urlListView);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
					String url = ((TextView) arg1.findViewById(R.id.recommend_url)).getText().toString();
					mWebView.loadUrl(url);
			}
		});
		
		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.browsing_menu, menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
	   	switch(item.getItemId()) {
		   	case R.id.browsing_next:
		   		DrawerLayout mDrawerLayout = (DrawerLayout)mActivity.findViewById(R.id.drawer_layout);
		   		
		   		if (!mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
		   			mDrawerLayout.closeDrawer(Gravity.LEFT);
		   			mDrawerLayout.openDrawer(Gravity.RIGHT);
		   		} else if (mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
		   			mDrawerLayout.closeDrawer(Gravity.RIGHT);
		   		}
		   		return true;
		   	case R.id.browsing_home:
		   		// reset action view
		   		mActionBar.setCustomView(null);
				mActionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME);
		   		
				Const.URL = Const.HOME_URL;
		   		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				RecentStickyView stickyFragment = new RecentStickyView();
				stickyFragment.getRecentStickyAsyncTask();
		        ft.replace(R.id.drawer_main, stickyFragment, "RecentStickyView");
		        ft.commit();
		   		return true;
		   	default:
		   		return super.onOptionsItemSelected(item);
	   	}
   }
}
