package com.count2v.coolist.client.data;

public class VersionData {
	private long id;
	private int currentVersionCode;
	private int minimumVersionCode; 
	private String currentVersionName;
	private String minimumVersionName; 
	private String downloadUrl;
	private String announcement;
	private String publishDate;
	
	public void setId(long id) {
		this.id = id;
	}
	
	public void setCurrentVersionCode(int currentVersionCode) {
		this.currentVersionCode = currentVersionCode;
	}
	
	public void setMinimumVersionCode(int minimumVersionCode) {
		this.minimumVersionCode = minimumVersionCode;
	}
	
	public void setCurrentVersionName(String currentVersionName) {
		this.currentVersionName = currentVersionName;
	}
	
	public void setMinimumVersionName(String minimumVersionName) {
		this.minimumVersionName = minimumVersionName;
	}
	
	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}
	
	public void setAnnouncement(String announcement) {
		this.announcement = announcement;
	}
	
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	
	public long getId() {
		return id;
	}
	
	public int getCurrentVersionCode() {
		return currentVersionCode;
	}
	
	public int getMinimumVersionCode() {
		return minimumVersionCode;
	}
	
	public String getCurrentVersionName() {
		return currentVersionName;
	}
	
	public String getMinimumVersionName() {
		return minimumVersionName;
	}
	
	
	public String getDownloadUrl() {
		return downloadUrl;
	}
	
	public String getAnnouncement() {
		return announcement;
	}
	
	public String getPublishDate() {
		return publishDate;
	}
	
	@Override
	public String toString() {
		return "[id: " + id + ", currentVersionCode" + currentVersionCode + ", minimumVersionCode: " + minimumVersionCode +
				", currentVersionName: " + currentVersionName + ", minimumVersionName: " + minimumVersionName + 
				", downloadUrl: " + downloadUrl + ", announcement: " + announcement + ", publishDate: " + publishDate + "]";
	}
	

}
