package com.textus.textus.api;

import com.textus.textus.api.apiinterface.UserApiInterface;

/**
 * Hai Nguyen - 11/3/15.
 */
public class UserApi extends BaseApi {

	private UserApiInterface mInterface;

	public UserApi() {

		mInterface = mRetrofit.create(UserApiInterface.class);
	}

	public UserApiInterface getInterface() {
		return mInterface;
	}
}
