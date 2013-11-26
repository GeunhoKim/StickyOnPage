package com.inha.stickyonpage;

import java.sql.Connection;
import java.sql.SQLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inha.stickyonpage.db.DBConnectionModule;

/*
 *  Memo Create, Read, Update, Delete Class
 *
 */

public class MemoCRUDActivity extends Activity {

	Button saveBtn, cancleBtn;
	Context mContext;
	EditText mEditText;
	Intent mIntent;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		mContext = this;
		setContentView(R.layout.memo_write);
		
		// Set Dialog Size
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		mIntent = getIntent();
		int position = mIntent.getIntExtra(Const.MEMO_POSITION, 0);
		
		mEditText = (EditText)findViewById(R.id.editText1);
		
		if(position == 0) {
			// Write memo
		} else if (position == 5) {
			getMemoCRUDAsyncTask(position);
		} else {
			// Read memo
			getMemoCRUDAsyncTask(1);
		}
		
		saveBtn = (Button)findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				getMemoCRUDAsyncTask(0);
				
			}
		});
		
		cancleBtn = (Button)findViewById(R.id.cancleBtn);
		cancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}
	
	public void getMemoCRUDAsyncTask(int num){
		boolean isDialog = false;
		
		switch(num){
			case 0 :
				isDialog = true;	
				break;
			case 1 :
				isDialog = false;
				break;
		}
		new MemoCRUDAsyncTask(isDialog).execute(new Integer[]{num});
	}
	
	private class MemoCRUDAsyncTask extends AsyncTask<Integer, Integer, Integer>{
		/*
		 * Case 0 : Create Memo
		 * Case 1 : Read Memo
		 * Case 2 : Update Memo
		 * Case 3 : Delete Memo
		 * Case 4 : Good Memo
		 * Case 5 : Read Memo from RecentStickyFragment
		*/
		
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		
		MemoCRUDAsyncTask(boolean isDialog){
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
				case 0: // Create Memo
					try {
						String text = mEditText.getText().toString();
						String id = UserProfile.getInstacne(getApplicationContext()).getUserId();
						Connection conn = mDBConnectionModule.getConnection();	
						mDBConnectionModule.writeSticky(Const.URL, id, text, conn);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1: // Read Memo
					mEditText.setText(mIntent.getStringExtra(Const.MEMO_CONTENTS));
					mEditText.setEnabled(false);
					
					saveBtn.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							new MemoCRUDAsyncTask(false).execute(new Integer[]{4});
						}
					});
					break;
				case 2: // Update Memo
					break;
				case 3: // Delete Memo
					break;
				case 4: // Good Memo
					try {
						Connection conn = mDBConnectionModule.getConnection();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					String id = UserProfile.getInstacne(getApplicationContext()).getUserId();
					//mDBConnectionModule.addPreference(id, f_id, Const.URL, conn);
					break;
				case 5: // Read Memo from RecentStickyFragment
					mEditText.setText(mIntent.getStringExtra(Const.MEMO_CONTENTS));
					mEditText.setEnabled(false);

					saveBtn.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(mContext, MainActivity.class);
							intent.putExtra(Const.MEMO_URL_FROM_MEMO_READ, mIntent.getStringExtra(Const.MEMO_URL_FROM_MEMO_READ));
							((Activity) mContext).startActivity(intent);
						}
					});
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
				case 0: // Create Memo
	            	setResult(RESULT_OK);
					finish();
					break;
				case 1: // Read Memo
					saveBtn.setText("Good");
					break;
				case 2: // Update Memo
					finish();
					break;
				case 3: // Delete Memo
					finish();
					break;
				case 4: // Good Memo
					break;
				case 5: // Read Memo from RecentStickyFragment
					saveBtn.setText("Go to webpage");
					break;
			}
		}
		
		private void showProgress() {
			// TODO Auto-generated method stub
			mDialog = new ProgressDialog(MemoCRUDActivity.this);
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
