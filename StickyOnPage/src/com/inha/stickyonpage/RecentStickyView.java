package com.inha.stickyonpage;

import java.util.ArrayList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;

public class RecentStickyView extends Fragment {
	private ListView mListView;
	private ArrayList<String> mList;
	private ArrayAdapter<String> mAdapter;
	private Button mButton;
	private ScrollView mScrollView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.recentsticky, container, false);
				
		mButton = (Button)view.findViewById(R.id.recentsticky_button);
		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
				BrowsingWebView browsingFragment = new BrowsingWebView();
		        ft.replace(R.id.drawer_main, browsingFragment);
		        ft.commit();
			}
		});
		
		mList = new ArrayList<String>();
		for (int i = 0; i < 100; i++) {
			if (i % 3 == 0)
				mList.add("1");
			else if (i % 3 == 1)
				mList.add("2");
			else
				mList.add("3");
		}
		
		mAdapter = new ArrayAdapter<String>(getActivity(), R.layout.stickyview, mList);
		
		mScrollView = (ScrollView)view.findViewById(R.id.scroll_view);
		
		mListView = (ListView)view.findViewById(R.id.list_view);
		mListView.setBackgroundColor(Color.WHITE);
		mListView.setAdapter(mAdapter);
		mListView.setChoiceMode(ListView.CHOICE_MODE_NONE);
		mListView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mScrollView.requestDisallowInterceptTouchEvent(true);
				return false;
			}
		} );

		return view;
	}
}
