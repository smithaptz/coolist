package com.count2v.coolist.check;

import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.LoginCheckData;
import com.count2v.coolist.contact.ContactActivity;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.core.RequestUIHandler;
import com.facebook.Session;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

public class ServerLoginStatus extends CheckStatus {
	private static final int CHECK = 1;
	private static final int NOT_QUALIFIED = 2;
	private static final int LOGIN_FAIL = 3;
	
	private int currentStatus = CHECK;
	private AlertDialog dialog;
	
	private static String facebookAccessToken;
	private static Session facebookSession;
	
	private ClientRequest<LoginCheckData> loginRequest;
	private ClientRequest<Integer> userPointsRequest;
	
	
	

	public ServerLoginStatus(BaseActivity activity, CheckStatusAsyncTask task) {
		super(activity, task);
		
		initialize();
	}
	
	private void initialize() {
		dialog = new ProgressDialog(getBaseActivity());
		dialog.setCancelable(false);
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitApplication();
			}
		});
	}
	
	private void setRequest() {
		loginRequest = ServerCommunicator.requestLogin(facebookAccessToken, loginListener);
		userPointsRequest = ServerCommunicator.requestGetUserPoints(userPointsListener);
	}
	
	private ServerCommunicator.Callback<LoginCheckData> loginListener = new ServerCommunicator.Callback<LoginCheckData>() {

		@Override
		public void onComplete(LoginCheckData element, int statusCode) {
			if(isStopRunning()) {
				return;
			}
			
			if(statusCode == StatusType.CONNECTION_SUCCESS && element != null) {
				Log.d(this.getClass().toString(), "login successfully");
				getBaseActivity().setUserName(element.getUserName());
				
				ServerCommunicator.execute(userPointsRequest);
				
			} else {
				if(statusCode == StatusType.CONNECTION_TIMEOUT || 
						statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "timeout or received null data");
					ServerCommunicator.execute(loginRequest);
				} else if(statusCode == StatusType.FACEBOOK_ACCOUNT_NOT_QUALIFIED) {
					Log.d(this.getClass().toString(), "facebook account is not qualified, statusCode: " + statusCode);
					currentStatus = NOT_QUALIFIED;
					publishNewProgress();
				} else {
					Log.d(this.getClass().toString(), "fail, statusCode: " + statusCode);
					currentStatus = LOGIN_FAIL;
					publishNewProgress();
				}
			}
		}
	};
	
	private ServerCommunicator.Callback<Integer> userPointsListener = new ServerCommunicator.Callback<Integer>() {

		@Override
		public void onComplete(Integer element, int statusCode) {
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				Log.d(this.getClass().toString(), "current user points: " + element);
				getBaseActivity().setUserPoints(element);
				
				finish();
			} else {
				/*
				 * 登入完執行，不可能沒有權限。若是發生沒有權限的狀況，
				 * 又丟給RequestUIHandler.showErrorDialog，將造成無限迴圈的dialog
				 */
				
				if(statusCode == StatusType.NO_PERMISSION) {
					currentStatus = LOGIN_FAIL;
					
					publishNewProgress();
				} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
						statusCode == StatusType.RECEIVED_NULL_DATA) {
					Log.d(this.getClass().toString(), "timeout or received null data");
					ServerCommunicator.execute(userPointsRequest);
				} else {
					RequestUIHandler.showErrorDialog(getBaseActivity(), userPointsRequest, statusCode);
				}
			}
			
		}
		
	};

	@Override
	void checkProcess() {
		Log.d(this.getClass().toString(), "checking login status...");
		
		
		
		facebookSession = Session.getActiveSession();
		facebookAccessToken = (facebookSession != null) ? facebookSession.getAccessToken() : null; 

		
		// temp for testing
		
		
//		facebookAccessToken = "CAACB0MPdsowBANX0hlHTFQykf4shOEx939bjs0noKrRZB7BOkCx" +
//				"EyhZARk5IH9PeeKMwDiQQZBUZCqKvM9eoqPM5iXOJjZB263r3snf3hXKWOBR5plLkR" +
//				"chC1ZCpfOP7M4ZBvUstTg6ZB45HMkMYVh2NsXggvfEdblCW6qLFALZCqFZAO244233" +
//				"vuv3ehvuCbAE6ZBGiN9MpZCWXhVQybYrSEJ4qzg70AizN49Am5VLvqr0JlgZDZD";
		
		
		
		Log.d(this.getClass().toString(), "facebookAccessToken: " + facebookAccessToken);
		
		publishNewProgress();
		
		checkLogin();
	}

	@Override
	void displayUI() {
		
		if(!dialog.isShowing()) {
			dialog.show();
		}
		
		switch(currentStatus) {
		case CHECK:
			dialog.setMessage("正在檢查登入狀態...");
			break;
			
		case NOT_QUALIFIED:
			facebookAccountNotQualified();
			break;
			
		case LOGIN_FAIL:
			loginFail();
			break;
		}
	}

	@Override
	void dismissUI() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	private void facebookAccountNotQualified() {
		// 清除FB token cache:
		facebookSession.closeAndClearTokenInformation();
		dismissUI();
		dialog = RequestUIHandler.getDialogWithExitOption(getBaseActivity(), "您所使用的Facebook帳號不符合會員資格", "聯絡我們", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(getBaseActivity(), ContactActivity.class);
				getBaseActivity().startActivity(intent);
			}
		});
		dialog.show();
	}
	
	private void loginFail() {
		// 清除FB token cache:
		facebookSession.closeAndClearTokenInformation();
		dismissUI();
		dialog = RequestUIHandler.getDialogWithExitOption(getBaseActivity(), "無法登入", "聯絡我們", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				Intent intent = new Intent(getBaseActivity(), ContactActivity.class);
				getBaseActivity().startActivity(intent);
			}
		});
		dialog.show();
		RequestUIHandler.setMessageGravityCenter(dialog);
	}
	
	private void checkLogin() {
		setRequest();
		ServerCommunicator.execute(loginRequest);
	}

	@Override
	CheckStatusType getType() {
		return CheckStatusType.SERVER_LOGIN;
	}

}
