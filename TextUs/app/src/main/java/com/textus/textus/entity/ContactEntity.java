package com.textus.textus.entity;

import java.util.List;

/**
 * Hai Nguyen - 5/25/16.
 */
public class ContactEntity extends BaseEntity {

	String firstName;
	String lastName;
	String businessName;
	String number;
	String type;
	List<ContactEntity> numbers;

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

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

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<ContactEntity> getNumbers() {
		return numbers;
	}

	public void setNumbers(List<ContactEntity> numbers) {
		this.numbers = numbers;
	}
}
