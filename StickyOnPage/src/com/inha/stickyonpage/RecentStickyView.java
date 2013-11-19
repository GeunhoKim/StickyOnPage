package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.List;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.Sticky;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

public class RecentStickyView extends Fragment {
	private ListView mListView;
	private List<Sticky> mStickyList;
	private RecentStickyAdapter mAdapter;
	private Button mButton;
	private ScrollView mScrollView;
	private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recentsticky, container, false);
				
		mContext = getActivity();
		
		mButton = (Button)view.findViewById(R.id.recentsticky_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				BrowsingWebView browsingFragment = new BrowsingWebView();
		        ft.replace(R.id.drawer_main, browsingFragment, "BrowsingWebView");
		        ft.commit();
			}
		});

		mScrollView = (ScrollView)view.findViewById(R.id.scroll_view);
		mListView = (ListView)view.findViewById(R.id.list_view);
		mListView.setBackgroundColor(Color.WHITE);
		mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);

		mListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mScrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		} );

		mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				TextView mTextView = (TextView)view.findViewById(R.id.memo_textview);
				i.putExtra(Const.MEMO_POSITION, position);
				i.putExtra(Const.MEMO_CONTENTS, mTextView.getText());
				((Activity) mContext).startActivityForResult(i, Const.MEMO_REFRESH_CODE);
				return false;
			}
		});

		return view;
	}
	
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

			mAdapter = new RecentStickyAdapter(getActivity(), R.layout.stickyview, mStickyList);
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
