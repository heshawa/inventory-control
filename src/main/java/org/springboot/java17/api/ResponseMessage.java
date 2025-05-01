package org.springboot.java17.api;

public class ResponseMessage {
	private final String message;
	private final String description;
	
	public ResponseMessage(String message, String description) {
		this.message = message;
		this.description = description;
	}

	public String getMessage() {
		return message;
	}

	public String getDescription() {
		return description;
	}
}
