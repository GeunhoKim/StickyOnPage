package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.HelperClass;
import com.inha.stickyonpage.db.Sticky;

public class MemoLinearLayout extends LinearLayout {

	Context mContext;
	TextView goodOrder, dateOrder;
	LinearLayout ll;
	GridView mGridView;
	List<Sticky> stickies;
	MemoAdapter mMemoAdpater;
	
	public MemoLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public MemoLinearLayout(Context context) {
		super(context);
		init(context);
	}
	
	public void init(Context context) {
		mContext = context;
		String infService = mContext.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) mContext.getSystemService(infService);
		
		View v = li.inflate(R.layout.memo_read, this, false);
		addView(v);
		
		 ll = (LinearLayout)v.findViewById(R.id.memo_layout);
	   
	    mGridView = (GridView)v.findViewById(R.id.gridView1);
	    mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				i.putExtra(Const.MEMO_POSITION, position);
				i.putExtra(Const.MEMO_INFO, stickies.get(position));
				((Activity) mContext).startActivityForResult(i, Const.MEMO_REFRESH_CODE);
			}
	    });
	    
	
	    goodOrder = (TextView)v.findViewById(R.id.goodOrder);
	    goodOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				goodOrder.setTextColor(Color.parseColor("#C3FFB8"));
				dateOrder.setTextColor(Color.parseColor("#ffffff"));
				getMemoList(2);
			}
		});
		
		dateOrder = (TextView)v.findViewById(R.id.dateOrder);
		dateOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dateOrder.setTextColor(Color.parseColor("#C3FFB8"));
				goodOrder.setTextColor(Color.parseColor("#ffffff"));
				getMemoList(1);
			}
		});
		
	}
	
	public void getMemoList(int num){
		new MemoListAsyncTask(false).execute(new Integer[]{num});
	}
	
	private class MemoListAsyncTask extends AsyncTask<Integer, Integer, Integer>{
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		TextView mStickyCount;
		
		MemoListAsyncTask(boolean isDialog){
			this.isDialog = isDialog;
		}
		
		protected void onPreExecute() {
			Activity browsingActivity = BrowsingWebView.getBrowserActivity();
			mStickyCount = (TextView)browsingActivity.findViewById(R.id.browser_sticky_count);

			mDBConnectionModule = DBConnectionModule.getInstance();
			if(isDialog)
				showProgress();
		};
		
		@Override
		protected Integer doInBackground(Integer... params) {
			switch(params[0]){
				case 0: // first load
					Connection conn;
					try {
						HashSet<String> friendsList = UserProfile.getInstacne(mContext).getFriendsList();
						Iterator<String> it = friendsList.iterator();
						
						conn = mDBConnectionModule.getConnection();
						
						stickies = mDBConnectionModule.getStickies(Const.URL, UserProfile.getInstacne(mContext).getUserId(), conn);
						while(it.hasNext()) {
							stickies.addAll(mDBConnectionModule.getStickies(Const.URL, it.next(), conn));
						}
						Collections.sort(stickies);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1: // order by date
					Collections.sort(stickies);
					break;
				case 2: // order by like
					stickies = HelperClass.sortStickyByLike(stickies);
					break;
			}
			return params[0];
		}
	
		@Override
		protected void onPostExecute(Integer result) {
			mStickyCount.setText(String.valueOf(stickies.size()));
			
			if(isDialog)
				hideProgress();
			
			switch(result){
				case 0: 
					dateOrder.setTextColor(Color.parseColor("#C3FFB8"));
					goodOrder.setTextColor(Color.parseColor("#ffffff"));
					mMemoAdpater = new MemoAdapter(mContext, R.layout.memo, stickies);
					mGridView.setAdapter(mMemoAdpater);
					break;
				case 1: // order by date
					mMemoAdpater.notifyDataSetChanged();
					break;
				case 2: // order by like
					mMemoAdpater.notifyDataSetChanged();
					break;
			}
		}
		
		private void showProgress() {
			// TODO Auto-generated method stub
			mDialog = new ProgressDialog(mContext);
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
