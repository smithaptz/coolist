package com.count2v.coolist.advertisement;




import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.FacebookAdvertisementEventType;
import com.count2v.coolist.client.GameAdvertisementEventType;
import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;
import com.count2v.coolist.client.data.FacebookSharedPostInfoData;
import com.count2v.coolist.contact.ContactActivity;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.db.DBAccessor;
import com.count2v.coolist.facebook.FacebookPermissions;
import com.count2v.coolist.game.LotteryView;
import com.count2v.coolist.game.ScratchCardView;
import com.count2v.coolist.game.base.GameListener;
import com.count2v.coolist.game.base.GameView;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;
import com.facebook.widget.FacebookDialog.ShareDialogBuilder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class AdvertisementActivity extends BaseActivity {
	private static final float GAME_FINISH_PERCENT_THRESHOLD = 96.0F;
	private static final String FACEBOOK_ANDROID_MARKET_ID = "com.facebook.katana";
	private static final String FACEBOOK_URL = "https://www.facebook.com/";
	
	private ImageLoaderConfiguration config;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	
	private ViewGroup mainLayout;
	private ViewGroup subLayout;
	
	
	private ViewGroup infoLayout;
	private ViewGroup eventLayout;
	private ViewGroup loadingLayout;
	
	private ImageView infoImageView;
	private ImageView fbLikedImageView;
	private ImageView fbSharedImageView;
	private ImageView signupImageView;
	private ImageView downlaodedImageView;
	
	private ImageView refreshImageView;
	private ProgressBar loadingProgressBar;
	
	private long advertisementId;
	private AdvertisementData advertisementData;
	private SparseArray<FacebookAdvertisementEventData> facebookEvents;
	private FacebookAdvertisementEventData fbLikedEventData;
	private FacebookAdvertisementEventData fbSharedEventData;
	private AdvertisementAppInfoData advertisementAppInfoData;
	private FacebookSharedPostInfoData fbSharedPostInfoData;
	
	private boolean reportGameDone = false;
	private boolean reportCheckDownloadedEventDone = false;
	private boolean reportCheckFBLikedEventDone = false;
	private boolean reportCheckFBSharedEventDone = false;
	
	private String userFBSharedPostId;
	
	
	private boolean hasInitialized = false;
	
	private DBAccessor dbAccessor;
	
	private ScratchCardView scratchCardView;
	
	private ImageView advertisementImageView;
	private Bitmap advertisementBitmap;
	
	private UiLifecycleHelper uiHelper;
	
	
	@Override
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
		if(scratchCardView != null) {
			scratchCardView.onDestroy();
		}
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);  
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		advertisementId = getIntent().getLongExtra("advertisementId", -1);
		
		if(advertisementId < 0) {
			Log.d(this.getClass().toString(), "Must set advertisemntId");
			this.finish();
		}
		
		uiHelper = new UiLifecycleHelper(this, new Session.StatusCallback() {

			@Override
			public void call(Session session, SessionState state,
					Exception exception) {
				Log.d(this.getClass().toString(), "session: " + session + 
						", state: " + state + ", exception: " + exception);
			}
			
		});
		
		uiHelper.onCreate(savedInstanceState);

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
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
	
	}
	
	private void setView() {
		mainLayout = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.activity_advertisement, null);
		
		setContentView(mainLayout);
		
		setUserInfoBarVisibility(View.GONE);
		
		subLayout = (ViewGroup) findViewById(R.id.advertisementLinearLayout01);
		
		infoLayout = (ViewGroup) findViewById(R.id.advertisementLinearLayout03);
		eventLayout = (ViewGroup) findViewById(R.id.advertisementLinearLayout04);
		loadingLayout = (ViewGroup) findViewById(R.id.advertisementLinearLayout05);
		
		infoImageView = (ImageView) findViewById(R.id.advertisementImageView01);
		fbLikedImageView = (ImageView) findViewById(R.id.advertisementImageView02);
		fbSharedImageView = (ImageView) findViewById(R.id.advertisementImageView03);
		signupImageView = (ImageView) findViewById(R.id.advertisementImageView04);
		downlaodedImageView = (ImageView) findViewById(R.id.advertisementImageView05);
		
		refreshImageView = (ImageView) findViewById(R.id.advertisementImageView06);
		loadingProgressBar = (ProgressBar) findViewById(R.id.advertisementProgressBar01);
		
		refreshImageView.setVisibility(View.GONE);
	}
	
	private void setListener() {
		infoImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(AdvertisementActivity.this, ContactActivity.class);
				AdvertisementActivity.this.startActivity(intent);
			}
		});
		
		refreshImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.d(this.getClass().toString(), "refresh ImageView");
				
				if(loadingProgressBar.getVisibility() != View.VISIBLE) {
					loadingProgressBar.setVisibility(View.VISIBLE);
				}
				
				if(refreshImageView.getVisibility() != View.GONE) {
					refreshImageView.setVisibility(View.GONE);
				}
				
				loadingAdvertisementImage();
			}
		});
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);

	    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
	        @Override
	        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
	            Log.e(this.getClass().toString(), String.format("Error: %s", error.toString()));
	        }

	        @Override
	        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
	        	boolean didComplete = FacebookDialog.getNativeDialogDidComplete(data);
	        	String completionGesture = FacebookDialog.getNativeDialogCompletionGesture(data);
	        	String postId = FacebookDialog.getNativeDialogPostId(data);
	        	
	        	if(didComplete && postId != null) {
	        		reportCheckFBSharedEventDone = true;
	        		int startIndex = postId.indexOf("_") + 1;
	        		userFBSharedPostId = postId.substring(startIndex);
	        	}
	        	
	        	Log.d(this.getClass().toString(), "didCompete: " + didComplete + 
	        			", completionGesture: " + completionGesture + ", postId: " + postId);
	        }
	    });
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	setFinishResult();
	    	finish();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private void setFinishResult() {
		boolean report = false;
		
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		
		bundle.putLong("advertisementId", advertisementId);
		
		if(reportGameDone) {
			bundle.putBoolean("advertisementGameDone", true);
			report = true;
		}
		
		
		if(reportCheckFBLikedEventDone) {
			bundle.putLong("facebookLikedEventId", fbLikedEventData.getFacebookAdvertisementEventId());
			report = true;
		}
		
		if(reportCheckFBSharedEventDone) {
			bundle.putLong("facebookSharedEventId", fbSharedEventData.getFacebookAdvertisementEventId());
			bundle.putString("facebookUserSharedPostId", userFBSharedPostId);
			report = true;
		}
		
		
		if(reportCheckDownloadedEventDone && isAppInstalled(
				advertisementAppInfoData.getAndroidMarketId())) {
			bundle.putLong("advertisementAppId", advertisementAppInfoData.getAdvertisementAppId());
			report = true;
		}
		
		if(report) {
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
		}
		
	}
	
	private boolean isGameAvailable() {
		if(advertisementData.getGameType() != GameAdvertisementEventType.SCRATCH_OFF) {
			return false;
		}
		
		return advertisementData.isAvailable() || !advertisementData.isRead();
	}
	
	private boolean isGameDone() {
		return advertisementData.isRead();
	}
		
	private void displayEventView(boolean display) {
		if(display) {
			eventLayout.setVisibility(View.VISIBLE);
			infoLayout.setVisibility(View.GONE);
		} else {
			eventLayout.setVisibility(View.GONE);
			infoLayout.setVisibility(View.VISIBLE);
		}
		
	}
	
	private void setFacebookLikedEvent() {
		
		fbLikedEventData = facebookEvents.get(
				FacebookAdvertisementEventType.LIKE_FAN_PAGE);
		
		if(fbLikedEventData == null) {
			fbLikedImageView.setVisibility(View.GONE);
			return;
		}
		
		if(fbLikedEventData.isDone()) {
			fbLikedImageView.setImageResource(R.drawable.like_done);
		}
		
		fbLikedImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(fbLikedEventData.isDone()) {
					Toast.makeText(AdvertisementActivity.this, "任務已完成", 
							Toast.LENGTH_SHORT).show();
					return;
				}
				
				String url = FACEBOOK_URL + fbLikedEventData.getPageId();
				showFacebookWebViewDialog(url);
				
				if(!fbLikedEventData.isDone()) {
					reportCheckFBLikedEventDone = true;
				}
			}
		});
	}
	
	private void setFacebookSharedEvent() {
		fbSharedEventData = facebookEvents.get(
				FacebookAdvertisementEventType.SHARE_POST);
		
		if(fbSharedEventData == null) {
			fbSharedImageView.setVisibility(View.GONE);
			return;
		}
				
		fbSharedPostInfoData = dbAccessor
				.getFacebookSharedPostInfoDataByFacebookAdvertisementEventId(
						fbSharedEventData.getFacebookAdvertisementEventId());
			
		
		if(fbSharedPostInfoData == null) {
			fbSharedImageView.setVisibility(View.GONE);
			return;
		}
		
		if(fbSharedEventData.isDone()) {
			fbSharedImageView.setImageResource(R.drawable.share_done);
		}
		
		fbSharedImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(!isAppInstalled(FACEBOOK_ANDROID_MARKET_ID)) {
					showInstallFacebookAppDialog();
					return;
				}
				
				Session session = Session.getActiveSession();
				
				if(session == null || !session.isOpened()) {
					Toast.makeText(AdvertisementActivity.this, "請重新登入Facebook", Toast.LENGTH_SHORT).show();
					return;
				}
				
				if(!FacebookPermissions.hasPublishPermissions(session)) {
					FacebookPermissions.askUserPublishPermissions(AdvertisementActivity.this, session);
					return;
				}
				
				if(FacebookDialog.canPresentShareDialog(AdvertisementActivity.this, 
                        FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
					try{
						ShareDialogBuilder shareDialogBuidler = 
								new FacebookDialog.ShareDialogBuilder(AdvertisementActivity.this)
						.setLink(fbSharedPostInfoData.getUrl());

						String name = fbSharedPostInfoData.getName();
						String caption = fbSharedPostInfoData.getCaption();
						String description = fbSharedPostInfoData.getDescription();
						String pictureUrl = fbSharedPostInfoData.getPictureUrl();
						String place = fbSharedPostInfoData.getPlace();
						String ref = fbSharedPostInfoData.getRef();
						
						if(name != null && name.length() > 0) {
							shareDialogBuidler.setName(name);
						}
						
						if(caption != null && caption.length() > 0) {
							shareDialogBuidler.setCaption(caption);
						}
						
						if(description != null && description.length() > 0) {
							shareDialogBuidler.setDescription(description);
						}
						
						if(pictureUrl != null && pictureUrl.length() > 0) {
							shareDialogBuidler.setPicture(pictureUrl);
						}
				        
						if(place != null && place.length() > 0) {
							shareDialogBuidler.setPlace(place);
						}
						
						if(ref != null && ref.length() > 0) {
							shareDialogBuidler.setRef(ref);
						}

						FacebookDialog shareDialog = shareDialogBuidler.build();
						uiHelper.trackPendingDialogCall(shareDialog.present());
					} catch(Exception e) {
						showUpdateFacebookAppDialog();
					}
				} else {
					showUpdateFacebookAppDialog();
				}
			}
		});
	}
	
	private void setSignupEvent() {
		signupImageView.setVisibility(View.GONE);
	}
	
	private void setAppDownloadedEvent() {
		if(advertisementAppInfoData == null) {
			downlaodedImageView.setVisibility(View.GONE);
			return;
		}
		
		if(advertisementAppInfoData.isDone()) {
			downlaodedImageView.setImageResource(R.drawable.download_done);
		}
		
		downlaodedImageView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startGoogleMarketIntent(advertisementAppInfoData.getAndroidMarketId());
				reportCheckDownloadedEventDone = true;
			}
		});
		
		
	}
	
	private void setAdvertisement() {
		advertisementImageView = new ImageView(this);
		advertisementImageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		subLayout.addView(advertisementImageView);
		displayEventView(isGameDone());
		
		loadingAdvertisementImage();
	}
	
	private void loadingAdvertisementImage() {
		imageLoader.loadImage(advertisementData.getPictureUrl(), displayOptions, imageLoadingListener);
	}
	
	private void setGame() {
		int gameType = advertisementData.getGameType();
		
		System.out.println("game type: " + gameType);
		
		switch(gameType) {
			case GameAdvertisementEventType.SCRATCH_OFF:
				scratchCardView = new ScratchCardView(this, gameListener);
				Toast.makeText(this, "請滑動螢幕並刮除所有銀漆", Toast.LENGTH_LONG).show();		
				break;	
		}
		
		if(scratchCardView == null) {
			return;
		}

		advertisementImageView.setVisibility(View.GONE);
		subLayout.addView(scratchCardView);
		
		scratchCardView.start(advertisementBitmap, subLayout.getWidth(), subLayout.getHeight());
	}
	
	private Dialog showFacebookWebViewDialog(String url) {
		
		final String likeBoxUrl = getFacebookLikeBoxUrl(url);
		
		WebView webView = new WebView(AdvertisementActivity.this);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		webView.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				System.out.println(url);
				view.loadUrl(url);
				return true;
			}
			
			@Override
			public void onPageFinished(WebView view, String url) {
				Log.d(this.getClass().toString(), url);
				
				
				if(url.contains("facebook.com/plugins/close_popup")) {
					Log.d(this.getClass().toString(), "contains");
					view.loadUrl(likeBoxUrl);
				}
			}
		});
		
		webView.loadUrl(likeBoxUrl);
		
		ViewGroup viewGroup = new FrameLayout(this);
		viewGroup.addView(webView);
		
		Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(viewGroup);
		dialog.setCancelable(true);
		
		dialog.show();
		
		return dialog;
	}
	
	private String getFacebookLikeBoxUrl(String url) {
		/*
		return "http://www.facebook.com/plugins/likebox.php?href=" + url + 
				"&width=292&height=558&colorscheme=light&show_faces=true&header=false&" +
				"stream=true&show_border=false&appId=" + 
				AdvertisementActivity.this.getString(R.string.app_id);
		*/
		
		return url;
	}
	
	/*
	private boolean startFacebookIntent(String uri) {
		boolean result = true;
		
		try { 
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
			startActivity(intent);   
		} catch (ActivityNotFoundException ex){
			result = false;
			showInstallFacebookAppDialog();
		}
		
		return result;
	}
	*/
	
	private void showInstallFacebookAppDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(true);
		
		dialog.setMessage("尚未安裝Facebook應用程式");
		dialog.setPositiveButton("前往下載", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				startGoogleMarketIntent("com.facebook.katana");
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Toast.makeText(AdvertisementActivity.this, "請安裝Facebook應用程式", Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}
	
	private void showUpdateFacebookAppDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setCancelable(true);
		
		dialog.setMessage("您目前所使用的Facebook應用程式不支援此功能，請更新應用程式");
		dialog.setPositiveButton("前往更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				startGoogleMarketIntent("com.facebook.katana");
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Toast.makeText(AdvertisementActivity.this, "請更新Facebook應用程式", Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}
	
	private boolean startGoogleMarketIntent(String appId) {
		boolean result = true;
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + appId));
			startActivity(intent); 
		} catch (ActivityNotFoundException ex){
			result = false;
			Toast.makeText(this, "無法前往Google Play", Toast.LENGTH_LONG).show();
		}
		
		return result;
	}
	
	private boolean isAppInstalled(String packageName) {
		/*
		 * 在New HTC One上失敗...
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        Log.d(this.getClass().toString(), "isAppInstalled, packageName: " + packageName + 
        		", result: " + (intent != null));
        return (intent != null);
        */
		
		PackageManager pm = getPackageManager();

        List<ApplicationInfo> list = pm.getInstalledApplications(0);

        for(int i = 0; i < list.size(); i++) {         
        	if(list.get(i).packageName.equals(packageName)){
        		return true;
        	}
        }
        
        return false;
    }

	@Override
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		super.onCheckStatusComplete(result, lastCheckStatus);
		
		if(!hasInitialized) {
			setData();
			hasInitialized = true;
		}
		
	}
	
	private void setData() {
		advertisementData = dbAccessor.getAdvertisementData(advertisementId);
		facebookEvents = new SparseArray<FacebookAdvertisementEventData>();
		advertisementAppInfoData = dbAccessor.getAdvertisementAppInfoDataByAdvertisementId(advertisementId);


		for(FacebookAdvertisementEventData data : dbAccessor.
				getFacebookEventDataListByAdvertisementId(advertisementId)) {
			if(!data.isTypeValid()) {
				continue;
			}
			
			int type = data.getType();
			
			// 同一種類別只能有一項:
			
			if(facebookEvents.get(type) == null) {
				facebookEvents.put(type, data);
			}
		}

		// 要先設定eventView
		setFacebookLikedEvent();
		setFacebookSharedEvent();
		setSignupEvent();
		setAppDownloadedEvent();
		setAdvertisement();
	}
	
	private SimpleImageLoadingListener imageLoadingListener = new SimpleImageLoadingListener() {
		
		@Override
	    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
			
			// recycle previous bitmap:
			if(advertisementBitmap != null) {
				advertisementBitmap.recycle();
			}
			
			advertisementBitmap = loadedImage;
			advertisementImageView.setImageBitmap(loadedImage);
			
			
			if(isGameAvailable()) {
				advertisementImageView.setVisibility(View.GONE);
				setGame();
			}
			
			//System.out.println("loadingLayout : "  + loadingLayout);
			loadingLayout.setVisibility(View.GONE);
		}
		
		
		public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
			Log.d(this.getClass().toString(), "onLoadingFailed");
			
			if(loadingProgressBar.getVisibility() != View.GONE) {
				loadingProgressBar.setVisibility(View.GONE);
			}
			
			if(refreshImageView.getVisibility() != View.VISIBLE) {
				refreshImageView.setVisibility(View.VISIBLE);
			}
			
			//failReason.getType();
			
		}
	};
	
	private ScratchCardView.GameListener gameListener = new ScratchCardView.GameListener() {

		@Override
		public void onProgress(float progress) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFinish() {
			Toast.makeText(AdvertisementActivity.this, "完成！", Toast.LENGTH_LONG).show();
			
			scratchCardView.setVisibility(View.GONE);
			advertisementImageView.setVisibility(View.VISIBLE);
			
			if(isGameAvailable()) {
				reportGameDone = true;
			}
			
			displayEventView(true);
		}
		
	};

}
