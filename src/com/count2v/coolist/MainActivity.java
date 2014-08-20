package com.count2v.coolist;



import com.count2v.coolist.R;
import com.count2v.coolist.advertisement.AdvertisementListActivity;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.contact.ContactActivity;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.facebook.LoginFacebook;
import com.count2v.coolist.shop.ShopListActivity;
import com.count2v.coolist.tuition.TuitionActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;


public class MainActivity extends BaseActivity {
	
	private ViewGroup mainLayout;
	
	private ImageView imageViewGameOn;
	private ImageView imageViewExchange;
	private ImageView imageViewInfo;
	
	private boolean hasInitialized = false;
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
		setView();
	}
	
	
	private void initialize() {
		// 某些型號的手機，即便結束應用程式，Process依然存在，造成static variable的記憶體空間未被清除
		setNewUser(false);
		setCheckStatus(FULL_CHECK_STATUS_LIST);
	}
	
	private void setView() {
		mainLayout = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.activity_main, null);
		
		setContentView(mainLayout);
		
		imageViewGameOn = (ImageView) findViewById(R.id.mainImageView01);
		imageViewExchange = (ImageView) findViewById(R.id.mainImageView02);
		imageViewInfo = (ImageView) findViewById(R.id.mainImageView03);
		
		imageViewGameOn.setLongClickable(true);
		imageViewExchange.setLongClickable(true);
		
		
	}
	
	private void setListener() {
		
		imageViewInfo.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Toast.makeText(MainActivity.this, "Alpha version", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent(MainActivity.this, ContactActivity.class);
				MainActivity.this.startActivity(intent);
			}
		});
		
		imageViewGameOn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startAdvertisementListActivity();
			}
		});
		
		imageViewExchange.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startShopListActivtiy();
			}
		});
		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(resultCode == RESULT_OK) {
			boolean newUser = data.getBooleanExtra(LoginFacebook.NEW_USER, false);
			if(newUser) {
				Log.d(this.getClass().toString(), "new user");
				setNewUser(true);
			}
		}
	}
	
	private void startShopListActivtiy() {
		Intent intent = new Intent(MainActivity.this, ShopListActivity.class);
		MainActivity.this.startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);  
	}
	
	private void startAdvertisementListActivity() {
		Intent intent = new Intent(MainActivity.this, AdvertisementListActivity.class);
		MainActivity.this.startActivity(intent);
		overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);  
	}
	
	private void startTutorialActivity() {
		Intent intent = new Intent(MainActivity.this, TuitionActivity.class);
		MainActivity.this.startActivity(intent);
	}

	@Override
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		super.onCheckStatusComplete(result, lastCheckStatus);
		
		if(result && !hasInitialized) {
			setListener();
			hasInitialized = true;
			
			setCheckStatus(DEFAULT_CHECK_STATUS_LIST);
			
			Log.d(this.getClass().toString(), "Activity has been initialized.");
			
			if(isNewUser()) {
				Toast.makeText(MainActivity.this, "歡迎使用酷集點", Toast.LENGTH_LONG).show();
				startTutorialActivity();
			}
		}
	}
}
