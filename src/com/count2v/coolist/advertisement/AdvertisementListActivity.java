package com.count2v.coolist.advertisement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.count2v.coolist.R;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.CheckData;
import com.count2v.coolist.client.data.EventCheckData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.core.RequestUIHandler;
import com.count2v.coolist.db.DBAccessor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class AdvertisementListActivity extends BaseActivity  {
	public static final int REQUEST_CODE = 1;
	
	private static AdvertisementListAdapter listAdapter;
	
	private ListView listView;
	
	private ClientRequest<Integer> userPointsRequest;
	private RequestData<?> currentReqeustData;
	
	private ConcurrentLinkedQueue<RequestData<?>> requestQueue = 
			new ConcurrentLinkedQueue<RequestData<?>>();
	
	private List<AdvertisementData> advertisementDataList;
	private HashMap<Long, List<FacebookAdvertisementEventData>> facebookEventMap;
	private HashMap<Long, AdvertisementAppInfoData> downloadEventMap;
	private ArrayList<AdvertisementListItem> itemList;
	

	private ProgressDialog waitingDialog;
	
	//private long lastClickedItemId = -1;
	//private long firstVisibleItemId = -1;
	
	private boolean alreadSentRequest = false;
	
	private DBAccessor dbAccessor;
	
		
	
	@Override
	protected void onPause() {
		super.onPause();
		
		//setListViewPosition();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
		setView();
		setListener();
	}
	
	/*
	 * 初始化
	 */
	
	private void initialize() {
		dbAccessor = DBAccessor.instance(this);
		userPointsRequest = ServerCommunicator.requestGetUserPoints(userPointsRequestListener);
		
		enableConnectionAllTheTime(false);
	}
	
	private void setView() {
		setContentView(R.layout.activity_advertisemnt_list);
		listView = (ListView) findViewById(R.id.advertisementListListView);
		
		waitingDialog = new ProgressDialog(this);
		waitingDialog.setCancelable(false);
		waitingDialog.setMessage("讀取中，請稍帶片刻");
	}
		
	private void setListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
				if(!isNetworkConnected()) {
					Toast.makeText(AdvertisementListActivity.this, "網路已失去連線...", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(isItemPending(id)) {
					Toast.makeText(AdvertisementListActivity.this, "驗證中...", Toast.LENGTH_SHORT).show();
					return;
				}
				
				//lastClickedItemId = id;
				
				
				Intent intent = new Intent(AdvertisementListActivity.this, AdvertisementActivity.class);
				intent.putExtra("advertisementId", id);
				
				AdvertisementListActivity.this.startActivityForResult(intent, REQUEST_CODE);
				overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);  
			}
			
		});
	}
	
	/*
	 * 設定listview，包含之後的更新
	 */
	
	private void setListView() {
		itemList = new ArrayList<AdvertisementListItem>();
		
		for(AdvertisementData data : advertisementDataList) {
			long id = data.getAdvertisementId();
			
			List<FacebookAdvertisementEventData> facebookEventList = null;
			AdvertisementAppInfoData advertisementAppInfoData = null;
			
			if(facebookEventMap.containsKey(id)) {
				facebookEventList = facebookEventMap.get(id);
			}
			
			if(downloadEventMap.containsKey(id)) {
				advertisementAppInfoData = downloadEventMap.get(id);
			}
			
			itemList.add(new AdvertisementListItem(data, 
					facebookEventList, advertisementAppInfoData));
		}
		
		// listAdapter is a static variable
		if(listAdapter != null) {
			if(listView.getAdapter() == null) {
				listView.setAdapter(listAdapter);
			}
			
			listAdapter.setItemListWithPreviousPendingStatus(itemList);
			listAdapter.notifyDataSetChanged();
		} else {
			listAdapter = new AdvertisementListAdapter(this, itemList);
			listView.setAdapter(listAdapter);
		}
	}
	
	/*
	 * 回到使用者上次點選項目的位置
	 */
	
	/*
	private void setListViewPosition() {
		if(listAdapter == null || listView.getCount() == 0) {
			return;
		}
		
		firstVisibleItemId = listAdapter.getItemId(
				listView.getFirstVisiblePosition());
	}
	*/
	
	/*
	 * 還原使用者上次點選的item位置
	 */
	
	/*
	private void restoreListViewPostion() {
		
		if(lastClickedItemId >= 0) {
			int position = listAdapter.getItemPosition(lastClickedItemId);
			
			lastClickedItemId = -1;
			
			if(position >= 0) {
				listView.setSelection(position);
				return;
			}
		}
		
		if(firstVisibleItemId >= 0) {
			int position = listAdapter.getItemPosition(firstVisibleItemId);
			
			if(position >= 0) {
				listView.setSelection(position);
			}
		}
	}
	*/
	
	
	private void setItemPendingStatusAndRefreshView(long advertisementId, boolean pending) {
		
		
		if(listAdapter.isItemPending(advertisementId) == pending) {
			return;
		}
		
		listAdapter.setItemPendingStatus(advertisementId, pending);
		listAdapter.notifyDataSetChanged();
	}
	
	private void setItemPendingStatus(long advertisementId, boolean pending) {
		listAdapter.setItemPendingStatus(advertisementId, pending);
	}
	
	private boolean isItemPending(long advertisementId) {
		return listAdapter.isItemPending(advertisementId);
	}
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/*
	 * 跳出等待UI告示 ProgressDialog
	 */
	
	private void showWaitingDialog() {
		if(!waitingDialog.isShowing()) {
			waitingDialog.show();
		}
	}
	
	/*
	 * 關閉等待UI的告示
	 */
	
	private void dismissWaitingDialog() {
		if(waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
	}
	
	/*
	 * 從AdvertisementActivity返回的結果
	 * 檢查是否需要向Server回報已讀取廣告和完成FB event
	 */
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			
			Bundle bundle = data.getExtras();
			long advertisementId = bundle.getLong("advertisementId", -1);
			boolean gameDone = bundle.getBoolean("advertisementGameDone", false);
			long facebookLikedEventId = bundle.getLong("facebookLikedEventId", -1);
			long facebookSharedEventId = bundle.getLong("facebookSharedEventId", -1);
			long advertisementAppId = bundle.getLong("advertisementAppId", -1);
			String facebookUserSharedPostId = bundle.getString("facebookUserSharedPostId");
			

			
			//restoreListViewPostion();
			
			//showWaitingDialog();
			
			// 接下來將開始執行已讀取廣告和檢查FB event的請求
			
			if(advertisementId < 0) {
				return;
			}
			
			// listAdapter 可能莫名其妙被GC回收
			if(listAdapter == null) {
				updateListView();
			}
			
			setItemPendingStatusAndRefreshView(advertisementId, true);
			
			if(gameDone) {
				ClientRequest<CheckData> request = ServerCommunicator.requestSetReadAdvertisement(
						advertisementId, readAdvertisementRequestListener);
				Bundle requestBundle = new Bundle();
				requestBundle.putLong("advertisementId", advertisementId);
				
				requestQueue.add(new RequestData<CheckData>(request, requestBundle));	
			} 
			
			if(facebookLikedEventId >= 0) {
				ClientRequest<EventCheckData> request = ServerCommunicator.requestIsFacebookAdvertisementEventDone(
						facebookLikedEventId, checkFacebookEventRequestListener);
				Bundle requestBundle = new Bundle();
				requestBundle.putLong("facebookAdvertisementEventId", facebookLikedEventId);
				
				requestQueue.add(new RequestData<EventCheckData>(request, requestBundle));	
			}
			
			if(facebookSharedEventId >= 0 && facebookUserSharedPostId != null) {
				ClientRequest<EventCheckData> request = ServerCommunicator.requestIsFacebookAdvertisementEventDone(
						facebookSharedEventId, facebookUserSharedPostId, checkFacebookEventRequestListener);
				Bundle requestBundle = new Bundle();
				requestBundle.putLong("facebookAdvertisementEventId", facebookSharedEventId);
				requestBundle.putString("facebookUserSharedPostId", facebookUserSharedPostId);
				
				requestQueue.add(new RequestData<EventCheckData>(request, requestBundle));	
			}
			
			
			if(advertisementAppId >= 0) {
				ClientRequest<CheckData> request = ServerCommunicator.requestSetAppDownloaded(
						advertisementAppId, setAppDownloadedRequestListener);
				Bundle requestBundle = new Bundle();
				requestBundle.putLong("advertisementAppId", advertisementAppId);
				
				requestQueue.add(new RequestData<CheckData>(request, requestBundle));	
			}
			
			Bundle requestBundle = new Bundle();
			requestBundle.putLong("advertisementId", advertisementId);
			requestQueue.add(new RequestData<Integer>(userPointsRequest, requestBundle));
			
			
			if(!alreadSentRequest) {
				alreadSentRequest = true;
				executeNextRequest();
			}
		}
	}
	
	/*
	 * 傳回當前正在執行的RequestData
	 * 
	 */
	
	private RequestData<?> getCurrentRequestData() {
		return currentReqeustData;
	}
	
	/*
	 * 執行下一個向Server的Request
	 */
	
	private void executeNextRequest() {
		
		if(requestQueue.isEmpty()) {
			alreadSentRequest = false;
			return;
		}
		
		currentReqeustData = requestQueue.poll();
		
		if(currentReqeustData != null) {
			ServerCommunicator.execute(currentReqeustData.getRequest());
		}
	}


	@Override
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		super.onCheckStatusComplete(result, lastCheckStatus);
		
		/*
		 * 為了避免之前的請求因為沒有登入而重新發送
		 * 進到FB葉面跳回來後，這裡又傳送新的請求出去，
		 * 請求將越來越多，最後造成程式崩潰
		 * 當完成請求時，alreadSentRequest=false
		 * 則可再次發送新的請求，如此保證同一時間只會有一個晴求存在
		 */
		
		if(alreadSentRequest) {
			return;
		}
		
		updateListView();
	}
	
	/*
	 * 更新ListView裡面所使用的Item
	 */
	
	private void updateListView() {
		advertisementDataList = dbAccessor.getAdvertisementDataList();
		facebookEventMap = new HashMap<Long, List<FacebookAdvertisementEventData>>();
		downloadEventMap = new HashMap<Long, AdvertisementAppInfoData>();
		
		for(FacebookAdvertisementEventData data : dbAccessor.getFacebookEventDataList()) {
			long advertisementId = data.getAdvertisementId();
			
			List<FacebookAdvertisementEventData> list;
			
			
			if(!facebookEventMap.containsKey(advertisementId)) {
				list = new ArrayList<FacebookAdvertisementEventData>();
				facebookEventMap.put(advertisementId, list);
			} else {
				list = facebookEventMap.get(advertisementId);
			}
			
			list.add(data);
		}
		
		for(AdvertisementAppInfoData data : dbAccessor.getAdvertisementAppInfoDataList()) {
			long advertisementId = data.getAdvertisementId();
			
			downloadEventMap.put(advertisementId, data);
		}
		
		setListView();
		
		// 完成請求
		alreadSentRequest = false;
		
		//dismissWaitingDialog();
	}
	
	/*
	 * 向Server要求檢察廣告以讀取的Callback
	 */
		
	private ServerCommunicator.Callback<CheckData> readAdvertisementRequestListener = new ServerCommunicator.Callback<CheckData>() {

		@Override
		public void onComplete(CheckData element, int statusCode) {
			
			RequestData<?> requestData = getCurrentRequestData();
			
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				Bundle bundle = requestData.getBundle();
				AdvertisementData data = dbAccessor.getAdvertisementData(bundle.getLong("advertisementId"));
				
				// 修改本地端資料庫內的資料
				if(data != null) {
					Log.d(this.getClass().toString(), "from local database: " + data.toString());
					data.setAvailable(false);
					data.setRead(true);
					dbAccessor.setAdvertisement(data);
				} else {
					Log.d(this.getClass().toString(), "cannot find data from local database");
				}
				
				executeNextRequest();
			} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
					statusCode == StatusType.RECEIVED_NULL_DATA) {
				Log.d(this.getClass().toString(), "requestIsFacebookAdvertisementEventDone timeout" +
						" or recieved null data");
				ServerCommunicator.execute(requestData.getRequest());
				ServerCommunicator.postErrorMessage(requestData.getRequest(), statusCode);
			} else if(!(statusCode == StatusType.ADVERTISEMENT_HAS_BEEN_READ || 
						statusCode == StatusType.ADVERTISEMENT_HAS_NOT_BEEN_READ || 
						statusCode == StatusType.ADVERTISEMENT_CAN_BE_READ || 
						statusCode == StatusType.ADVERTISEMENT_CAN_NOT_BE_READ) ||
						statusCode == StatusType.USER_ALREADY_READ_THE_ADVERTISEMNT) {
					RequestUIHandler.showErrorDialog(AdvertisementListActivity.this, 
							requestData.getRequest(), statusCode);
			}
		}
		
	};
	
	private ServerCommunicator.Callback<CheckData> setAppDownloadedRequestListener = new ServerCommunicator.Callback<CheckData>() {

		@Override
		public void onComplete(CheckData element, int statusCode) {
			
			RequestData<?> requestData = getCurrentRequestData();
			
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				Bundle bundle = requestData.getBundle();
				AdvertisementAppInfoData data = dbAccessor.getAdvertisementAppInfoData(bundle.getLong("advertisementAppId"));
				
				// 修改本地端資料庫內的資料
				if(data != null) {
					Log.d(this.getClass().toString(), "from local database: " + data.toString());
					data.setDone(true);
					dbAccessor.setAdvertisementAppInfo(data);
				} else {
					Log.d(this.getClass().toString(), "cannot find data from local database");
				}
				
				executeNextRequest();
			} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
					statusCode == StatusType.RECEIVED_NULL_DATA) {
				Log.d(this.getClass().toString(), "requestSetAppDownloaded timeout" +
						" or receieved null data");
				ServerCommunicator.execute(requestData.getRequest());
				ServerCommunicator.postErrorMessage(requestData.getRequest(), statusCode);
			} else if(!(statusCode == StatusType.ADVERTISEMENT_APP_DOWNLOADED_EVENT_HAS_DONE || 
						statusCode == StatusType.ADVERTISEMENT_APP_DOWNLOADED_EVENT_NOT_EXIST)) {
					RequestUIHandler.showErrorDialog(AdvertisementListActivity.this, 
							requestData.getRequest(), statusCode);
			} else {
				executeNextRequest();
			}
		}
		
	};
	
	private ServerCommunicator.Callback<EventCheckData> checkFacebookEventRequestListener = new ServerCommunicator.Callback<EventCheckData>() {

		@Override
		public void onComplete(EventCheckData element, int statusCode) {
			
			RequestData<?> requestData = getCurrentRequestData();
			
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				Bundle bundle = requestData.getBundle();
				FacebookAdvertisementEventData data = dbAccessor.getFacebookEventData(bundle.getLong("facebookAdvertisementEventId"));
				
				// 修改本地端資料庫內的資料
				if(data != null) {
					Log.d(this.getClass().toString(), "from local database: " + data.toString());
					data.setDone(true);
					dbAccessor.setFacebookAdvertisementEvent(data);
				} else {
					Log.d(this.getClass().toString(), "cannot find data from local database");
				}
				
				executeNextRequest();
			} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
					statusCode == StatusType.RECEIVED_NULL_DATA) {
				Log.d(this.getClass().toString(), "requestIsFacebookAdvertisementEventDone timeout" +
						" or received null data");
				ServerCommunicator.execute(requestData.getRequest());
				ServerCommunicator.postErrorMessage(requestData.getRequest(), statusCode);
			} else if(statusCode != StatusType.FACEBOOK_FAILED_OR_REPEATED) {
					RequestUIHandler.showErrorDialog(AdvertisementListActivity.this, 
							requestData.getRequest(), statusCode);
			} else {
				executeNextRequest();
			}
		}
		
	};
	
	private ServerCommunicator.Callback<Integer> userPointsRequestListener = new ServerCommunicator.Callback<Integer>() {

		@Override
		public void onComplete(Integer element, int statusCode) {
			
			RequestData<?> requestData = getCurrentRequestData();
			
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				Log.d(this.getClass().toString(), "previous user points: " + getUserPoints() + 
						", current user points: " + element);
				
				int userGetPoints = element - getUserPoints();
				
				// 如果得到的點數大於0，則告知使用者得到多少點數
				if(userGetPoints > 0) {
					Toast.makeText(AdvertisementListActivity.this, "+" + userGetPoints, Toast.LENGTH_LONG).show();
				}
				
				setUserPoints(element);
				
				// 傳送完畢，將item的pending狀態取消
				Bundle bundle = requestData.getBundle();
				long advertisementId = bundle.getLong("advertisementId");
				setItemPendingStatus(advertisementId, false);
				
				updateListView();
				
				executeNextRequest();
			} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
					statusCode == StatusType.RECEIVED_NULL_DATA) {
				Log.d(this.getClass().toString(), "requestGetUserPoints timeout" +
						" or received null data");
				ServerCommunicator.execute(userPointsRequest);
				ServerCommunicator.postErrorMessage(userPointsRequest, statusCode);
			} else {
				RequestUIHandler.showErrorDialog(AdvertisementListActivity.this, userPointsRequest, statusCode);
			}
		}
		
	};
	
	private class RequestData<T> {
		private Bundle bundle;
		private ClientRequest<T> request;
		
		public RequestData(ClientRequest<T> request, Bundle bundle) {
			this.request = request;
			this.bundle = bundle;
		}
		
		
		public Bundle getBundle() {
			return bundle;
		}
		
		public ClientRequest<T> getRequest() {
			return request;
		}
	}
	
}
