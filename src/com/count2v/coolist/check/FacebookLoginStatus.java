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
		dialog.setMessage("���b�s�uFacebook...");
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "�����{��", new DialogInterface.OnClickListener() {

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
		 * �A���T�{�O�_�n�J
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
			dialog.setMessage("���b�T�{Facebook�ϥΪ�...");
			return;
		}
		
		/*
		 * �n�D�ϥΪ̵n�J������
		 */
		
		dismissUI();
		
		dialog = RequestUIHandler.getDialogWithExitOption(getBaseActivity(), "�w��ϥλŶ��I ", "�ϥ�Facebook�n�J", 
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				Intent intent = new Intent(getBaseActivity(), LoginFacebook.class);
				getBaseActivity().startActivityForResult(intent, 1);
				// Activity�|�۰�finish CheckStatus
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
