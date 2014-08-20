package com.count2v.coolist.client.data;

import java.util.HashMap;
import java.util.Map;

public class TransactionResultData {
	private boolean success;
	private int errorCode;
	
	private String log;
	private String serialCode;
	
	private int type;
	
	private HashMap<String, String> extraInfoMap;
	
	
	public void setSuccess(boolean success) {
		this.success = success;
	}
	

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public void setLog(String log) {
		this.log = log;
	}
	
	public void setSerialCode(String serialCode) {
		this.serialCode = serialCode;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setExtraInfoMap(Map<String, String> extraInfoMap) {
		this.extraInfoMap = new HashMap<String, String>(extraInfoMap);
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
	
	public String getSerialCode() {
		return serialCode;
	}
	
	public int getType() {
		return type;
	}
	
	public Map<String, String> getExtraInfoMap() {
		return new HashMap<String, String>(extraInfoMap);
	}
	
	@Override
	public String toString() {
		return "[success: " + success + ", errorCode: " + errorCode + ", log: "+ log + ", serialCode: " + serialCode + 
				", type: " + type + ", extraInfoMap: " + extraInfoMap + "]";
	}
}
