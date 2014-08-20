package com.count2v.coolist.client.data;

public class ServerAnnouncementData {
	private long id;
	private String announcement;
	private String timeStamp;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public long getId() {
		return id;
	}
	
	public String getAnnouncement() {
		return announcement;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String toString() {
		return "[id: " + id + ", announcement: " + announcement + ", timeStamp: " + timeStamp + "]";
	}
}
