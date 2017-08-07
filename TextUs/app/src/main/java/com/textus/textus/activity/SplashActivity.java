package com.textus.textus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;

import com.textus.textus.R;
import com.textus.textus.constant.Constants;
import com.textus.textus.utility.CustomPreferences;

public class SplashActivity extends BaseActivity {

	@Override
	protected int addView() {
		return R.layout.activity_splash;
	}

	@Override
	protected void init(@Nullable Bundle savedInstanceState) {
		super.init(savedInstanceState);

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {

				String strUserName = CustomPreferences.getPreferences(
						Constants.PREF_USERNAME, "");
				if (strUserName == null || strUserName.equals("")) {

					startActivity(new Intent(SplashActivity.this,
							LoginActivity.class));
					finish();
					return;
				}

				startActivity(new Intent(SplashActivity.this,
						MainActivity.class));
				finish();
			}
		}, 1000);
	}
}
