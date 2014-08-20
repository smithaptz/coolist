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
		setTransactionFail("�L�k�T�{�ӫ~���O\n���O�N�X�G" + data.getType());
		
		String errorMsg = "�������I�����O, shop_item_id: " + data.getShopItemId() + ", type: " + data.getType();
		ServerCommunicator.postErrorMessage(errorMsg, StatusType.UNKNOWN_TRANSACTION_TYPE);
	}

}
