package com.textus.textus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;

/**
 * 1/19/15.
 */
public class CustomEditText extends EditText {

	public CustomEditText(Context context) {
		super(context);
		setFont();
	}

	public CustomEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setFont();
	}

	public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setFont();
	}

	public void setFont() {

		// if (isInEditMode()) {
		//
		// return;
		// }
		//
		// int style = 0;
		// Typeface typeFace = getTypeface();
		// if (typeFace != null) {
		//
		// style = typeFace.getStyle();
		// }
		//
		// switch (style) {
		// case 1:
		//
		// typeFace = Typeface.createFromAsset(getContext().getAssets(),
		// "Roboto-Bold.ttf");
		// break;
		// case 2:
		//
		// typeFace = Typeface.createFromAsset(getContext().getAssets(),
		// "Roboto-LightItalic.ttf");
		// break;
		// case 3:
		//
		// typeFace = Typeface.createFromAsset(getContext().getAssets(),
		// "Roboto-BoldItalic.ttf");
		// break;
		// default:
		//
		// typeFace = Typeface.createFromAsset(getContext().getAssets(),
		// "Roboto-Light.ttf");
		// break;
		// }
		//
		// setTypeface(typeFace);
	}
}
