package com.count2v.coolist.core;

import java.util.ArrayList;
import java.util.List;

import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;
import com.count2v.coolist.client.data.FacebookSharedPostInfoData;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.db.DBAccessor;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.util.Log;

public class DBUpdatedService extends Service {

	public static final long UPDATE_INTERVAL = 300 * 1000;
	public static final long TIME_OUT_RESEND_INTERVAL = 3 * 1000;

	private DBAccessor dbAccessor;
	private ArrayList<ClientRequest<?>> requestList;
	
	private ClientRequest<List<ShopData>> getShopRequest;
	private ClientRequest<List<ShopItemData>> getShopItemRequest;
	private ClientRequest<List<AdvertisementData>> getAdvertisementRequest;
	private ClientRequest<List<FacebookAdvertisementEventData>> getFacebookEventRequest;
	private ClientRequest<List<FacebookSharedPostInfoData>> getFacebookSharedPostInfoRequest;
	private ClientRequest<List<AdvertisementAppInfoData>> getAdvertisementAppInfoRequest;
	
	private Thread updateThread;
	
	private boolean stopRunning = false;

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		checkAndStartUpdateThread();
		
		return null;
	}
	
	@Override
	public void onDestroy() {
		stopRunning = true;
	}
	
	@Override
	public void onCreate() {
		Log.d(this.getClass().toString(), "Start Service");
		//registerReceiver(boradcastReceiver, new IntentFilter(BroadcastInterface.BROADCAST_CHANNEL));
		
		dbAccessor = DBAccessor.instance(this);
		setRequest();
		
		checkAndStartUpdateThread();
	}
	
	private void checkAndStartUpdateThread() {
		Log.d(this.getClass().toString(), "checkAndStartUpdateThread");
		if(updateThread == null || !updateThread.isAlive()) {
			Log.d(this.getClass().toString(), "updateThread == null || !updateThread.isAlive()");
			updateThread = new UpdateThread();
			updateThread.start();
		}
	}
	

	
	private void setRequest() {
		getShopRequest = ServerCommunicator.requestGetShops(
				new ServerCommunicator.Callback<List<ShopData>>() {

			@Override
			public void onComplete(List<ShopData> element, int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: requestGetShops is null data");
						ServerCommunicator.executeOnDefaultThread(getShopRequest);
						ServerCommunicator.postErrorMessage(getShopRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetShop(element);
						Log.d(this.getClass().toString(), "Update: Shops");
					}
	
					return;
				}
				
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetShops");
					//ServerCommunicator.executeOnDefaultThread(getShopRequest);
				} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: requestGetShops is null data");
					ServerCommunicator.executeOnDefaultThread(getShopRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetShops, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getShopRequest, statusCode);
			}
			
		});
		
		getShopItemRequest = ServerCommunicator.requestGetShopItems(
				new ServerCommunicator.Callback<List<ShopItemData>>() {

			@Override
			public void onComplete(List<ShopItemData> element, int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: requestGetShopItems is null data");
						ServerCommunicator.executeOnDefaultThread(getShopItemRequest);
						ServerCommunicator.postErrorMessage(getShopItemRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetShopItem(element);
						Log.d(this.getClass().toString(), "Update: ShopItems");
					}
					
					return;
				}
				
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetShopItems");
					//ServerCommunicator.executeOnDefaultThread(getShopItemRequest);
				} if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: requestGetShopItems is null data");
					ServerCommunicator.executeOnDefaultThread(getShopItemRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetShopItems, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getShopItemRequest, statusCode);
			}
			
		});
		

		getAdvertisementRequest = ServerCommunicator.requestGetAdvertisements(
				new ServerCommunicator.Callback<List<AdvertisementData>>() {

			@Override
			public void onComplete(List<AdvertisementData> element,
					int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: getAdvertisementRequest is null data");
						ServerCommunicator.executeOnDefaultThread(getAdvertisementRequest);
						ServerCommunicator.postErrorMessage(getAdvertisementRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetAdvertisement(element);
						Log.d(this.getClass().toString(), "Update: Advertisements");
					}
					
					return;
				}
					
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetAdvertisements");
					//ServerCommunicator.executeOnDefaultThread(getAdvertisementRequest);
				} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: getAdvertisementRequest is null data");
					ServerCommunicator.executeOnDefaultThread(getAdvertisementRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetAdvertisements, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getAdvertisementRequest, statusCode);
			}
		});
		
		getFacebookEventRequest = ServerCommunicator.requestGetFacebookAdvertisementEvents(
				new ServerCommunicator.Callback<List<FacebookAdvertisementEventData>>() {

			@Override
			public void onComplete(List<FacebookAdvertisementEventData> element,
					int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: requestGetFacebookAdvertisementEvents is null data");
						ServerCommunicator.executeOnDefaultThread(getFacebookEventRequest);
						ServerCommunicator.postErrorMessage(getFacebookEventRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetFacebookAdvertisementEvent(element);
						Log.d(this.getClass().toString(), "Update: FacebookAdvertisementEvents");
					}
					
					return;
				} 
				
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetFacebookAdvertisementEvents");
					//ServerCommunicator.executeOnDefaultThread(getFacebookEventRequest);
				} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: requestGetFacebookAdvertisementEvents is null data");
					ServerCommunicator.executeOnDefaultThread(getFacebookEventRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetFacebookAdvertisementEvents, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getFacebookEventRequest, statusCode);
			}
		});
		
		getFacebookSharedPostInfoRequest = ServerCommunicator.requestGetFacebookSharedPostInfo(
				new ServerCommunicator.Callback<List<FacebookSharedPostInfoData>>() {

			@Override
			public void onComplete(List<FacebookSharedPostInfoData> element,
					int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: requestGetFacebookSharedPostInfo is null data");
						ServerCommunicator.executeOnDefaultThread(getFacebookSharedPostInfoRequest);
						ServerCommunicator.postErrorMessage(getFacebookSharedPostInfoRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetFacebookSharedPostInfo(element);
						Log.d(this.getClass().toString(), "Update: FacebookSharedPostInfo");
					}
					
					return;
				}
				
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetFacebookSharedPostInfo");
					//ServerCommunicator.executeOnDefaultThread(getFacebookSharedPostInfoRequest);
				} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: requestGetFacebookSharedPostInfo is null data");
					ServerCommunicator.executeOnDefaultThread(getFacebookSharedPostInfoRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetFacebookSharedPostInfo, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getFacebookSharedPostInfoRequest, statusCode);
			}
		});
		
		getAdvertisementAppInfoRequest = ServerCommunicator.requestGetAdvertisementAppInfo(
				new ServerCommunicator.Callback<List<AdvertisementAppInfoData>>() {

			@Override
			public void onComplete(List<AdvertisementAppInfoData> element,
					int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						Log.d(this.getClass().toString(), "Fail: requestGetAdvertisementAppInfo is null data");
						ServerCommunicator.executeOnDefaultThread(getAdvertisementAppInfoRequest);
						ServerCommunicator.postErrorMessage(getAdvertisementAppInfoRequest, StatusType.RECEIVED_NULL_DATA);
					} else {
						dbAccessor.resetAdvertisementAppInfo(element);
						Log.d(this.getClass().toString(), "Update: AdvertisementAppInfo");
					}
					
					return;
				}
				
				if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					Log.d(this.getClass().toString(), "Timeout: requestGetAdvertisementAppInfo");
					//ServerCommunicator.executeOnDefaultThread(getAdvertisementAppInfoRequest);
				} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "Fail: requestGetAdvertisementAppInfo is null data");
					ServerCommunicator.executeOnDefaultThread(getAdvertisementAppInfoRequest);
				} else {
					Log.d(this.getClass().toString(), "Error: requestGetAdvertisementAppInfo, statusCode: " + statusCode);
				}
				
				ServerCommunicator.postErrorMessage(getAdvertisementAppInfoRequest, statusCode);
			}
		});
		
		requestList = new ArrayList<ClientRequest<?>>();
		requestList.add(getShopRequest);
		requestList.add(getShopItemRequest);
		requestList.add(getAdvertisementRequest);
		requestList.add(getFacebookEventRequest);
		requestList.add(getFacebookSharedPostInfoRequest);
		requestList.add(getAdvertisementAppInfoRequest);
	}
	
	/*
	private void sendRequest(final ClientRequest<?> request) {
		new Thread() {
			@Override
			public void run() {
				while(!interrupted()) {
					ServerCommunicator.executeOnDefaultThread(request);
				}
			}
			
		}.start();
	}
	*/
	
	
			
	/*
	private BroadcastReceiver boradcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			if(!BroadcastInterface.BROADCAST_CHANNEL.equals(intent.getAction())) {
				return;
			}
			
			
			
		}
		
	};
	*/
	
	private class UpdateThread extends Thread {
		@Override
		public void run() {
			while(!stopRunning) {
				ServerCommunicator.executeOnDefaultThread(requestList);
				try {
					sleep(UPDATE_INTERVAL);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
	};
	
	public class IBinder extends Binder {  
		public Service getService() {  
			return DBUpdatedService.this;
		}  
	}  

}
