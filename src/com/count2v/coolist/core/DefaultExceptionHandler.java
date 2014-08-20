package com.count2v.coolist.core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;

public class DefaultExceptionHandler implements UncaughtExceptionHandler {
	private static final int ANDROID_OS_SDK_VERSION = android.os.Build.VERSION.SDK_INT;
	
	private static final String MODEL = android.os.Build.MODEL;
	private static final String MANUFACTURER = android.os.Build.MANUFACTURER;
	
	private BaseActivity activity;
	private UncaughtExceptionHandler exceptionHandler;
	
	
	public DefaultExceptionHandler(BaseActivity activity, UncaughtExceptionHandler exceptionHandler) {
		this.activity = activity;
		this.exceptionHandler = exceptionHandler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		PackageInfo packageInfo = getPackageInfo();
		int versionCode = packageInfo.versionCode;
		String versionName = packageInfo.versionName;
		
		StringWriter trace = new StringWriter();
		ex.printStackTrace(new PrintWriter(trace));
		
		ServerCommunicator.postErrorMessage("VERSION_CODE: " + versionCode + ", VERSION_NAME: " + versionName + 
				", ANDROID_OS_SDK_VERSION: " + ANDROID_OS_SDK_VERSION + ", MANUFACTURER: " + MANUFACTURER + ", MODEL: " + MODEL + 
				", printStackTrace: " + trace.toString(), 
				StatusType.EXCEPTION);
		
		try {
			thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		exceptionHandler.uncaughtException(thread, ex);
		
	}
	
	private BaseActivity getBaseActivity() {
		return activity;
	}
	
	private PackageInfo getPackageInfo() {        
        PackageManager packageManager = getBaseActivity().getPackageManager();
        String packageName = getBaseActivity().getPackageName();
        
        try {
        	return packageManager.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        
        return null;
    }
	
	

}
