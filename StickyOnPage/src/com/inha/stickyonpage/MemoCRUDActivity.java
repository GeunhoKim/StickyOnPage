package com.inha.stickyonpage;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MemoCRUDActivity extends Activity {

	Button saveBtn, cancleBtn;
	TextView tv;
	EditText et;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		setContentView(R.layout.memo_write);
		
		getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

		Intent i = getIntent();
		int position = i.getIntExtra(Const.MEMO_POSITION, 0);
		
		et = (EditText)findViewById(R.id.editText1);
		
		if(position == 0) {
		
			
		} else {
			et.setText(i.getStringExtra(Const.MEMO_CONTENTS));
			et.setEnabled(false);
		}
		
		saveBtn = (Button)findViewById(R.id.saveBtn);
		saveBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// Save Database NoSQL
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
}
