package com.inha.stickyonpage;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.Button;

public class TwitterLoginButton extends Button {
	
	private String loginText;

	/**
	 * Create the LoginButton by inflating from XML
	 */
	public TwitterLoginButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		if (attrs.getStyleAttribute() == 0) {
			this.setGravity(Gravity.CENTER);
            this.setTextColor(getResources().getColor(R.color.twitter_loginview_text_color));
            this.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    getResources().getDimension(R.dimen.twitter_loginview_text_size));
            this.setTypeface(Typeface.DEFAULT_BOLD);

            /*
            if (isInEditMode()) {
                this.setBackgroundColor(getResources().getColor(R.color.twitter_blue));
                loginText = "Log in with Twitter";
            } else {
            */
                this.setBackgroundResource(R.drawable.twitter_button_blue);
                this.setCompoundDrawablesWithIntrinsicBounds(R.drawable.twitter_inverse_icon, 0, 0, 0);
                this.setCompoundDrawablePadding(
                        getResources().getDimensionPixelSize(R.dimen.twitter_loginview_compound_drawable_padding));
                this.setPadding(getResources().getDimensionPixelSize(R.dimen.twitter_loginview_padding_left),
                        getResources().getDimensionPixelSize(R.dimen.twitter_loginview_padding_top),
                        getResources().getDimensionPixelSize(R.dimen.twitter_loginview_padding_right),
                        getResources().getDimensionPixelSize(R.dimen.twitter_loginview_padding_bottom));
            //}
		}
	}
}
