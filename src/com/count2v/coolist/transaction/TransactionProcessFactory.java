package com.count2v.coolist.transaction;

import com.count2v.coolist.client.TransactionType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;

class TransactionProcessFactory {
	public static TransactionProcess instanceProcess(TransactionActivity transactionActivity, 
			ShopData shopData, ShopItemData shopItemData) {
		int type = shopItemData.getType();
		
		switch(type) {
		case TransactionType.SCAN_QRCODE:
			return new QRCodeScanTransactionProcess(transactionActivity, shopData, shopItemData);
		case TransactionType.COUPON_IMAGE_TYPE_A:
			return new CouponImageTransactionProcess(transactionActivity, shopData, shopItemData);
		case TransactionType.COUPON_IMAGE_TYPE_B:
			return new CouponImageTransactionProcess(transactionActivity, shopData, shopItemData);
		case TransactionType.COUPON_CODE:
			return new CouponCodeTransactionProcess(transactionActivity, shopData, shopItemData);
		}
		
		return new NullTransactionProcess(transactionActivity, shopData, shopItemData);
	}
}
