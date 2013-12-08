package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.HelperClass;
import com.inha.stickyonpage.db.Sticky;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

	private Activity mActivity;
	private RecentStickyAdapter mAdapter;
	private Button mButton;
	private Context mContext;
	private ListView mListView;
	private ScrollView mScrollView;
	private List<Sticky> mStickyList;
	private EditText mUrl;
	private TextView mNoFriend, mStatistics;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recentsticky_list, container, false);
		
		mActivity = getActivity();
		mContext = getActivity().getApplicationContext();
		getRecentStickyAsyncTask();
		
		Const.URL = Const.HOME_URL;
		
		// Set action bar
		setHasOptionsMenu(true);
		ActionBar mActionBar = (mActivity).getActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
		
		// Set list view
		mNoFriend = (TextView)view.findViewById(R.id.scroll_text);
		mStatistics = (TextView)view.findViewById(R.id.scroll_statistics);
		
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
				//TextView memoText = (TextView)view.findViewById(R.id.memo_textview);
				//TextView urlText = (TextView)view.findViewById(R.id.url_textview);
				
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				i.putExtra(Const.MEMO_POSITION, 1);
				i.putExtra(Const.MEMO_INFO, mStickyList.get(position));
				(mActivity).startActivity(i);
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
		mUrl = (EditText)mView.findViewById(R.id.recent_actionview_url);
		mButton = (Button)mView.findViewById(R.id.recent_actionview_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String url = mUrl.getText().toString();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.recent_logout:
			Intent i = new Intent(mContext, LoginActivity.class);
			startActivity(i);
			mActivity.finish();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void getRecentStickyAsyncTask(){
		new RecentStickyAsyncTask(false).execute(new Integer[]{0});
	}
	
	private class RecentStickyAsyncTask extends AsyncTask<Integer, Integer, Integer>{
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		UserProfile mProfile;
		boolean isDialog;
		int myStickyCount, stickyCount, urlCount;
		
		RecentStickyAsyncTask(boolean isDialog){
			this.isDialog = isDialog;
		}
		
		protected void onPreExecute() {
			mDBConnectionModule = DBConnectionModule.getInstance();
			mProfile = UserProfile.getInstacne(mContext);
			
			if (isDialog) {
				showProgress();
			}
		};
		
		@Override
		protected Integer doInBackground(Integer... params) {
			Connection conn;
			HashSet<String> friendsList = mProfile.getFriendsList();
			mStickyList = new ArrayList<Sticky>();
			
			try {
				conn = mDBConnectionModule.getConnection();
				Iterator<String> iterator = friendsList.iterator();
				
				// Get sticky list. Friends' and mine will be included
				Sticky sticky = mDBConnectionModule.getLatestSticky(mProfile.getUserId(), conn);
				if (sticky != null) {
					mStickyList.add(sticky);
				}
				
				while (iterator.hasNext()) {
					sticky = mDBConnectionModule.getLatestSticky(iterator.next(), conn);
					if (sticky != null) {
						mStickyList.add(sticky);
					}
				}
				
				// Set statistics
				myStickyCount = mDBConnectionModule.getUserStickyCount(mProfile.getUserId(), conn);
				stickyCount = mDBConnectionModule.getCFCount("Sticky", conn);
				urlCount = mDBConnectionModule.getCFCount("URL", conn);
				
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

			if (mStickyList.size() != 0) {
				mNoFriend.setVisibility(View.GONE);
				mAdapter = new RecentStickyAdapter(getActivity(), R.layout.recentsticky, mStickyList);
				mListView.setAdapter(mAdapter);
			} else {
				mListView.setVisibility(View.INVISIBLE);
				mNoFriend.setVisibility(View.VISIBLE);
			}
			
			mStatistics.setText(setStatistics(myStickyCount, stickyCount, urlCount));
		}
		
		private String setStatistics(int myStickyCount, int stickyCount, int urlCount) {
			String str = "";
			if (myStickyCount > 1) {
				str += "You've made " + myStickyCount + " stickies.\n";
			} else {
				str += "You've made " + myStickyCount + " sticky.\n";
			}
			
			if (stickyCount > 1) {
				str += stickyCount + " stickies spread over ";
			} else {
				str += stickyCount + " sticky spreads over ";
			}
			
			if (urlCount > 1) {
				str += urlCount + " pages.";
			} else {
				str +=urlCount + " page.";
			}
			
			return str;
		}
		
		private void showProgress() {
			mDialog = new ProgressDialog(getActivity());
			mDialog.setTitle("Sticky On Page");
			mDialog.setMessage("잠시만 기다려 주세요.");
			mDialog.setCancelable(false);
			mDialog.show();
		}
		
		private void hideProgress(){
			mDialog.cancel();
		}
	}
}
