package com.inha.stickyonpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(mContext, MemoCRUDActivity.class);
				i.putExtra(Const.MEMO_POSITION, position);
				i.putExtra(Const.MEMO_CONTENTS, ((TextView)arg1).getText());
				mContext.startActivity(i);
			}
	    });
	   
//	    mGridView.setAdapter(new MemoAdapter(mContext, R.layout.memo, null, null, to, flags));
	    
	    String []sample = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
	
	    mGridView.setAdapter(new MemoAdapter(mContext, R.layout.memo, sample));
	    
	   
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
}
