package com.count2v.coolist.client.data;

public class CheckData {	
	private boolean success;
	private int errorCode;
	private String log;
	
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public void setLog(String log) {
		this.log = log;
	}
	
	public boolean isSuccess() {
		return success;
	}
	

	
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getLog() {
		return log;
	}
	
	@Override
	public String toString() {
		return "[success: " + success + ", errorCode: " + errorCode + ", log: "+ log +"]";
	}
}
