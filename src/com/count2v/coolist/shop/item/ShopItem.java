package com.count2v.coolist.shop.item;


import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;

public class ShopItem {
	
	private BaseActivity baseActivity;
	private ShopData shopData;
	private ShopItemData itemData;
	
	public ShopItem(BaseActivity baseActivity, ShopData shopData, ShopItemData itemData) {
		this.baseActivity = baseActivity;
		this.shopData = shopData;
		this.itemData = itemData;
	}
	
	protected BaseActivity getBaseActivity() {
		return baseActivity;
	}
	
	protected ShopData getShopData() {
		return shopData;
	}
	
	protected ShopItemData getShopItemData() {
		return itemData;
	}
	
	public long getShopItemId() {
		return itemData.getShopItemId();
	}
	
	public String getName() {
		return itemData.getName();
	}
	
	public String getDescription() {
		return itemData.getDescription();
	}
	
	public String getIconUrl() {
		return itemData.getIconUrl();
	}
	
	public int getPoints() {
		return itemData.getPoints();
	}
	
	/*
	 * 是否可以交易，包含使用者點數是否足夠
	 */
	
	public boolean isAvailable() {
		return isOnService() && isUserAfford();
	}
	
	/*
	 * 是否正常服務
	 */
	
	public boolean isOnService() {
		if(!shopData.isAvailable() || shopData.isNotUsed()) {
			return false;
		} else if(!itemData.isAvailable() || itemData.isNotUsed()) {
			return false;
		} else if(itemData.getCount() == 0) {
			return false;
		}
		
		
		return true;
	}
	
	/*
	 * 使用者是否符合交易條件
	 */
	
	public boolean isUserAfford() {
		int userPoints = getBaseActivity().getUserPoints();
		
		return userPoints >= getPoints();
	}
	

	
	@Override
	public String toString() {
		return "[getShopItemId: " + getShopItemId() + ", getName: " + getName() + ", getDescription: " + getDescription() + 
				", getIconUrl: " + getIconUrl() + ", getPoints: " + getPoints() + ", isAvailable: " + isAvailable() + "]";
	}
}
