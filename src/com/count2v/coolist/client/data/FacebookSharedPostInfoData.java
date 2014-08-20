package com.count2v.coolist.client.data;

public class FacebookSharedPostInfoData {
	private long facebookAdvertisementEventId;
	private long advertisementId;
	
	private String name;
	private String caption;
	private String description;
	private String url;
	private String pictureUrl;
	private String place;
	private String ref;
	private int popularity;

	
	public void setFacebookAdvertisementEventId(long facebookAdvertisementEventId) {
		this.facebookAdvertisementEventId = facebookAdvertisementEventId;
	}
	
	public void setAdvertisementId(long advertisementId) {
		this.advertisementId = advertisementId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setCaption(String caption) {
		this.caption = caption;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setPictureUrl(String pictureUrl) {
		this.pictureUrl = pictureUrl;
	}
	
	public void setPlace(String place) {
		this.place = place;
	}
	
	public void setRef(String ref) {
		this.ref = ref;
	}
	
	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	
	public long getFacebookAdvertisementEventId() {
		return facebookAdvertisementEventId;
	}
	
	public long getAdvertisementId() {
		return advertisementId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCaption() {
		return caption;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getPictureUrl() {
		return pictureUrl;
	}
	
	public String getPlace() {
		return place;
	}
	
	public String getRef() {
		return ref;
	}
	
	public int getPopularity() {
		return popularity;
	}
	
	@Override
	public String toString() {
		return "[facebookAdvertisementEventId: " + facebookAdvertisementEventId + ", advertisementId: " + advertisementId + 
				", name: " + name + ", caption: " + caption + ", description: " + description + ", url: " + url + 
				", pictureUrl: " + pictureUrl + ", place: " + place + ", ref: " + ref + ", populairty: " + popularity + "]";
	}
}
