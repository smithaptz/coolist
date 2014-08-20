package com.count2v.coolist.shop;

import java.util.ArrayList;
import java.util.List;

import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;

class ShopListItem {
	private BaseActivity baseActivty;
	private ShopData shopData;
	private List<ShopItemData> shopItemList;
	private boolean shopAvailable;
	private int minRequiredPoints;
	
	public ShopListItem(BaseActivity baseActivty, ShopData shopData, List<ShopItemData> shopItemList) {
		this.baseActivty = baseActivty;
		this.shopData = shopData;
		this.shopItemList = new ArrayList<ShopItemData>(shopItemList);
		
		initialize();
	}
	
	private void initialize() {
		int minItemPoints = Integer.MAX_VALUE;
		for(ShopItemData item : shopItemList) {
			minItemPoints = Math.min(minItemPoints, item.getPoints());
		}
		
		shopAvailable = shopData.isAvailable() && !shopData.isNotUsed();
		
		if(shopItemList.size() == 0) {
			shopAvailable = false;
			minRequiredPoints = 0;
		} else {
			minRequiredPoints = minItemPoints;
		}
		
	}
	
	public long getShopId() {
		return shopData.getShopId();
	}
	
	public String getName() {
		return shopData.getName();
	}
	
	public String getDescription() {
		return shopData.getDescription();
	}
	
	public String getDescriptionUrl() {
		return shopData.getDescriptionUrl();
	}
	
	public String getIconUrl() {
		return shopData.getLogoUrl();
	}
	
	
	public int getMinRequiredPoints() {
		return minRequiredPoints;
	}
	
	public boolean isAvailable() {
		int userPoints = baseActivty.getUserPoints();
		
		return shopAvailable && (userPoints >= minRequiredPoints);
	}
	
	@Override
	public String toString() {
		return "[getShopId: " + getShopId() + ", getName: " + getName() + ", getDescription: " + getDescription() + 
				", getDescriptionUrl: " + getDescriptionUrl() + ", getIconUrl: " + getIconUrl() + ", getMinRequiredPoints: " + 
				getMinRequiredPoints() + ", isAvailable: " + isAvailable() + "]";
	}

}
