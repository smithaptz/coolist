package com.count2v.coolist.client.data;

public class ShopItemData {
	private long shopItemId;
	private long shopId;
	
	private String name;
	private String description;
	private String iconUrl;
	
	private int type;
	private int points;
	private int count;
	
	private boolean available;
	private boolean notUsed;
	
	public void setShopItemId(long shopItemId) {
		this.shopItemId = shopItemId;
	}
	
	public void setShopId(long shopId) {
		this.shopId = shopId;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public void setIconUrl(String iconUrl) {
		this.iconUrl = iconUrl;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public void setPoints(int points) {
		this.points = points;
	}
	
	public void setCount(int count) {
		this.count = count;
	}
	
	public void setAvailable(boolean available) {
		this.available = available;
	}
	
	public void setNotUsed(boolean notUsed) {
		this.notUsed = notUsed;
	}
	
	public long getShopItemId() {
		return shopItemId;
	}
	
	public long getShopId() {
		return shopId;
	}
	
	public String getName() {
		return name;
	}
	
	public String getDescription() {
		return description;
	}
	
	public String getIconUrl() {
		return iconUrl;
	}
	
	public int getType() {
		return type;
	}
	
	public int getPoints() {
		return points;
	}
	
	public int getCount() {
		return count;
	}
	
	public boolean isAvailable() {
		return available && !notUsed;
	}
	
	public boolean isNotUsed() {
		return notUsed;
	}
	
	@Override
	public String toString() {
		return "[shopItemId: " + shopItemId + ", shopId: " + shopId + ", name: " + name + 
				", description: " + description + ", iconUrl: " + iconUrl + ", type: " + type +
				", points: " + points + ", count: " + count + ", available: " + available + 
				", notUsed: " + notUsed + "]";
	}
} 
