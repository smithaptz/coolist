package com.count2v.coolist.core;



import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.count2v.coolist.R;
import com.count2v.coolist.check.CheckStatusAsyncTask;
import com.count2v.coolist.check.CheckStatusAsyncTaskManager;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.net.ConnectivityChangedReceiver;
import com.google.analytics.tracking.android.EasyTracker;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public abstract class BaseActivity extends Activity {
	
	public static final List<CheckStatusType> FULL_CHECK_STATUS_LIST = Arrays.asList(
			new CheckStatusType[] {CheckStatusType.NETWORK, CheckStatusType.VERSION, CheckStatusType.FACEBOOK_LOGIN, CheckStatusType.SERVER_LOGIN});

//	public static final List<CheckStatusType> FULL_CHECK_STATUS_LIST = Arrays.asList(
//			new CheckStatusType[] {CheckStatusType.NETWORK, CheckStatusType.VERSION, CheckStatusType.SERVER_LOGIN});

	public static final List<CheckStatusType> DEFAULT_CHECK_STATUS_LIST = Arrays.asList(
			new CheckStatusType[] {CheckStatusType.NETWORK});
	
	//private static boolean hasSetExceptionHandler = false;
	private static boolean exit = false;
	
	private NetReceiver netReceiver;
	private IntentFilter intentFilter;
	
	private List<CheckStatusType> checkStatusList;
	private CheckStatusAsyncTask checkStatusAsyncTask;
	
	private LinearLayout mainLayout;
	private ViewGroup userInfoView;
	private TextView userNameFieldText;
	private TextView userPointsFieldText;
	
	private ViewGroup subLayout;
	
	private static String userName = "";
	private static int userPoints = 0;
	
	private static boolean newUser = false;
	
	private ProgressDialog netProgressDialog;
	private boolean keepConnectionAllTheTime = true;
	
	private List<ConnectivityChangedListener> ConnectivityChangedListenerList;
	
	private CheckStatusAsyncTaskManager checkStatusTaskManager;
	
	private boolean hasBindedDBUpdatedService = false;
	
	private EasyTracker analyticsTracker;
	
	public interface ConnectivityChangedListener {
		void disconnectionAction();
		void connectionChangeAction();
		void reconnectionAction();
	}
	
	
	@Override
	protected void onStart() {
		super.onStart();
		analyticsTracker.activityStart(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		if(isExitApplication()) {
			exitApplication();
		}
		
		refreshUserPointsView();
		
		//Log.d(this.getClass().toString(), "registerReceiver");
		//registerReceiver(netReceiver, intentFilter);  
		
		checkStatus();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		checkStatusTaskManager.stopRunningAll();
		
		//Log.d(this.getClass().toString(), "unregisterReceiver");
		//unregisterReceiver(netReceiver);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		analyticsTracker.activityStop(this);
	}
	
	@Override
	protected void onDestroy() {
		
		if(hasBindedDBUpdatedService) {			
			Log.d(this.getClass().toString(), "unbindService");
			unbindService(serviceConnection);
			hasBindedDBUpdatedService = false;
		}

		super.onDestroy();
		
		Log.d(this.getClass().toString(), "unregisterReceiver");
		unregisterReceiver(netReceiver);
		
		exit = false;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		analyticsTracker = EasyTracker.getInstance(this);

		initialize();
		setView();
		setListener();
	}
	
	private void initialize() {
		/*
		if(!hasSetExceptionHandler) {
			Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler(
					this, Thread.getDefaultUncaughtExceptionHandler()));
			hasSetExceptionHandler = true;
		}
		*/
		
		intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);  
		netReceiver = new NetReceiver();
		checkStatusList = DEFAULT_CHECK_STATUS_LIST;
		ConnectivityChangedListenerList = new CopyOnWriteArrayList<ConnectivityChangedListener>();
		checkStatusTaskManager = CheckStatusAsyncTaskManager.getInstance(this);
		
		Log.d(this.getClass().toString(), "registerReceiver");
		registerReceiver(netReceiver, intentFilter);  
	}
	
	private void setView() {
		mainLayout = (LinearLayout) LayoutInflater.from(this).
				inflate(R.layout.activity_base, null);
		super.setContentView(mainLayout);
		
		userInfoView = (ViewGroup) findViewById(R.id.baseActivityLinearLayout01);
		
		userNameFieldText = (TextView) findViewById(R.id.baseActivityTextView01);
		userPointsFieldText = (TextView) findViewById(R.id.baseActivityTextView02);
		
		setUserName(userName);
		setUserPoints(userPoints);
		
		netProgressDialog = new ProgressDialog(this);
		netProgressDialog.setCancelable(false);
	}
	
	private void setListener() {
		netProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitApplication();
			}
		});
	}
	
	protected EasyTracker getAnalyticsTracker() {
		return analyticsTracker;
	}
	
	/*
	 * 設定是否顯示使用者資訊列
	 */
	
	protected void setUserInfoBarVisibility(int visibility) {
		userInfoView.setVisibility(visibility);
	}
	
	/*
	 * 設定SubLayout上的View
	 */
	
	@Override
	public void setContentView(int layoutResID) {
		ViewGroup subLayout = (ViewGroup) LayoutInflater.from(this).inflate(layoutResID, null);
		
		setContentView(subLayout, new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/*
	 * 設定SubLayout上的View
	 */
	
	@Override
	public void setContentView(View view) {
		setContentView(view, new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/*
	 * 設定SubLayout上的View
	 */
	
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		removeSubLayout();
		subLayout = (ViewGroup) view;
		mainLayout.addView(subLayout, params);
	}
	
	/*
	 * 新增View到SubLayout上
	 */
	
	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		subLayout.addView(view, params);
	}
	
	/*
	 * 移除SubLayout
	 */
	
	private void removeSubLayout() {
		if(subLayout == null) {
			return;
		}
		
		mainLayout.removeView(subLayout);
	}
	
	public void refreshUserPointsView() {
		setUserPoints(getUserPoints());
	}
	
	/*
	 * 設定使用者名稱
	 */
	
	public void setUserName(String name) {
		userName = name;
		userNameFieldText.setText("用戶名稱 " + userName);
	}
	
	/*
	 * 設定使用者點數
	 */
	
	public void setUserPoints(int points) {
		userPoints = points;
		userPointsFieldText.setText("點數 " + String.valueOf(points) + " 點");
	}
	
	public void setNewUser(boolean result) {
		Log.d(this.getClass().toString(), "NewUser: " + result);
		newUser = result;
	}
	
	/*
	 * 取得使用者的點數
	 */
	
	public int getUserPoints() {
		return userPoints;
	}
	
	/*
	 * 取得使用者名稱
	 */
	
	public String getUserName() {
		return userName;
	}
	
	public boolean isNewUser() {
		return newUser;
	}
	
	/*
	 * 檢查狀態，若沒設定"setCheckStatus"，則使用預設檢查流程
	 */
	
	protected void checkStatus() {
		checkStatusAsyncTask = checkStatusTaskManager.instanceCheckStatusTask(checkStatusList, new CheckStatusAsyncTask.Callback() {
			
			@Override
			public void onComplete(boolean result, CheckStatusType lastCheckStatus) {
				onCheckStatusComplete(result, lastCheckStatus);
			}
		});
		checkStatusAsyncTask.run();
	}
	
	/*
	 * 設定要檢查的狀態類別順序，若不設定則使用預設檢查流程
	 */
	
	protected void setCheckStatus(List<CheckStatusType> checkStatusList) {
		this.checkStatusList = checkStatusList;
	}
	
	/*
	 * 網路失去連線時會呼叫此方法
	 */
	
	protected void disconnectionAction() {
		if(getActiveNetworkInfo() != null) {
			Log.d(this.getClass().toString(), getActiveNetworkInfo().getTypeName());
		}
		
		if(!isKeepConnectionAllTheTime()) {
			Log.d(this.getClass().toString(), "isKeepConnectionAllTheTime() : false");
			return;
		}
		
		netProgressDialog.setMessage("網路已失去連線，等待連線中...");
		
		netProgressDialog.show();
	}
	
	/*
	 * 當網路連線狀態變化時會出較此方法
	 * 例如由3G變成WIFI
	 */

	protected void connectionChangeAction() {
		if(getActiveNetworkInfo() != null) {
			Log.d(this.getClass().toString(), getActiveNetworkInfo().getTypeName());
		}
		
		if(netProgressDialog.isShowing()) {
			netProgressDialog.dismiss();
		}
	}
	
	/*
	 * 重新連上網路時會呼叫此方法
	 */

	protected void reconnectAction() {
		if(getActiveNetworkInfo() != null) {
			Log.d(this.getClass().toString(), getActiveNetworkInfo().getTypeName());
		}
		
		if(!isKeepConnectionAllTheTime()) {
			Log.d(this.getClass().toString(), "isKeepConnectionAllTheTime() : false");
			return;
		}
		
		if(netProgressDialog.isShowing()) {
			netProgressDialog.dismiss();
		}
		
		checkStatus();
	}
	
	/*
	 * 當狀態檢查完成後，會呼叫此方法
	 * 確認DBUpdatedService是否存在，不存在就再次執行
	 * 
	 */
	
	
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		if(result && !hasBindedDBUpdatedService) {
				bindService(new Intent(this, DBUpdatedService.class), 
						serviceConnection, BIND_AUTO_CREATE);
				hasBindedDBUpdatedService = true;
		}
		
		/*
		if(result && !isServiceExisted(DBUpdatedService.class.getName())) {
			//Log.d(this.getClass().toString(), "startService: DBUpdatedService");
			//startService(new Intent(this, DBUpdatedService.class));
			
		}
		*/
	}
	
	
	/*
	 * 拿去當前網路連線的狀態訊息
	 * 若網路沒有連線則傳回null
	 */
	
	public NetworkInfo getActiveNetworkInfo() {
		return netReceiver.getActiveNetworkInfo();
	}
	
	
	public boolean isNetworkConnected() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnected();
	}
	
	
	/*
	 * 註冊監聽網路變化的listener
	 */
	
	public void registerConnectivityReceiver(ConnectivityChangedListener listener) {
		Log.d(this.getClass().toString(), "registerConnectivityReceiver");
		synchronized(ConnectivityChangedListenerList) {
			ConnectivityChangedListenerList.add(listener);
		}
		
	}
	
	/*
	 * 註銷監聽網路變化的listener
	 */
	
	public void unregisterConnectivityReceiver(ConnectivityChangedListener listener) {
		Log.d(this.getClass().toString(), "unregisterConnectivityReceiver");
		synchronized(ConnectivityChangedListenerList) {
			ConnectivityChangedListenerList.remove(listener);
		}
	}
	
	/*
	 * 是否需要全程保持連線
	 */
	
	protected boolean isKeepConnectionAllTheTime() {
		return keepConnectionAllTheTime;
	}
	
	
	/*
	 * 是否強制全程連線，若一斷線，則立刻停止使用者目前的操作，直到恢復連線為止
	 */
	
	protected void enableConnectionAllTheTime(boolean enable) {
		keepConnectionAllTheTime = enable;
	}
	
	/*
	 * 判斷是否退出應用程式，目的是為了可以連鎖finish Activity
	 */
	
	private boolean isExitApplication() {
		return exit;
	}
	
	/*
	 * 退出應用程式
	 */
	
	public void exitApplication() {
		exit = true;
		
		stopService(new Intent(this, DBUpdatedService.class));
		
		this.finish();
	}
	
	private class NetReceiver extends ConnectivityChangedReceiver {

		@Override
		public void disconnectionAction() {
			BaseActivity.this.disconnectionAction();
			
			synchronized(ConnectivityChangedListenerList) {
				for(ConnectivityChangedListener listener : ConnectivityChangedListenerList) {
					listener.disconnectionAction();
				}
			}
			
		}

		@Override
		public void connectionChangeAction() {
			BaseActivity.this.connectionChangeAction();
			
			synchronized(ConnectivityChangedListenerList) {
				for(ConnectivityChangedListener listener : ConnectivityChangedListenerList) {
					listener.connectionChangeAction();
				}
			}
		}

		@Override
		public void reconnectionAction() {
			BaseActivity.this.connectionChangeAction();
			
			synchronized(ConnectivityChangedListenerList) {
				for(ConnectivityChangedListener listener : ConnectivityChangedListenerList) {
					listener.reconnectionAction();
				}
			}	
		}
		
	}
	
	/*
	 * 確認Service是否存在
	 */
	
	public boolean isServiceExisted(String className) {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager.getRunningServices(Integer.MAX_VALUE);

        if(!(serviceList.size() > 0)) {
            return false;
        }

        for(int i = 0; i < serviceList.size(); i++) {
            RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;

            if(serviceName.getClassName().equals(className)) {
                return true;
            }
        } 
        return false;
    }
	
	private ServiceConnection serviceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(BaseActivity.this.getClass().toString(), "onServiceConnected");
			
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(BaseActivity.this.getClass().toString(), "onServiceDisconnected");
		}
		
	};
	
	

}
