package com.count2v.coolist.client.data;

public class AdvertiserData {
	private long advertiserId;
	private String name;
	private String websiteUrl;
	private String pageUrl;
	private String logoUrl;
	private String description;
	
	public void setAdvertiserId(long advertiserId) {
		this.advertiserId = advertiserId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public void setPageUrl(String pageUrl) {
		this.pageUrl = pageUrl;
	}
	
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public long getAdvertiserId() {
		return advertiserId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	
	public String getPageUrl() {
		return pageUrl;
	}
	
	public String getLogoUrl() {
		return logoUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	@Override
	public String toString() {
		return "[advertiserId: " + advertiserId + ", name: " + name + ", websiteUrl: " + websiteUrl + 
				", pageUrl: " + pageUrl + ", logoUrl: " + logoUrl + ", description: " + description + "]";
	}
}
