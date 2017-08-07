package com.textus.textus.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Hai Nguyen - 12/27/15.
 */
public class UserEntity extends BaseEntity {

	private String email;

	@SerializedName("last_name")
	private String lastName;

	@SerializedName("first_name")
	private String firstName;

	@SerializedName("business_name")
	private String businessName;

	@SerializedName("phone")
	private String phone;

	@SerializedName("account_id")
	private long accountId;

	@SerializedName("created_at")
	private String createdAt;

	@SerializedName("last_sign_in_at")
	private String lastSignInAt;

	@SerializedName("forward_messages")
	private String forwardMessages;

	@SerializedName("should_play_audio")
	private boolean shouldPlayAudio;

	@SerializedName("opted_out")
	private boolean optedOut;

	public String getEmail() {
		return email;
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public String getLastSignInAt() {
		return lastSignInAt;
	}

	public String getForwardMessages() {
		return forwardMessages;
	}

	public boolean isShouldPlayAudio() {
		return shouldPlayAudio;
	}

	public String getBusinessName() {
		return businessName;
	}

	public String getPhone() {
		return phone;
	}

	public boolean isOptedOut() {
		return optedOut;
	}

	public void setOptedOut(boolean optedOut) {
		this.optedOut = optedOut;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public long getAccountId() {
		return accountId;
	}
}
