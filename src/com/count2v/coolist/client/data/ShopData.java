package com.count2v.coolist.client.data;

public class ShopData {	
	private long shopId;
	private String name;
	private String logoUrl;
	private String websiteUrl;
	private String phone;
	private String address;
	private String zipCode;
	private String city;
	private String district;
	private String GPSCoordinates ;
	private String mapUrl;
	private String description;
	private String descriptionUrl;
	private boolean available;
	private boolean notUsed;
	private int points;
	private int minPoints;
	
	
	public void setShopId(long shopId) {
		this.shopId = shopId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}
	
	public void setWebsiteUrl(String websiteUrl) {
		this.websiteUrl = websiteUrl;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	
	public void setCity(String city) {
		this.city = city;
	}
	
	public void setDistrict(String district) {
		this.district = district;
	}
	
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public void setGPSCoordinates(String GPSCoordinates) {
		this.GPSCoordinates = GPSCoordinates;
	}
	
	public void setMapUrl(String mapUrl) {
		this.mapUrl = mapUrl;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setDescriptionUrl(String descriptionUrl) {
		this.descriptionUrl = descriptionUrl;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setMinPoints(int minPoints) {
		this.minPoints = minPoints;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public void setNotUsed(boolean notUsed) {
		this.notUsed = notUsed;
	}
	
	public long getShopId() {
		return shopId;
	}
	
	public String getName()	{
		return name;
	}
	
	public String getLogoUrl() {
		return logoUrl;
	}
	
	public String getWebsiteUrl() {
		return websiteUrl;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public String getZipCode() {
		return zipCode;
	}
	
	public String getCity() {
		return city;
	}
	
	public String getDistrict() {
		return district;
	}
	
	public String getAddress() {
		return address;
	}
	
	public String getGPSCoordinates() {
		return GPSCoordinates;
	}
	
	public String getMapUrl() {
		return mapUrl;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getDescriptionUrl() {
		return descriptionUrl;
	}
	
	public int getPoints() {
		return points;
	}
	
	public int getMinPoints() {
		return minPoints;
	}
	
	public boolean isAvailable() {
		return available && !notUsed && (points > minPoints);
	}
	
	public boolean isNotUsed() {
		return notUsed;
	}
	
	
	
	@Override
	public String toString() {
		return 	"[shopId: " + shopId + ", name: " + name + ", logoUrl: " + logoUrl + ", websiteUrl: " + websiteUrl + 
				", description: " + description + ", descriptionUrl: " + descriptionUrl + ", available: " +
				available + " phone: " + phone + ", zipCode: " + zipCode + ", city: " + city + ", district: " + district + 
				", address: " + address + ", GPSCoordinates: " + GPSCoordinates + ", mapUrl: " + mapUrl + ", points: " + 
				points + ", minPoints: " + minPoints + ", notUsed: " + notUsed + "]";
	}

}
