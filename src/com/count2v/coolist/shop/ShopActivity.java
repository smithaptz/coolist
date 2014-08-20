package com.count2v.coolist.shop;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.db.DBAccessor;
import com.count2v.coolist.shop.item.ShopItem;
import com.count2v.coolist.shop.item.ShopItemFactory;
import com.count2v.coolist.transaction.TransactionActivity;
import com.count2v.coolist.util.DefaultImageLoadingListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class ShopActivity extends BaseActivity {
	private long shopId;
	private ShopData shopData;
	private ShopListItem shopListItem;
	private List<ShopItemData> shopItemList;
	private List<ShopItem> itemList;
	
	private ImageLoaderConfiguration config;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	private ImageLoadingListener imageLoadingListner;
	
	private DBAccessor dbAccessor;
	
	private Drawable serviceAvailableDrawable;
	private Drawable serviceUnavailableDrawable;
	
	private Drawable expandDrawable;
	private Drawable collapseDrawable;
	
	private ViewGroup mainLayout;
	private ViewGroup shopInfoLayout;
	private ImageView iconImageView;
	private ImageView serviceImageView;
	private ImageView descriptionButtonImageView;
	private TextView shopNameTextView;
	private TextView shopDescriptionTextView;
	private TextView minRequiredPointTextView;
	private WebView shopDescriptionWebView;
	
	private ListView listView;
	private ShopAdapter listAdapter;
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		serviceAvailableDrawable.setCallback(null);
		serviceUnavailableDrawable.setCallback(null);
		
		expandDrawable.setCallback(null);
		collapseDrawable.setCallback(null);
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		shopId = getIntent().getLongExtra("shopId", -1);
		
		if(shopId < 0) {
			Log.d(this.getClass().toString(), "shopId is not exist");
			this.finish();
		}
		
		initialize();
		setView();
		setListener();
	}
	
	private void initialize() {
		dbAccessor = DBAccessor.instance(this);
		
		config = new ImageLoaderConfiguration.Builder(this).build();
		imageLoader = ImageLoader.getInstance();
		
		if(!imageLoader.isInited()) {
			imageLoader.init(config);
		}
		
		
		displayOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageForEmptyUri(R.drawable.shop_icon_default)
		.showImageOnFail(R.drawable.shop_icon_default)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		imageLoadingListner = new DefaultImageLoadingListener();
	}
	
	private void setView() {
		mainLayout = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.activity_shop, null);
		
		setContentView(mainLayout);
		
		serviceAvailableDrawable = getResources().getDrawable(R.drawable.service_available);
		serviceUnavailableDrawable = getResources().getDrawable(R.drawable.service_unavailable);
		
		expandDrawable = getResources().getDrawable(R.drawable.button_expand);
		collapseDrawable = getResources().getDrawable(R.drawable.button_collapse);
		
		shopInfoLayout = (ViewGroup) findViewById(R.id.shopRelativeLayout01);
		iconImageView = (ImageView) findViewById(R.id.shopImageView01);
		serviceImageView = (ImageView) findViewById(R.id.shopImageView02);
		descriptionButtonImageView = (ImageView) findViewById(R.id.shopImageView03);
		shopNameTextView = (TextView) findViewById(R.id.shopTextView01);
		shopDescriptionTextView = (TextView) findViewById(R.id.shopTextView02);
		minRequiredPointTextView = (TextView) findViewById(R.id.shopTextView03);
		shopDescriptionWebView = (WebView) findViewById(R.id.shopWebView01);
		listView = (ListView) findViewById(R.id.shopListView01);
		
		descriptionButtonImageView.setImageDrawable(expandDrawable);
		
		setWebView();
	}
	
	@SuppressLint("SetJavaScriptEnabled")
	private void setWebView() {
		shopDescriptionWebView.setVisibility(View.GONE);
		shopDescriptionWebView.setWebViewClient(new WebViewClient());

		shopDescriptionWebView.getSettings().setJavaScriptEnabled(true);
		shopDescriptionWebView.getSettings().setAppCacheEnabled(true);
		shopDescriptionWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		shopDescriptionWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.NARROW_COLUMNS);
		shopDescriptionWebView.getSettings().setBuiltInZoomControls(true);
		shopDescriptionWebView.getSettings().setSupportZoom(true);

		
	}
	
	private void setListener() {
		shopInfoLayout.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOrDimissDescriptionView();
			}
		});
		
		descriptionButtonImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showOrDimissDescriptionView();
			}
		});
		
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, final long id)  {
				if(!isNetworkConnected()) {
					Toast.makeText(ShopActivity.this, "網路已失去連線...", Toast.LENGTH_LONG).show();
					return;
				}
				
				ShopItem item = listAdapter.getItem(position);
				
				
				if(!item.isOnService()) {
					Toast.makeText(ShopActivity.this, "暫停服務", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!item.isUserAfford()) {
					Toast.makeText(ShopActivity.this, "點數不足", Toast.LENGTH_SHORT).show();
					return;
				}
				
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(ShopActivity.this);
				dialog.setTitle("兌換");
				dialog.setMessage("商品名稱：" + item.getName() + "\n扣除點數：" + item.getPoints());
				dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {

						Intent intent = new Intent(ShopActivity.this, TransactionActivity.class);
						intent.putExtra("shopItemId", id);
						
						ShopActivity.this.startActivity(intent);
					}
				});
				dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				});
				dialog.show();
				
			}
			
		});
	}
	
	private void setListView() {
		itemList = new ArrayList<ShopItem>();
		
		for(ShopItemData data : shopItemList) {			
			itemList.add(ShopItemFactory.instanceShopItem(this, shopData, data));
		}
		
		if(listAdapter != null) {
			if(listView.getAdapter() == null) {
				listView.setAdapter(listAdapter);
			}
			
			listAdapter.setItemList(itemList);
			listAdapter.notifyDataSetChanged();
		} else {
			listAdapter = new ShopAdapter(this, itemList);
			listView.setAdapter(listAdapter);
		}
	}
	
	private void updateView() {
		shopData = dbAccessor.getShopData(shopId);
		shopItemList = dbAccessor.getShopItemDataListByShopId(shopId);
		
		shopListItem = new ShopListItem(this, shopData, shopItemList);
		
		imageLoader.displayImage(shopListItem.getIconUrl(), iconImageView, displayOptions, imageLoadingListner);
		shopNameTextView.setText(shopListItem.getName());
		shopDescriptionTextView.setText(shopListItem.getDescription());
		shopDescriptionWebView.loadUrl(shopListItem.getDescriptionUrl());
		minRequiredPointTextView.setText(String.valueOf(shopListItem.getMinRequiredPoints()));
		
		if(shopListItem.isAvailable()) {
			serviceImageView.setImageDrawable(serviceAvailableDrawable);
		} else {
			serviceImageView.setImageDrawable(serviceUnavailableDrawable);
		}
		
		setListView();
	}
	
	private void showOrDimissDescriptionView() {
		if(shopDescriptionWebView.getVisibility() != View.VISIBLE) {
			descriptionButtonImageView.setImageDrawable(collapseDrawable);
			shopDescriptionWebView.setVisibility(View.VISIBLE);
		} else {
			descriptionButtonImageView.setImageDrawable(expandDrawable);
			shopDescriptionWebView.setVisibility(View.GONE);
			shopDescriptionWebView.loadUrl(shopListItem.getDescriptionUrl());
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
		
		updateView();
	}
}
