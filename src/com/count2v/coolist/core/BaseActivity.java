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
		netProgressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "�����{��", new DialogInterface.OnClickListener() {

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
	 * �]�w�O�_��ܨϥΪ̸�T�C
	 */
	
	protected void setUserInfoBarVisibility(int visibility) {
		userInfoView.setVisibility(visibility);
	}
	
	/*
	 * �]�wSubLayout�W��View
	 */
	
	@Override
	public void setContentView(int layoutResID) {
		ViewGroup subLayout = (ViewGroup) LayoutInflater.from(this).inflate(layoutResID, null);
		
		setContentView(subLayout, new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/*
	 * �]�wSubLayout�W��View
	 */
	
	@Override
	public void setContentView(View view) {
		setContentView(view, new ViewGroup.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
	}
	
	/*
	 * �]�wSubLayout�W��View
	 */
	
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params) {
		removeSubLayout();
		subLayout = (ViewGroup) view;
		mainLayout.addView(subLayout, params);
	}
	
	/*
	 * �s�WView��SubLayout�W
	 */
	
	@Override
	public void addContentView(View view, ViewGroup.LayoutParams params) {
		subLayout.addView(view, params);
	}
	
	/*
	 * ����SubLayout
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
	 * �]�w�ϥΪ̦W��
	 */
	
	public void setUserName(String name) {
		userName = name;
		userNameFieldText.setText("�Τ�W�� " + userName);
	}
	
	/*
	 * �]�w�ϥΪ��I��
	 */
	
	public void setUserPoints(int points) {
		userPoints = points;
		userPointsFieldText.setText("�I�� " + String.valueOf(points) + " �I");
	}
	
	public void setNewUser(boolean result) {
		Log.d(this.getClass().toString(), "NewUser: " + result);
		newUser = result;
	}
	
	/*
	 * ���o�ϥΪ̪��I��
	 */
	
	public int getUserPoints() {
		return userPoints;
	}
	
	/*
	 * ���o�ϥΪ̦W��
	 */
	
	public String getUserName() {
		return userName;
	}
	
	public boolean isNewUser() {
		return newUser;
	}
	
	/*
	 * �ˬd���A�A�Y�S�]�w"setCheckStatus"�A�h�ϥιw�]�ˬd�y�{
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
	 * �]�w�n�ˬd�����A���O���ǡA�Y���]�w�h�ϥιw�]�ˬd�y�{
	 */
	
	protected void setCheckStatus(List<CheckStatusType> checkStatusList) {
		this.checkStatusList = checkStatusList;
	}
	
	/*
	 * �������h�s�u�ɷ|�I�s����k
	 */
	
	protected void disconnectionAction() {
		if(getActiveNetworkInfo() != null) {
			Log.d(this.getClass().toString(), getActiveNetworkInfo().getTypeName());
		}
		
		if(!isKeepConnectionAllTheTime()) {
			Log.d(this.getClass().toString(), "isKeepConnectionAllTheTime() : false");
			return;
		}
		
		netProgressDialog.setMessage("�����w���h�s�u�A���ݳs�u��...");
		
		netProgressDialog.show();
	}
	
	/*
	 * ������s�u���A�ܤƮɷ|�X������k
	 * �Ҧp��3G�ܦ�WIFI
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
	 * ���s�s�W�����ɷ|�I�s����k
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
	 * ���A�ˬd������A�|�I�s����k
	 * �T�{DBUpdatedService�O�_�s�b�A���s�b�N�A������
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
	 * ���h��e�����s�u�����A�T��
	 * �Y�����S���s�u�h�Ǧ^null
	 */
	
	public NetworkInfo getActiveNetworkInfo() {
		return netReceiver.getActiveNetworkInfo();
	}
	
	
	public boolean isNetworkConnected() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		
		return networkInfo != null && networkInfo.isConnected();
	}
	
	
	/*
	 * ���U��ť�����ܤƪ�listener
	 */
	
	public void registerConnectivityReceiver(ConnectivityChangedListener listener) {
		Log.d(this.getClass().toString(), "registerConnectivityReceiver");
		synchronized(ConnectivityChangedListenerList) {
			ConnectivityChangedListenerList.add(listener);
		}
		
	}
	
	/*
	 * ���P��ť�����ܤƪ�listener
	 */
	
	public void unregisterConnectivityReceiver(ConnectivityChangedListener listener) {
		Log.d(this.getClass().toString(), "unregisterConnectivityReceiver");
		synchronized(ConnectivityChangedListenerList) {
			ConnectivityChangedListenerList.remove(listener);
		}
	}
	
	/*
	 * �O�_�ݭn���{�O���s�u
	 */
	
	protected boolean isKeepConnectionAllTheTime() {
		return keepConnectionAllTheTime;
	}
	
	
	/*
	 * �O�_�j����{�s�u�A�Y�@�_�u�A�h�ߨ谱��ϥΪ̥ثe���ާ@�A�����_�s�u����
	 */
	
	protected void enableConnectionAllTheTime(boolean enable) {
		keepConnectionAllTheTime = enable;
	}
	
	/*
	 * �P�_�O�_�h�X���ε{���A�ت��O���F�i�H�s��finish Activity
	 */
	
	private boolean isExitApplication() {
		return exit;
	}
	
	/*
	 * �h�X���ε{��
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
	 * �T�{Service�O�_�s�b
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
