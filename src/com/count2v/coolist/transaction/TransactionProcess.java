package com.count2v.coolist.transaction;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.RequestUIHandler;


abstract class TransactionProcess {
	private static final SimpleDateFormat sdFormat = 
			new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
	
	private TransactionActivity transactionActivity;
	
	private ClientRequest<Integer> userPointsRequest;
	
	private ShopData shopData;
	private ShopItemData shopItemData;
	
	private String transactionTime;
	
	private boolean onUnconfirmStauts = false;
	

	public TransactionProcess(TransactionActivity transactionActivity, ShopData shopData, ShopItemData shopItemData) {
		this.transactionActivity = transactionActivity;
		this.shopData = shopData;
		this.shopItemData = shopItemData;
		
		initialzie();
	}
	
	private void initialzie() {
		userPointsRequest = ServerCommunicator.requestGetUserPoints(userPointsListener);
	}
	
	protected TransactionActivity getTransactionActivity() {
		return transactionActivity;
	}
	
	protected ShopData getShopData() {
		return shopData;
	}
	
	protected ShopItemData getShopItemData() {
		return shopItemData;
	}
	
	public int getPoints() {
		return shopItemData.getPoints();
	}
	
	public boolean isUserAfford() {
		int userPoints = getTransactionActivity().getUserPoints();
		
		return userPoints >= getPoints();
	}
	
	public boolean isOnService() {
		if(!shopData.isAvailable() || shopData.isNotUsed()) {
			return false;
		}
		
		if(!shopItemData.isAvailable() || shopItemData.isNotUsed()) {
			return false;
		} else if(shopItemData.getCount() == 0) {
			return false;
		}
		
		return true;
	}
	
	public String getTime() {
		return sdFormat.format(new Date());
	}
	
	protected void showWaitingDialog() {
		getTransactionActivity().showWaitingDialog();
	}
	
	protected void dismissWaitingDialog() {
		getTransactionActivity().dismissWaitingDialog();
	}
	
	protected void setTransactionUnconfirm(String transactionTime) {
		showWaitingDialog();
		onUnconfirmStauts = true;
		ServerCommunicator.execute(userPointsRequest);
	}
	
	protected void setTransactionSuccess(String serialNum, String transactionTime) {
		this.transactionTime = transactionTime;
		
		getTransactionActivity().transactionSuccess(serialNum, transactionTime);
		setTransactionFinish();
	}
	
	protected void setTransactionFail(String result) {
		getTransactionActivity().transactionFail(result);
		setTransactionFinish();
	}
	
	private void setTransactionFinish() {
		dismissWaitingDialog();
		
		// 因為在setTransactionUnconfirm已經先執行了
		if(onUnconfirmStauts) {
			ServerCommunicator.execute(userPointsRequest);
		}
	}
	
	protected void setContentDetailView(View view) {
		getTransactionActivity().setContentDetailView(view);
	}
	
	
	public abstract void startTransaction();
	
	public boolean waitForAnotherActivityResult() {
		return false;
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		// null
	}
	
	public String getSuccessConfirmMessage() {
		return "是否離開兌換明細？";
	}
	
	private ServerCommunicator.Callback<Integer> userPointsListener = new ServerCommunicator.Callback<Integer>() {

		@Override
		public void onComplete(Integer element, int statusCode) {
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				
				int previousUserPoints = getTransactionActivity().getUserPoints();
				int currentUserPoints = element;
				
				Log.d(this.getClass().toString(), "current user points: " + element);
				getTransactionActivity().setUserPoints(currentUserPoints);
				
				if(onUnconfirmStauts) {
					if(previousUserPoints > currentUserPoints) {
						setTransactionSuccess("", transactionTime);
					} else {
						setTransactionFail("");
					}
					onUnconfirmStauts = false;
				}
			} else if(statusCode == StatusType.CONNECTION_SUCCESS || 
					statusCode == StatusType.RECEIVED_NULL_DATA) {
				Log.d(this.getClass().toString(), "timeout or received null data");
				
				ServerCommunicator.execute(userPointsRequest);
				ServerCommunicator.postErrorMessage(userPointsRequest, statusCode);
			} else {
				RequestUIHandler.showErrorDialog(getTransactionActivity(), userPointsRequest, statusCode);
			}
			
		}
		
	};
	
}
