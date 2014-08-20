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
	 * �B�̦@�P���s�u���~�T���A����ܨ��������ܤ��
	 */
	
	public static void showErrorDialog(final BaseActivity activity, final ClientRequest<?> request, int errorCode) {
		CheckStatusAsyncTaskManager checkStatusTaskManager = CheckStatusAsyncTaskManager.getInstance(activity);
		
		ServerCommunicator.postErrorMessage(request, errorCode);
		
		// function���~�A�i��OApp�����ӴN�y����:
		if(errorCode == StatusType.MISSING_ARGUMENT || errorCode == StatusType.CONNECTION_RESPOND_NOT_JSON || 
				errorCode == StatusType.RECEIVED_JSON_NOT_MATCH) {
			
			CheckStatusAsyncTask checkStatusAsyncTask = checkStatusTaskManager.instanceCheckStatusTask(CheckStatusType.VERSION, 
					new CheckStatusAsyncTask.Callback() {
				@Override
				public void onComplete(boolean result, CheckStatusType lastCheckStatus) {
					getExitDialog(activity, "�{�����`�A�нT�{�����O�_�w�ɯ�").show();
				}
			});
			checkStatusAsyncTask.run();

		} else if(errorCode == StatusType.NO_PERMISSION) {
			// �S���v���A�n�D�ϥΪ̦A���n�J:
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
	 * ��ܭn�D���s�s�u����ܤ��
	 */
	public static AlertDialog getResendRequestDialog(final BaseActivity activity, final ClientRequest<?> request) {
		return getResendRequestDialog(activity, request, "�s�u�ɵo�Ϳ��~");
	}
	
	/*
	 * ��ܭn�D���s�s�u����ܤ��
	 */
	
	public static AlertDialog getResendRequestDialog(final BaseActivity activity, final ClientRequest<?> request, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setPositiveButton("���s�s�u", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				ServerCommunicator.execute(request);
			}
		});
		builder.setNegativeButton("�����{��", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				
				activity.exitApplication();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * ��ܥu�����}���ε{���ﶵ����ܤ��
	 */
	
	public static AlertDialog getExitDialog(final BaseActivity activity, String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setNegativeButton("�T�w", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				activity.exitApplication();
			}
		});
		
		return builder.create();
	}
	
	/*
	 * ��ܧt�����}���ε{���ﶵ����ܤ��
	 */
	
	public static AlertDialog getDialogWithExitOption(final BaseActivity activity, String message, String postiveButtonMsg, 
			DialogInterface.OnClickListener postiveButtonClickListener) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false);
		
		builder.setMessage(message);
		builder.setPositiveButton(postiveButtonMsg, postiveButtonClickListener);
		builder.setNegativeButton("�����{��", new DialogInterface.OnClickListener() {

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
