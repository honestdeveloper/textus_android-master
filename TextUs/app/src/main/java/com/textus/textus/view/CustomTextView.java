package com.textus.textus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextView extends TextView {

	public CustomTextView(Context paramContext) {
		super(paramContext);
		setFont();
	}

	public CustomTextView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		setFont();
	}

	public CustomTextView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		setFont();
	}

	public void setFont() {

		// if(isInEditMode()){
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
