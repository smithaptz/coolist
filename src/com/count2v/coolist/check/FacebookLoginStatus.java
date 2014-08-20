package com.count2v.coolist.check;

import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.core.RequestUIHandler;
import com.count2v.coolist.facebook.FacebookPermissions;
import com.count2v.coolist.facebook.LoginFacebook;
import com.facebook.Session;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;

class FacebookLoginStatus extends CheckStatus {	
	private Session session;
	private boolean userNotLoggin = false;
	
	private AlertDialog dialog;

	public FacebookLoginStatus(BaseActivity activity, CheckStatusAsyncTask task) {
		super(activity, task);
		
		initialize();
	}
	
	private void initialize() {
		dialog = new ProgressDialog(getBaseActivity());
		dialog.setCancelable(false);
		dialog.setMessage("正在連線Facebook...");
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitApplication();
			}
		});
	}

	@Override
	void checkProcess() {
		Log.d("CheckStatusThread.checkFacebookLoginStatus()", "checking facebook login status...");
		
		publishNewProgress();
		
		session = Session.getActiveSession();
		
		
		
		if(isSessionAvailable()) {
			if(!FacebookPermissions.hasReadPermissions(session)) {
				session.close();
				userNotLoggin = true;
				publishNewProgress();
			} else {
				finish();
			}
			return;
		}
		
		
		Log.d("CheckStatusThread.checkFacebookLoginStatus()", "Session.getActiveSession() == null || not open");
		
		
		session = Session.openActiveSessionFromCache(getBaseActivity());
		
		/*
		 * 再次確認是否登入
		 */
		
		
		if(isSessionAvailable()) {
			if(!FacebookPermissions.hasReadPermissions(session)) {
				session.close();
				userNotLoggin = true;
				publishNewProgress();
			} else {
				finish();
			}
			return;
		}
		
		
		userNotLoggin = true;
		
		publishNewProgress();
	}

	@Override
	void displayUI() {
		
		if(!dialog.isShowing()) {
			dialog.show();
		}

		
		if(!userNotLoggin) {
			dialog.setMessage("正在確認Facebook使用者...");
			return;
		}
		
		/*
		 * 要求使用者登入的介面
		 */
		
		dismissUI();
		
		dialog = RequestUIHandler.getDialogWithExitOption(getBaseActivity(), "歡迎使用酷集點 ", "使用Facebook登入", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				Intent intent = new Intent(getBaseActivity(), LoginFacebook.class);
				getBaseActivity().startActivityForResult(intent, 1);
				// Activity會自動finish CheckStatus
			}
		});
		dialog.show();
		//RequestUIHandler.setMessageGravityCenter(dialog);
	}
	

	@Override
	void dismissUI() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	private boolean isSessionAvailable() {
		return session != null && session.isOpened();
	}

	@Override
	CheckStatusType getType() {
		return CheckStatusType.FACEBOOK_LOGIN;
	}


}
