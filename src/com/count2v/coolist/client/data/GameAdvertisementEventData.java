package com.count2v.coolist.client.data;

public class GameAdvertisementEventData {
	private long gameAdvertisementEventId;
	private long advertisementId;
	private int type;
	private int points;
	private boolean done;
	
	public void setGacebookAdvertisementEventId(long gameAdvertisementEventId) {
		this.gameAdvertisementEventId = gameAdvertisementEventId;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public long getGacebookAdvertisementEventId() {
		return gameAdvertisementEventId;
	}
	
	public long getAdvertisementId() {
		return advertisementId;
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
	
	@Override
	public String toString() {
		return "[gameAdvertisementEventId: " + gameAdvertisementEventId + ", advertisementId: " + advertisementId + 
				", type: " + type + ", points: " + points + ", done: " + done + "]";
	}
}
