package com.example.demo.model;

public class ReservationConfirmation {

	private String confirmationMessage;
	private Boolean isSuccessFul;
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getConfirmationMessage() {
		return confirmationMessage;
	}

	public void setConfirmationMessage(String confirmationMessage) {
		this.confirmationMessage = confirmationMessage;
	}

	public Boolean getIsSuccessFul() {
		return isSuccessFul;
	}

	public void setIsSuccessFul(Boolean isSuccessFul) {
		this.isSuccessFul = isSuccessFul;
	}

}
