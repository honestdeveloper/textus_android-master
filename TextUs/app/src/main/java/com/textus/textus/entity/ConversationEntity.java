package com.textus.textus.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Hai Nguyen - 12/27/15.
 */
public class ConversationEntity extends BaseEntity {

	private UserEntity contact;
	private List<MessageEntity> messages;

	private boolean read;
	private String content;

	@SerializedName("deliver_at")
	private String deliverAt;

	@SerializedName("sender_id")
	private long senderId;

	@SerializedName("sender_phone")
	private String senderPhone;

	@SerializedName("sender_name")
	private String senderName;

	@SerializedName("receiver_id")
	private long receiverId;

	@SerializedName("receiver_phone")
	private String receiverPhone;

	@SerializedName("receiver_name")
	private String receiverName;

	@SerializedName("image_url")
	private String imageUrl;

	private String lastName;
	private String firstName;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public UserEntity getContact() {
		return contact;
	}

	public List<MessageEntity> getMessages() {
		return messages;
	}

	public boolean isRead() {
		return read;
	}

	public String getContent() {
		return content;
	}

	public String getDeliverAt() {
		return deliverAt;
	}

	public long getSenderId() {
		return senderId;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public String getSenderName() {
		return senderName;
	}

	public long getReceiverId() {
		return receiverId;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getImageUrl() {return imageUrl;}

	public void setImageUrl(String imageUrl) {this.imageUrl = imageUrl;}
}
