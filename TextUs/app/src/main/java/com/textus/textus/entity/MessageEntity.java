package com.textus.textus.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 12/27/15.
 */
public class MessageEntity extends BaseEntity {

	private String title;
	private String status;
	private String content;

	@SerializedName("deliver_at")
	private String deliverAt;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("updated_at")
	private String updatedAt;

	@SerializedName("sender_type")
	private String senderType;

	@SerializedName("sender_name")
	private String senderName;

	@SerializedName("sender_phone")
	private String senderPhone;

	@SerializedName("broadcast_id")
	private String broadcastId;

	@SerializedName("receiver_name")
	private String receiverName;

	@SerializedName("receiver_type")
	private String receiverType;

	@SerializedName("receiver_phone")
	private String receiverPhone;

	public String getTitle() {
		return title;
	}

	private boolean read;

	@SerializedName("sender_id")
	private long senderId;

	@SerializedName("receiver_id")
	private long receiverId;

	public String getStatus() {
		return status;
	}

	public String getContent() {
		return content;
	}

	public String getDeliverAt() {
		return deliverAt;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public String getSenderType() {
		return senderType;
	}

	public String getSenderName() {
		return senderName;
	}

	public String getSenderPhone() {
		return senderPhone;
	}

	public String getBroadcastId() {
		return broadcastId;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public String getReceiverType() {
		return receiverType;
	}

	public String getReceiverPhone() {
		return receiverPhone;
	}

	public boolean isRead() {
		return read;
	}

	public void setRead(boolean read) {
		this.read = read;
	}

	public long getSenderId() {
		return senderId;
	}

	public long getReceiverId() {
		return receiverId;
	}
}
