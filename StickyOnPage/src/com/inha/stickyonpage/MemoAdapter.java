package com.inha.stickyonpage;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class MemoAdapter extends ArrayAdapter<String> {
	
	LayoutInflater inflater;
	String[] array;
	
	public MemoAdapter(Context context, int resource, String[] objects) {
		super(context, resource, objects);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		array = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv; 
		
		if(convertView == null)
			convertView = inflater.inflate(R.layout.memo, parent, false);
		
		tv = (TextView)convertView.findViewById(R.id.memo);
		
		if(position == 0){
			tv.setTextSize(70);
			tv.setGravity(Gravity.CENTER);
			tv.setText("+");
		}
		else if(position % 4 == 1 || position % 4 == 2)
		{
			convertView.setBackgroundColor(Color.parseColor("#F9FFB8"));
			tv.setText(array[position]);
			tv.setTextSize(15);
			tv.setGravity(Gravity.TOP);
		}else{
			convertView.setBackgroundColor(Color.parseColor("#C3FFB8"));
			tv.setText(array[position]);
			tv.setTextSize(15);
			tv.setGravity(Gravity.TOP);
		}
		
		return convertView;
	}
}
