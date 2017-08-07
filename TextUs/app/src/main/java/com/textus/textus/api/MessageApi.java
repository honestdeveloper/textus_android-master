package com.textus.textus.api;

import com.textus.textus.api.apiinterface.MessageApiInterface;
import com.textus.textus.api.apiinterface.UserApiInterface;

/**
 * Hai Nguyen - 11/3/15.
 */
public class MessageApi extends BaseApi {

	private MessageApiInterface mInterface;

	public MessageApi() {

		mInterface = mRetrofit.create(MessageApiInterface.class);
	}

	public MessageApiInterface getInterface() {
		return mInterface;
	}
}
