package com.count2v.coolist.client.data;

public class LoginCheckData extends CheckData {	
	private String userName;
	
	public void setUserName(String userName) {
		this.userName = userName;
	}

	
	public String getUserName() {
		return userName;
	}
	
	
	@Override
	public String toString() {
		return "[success: " + isSuccess() + ", errorCode: " + getErrorCode() + ", log: "+ getLog() + 
				", userName: " + getUserName() + "]";
	}
}
