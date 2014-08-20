package com.count2v.coolist.client.data;



public class AdvertisementData {
	private long advertisementId;
	private long advertiserId;
	private String advertisementName;
	private String pictureUrl;
	private String websiteUrl;
	private String iconUrl;
	private String description;
	private long popularity;
	
	private String startTime;
	private String endTime;
	
	private boolean read;
	private boolean available;
	
	private int readTimes;
	
	private int gameType;
	private int gamePoints;
	
	

	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setAdvertiserId(long advertiserId) {
		this.advertiserId = advertiserId;
	}
	
	public void setAdvertisementName(String advertisementName) {
		this.advertisementName = advertisementName;
	}
	
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setGameType(int gameType) {
		this.gameType = gameType;
	}
	
	public void setGamePoints(int gamePoints) {
		this.gamePoints = (gamePoints > 0) ? gamePoints : 0;
	}
	
	public void setRead(boolean read) {
		this.read = read;
	}

	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public void setReadTimes(int readTimes) {		
		this.readTimes = (readTimes > 0) ? readTimes : 0;
	}
	
	public void setPopularity(long popularity) {
		this.popularity = popularity;
	}
	
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	
	public long getAdvertisementId() {
		return advertisementId;
	}
	
	public long getAdvertiserId() {
		return advertiserId;
	}
	
	public String getAdvertisementName() {
		return advertisementName;
	}
	
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public int getGameType() {
		return gameType;
	}
	
	public int getGamePoints() {
		return gamePoints;
	}
	
	public boolean isRead() {
		return read;
	}
	
	public boolean isAvailable() {
		return available;
	}
	
	public int getReadTimes() {
		return readTimes;
	}
	
	public long getPopularity() {
		return popularity;
	}
	
	public String getStartTime() {
		return startTime;
	}
	
	public String getEndTime() {
		return endTime;
	}
	

	
	@Override
	public String toString() {
		return "[advertisementId: " + advertisementId + ", advertiserId: " + advertiserId + ", advertisementName: " + 
				advertisementName + ", pictureUrl: " + pictureUrl + ", websiteUrl: " + websiteUrl + ", iconUrl: " + iconUrl + 
				", decription: " + description + ", gameType: " + gameType + ", gamePoints: " + gamePoints + ", read:" + read + 
				", availalbe: " + available + ", readTimes: " + readTimes + ", popularity: " + popularity + ", startTime: " + 
				startTime + ", endTime: " + endTime + "]";
	}
}
