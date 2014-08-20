package com.count2v.coolist.check;


import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.VersionData;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.core.RequestUIHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

class VersionStatus extends CheckStatus {
	//private static final String FILE_NAME = "coolist.apk";
	
	
	private static final int FAIL_CONNECTION = -1;
	private static final int CHECK = 1;
	private static final int REQUIRE_UPDATE = 2;
	private static final int AVAILABLE_UPDATE = 3;
	
	private static boolean doNotWantToUpdateNow = false; 
	
	
	private int currentStatus = CHECK;
	
	private AlertDialog dialog;
	private Dialog errorDialog;
	private VersionData versionData;
	
	private ClientRequest<VersionData> request;

	public VersionStatus(BaseActivity activity, CheckStatusAsyncTask task) {
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
		request = ServerCommunicator.requestGetVersion(new ServerCommunicator.Callback<VersionData>() {
			
			@Override
			public void onComplete(VersionData element, int statusCode) {
				if(isStopRunning()) {
					return;
				}
				
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					if(element == null) {
						finish();
						publishNewProgress();
						return;
					}
					
					versionData = element;
					int versionCode = getVersionCode();
					if(versionCode < versionData.getMinimumVersionCode()) {
						Log.d(this.getClass().toString(), "versionCode < versionData.getMinimumVersionCode(), versionCode: " + 
								versionCode + ", versionData.getMinimumVersionCode(): " + versionData.getMinimumVersionCode());
						currentStatus = REQUIRE_UPDATE;
					} else if(versionCode < versionData.getCurrentVersionCode()) {
						Log.d(this.getClass().toString(), "versionCode < versionData.getCurrentVersionCode(), versionCode: " + 
								versionCode + ", versionData.getCurrentVersionCode(): " + versionData.getCurrentVersionCode());
						
						currentStatus = AVAILABLE_UPDATE;
					} else {
						Log.d(this.getClass().toString(), "versionCode >= versionData.getCurrentVersionCode(), versionCode: " + 
								versionCode + ", versionData.getCurrentVersionCode(): " + versionData.getCurrentVersionCode());
						
						finish();
					}
					publishNewProgress();
				} else if(statusCode == StatusType.CONNECTION_TIMEOUT || 
						statusCode == StatusType.RECEIVED_NULL_DATA) {
					ServerCommunicator.execute(request);	
				} else {
					errorDialog = RequestUIHandler.getExitDialog(getBaseActivity(), "程式錯誤");
					errorDialog.show();
				}
			}
		});
	}

	@Override
	void checkProcess() {		
		Log.d(this.getClass().toString(), "checking version status...");
		
		publishNewProgress();
		checkVersion();
	}

	@Override
	void displayUI() {
		if(!dialog.isShowing()) {
			dialog.show();
		}
		
		switch(currentStatus) {
		case CHECK:
			dialog.setMessage("正在嘗試連線伺服器...");
			break;
		case AVAILABLE_UPDATE:
			availableUpdate();
			
			break;
		case REQUIRE_UPDATE:
			requireUpdate();
			
			break;
		case FAIL_CONNECTION:
			connectionError();
			break;
			
		}
		
	}

	@Override
	void dismissUI() {
		if(dialog.isShowing()) {
			dialog.dismiss();
		}
		
		if(errorDialog != null && errorDialog.isShowing()) {
			errorDialog.dismiss();
		}
	}
	
	private void availableUpdate() {
		dismissUI();
		
		if(doNotWantToUpdateNow) {
			finish();
			return;
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
		builder.setCancelable(false);
		
		builder.setTitle("更新通知");
		builder.setMessage("檢查到新的更新" + "\n\n更新版本: " + versionData.getCurrentVersionName() +
				"\n目前版本: " + getVersionName() + "\n\n更新內容:\n" + versionData.getAnnouncement());
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dismissUI(); // 關閉目前的介面
				startUpdateProcess();
				exitApplication();
			}
		});
		builder.setNegativeButton("暫時不用", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				doNotWantToUpdateNow = true;
				finish();
			}
		});
		
		dialog = builder.create();
		dialog.show();
	}
	
	private void requireUpdate() {
		dismissUI();
		AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
		builder.setCancelable(false);
		
		builder.setTitle("更新通知");
		builder.setMessage("您目前所使用的版本不符合最低需求" + "\n\n更新版本: " + versionData.getCurrentVersionName() +
				"\n要求版本: " + versionData.getMinimumVersionName() +
				"\n目前版本: " + getVersionName() + "\n\n更新內容:\n" + versionData.getAnnouncement());
		builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dismissUI(); // 關閉目前的介面
				startUpdateProcess();
				exitApplication();
			}
		});
		builder.setNegativeButton("關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				exitApplication();
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	
	private void startUpdateProcess() {
		if(startGooglePlayIntent()) {
			return;
		}
		
		if(startWebBrowserIntent()) {
			return;
		}
	}
	
	private boolean startGooglePlayIntent() {
		String packageName = getBaseActivity().getPackageName();
		
		boolean result = true;
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + packageName));
			getBaseActivity().startActivity(intent); 
		} catch (ActivityNotFoundException ex){
			result = false;
			Toast.makeText(getBaseActivity(), "無法開啟GooglePlay", Toast.LENGTH_LONG).show();
		}
		
		return result;
	}
	
	private boolean startWebBrowserIntent() {
		String packageName = getBaseActivity().getPackageName();
		
		boolean result = true;
		try {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
			getBaseActivity().startActivity(intent); 
		} catch (ActivityNotFoundException ex){
			result = false;
			Toast.makeText(getBaseActivity(), "找不到瀏覽器", Toast.LENGTH_LONG).show();
		}
		
		return result;
	}
	
	private void connectionError() {
		RequestUIHandler.getExitDialog(getBaseActivity(), "目前無法連線至伺服器").show();
	}
	
	private int getVersionCode() {
        int versionCode = 0;
        // AndroidManifest.xml下android:versionCode
        
        PackageManager packageManager = getBaseActivity().getPackageManager();
        String packageName = getBaseActivity().getPackageName();
        
        try {
            versionCode = packageManager.getPackageInfo(packageName, 0).versionCode;
            
            Log.d(this.getClass().toString(), "VersionCode: " + versionCode);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        return versionCode;
    }
	
	private String getVersionName() {
		String result = null;
        // AndroidManifest.xml下android:versionCode
        
        PackageManager packageManager = getBaseActivity().getPackageManager();
        String packageName = getBaseActivity().getPackageName();
        
        try {
        	result = packageManager.getPackageInfo(packageName, 0).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        return result;
	}
	
	
	private void checkVersion() {
		setRequest();
		ServerCommunicator.execute(request);
	}

	@Override
	CheckStatusType getType() {
		return CheckStatusType.VERSION;
	}
	

}
