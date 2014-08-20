package com.count2v.coolist.shop.item;


import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;

class NullItem extends ShopItem {

	public NullItem(BaseActivity baseActivity, ShopData shopData,
			ShopItemData itemData) {
		super(baseActivity, shopData, itemData);
	}
	
	@Override
	public boolean isOnService() {
		return false;
	}

}
