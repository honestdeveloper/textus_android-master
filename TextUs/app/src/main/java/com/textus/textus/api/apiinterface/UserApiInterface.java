package com.textus.textus.api.apiinterface;

import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.UserEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Hai Nguyen - 11/3/15.
 */
public interface UserApiInterface {

	@GET("users/current")
	Call<UserEntity> doLogin();

	@POST("opt_outs")
	Call<BaseEntity> optOutUser(@Query("phone") String phone);

	@GET("opt_outs")
	Call<List<BaseEntity>> getOptOut(@Query("q") String phone);

	@DELETE("opt_outs/{id}")
	Call<BaseEntity> deleteOpt(@Path("id") long id, @Query("id") long optId);

	@PUT("contacts/{id}")
	Call<UserEntity> editContact(@Path("id") long id,
			@Query("phone") String phone,
			@Query("first_name") String firstName,
			@Query("last_name") String lastName,
			@Query("business_name") String name);

	@POST("contacts")
	Call<UserEntity> addContact(@Query("phone") String phone,
			@Query("first_name") String firstName,
			@Query("last_name") String lastName,
			@Query("business_name") String name);

	@GET("contacts")
	Call<List<UserEntity>> importContact(@Query("q") String phoneNum);

	@POST("contacts")
	Call<UserEntity> importContact(@Query("phone") String phoneNum,
			@Query("first_name") String firstName,
			@Query("last_name") String lastName,
			@Query("business_name") String businessName);
}
