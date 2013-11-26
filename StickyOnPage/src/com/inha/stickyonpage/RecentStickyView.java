package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.List;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.Sticky;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecentStickyView extends Fragment {

	private RecentStickyAdapter mAdapter;
	private Button mButton;
	private Context mContext;
	private ListView mListView;
	private ScrollView mScrollView;
	private List<Sticky> mStickyList;
	private EditText mText;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recentsticky_list, container, false);
				
		mContext = getActivity();
		
		setHasOptionsMenu(true);
		ActionBar mActionBar = ((Activity)mContext).getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		 
		mScrollView = (ScrollView)view.findViewById(R.id.scroll_view);
		mListView = (ListView)view.findViewById(R.id.list_view);
		mListView.setBackgroundColor(Color.WHITE);
		mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

		mListView.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				mScrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		} );

		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				TextView memoText = (TextView)view.findViewById(R.id.memo_textview);
				TextView urlText = (TextView)view.findViewById(R.id.url_textview);
				
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				i.putExtra(Const.MEMO_POSITION, 5); //i.putExtra(Const.MEMO_POSITION, 1);
				i.putExtra(Const.MEMO_CONTENTS, memoText.getText());
				i.putExtra(Const.MEMO_URL_FROM_MEMO_READ, urlText.getText());
				((Activity) mContext).startActivityForResult(i, Const.MEMO_REFRESH_CODE);
			}
		});

		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				TextView urlText = (TextView)view.findViewById(R.id.url_textview);
				Bundle bundle = new Bundle();
				bundle.putString(Const.MEMO_URL_FROM_MEMO_LIST, urlText.getText().toString());
				
				BrowsingWebView browsingFragment = new BrowsingWebView();
				browsingFragment.setArguments(bundle);
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.drawer_main, browsingFragment, "BrowsingWebView");
		        ft.commit();
		        
				return true;
			}
		});

		return view;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.recentsticky_menu, menu);
		
		View mView = menu.findItem(R.id.recent_search).getActionView();
		mText = (EditText)mView.findViewById(R.id.recent_actionview_url);
		mButton = (Button)mView.findViewById(R.id.recent_actionview_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = mText.getText().toString();
				BrowsingWebView browsingFragment = new BrowsingWebView();
				
				if (!url.equals("")) {
					if (!url.startsWith("http://")) {
						url = "http://" + url;
					}
					
					Bundle bundle = new Bundle();
					bundle.putString(Const.MEMO_URL_FROM_ACTIONBAR, url);
					browsingFragment.setArguments(bundle);
			        
				}
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				ft.replace(R.id.drawer_main, browsingFragment, "BrowsingWebView");
		        ft.commit();
			}
		});
		
	}
	
	/*
	 @Override
     public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	default:
    		return super.onOptionsItemSelected(item);
    	}
    }
    */
	
	public void getRecentStickyAsyncTask(){
		new RecentStickyAsyncTask(false).execute(new Integer[]{0});
	}
	
	private class RecentStickyAsyncTask extends AsyncTask<Integer, Integer, Integer>{
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		
		RecentStickyAsyncTask(boolean isDialog){
			this.isDialog = isDialog;
		}
		
		protected void onPreExecute() {
			mDBConnectionModule = DBConnectionModule.getInstance();
			
			if (isDialog) {
				showProgress();
			}
		};
		
		@Override
		protected Integer doInBackground(Integer... params) {
			Connection conn;
			try {
				conn = mDBConnectionModule.getConnection();
				//mStickyList = mDBConnectionModule.getAllStickies("http://m.daum.net/", conn);
				mStickyList = mDBConnectionModule.getAllStickies(conn);
			} catch (Exception e) {
				e.printStackTrace();
			}	

			return params[0];
		}
	
		@Override
		protected void onPostExecute(Integer result) {
			if (isDialog) {
				hideProgress();
			}

			mAdapter = new RecentStickyAdapter(getActivity(), R.layout.recentsticky, mStickyList);
			mListView.setAdapter(mAdapter);
		}
		
		private void showProgress() {
			mDialog = new ProgressDialog(getActivity());
			mDialog.setTitle("Sticky On Page");
			mDialog.setMessage("잠시만 기다려 주세요");
			mDialog.setCancelable(false);
			mDialog.show();
		}
		
		private void hideProgress(){
			mDialog.cancel();
		}
	}
}
