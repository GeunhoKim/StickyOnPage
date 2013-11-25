package com.inha.stickyonpage;

import java.util.TreeMap;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class UrlAdapter extends ArrayAdapter<String> {

	String[] urlList;
	Double[] similarityList;
	int mResource;
	LayoutInflater inflater;
	Context mContext;
	
	public UrlAdapter(Context context, int resource, TreeMap<Double, String> recommendList) {
		super(context, resource, recommendList.values().toArray(new String[recommendList.size()]));
		// TODO Auto-generated constructor stub
	
		urlList = recommendList.values().toArray(new String[getCount()]);
		similarityList = recommendList.keySet().toArray(new Double[getCount()]);
		
		mContext = context;
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mResource = resource;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		int mPosition = getCount()-position-1;
		TextView url, similarity; 
		
		if(convertView == null)
			convertView = inflater.inflate(mResource, parent, false);
		
		url = (TextView) convertView.findViewById(R.id.recommend_url);
		url.setText(urlList[mPosition]);
		
		similarity = (TextView) convertView.findViewById(R.id.similarity);
		similarity.setText((int)(similarityList[mPosition]*100)+"%");
		
		if(similarityList[mPosition]*100 > 50)
			url.setTextColor(Color.parseColor("#FF9828"));
		else
			url.setTextColor(Color.parseColor("#666666"));
		
		return convertView;
	}
	
	
}
