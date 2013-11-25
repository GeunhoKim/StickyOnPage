package com.inha.stickyonpage;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class RecentStickyActionView extends LinearLayout {

	private Button mButton;
	private EditText mText;
	
	public RecentStickyActionView(Context context) {
		super(context);

		mText = (EditText)findViewById(R.id.recent_actionview_url);
		mButton = (Button)findViewById(R.id.recent_actionview_button);

		mButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BrowsingWebView browsingFragment = new BrowsingWebView();
				
				String url = mText.getText().toString();
				if (url != null && !url.equals(Const.URL)) {
					Bundle savedInstanceState = new Bundle();
					savedInstanceState.putString("url", url);
					browsingFragment.setArguments(savedInstanceState);
				}
				
				/*
				FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		        ft.replace(R.id.drawer_main, browsingFragment, "BrowsingWebView");
		        ft.commit();
		        */
			}
		});
	}
}
