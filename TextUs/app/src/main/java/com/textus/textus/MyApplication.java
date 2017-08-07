package com.textus.textus;

import android.app.Application;

import com.onesignal.OneSignal;

/**
 * Hai Nguyen - 8/28/15.
 */
public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Parse.com
		// Parse.initialize(this, PARSE_ID, PARSE_KEY);
		// ParseInstallation.getCurrentInstallation().saveInBackground();
		OneSignal.startInit(this).init();
	}
}
