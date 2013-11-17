package com.inha.stickyonpage;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.inha.stickyonpage.db.Sticky;


public class MemoAdapter extends ArrayAdapter<Sticky> {
	
	LayoutInflater inflater;
	List<Sticky> stickies;
	int resource;
	
	public MemoAdapter(Context context, int resource, List<Sticky> stickies) {
		super(context, resource, stickies);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.stickies = stickies;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView tv; 
		
		if(convertView == null)
			convertView = inflater.inflate(resource, parent, false);
		
		tv = (TextView)convertView.findViewById(R.id.memo);
		
		if(position == 0){
			tv.setTextSize(70);
			tv.setGravity(Gravity.CENTER);
			tv.setText("+");
		}
		else if(position % 4 == 1 || position % 4 == 2)
		{
			convertView.setBackgroundColor(Color.parseColor("#F9FFB8"));
			tv.setText(stickies.get(position).getMemo());
			tv.setTextSize(15);
			tv.setGravity(Gravity.TOP);
		}else{
			convertView.setBackgroundColor(Color.parseColor("#C3FFB8"));
			tv.setText(stickies.get(position).getMemo());
			tv.setTextSize(15);
			tv.setGravity(Gravity.TOP);
		}
		
		return convertView;
	}

}
