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
		dialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "�����{��", new DialogInterface.OnClickListener() {

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
					errorDialog = RequestUIHandler.getExitDialog(getBaseActivity(), "�{�����~");
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
			dialog.setMessage("���b���ճs�u���A��...");
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
		
		builder.setTitle("��s�q��");
		builder.setMessage("�ˬd��s����s" + "\n\n��s����: " + versionData.getCurrentVersionName() +
				"\n�ثe����: " + getVersionName() + "\n\n��s���e:\n" + versionData.getAnnouncement());
		builder.setPositiveButton("�ߧY��s", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dismissUI(); // �����ثe������
				startUpdateProcess();
				exitApplication();
			}
		});
		builder.setNegativeButton("�Ȯɤ���", new DialogInterface.OnClickListener() {

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
		
		builder.setTitle("��s�q��");
		builder.setMessage("�z�ثe�ҨϥΪ��������ŦX�̧C�ݨD" + "\n\n��s����: " + versionData.getCurrentVersionName() +
				"\n�n�D����: " + versionData.getMinimumVersionName() +
				"\n�ثe����: " + getVersionName() + "\n\n��s���e:\n" + versionData.getAnnouncement());
		builder.setPositiveButton("�ߧY��s", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				dismissUI(); // �����ثe������
				startUpdateProcess();
				exitApplication();
			}
		});
		builder.setNegativeButton("�����{��", new DialogInterface.OnClickListener() {

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
			Toast.makeText(getBaseActivity(), "�L�k�}��GooglePlay", Toast.LENGTH_LONG).show();
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
			Toast.makeText(getBaseActivity(), "�䤣���s����", Toast.LENGTH_LONG).show();
		}
		
		return result;
	}
	
	private void connectionError() {
		RequestUIHandler.getExitDialog(getBaseActivity(), "�ثe�L�k�s�u�ܦ��A��").show();
	}
	
	private int getVersionCode() {
        int versionCode = 0;
        // AndroidManifest.xml�Uandroid:versionCode
        
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
        // AndroidManifest.xml�Uandroid:versionCode
        
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
