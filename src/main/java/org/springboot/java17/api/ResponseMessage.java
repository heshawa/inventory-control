package org.springboot.java17.api;

import java.util.ArrayList;
import java.util.List;

public class ResponseMessage<T> {
	private final String message;
	private boolean success = true;
	
	private List<T> data;
	
	public ResponseMessage(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		success = success;
	}

	public List<T> getData() {
		if(data == null){
			data = new ArrayList();
		}
		return data;
	}

	public void setData(List<T> data) {
		this.data = data;
	}
}
