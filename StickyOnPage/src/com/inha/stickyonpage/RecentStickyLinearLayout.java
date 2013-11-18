package com.inha.stickyonpage;

import java.util.Random;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecentStickyLinearLayout extends LinearLayout {
	private int drawableCount = 3;
	/*
	private TextView idTextView;
    private TextView text2;
    private TextView text3;
    */
	public RecentStickyLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int[] drawableArray = {R.drawable.recentsticky_bg01, R.drawable.recentsticky_bg02, R.drawable.recentsticky_bg03};

		Random random = new Random();
		int rdIdx = random.nextInt(drawableCount);
		this.setBackgroundResource(drawableArray[rdIdx]);
		/*
		FrameLayout stickyRight = (FrameLayout)findViewById(R.id.sticky_right);
		stickyRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		*/
	}
}
