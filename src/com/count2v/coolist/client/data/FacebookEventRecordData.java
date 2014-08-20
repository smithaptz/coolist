package com.count2v.coolist.client.data;

public class FacebookEventRecordData {
	private long id;
	private long advertisementId;
	private String pageId;
	private String postId;
	private int type;
	private int points;
	private String timeStamp;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setPageId(String pageId) {
		this.pageId = pageId;
	}
	
	public void setPostId(String postId) {
		this.postId = postId;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public long getId() {
		return id;
	}
	
	public long getAdvertisementId() {
		return advertisementId;
	}
	
	public String getPageId() {
		return pageId;
	}
	
	public String getPostId() {
		return postId;
	}
	
	public int getType() {
		return type;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String toString() {
		return "[id: " + id + ", advertisementId: " + advertisementId + ", pageId: " + pageId + ", postId: " + postId + 
				"type: " + type + ", points: " + points + ", timeStmap: " + timeStamp + "]";
	}
}
