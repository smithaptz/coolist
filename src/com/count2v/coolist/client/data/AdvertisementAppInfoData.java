package com.count2v.coolist.client.data;


public class AdvertisementAppInfoData {
	private long advertisementAppId;
	private long advertisementId;
	private long advertiserId;
	private String androidMarketId;
	private String name;
	private int popuarity;
	private int points;
	private boolean done;
	
	public void setAdvertisementAppId(long advertisementAppId) {
		this.advertisementAppId = advertisementAppId;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setAdvertiserId(long advertiserId) {
		this.advertiserId = advertiserId;
	}
	
	public void setAndroidMarketId(String androidMarketId) {
		this.androidMarketId = androidMarketId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setPopuarity(int popuarity) {
		this.popuarity = popuarity;
	}
	
	public void setPoints(int points) {
		this.points = (points > 0) ? points : 0;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public long getAdvertisementAppId() {
		return advertisementAppId;
	}
	
	public long getAdvertisementId() {
		return advertisementId;
	}
	
	public long getAdverterId() {
		return advertiserId;
	}
	
	public String getAndroidMarketId() {
		return androidMarketId;
	}
	
	public String getName() {
		return name;
	}
	
	
	public int getPopuarity() {
		return popuarity;
	}
	
	public int getPoints() {
		return points;
	}
	
	public boolean isDone() {
		return done;
	}
	
	
	@Override
	public String toString() {
		return "[advertisementAppId: " + advertisementAppId + ", advertisementId: " + advertisementId + 
				", advertiserId: " + advertiserId + ", androidMarketId: " + androidMarketId + ", name: " + name + 
				", popuarity: " + popuarity + ", points: " + points + ", done: " + done + "]";
	}
}
