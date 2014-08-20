package com.count2v.coolist.client.data;

public class ConsumptionRecordData {
	private long id;
	private long shopId;
	private int points;
	private String timeStamp;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setShopId(long shopId) {
		this.shopId = shopId;
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
	
	public long getShopId() {
		return shopId;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String toString() {
		return "id: " + id + ", shopId: " + shopId + ", points: " + points + ", timeStmap: " + timeStamp;
	}

}
