package com.count2v.coolist.client.data;

public class ServerStatusData {
	private long id;
	private int status;
	private String url;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getId() {
		return id;
	}
	
	public int getStatus() {
		return status;
	}
	
	public String getUrl() {
		return url;
	}
	
	@Override
	public String toString() {
		return "[id: " + id + ", status: " + status + ", url: " + url + "]";
	}
}
