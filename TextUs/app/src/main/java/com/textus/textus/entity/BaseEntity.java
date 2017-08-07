package com.textus.textus.entity;

import java.io.Serializable;

/**
 * Hai Nguyen - 8/27/15.
 */
public class BaseEntity implements Serializable {

	private long id;
	private String error;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getError() {
		return error;
	}
}
