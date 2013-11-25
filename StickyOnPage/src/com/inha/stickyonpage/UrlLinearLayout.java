package com.inha.stickyonpage;

import java.util.TreeMap;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.inha.stickyonpage.db.DBConnectionModule;

public class UrlLinearLayout extends LinearLayout {

	Context mContext;
	LinearLayout mLinearLayout;
	ListView mListView;
	TextView mTextView;
	
	
	public UrlLinearLayout(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
		init(context);
	}
	
	public UrlLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	private void init(Context context) {
		
		mContext = context;
		String infService = mContext.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) mContext.getSystemService(infService);
		
		View v = li.inflate(R.layout.url_recommend, this, false);
		addView(v);
		
		mLinearLayout = (LinearLayout) v.findViewById(R.id.urlLinearLayout);
		mListView = (ListView) v.findViewById(R.id.urlListView);
		mTextView = (TextView) v.findViewById(R.id.urlTextView);
		
	}
	
	public void getUrlList() {
		new UrlListAsyncTask(false).execute(new Integer[]{0});
	}

	private class UrlListAsyncTask extends AsyncTask<Integer, TreeMap<Double, String>, TreeMap<Double, String>> {
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		TreeMap<Double, String> recommend_list;
		
		public UrlListAsyncTask(boolean isDialog) {
			// TODO Auto-generated constructor stub
			this.isDialog = isDialog;
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			mDBConnectionModule = DBConnectionModule.getInstance();
			if(isDialog)
				showProgress();
		}
	
		@Override
		protected TreeMap<Double, String> doInBackground(Integer... params) {
			// TODO Auto-generated method stub
				
			try {
				recommend_list = (TreeMap<Double, String>) mDBConnectionModule.getRecommendation(Const.URL);
				
				if(recommend_list.isEmpty()) {
					recommend_list = new TreeMap<Double, String>();
					
					recommend_list.put(0.1213, "http://m.naver.com");
					recommend_list.put(0.212312, "http://daum.net");
					recommend_list.put(0.3123, "http://nate.com");
					recommend_list.put(0.4111, "http://google.com");
					recommend_list.put(0.12443, "http://m.gmarket.com");
					recommend_list.put(0.242112, "http://m.auction.com");
					recommend_list.put(0.3733, "http://cafe.naver.com");
					recommend_list.put(0.4811, "http://igec.inha.ac.kr");
					recommend_list.put(0.1544, "http://cse.inha.ac.kr");
					recommend_list.put(0.80222, "http://inha.ac.kr");
					
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			return recommend_list;
		}
		
		@Override
		protected void onPostExecute(TreeMap<Double, String> result) {
			// TODO Auto-generated method stub
			if(isDialog)
				hideProgress();

			if(result != null) {
				mListView.setAdapter(new UrlAdapter(mContext, R.layout.url_listview_item, result));
			}
		}
		
		private void showProgress() {
			// TODO Auto-generated method stub
			mDialog = new ProgressDialog(mContext);
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
