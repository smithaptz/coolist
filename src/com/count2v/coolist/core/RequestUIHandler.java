package com.count2v.coolist.core;

import java.util.Arrays;
import java.util.List;

import com.count2v.coolist.check.CheckStatusAsyncTask;
import com.count2v.coolist.check.CheckStatusAsyncTaskManager;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.TextView;

public class RequestUIHandler {
	
	
	/*
	 * 處裡共同的連線錯誤訊息，並顯示其對應的對話方塊
	 */
	
	public static void showErrorDialog(final BaseActivity activity, final ClientRequest<?> request, int errorCode) {
		CheckStatusAsyncTaskManager checkStatusTaskManager = CheckStatusAsyncTaskManager.getInstance(activity);
		
		ServerCommunicator.postErrorMessage(request, errorCode);
		
		// function有誤，可能是App版本太就造成的:
		if(errorCode == StatusType.MISSING_ARGUMENT || errorCode == StatusType.CONNECTION_RESPOND_NOT_JSON || 
				errorCode == StatusType.RECEIVED_JSON_NOT_MATCH) {
			
			CheckStatusAsyncTask checkStatusAsyncTask = checkStatusTaskManager.instanceCheckStatusTask(CheckStatusType.VERSION, 
					new CheckStatusAsyncTask.Callback() {
				@Override
				public void onComplete(boolean result, CheckStatusType lastCheckStatus) {
					getExitDialog(activity, "程式異常，請確認版本是否已升級").show();
				}
			});
			checkStatusAsyncTask.run();

		} else if(errorCode == StatusType.NO_PERMISSION) {
			// 沒有權限，要求使用者再次登入:
			List<CheckStatusType> checkStatusList = Arrays.asList(new CheckStatusType[] {CheckStatusType.VERSION, 
					CheckStatusType.FACEBOOK_LOGIN, CheckStatusType.SERVER_LOGIN});
			CheckStatusAsyncTask checkStatusAsyncTask = checkStatusTaskManager.instanceCheckStatusTask(checkStatusList, 
					new CheckStatusAsyncTask.Callback() {
				@Override
				public void onComplete(boolean result, CheckStatusType lastCheckStatus) {
					ServerCommunicator.execute(request);
				}
			});
			checkStatusAsyncTask.run();
		} else {
			getResendRequestDialog(activity, request).show();
		}
	}

	/*
	 * 顯示要求重新連線的對話方塊
	 */
	public static AlertDialog getResendRequestDialog(final BaseActivity activity, final ClientRequest<?> request) {
		return getResendRequestDialog(activity, request, "連線時發生錯誤");
	}
	
	/*
	 * 顯示要求重新連線的對話方塊
	 */
	
	public static AlertDialog getResendRequestDialog(final BaseActivity activity, final ClientRequest<?> request, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setPositiveButton("重新連線", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ServerCommunicator.execute(request);
			}
		});
		builder.setNegativeButton("關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				activity.exitApplication();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * 顯示只有離開應用程式選項的對話方塊
	 */
	
	public static AlertDialog getExitDialog(final BaseActivity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setNegativeButton("確定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.exitApplication();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * 顯示含有離開應用程式選項的對話方塊
	 */
	
	public static AlertDialog getDialogWithExitOption(final BaseActivity activity, String message, String postiveButtonMsg, 
			DialogInterface.OnClickListener postiveButtonClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setPositiveButton(postiveButtonMsg, postiveButtonClickListener);
		builder.setNegativeButton("關閉程式", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.exitApplication();
			}
		});
		
		return builder.create();
	}
	
	public static AlertDialog setMessageGravityCenter(AlertDialog dialog) {
		TextView messageView = (TextView) dialog.findViewById(android.R.id.message);
		messageView.setGravity(Gravity.CENTER);
		
		return dialog;
	}
	
}
