package com.inha.stickyonpage;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class BrowsingWebView extends Fragment {

	ActionBar mActionBar;
	WebView mWebView;
	Button mButton, mCancelButton;
	EditText mText;
	TextView mStickyCount;
	static Activity mActivity = null;
	DrawerLayout mDrawerLayout;
	
	public static Activity getBrowserActivity() {
		return mActivity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View view = inflater.inflate(R.layout.webview, container, false);
		
		mActivity = getActivity();
		mDrawerLayout = (DrawerLayout)mActivity.findViewById(R.id.drawer_layout);
		mStickyCount = (TextView)view.findViewById(R.id.browser_sticky_count);
		mButton = (Button)view.findViewById(R.id.browser_button);
		mCancelButton = (Button)view.findViewById(R.id.browser_cancel);
		mText = (EditText)view.findViewById(R.id.browser_url);
		
		mText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				// Load url when ENTER is pressed
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					loadURL();
					return true;
				}
				
				return false;
			}
		});
		mText.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mCancelButton.setVisibility(View.VISIBLE);
				mCancelButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mText.setText("");
					}
				});
				
				return false;
			}
		});
		
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadURL();
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
		    	((MemoLinearLayout) getActivity().findViewById(R.id.drawer_left)).getMemoList(0);
		    	mText.setText(url);
		    }
		    
		    @Override
		    public void onPageFinished(WebView view, String url) {
		    	if(getActivity() != null)
		    		((UrlLinearLayout) getActivity().findViewById(R.id.drawer_right)).getUrlList();
		    }
		});
		
		WebSettings mSettings = mWebView.getSettings();
		mSettings.setJavaScriptEnabled(true);
		
		// Set up the first URL to load
		String url = Const.URL;
		Bundle bundle = this.getArguments();

		if (bundle != null) {
			if (bundle.containsKey(Const.MEMO_URL_FROM_MEMO_LIST)) {
				url = bundle.getString(Const.MEMO_URL_FROM_MEMO_LIST);
			} else if (bundle.containsKey(Const.MEMO_URL_FROM_ACTIONBAR)) {
				url = bundle.getString(Const.MEMO_URL_FROM_ACTIONBAR);
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
					if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT)) {
						mDrawerLayout.closeDrawer(Gravity.RIGHT);
					}
					String url = ((TextView) arg1.findViewById(R.id.recommend_url)).getText().toString();
					mWebView.loadUrl(url);
			}
		});
		
		return view;
	}
	
	private void loadURL() {
		InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mText.getWindowToken(),0);
		
		String url = mText.getText().toString();
		if (!url.equals("")) {
			if(!url.startsWith("http://"))
				url = "http://" + url;
		} else {
			url = Const.URL;
		}
		
		mText.clearFocus();
		mCancelButton.setVisibility(View.INVISIBLE); // invisible
		mWebView.loadUrl(url);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.browsing_menu, menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		
		switch(item.getItemId()) {
	   		case R.id.browsing_next:   		
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
				
				if(mDrawerLayout.isDrawerOpen(Gravity.RIGHT) || mDrawerLayout.isDrawerOpen(Gravity.LEFT)) {
					mDrawerLayout.closeDrawer(Gravity.RIGHT);
					mDrawerLayout.closeDrawer(Gravity.LEFT);
				}

		   		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				RecentStickyView stickyFragment = new RecentStickyView();
		        ft.replace(R.id.drawer_main, stickyFragment, "RecentStickyView");
		        ft.commit();
		   		return true;
		   	default:
		   		return super.onOptionsItemSelected(item);
	   	}
    }
}
