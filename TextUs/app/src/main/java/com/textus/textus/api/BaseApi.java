package com.textus.textus.api;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.textus.textus.constant.Constants;
import com.textus.textus.utility.CustomPreferences;
import com.textus.textus.utility.LogUtil;

import java.io.IOException;

/**
 * Hai Nguyen - 8/27/15.
 */
public class BaseApi {

	protected Retrofit mRetrofit;
	public BaseApi() {

		String strUserName = CustomPreferences.getPreferences(
				Constants.PREF_USERNAME, "");
		String strPassword = CustomPreferences.getPreferences(
				Constants.PREF_PASSWORD, "");
        final String credential = Credentials.basic(strUserName, strPassword);
		Gson gson = new GsonBuilder().disableHtmlEscaping().create();
		HttpLoggingInterceptor logging = new HttpLoggingInterceptor(
				new HttpLoggingInterceptor.Logger() {
					@Override
					public void log(String message) {

						LogUtil.e("OkHttp", message);
					}
				});

		logging.setLevel(HttpLoggingInterceptor.Level.BODY);
		OkHttpClient httpClient = new OkHttpClient.Builder()
				.addInterceptor(logging).addInterceptor(new Interceptor() {

					@Override
					public Response intercept(Chain chain) throws IOException {

						Request original = chain.request();
						Request request = original
								.newBuilder()
								.header("Authorization", credential)
								.header("Accept",
										"application/vnd.textus-v2+json")
								.method(original.method(), original.body())
								.build();
						return chain.proceed(request);
					}
				}).build();

		mRetrofit = new Retrofit.Builder().baseUrl(Constants.BASE_URL)
				.client(httpClient)
				.addConverterFactory(GsonConverterFactory.create(gson)).build();
	}
}
