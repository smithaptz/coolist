package com.count2v.coolist.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public abstract class ConnectivityChangedReceiver extends BroadcastReceiver {
	private NetworkInfo networkInfo;
	private Context context;
	private boolean disconnect;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext().
				getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivityManager == null) {
			Log.d("ConnectivityChangeReceiver", "connectivityManager == null");
			networkInfo = null;
			disconnect = true;
			disconnectionAction();
		} else {
			networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo == null || !networkInfo.isConnected()) {
				Log.d("ConnectivityChangeReceiver", "(networkInfo == null || !networkInfo.isConnected()) == true");
				disconnect = true;
				disconnectionAction();
				
			} else if(disconnect) {
				Log.d("ConnectivityChangeReceiver", "network has reconnected");
				reconnectionAction();
			} else {
				Log.d("ConnectivityChangeReceiver", "networkInfo.isConnectedOrConnecting() == true");
				connectionChangeAction();
			}
		}
	}
	
	protected Context getContext() {
		return context;
	}
	
	public NetworkInfo getActiveNetworkInfo() {
		return networkInfo;
	}
	
	public abstract void disconnectionAction();
	public abstract void connectionChangeAction();
	public abstract void reconnectionAction();

	
}
