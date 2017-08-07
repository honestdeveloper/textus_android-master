package com.textus.textus.constant;

import android.widget.ImageView;

/**
 * Hai Nguyen - 8/27/15.
 */
public class Constants {

	public static final String BASE_URL = "https://app.textus.com/api/";

	public static int INTENT_REQUEST_PERMISSION = 1001;
	public static ImageView imgView;
	// Pref
	public static final String PREF_USER_ID = "user_id";
	public static final String PREF_ACC_ID = "account_id";
	public static final String PREF_USERNAME = "username";
	public static final String PREF_PASSWORD = "password";
	public static final String PREF_IS_APP_ACTIVE = "is_app_active";

	// Intent
	public static final String INTENT_IS_NEW = "new_contact";
	public static final String INTENT_NEW_MESSAGE = "new_message";
	public static final String INTENT_SAVE_CONTACT = "save_contact";
	public static final String INTENT_TEMPLATE_CONTENT = "template_content";
	public static final String INTENT_TEMPLATE_SELECTED = "template_selected";
	public static final String INTENT_NEW_MESSAGE_CONTENT = "new_message_content";
}
