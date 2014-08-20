package com.count2v.coolist.shop;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;


import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.db.DBAccessor;
import com.count2v.coolist.R;

public class ShopListActivity extends BaseActivity {
	public static final int REQUEST_CODE = 100;
	
	private List<ShopData> shopDataList;
	private List<ShopListItem> itemList;
	private HashMap<Long, List<ShopItemData>> shopItemMap;
	
	private ListView listView;
	private ShopListAdapter listAdapter;
	
	//private long firstVisibleItemId = -1;
	//private long lastClickedItemId = -1;
	
	private ProgressDialog waitingDialog;
	
	private DBAccessor dbAccessor;
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		//setListViewPosition();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
		setView();
		setListener();
	}
	
	private void initialize() {
		dbAccessor = DBAccessor.instance(this);
		enableConnectionAllTheTime(false);
	}
	
	private void setView() {
		setContentView(R.layout.activity_shop_list);
		listView = (ListView) findViewById(R.id.shopListListView);
		
		waitingDialog = new ProgressDialog(this);
		waitingDialog.setCancelable(false);
		waitingDialog.setMessage("讀取中，請稍帶片刻");
	}
	
	private void setListener() {
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)  {
				if(!isNetworkConnected()) {
					Toast.makeText(ShopListActivity.this, "網路已失去連線...", Toast.LENGTH_LONG).show();
					return;
				}
				
				//lastClickedItemId = id;
				
				Intent intent = new Intent(ShopListActivity.this, ShopActivity.class);
				intent.putExtra("shopId", id);
				
				ShopListActivity.this.startActivity(intent);
				overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);  
			}
			
		});
	}

	
	private void setListView() {
		itemList = new ArrayList<ShopListItem>();
		
		for(ShopData data : shopDataList) {
			long id = data.getShopId();
			
			List<ShopItemData> shopItemList = shopItemMap.containsKey(id) ? 
					shopItemList = shopItemMap.get(id) : new ArrayList<ShopItemData>();
			
			itemList.add(new ShopListItem(this, data, shopItemList));
		}
		
		if(listAdapter != null) {
			if(listView.getAdapter() == null) {
				listView.setAdapter(listAdapter);
			}
			
			listAdapter.setItemList(itemList);
			listAdapter.notifyDataSetChanged();
		} else {
			listAdapter = new ShopListAdapter(this, itemList);
			listView.setAdapter(listAdapter);
		}
		
		//restoreListViewPostion();
	}
	
	private void updateListView() {
		shopItemMap = new HashMap<Long, List<ShopItemData>>();
		
		shopDataList = dbAccessor.getShopDataList();
		
		for(ShopItemData data : dbAccessor.getShopItemDataList()) {
			long shopId = data.getShopId();
			
			List<ShopItemData> list;
			
			
			if(!shopItemMap.containsKey(shopId)) {
				list = new ArrayList<ShopItemData>();
				shopItemMap.put(shopId, list);
			} else {
				list = shopItemMap.get(shopId);
			}
			
			list.add(data);
		}
		
		
		setListView();
	}
	
	
	/*
	private void setListViewPosition() {
		
		if(listAdapter == null || listView.getCount() == 0) {
			return;
		}
		firstVisibleItemId = listAdapter.getItemId(
				listView.getFirstVisiblePosition());
	}
	
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void showWaitingDialog() {
		if(!waitingDialog.isShowing()) {
			waitingDialog.show();
		}
	}
	
	private void dismissWaitingDialog() {
		if(waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			finish();
		}
	}
	
	
	@Override
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		super.onCheckStatusComplete(result, lastCheckStatus);
		
		/*
		 * 為了避免之前的請求因為沒有登入而重新發送
		 * 進到FB葉面跳回來後，這裡又傳送新的請求出去，
		 * 請求將越來越多，最後造成程式崩潰
		 * 當完成請求時，alreadSentRequest=false4
		 * 則可再次發送新的請求，如此保證同一時間只會有一個晴求存在
		 */
		
		updateListView();
	}
}
