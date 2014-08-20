package com.count2v.coolist.client.data;

import com.count2v.coolist.client.FacebookAdvertisementEventType;

public class FacebookAdvertisementEventData {
	private long facebookAdvertisementEventId;
	private long advertisementId;
	private long advertiserId;
	private String pageId;
	private String postId;
	private int type;
	private int points;
	private boolean done;
	
	public void setFacebookAdvertisementEventId(long facebookAdvertisementEventId) {
		this.facebookAdvertisementEventId = facebookAdvertisementEventId;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setAdvertiserId(long advertiserId) {
		this.advertiserId = advertiserId;
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
		this.points = (points > 0) ? points : 0;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public long getFacebookAdvertisementEventId() {
		return facebookAdvertisementEventId;
	}
	
	public long getAdvertisementId() {
		return advertisementId;
	}
	
	public long getAdverterId() {
		return advertiserId;
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
	
	public boolean isDone() {
		return done;
	}
	
	public boolean isTypeValid() {
		for(int vaildType : FacebookAdvertisementEventType.TYPES) {
			if(getType() == vaildType) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[facebookAdvertisementEventId: " + facebookAdvertisementEventId + ", advertisementId: " + advertisementId + 
				", advertiserId: " + advertiserId + ", pageId: " + pageId + ", postId: " + postId + ", type: " + type + 
				", points: " + points + ", done: " + done + "]";
	}
	
}
