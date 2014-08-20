package com.count2v.coolist.shop.item;

import com.count2v.coolist.client.TransactionType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;

public class ShopItemFactory {
	public static ShopItem instanceShopItem(BaseActivity baseActivity, ShopData shopData, 
			ShopItemData shopItemData) {
		int type = shopItemData.getType();
		
		switch(type) {
		case TransactionType.SCAN_QRCODE:
			return new ShopItem(baseActivity, shopData, shopItemData);
		case TransactionType.COUPON_IMAGE_TYPE_A:
			return new ShopItem(baseActivity, shopData, shopItemData);
		case TransactionType.COUPON_IMAGE_TYPE_B:
			return new ShopItem(baseActivity, shopData, shopItemData);
		case TransactionType.COUPON_CODE:
			return new ShopItem(baseActivity, shopData, shopItemData);
		}
		
		
		return new NullItem(baseActivity, shopData, shopItemData);
	}
}
