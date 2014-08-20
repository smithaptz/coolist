package com.count2v.coolist.check;


import com.count2v.coolist.core.BaseActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.net.NetworkInfo;
import android.util.Log;

class NetworkStatus extends CheckStatus {
	private AlertDialog dialog;
	private NetReceiver netReceiver;
	
	private static final int CHECK = 1;
	private static final int DISCONNECTION = 2;
	
	private int currentStatus = CHECK;

	public NetworkStatus(BaseActivity activity, CheckStatusAsyncTask task) {
		super(activity, task);
		
		initialize();
		setDialog();
	}
	
	private void initialize() {
		dialog = new ProgressDialog(getBaseActivity());
		netReceiver = new NetReceiver();
		
	}
	
	private void setDialog() {
		dialog.setCancelable(false);
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				exitApplication();
			}
		});
	}

	@Override
	void checkProcess() {
		Log.d("CheckStatusThread.checkNetworkStatus()", "checking network status...");
		
		publishNewProgress();
		
		getBaseActivity().registerConnectivityReceiver(netReceiver);
		
		if(isNetworkConnected()) {
			finish();
		}
		
		currentStatus = DISCONNECTION;
		publishNewProgress();
	}

	@Override
	void displayUI() {
		if(!dialog.isShowing()) {
			dialog.show();
		}
		
		switch(currentStatus) {
		case CHECK: 
			dialog.setMessage("正在確認網路連線狀態");
			break;
		case DISCONNECTION:
			dialog.setMessage("等待網路連線中...\n" + "請確認網路是否正常開啟");
			break;
		}
		
	}

	@Override
	void dismissUI() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
	}
	
	@Override
	protected void finish() {
		super.finish();
		
		getBaseActivity().unregisterConnectivityReceiver(netReceiver);
	}
	
	private boolean isNetworkConnected() {
		return getBaseActivity().isNetworkConnected();
	}
	
	private class NetReceiver implements BaseActivity.ConnectivityChangedListener {

		@Override
		public void disconnectionAction() {
			Log.d(this.getClass().toString(), "disconnectionAction");
		}

		@Override
		public void connectionChangeAction() {
			Log.d(this.getClass().toString(), "connectionChangeAction");
			if(isNetworkConnected()) {
				finish();
			}
		}

		@Override
		public void reconnectionAction() {
			Log.d(this.getClass().toString(), "reconnectionAction");
			if(isNetworkConnected()) {
				finish();
			}
		}
		
	}

	@Override
	CheckStatusType getType() {
		return CheckStatusType.NETWORK;
	}

}
