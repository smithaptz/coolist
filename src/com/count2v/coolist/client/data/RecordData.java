package com.count2v.coolist.client.data;

public class RecordData {
	private long recordId;
	private long eventId;
	private int type;
	private int points;
	private String name;
	private String timeStamp;
	
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}
	
	public void setEventId(long eventId) {
		this.eventId = eventId;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	public long getRecordId() {
		return recordId;
	}
	
	public long getEventId() {
		return eventId;
	}
	
	public int getType() {
		return type;
	}
	
	public int getPoints() {
		return points;
	}
	
	public String getName() {
		return name;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	@Override
	public String toString() {
		return "[recordId: " + recordId + ", eventId: " + eventId + ", type: " + type + ", points: " + 
				points + ", name: " + name + ", timeStamp: " + timeStamp + "]";
	}
	
}
