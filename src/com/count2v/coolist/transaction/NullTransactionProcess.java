package com.count2v.coolist.transaction;

import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;

import android.content.Intent;
public class NullTransactionProcess extends TransactionProcess {

	public NullTransactionProcess(TransactionActivity transactionActivity,
			ShopData shopData, ShopItemData shopItemData) {
		super(transactionActivity, shopData, shopItemData);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void startTransaction() {
		ShopItemData data = getShopItemData();
		TransactionActivity activity = getTransactionActivity();
		setTransactionFail("無法確認商品型別\n型別代碼：" + data.getType());
		
		String errorMsg = "未知的兌換型別, shop_item_id: " + data.getShopItemId() + ", type: " + data.getType();
		ServerCommunicator.postErrorMessage(errorMsg, StatusType.UNKNOWN_TRANSACTION_TYPE);
	}

}
