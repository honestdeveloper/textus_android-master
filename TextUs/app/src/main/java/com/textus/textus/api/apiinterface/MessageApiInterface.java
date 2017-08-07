package com.textus.textus.api.apiinterface;

import com.textus.textus.entity.BaseEntity;
import com.textus.textus.entity.ConversationEntity;
import com.textus.textus.entity.MessageEntity;
import com.textus.textus.entity.UserEntity;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Hai Nguyen - 11/3/15.
 */
public interface MessageApiInterface {

	@GET("conversations")
	Call<List<ConversationEntity>> getConversations(@Query("page") int page,
			@Query("per_page") int perPage);

	@GET("contacts")
	Call<List<UserEntity>> searchContacts(@Query("page") int page,
			@Query("per_page") int perPage, @Query("q") String query);

	@GET("messages")
	Call<List<ConversationEntity>> searchMessages(@Query("page") int page,
			@Query("per_page") int perPage, @Query("q") String query);

	@GET("messages/{id}")
	Call<List<ConversationEntity>> getChatHistory(@Path("id") long id,
			@Query("page") int page, @Query("per_page") int perPage,
			@Query("contact_id") long contactId);

	@POST("messages")
	Call<BaseEntity> sendMessage(@Query("content") String msg,
			@Query("sender") long senderId, @Query("receiver") long receiverId);

	@PUT("messages/{id}")
	Call<BaseEntity> markMessageRead(@Path("id") long id,
			@Query("read") String read, @Query("id") long msgId);

	@GET("message_templates")
	Call<List<MessageEntity>> getTemplates(@Query("page") int page,
			@Query("per_page") int perPage);
}
