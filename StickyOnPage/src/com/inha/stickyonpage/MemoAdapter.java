package com.inha.stickyonpage;

import java.util.Date;
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
		
		init();
	}
	
	public void init(){
		stickies.add(0, new Sticky("", "", "", "", new Date(Long.MAX_VALUE), Integer.MAX_VALUE));
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		TextView memo_contents, memo_info; 
		
		if(convertView == null)
			convertView = inflater.inflate(resource, parent, false);
		
		memo_contents = (TextView)convertView.findViewById(R.id.memo);
		memo_info = (TextView)convertView.findViewById(R.id.memo_info);
		
		float ratio = (float) stickies.get(position).getLike() / (float)10;
		if(ratio > 1) ratio = 1; 
		int red = (int) (Color.red(Color.parseColor("#FFE87F")) * ratio  +  Color.red(Color.parseColor("#FFFFBF")) * (1-ratio));
		int green = (int) (Color.green(Color.parseColor("#FFE87F")) * ratio + Color.green(Color.parseColor("#FFFFBF")) * (1-ratio));
		int blue = (int) (Color.blue(Color.parseColor("#FFE87F")) * ratio + Color.blue(Color.parseColor("#FFFFBF")) * (1-ratio));
		
		
		if(position == 0){
			convertView.setBackgroundColor(Color.parseColor("#FFFFBF"));
			memo_info.setText(null);
			memo_contents.setTextSize(70);
			memo_contents.setGravity(Gravity.CENTER);
			memo_contents.setText("+");
		} else if(position % 4 == 1 || position % 4 == 2) {
			convertView.setBackgroundColor(Color.rgb(red, green, blue));
			memo_info.setText(makeTitle(position));
			memo_contents.setText(makeContent(position));
			memo_contents.setTextSize(15);
			memo_contents.setGravity(Gravity.TOP);
		} else {
			convertView.setBackgroundColor(Color.rgb(red, green, blue));
			memo_info.setText(makeTitle(position));
			memo_contents.setText(makeContent(position));
			memo_contents.setTextSize(15);
			memo_contents.setGravity(Gravity.TOP);
		}
		
		return convertView;
	}
	
	private String makeTitle(int position) {
		StringBuffer sb = new StringBuffer();
		
		sb.append(stickies.get(position).getTimestamp())
		.append(" ")
		.append(stickies.get(position).getUserName());
		
		return sb.toString();
	}
	
	private String makeContent(int position) {
		StringBuffer sb = new StringBuffer(stickies.get(position).getMemo());		
		return sb.toString();
	}

}
