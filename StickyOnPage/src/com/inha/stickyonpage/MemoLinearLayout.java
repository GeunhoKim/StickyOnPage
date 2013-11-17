package com.inha.stickyonpage;

import java.sql.Connection;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.Sticky;


public class MemoLinearLayout extends LinearLayout {

	Context mContext;
	TextView goodOrder, dateOrder;
	LinearLayout ll;
	GridView mGridView;
	
	
	public MemoLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public MemoLinearLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}
	
	public void init(Context context) {
		
		mContext = context;
		String infService = mContext.LAYOUT_INFLATER_SERVICE;
		LayoutInflater li = (LayoutInflater) mContext.getSystemService(infService);
		
		View v = li.inflate(R.layout.memo_read, this, false);
		addView(v);
		
		 ll = (LinearLayout)v.findViewById(R.id.memo_layout);
	     ll.setOnClickListener(new OnClickListener() {
				
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
					Toast.makeText(mContext, "touchtouch!!", Toast.LENGTH_LONG).show();
			}
		});
	     
	    mGridView = (GridView)v.findViewById(R.id.gridView1);
	    mGridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				i.putExtra(Const.MEMO_POSITION, position);
				i.putExtra(Const.MEMO_CONTENTS, ((TextView)view).getText());
				((Activity) mContext).startActivityForResult(i, Const.MEMO_REFRESH_CODE);
			}
	    });
	   
//	    mGridView.setAdapter(new MemoAdapter(mContext, R.layout.memo, null, null, to, flags));
	    
//	    String []sample = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
//	
//	    mGridView.setAdapter(new MemoAdapter(mContext, R.layout.memo, sample));
	    
	   
	    goodOrder = (TextView)v.findViewById(R.id.goodOrder);
	    goodOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		dateOrder = (TextView)v.findViewById(R.id.dateOrder);
		dateOrder.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}
	
	public void getMemoList(){
		new MemoListAsyncTask(false).execute(new Integer[]{0});
	}
	
	private class MemoListAsyncTask extends AsyncTask<Integer, Integer, Integer>{

		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		List<Sticky> stickies;
		
		MemoListAsyncTask(boolean isDialog){
			this.isDialog = isDialog;
		}
		
		protected void onPreExecute() {
			mDBConnectionModule = DBConnectionModule.getInstance();
			if(isDialog)
				showProgress();
		};
		
		@Override
		protected Integer doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			switch(params[0]){
				case 0: // order by date
					Connection conn;
					try {
						conn = mDBConnectionModule.getConnection();
						stickies = mDBConnectionModule.getAllStickies(Const.URL, conn);
			
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1: // order by good
					break;
			}
			return params[0];
		}
	
		@Override
		protected void onPostExecute(Integer result) {
			// TODO Auto-generated method stub
			if(isDialog)
				hideProgress();
			
			switch(result){
				case 0: // order by date
					mGridView.setAdapter(new MemoAdapter(mContext, R.layout.memo, stickies));
					break;
				case 1: // order by good
					break;
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
