package com.inha.stickyonpage;

import java.sql.Connection;
import java.sql.SQLException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.inha.stickyonpage.db.DBConnectionModule;
import com.inha.stickyonpage.db.Sticky;

/*
 *  Memo Create, Read, Update, Delete Class
 *
 */

public class MemoCRUDActivity extends Activity {

	Button saveBtn, cancleBtn;
	EditText mEditText;
	Intent mIntent;
	
	Sticky sticky;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.memo_write);
		
		// Set Dialog Size
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		mIntent = getIntent();
		int position = mIntent.getIntExtra(Const.MEMO_POSITION, 0);
		sticky = (Sticky)mIntent.getSerializableExtra(Const.MEMO_INFO);
		
		mEditText = (EditText)findViewById(R.id.editText1);
		
		
		if(position == 0) {
			// Write memo
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
		*/
		
		ProgressDialog mDialog;
		DBConnectionModule mDBConnectionModule;
		boolean isDialog;
		boolean isGood;
		
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
						String id = UserProfile.getInstance(getApplicationContext()).getUserId();
						String name = UserProfile.getInstance(getApplicationContext()).getUserName();
						Connection conn = mDBConnectionModule.getConnection();
						mDBConnectionModule.writeSticky(Const.URL, id, text, name, conn);
						
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					break;
				case 1: // Read Memo
					mEditText.setText(sticky.getMemo());
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
						String id = UserProfile.getInstance(getApplicationContext()).getUserId();
						Long created = sticky.getTimestamp().getTime();
						isGood = mDBConnectionModule.addPreference(id, sticky.getUserID(), Const.URL, created, conn);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					if(isGood)
						Toast.makeText(MemoCRUDActivity.this, "이 스티키를 추천하였습니다.", Toast.LENGTH_SHORT).show();
					else
						Toast.makeText(MemoCRUDActivity.this, "추천은 한번만 가능합니다.", Toast.LENGTH_SHORT).show();
					break;
			}
		}
		
		private void showProgress() {
			// TODO Auto-generated method stub
			mDialog = new ProgressDialog(MemoCRUDActivity.this);
			mDialog.setTitle("Sticky On Page");
			mDialog.setMessage("�좎떆留�湲곕떎�ㅼ＜�몄슂.");
			mDialog.setCancelable(false);
			mDialog.show();
		}
		
		private void hideProgress(){
			mDialog.cancel();
		}
	}
}
