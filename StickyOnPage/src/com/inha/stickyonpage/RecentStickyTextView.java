package com.inha.stickyonpage;

import java.util.Random;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.TextView;

public class RecentStickyTextView extends TextView {

	private int drawableCount = 3;
	
	public RecentStickyTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		int[] drawableArray = {R.drawable.recentsticky_bg01, R.drawable.recentsticky_bg02, R.drawable.recentsticky_bg03};

		Random random = new Random();
		int rdIdx = random.nextInt(drawableCount);
		this.setBackgroundResource(drawableArray[rdIdx]);
	}
}
