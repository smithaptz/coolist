package com.count2v.coolist.client.data;

public class AdvertisementRecordData {
	private long id;
	private long advertisementId;
	private int points;
	private String timeStamp;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
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
	
	public int getPoints() {
		return points;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String toString() {
		return "id: " + id + ", advertisementId: " + advertisementId + ", points: " + points + ", timeStmap: " + timeStamp;
	}

}
