package com.count2v.coolist.check;

import com.count2v.coolist.core.BaseActivity;

import android.util.Log;

class CheckStatusSimpleFactory {
	private BaseActivity activity;
	private CheckStatusAsyncTask task;
	
	public CheckStatusSimpleFactory(BaseActivity activity, CheckStatusAsyncTask task) {
		this.activity = activity;
		this.task = task;
	}
	
	public CheckStatus getInstance(CheckStatusType type) {
		switch(type) {
		case NETWORK:
			return new NetworkStatus(activity, task);
		case VERSION:
			return new VersionStatus(activity, task);
		case FACEBOOK_LOGIN:
			return new FacebookLoginStatus(activity, task);
		case SERVER_LOGIN:
			return new ServerLoginStatus(activity, task);
		case FINISH:
			return new FinishStatus(activity, task);
		default:
			Log.e(this.getClass().toString(), "Cannot match the input type.");
			break;
		}
		
		return null;
	}
	
	
}
