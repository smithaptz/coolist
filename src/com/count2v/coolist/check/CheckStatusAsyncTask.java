package com.count2v.coolist.check;




import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.count2v.coolist.core.BaseActivity;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

public class CheckStatusAsyncTask extends AsyncTask<Void, CheckStatus, CheckStatusType> {
	private BaseActivity activity;
	private CheckStatusSimpleFactory factory;
	private List<CheckStatusType> statusOrderList;
	private List<CheckStatus> executeList;
	
	//private ProgressDialog progressDialog;
	
	private Callback callback;
	
	
	/**
	 * 
	 * @param context
	 * @param statusOrderList : ���ˬd���A������
	 */
	
	public interface Callback {
		void onComplete(boolean result, CheckStatusType lastCheckStatus);
	}
	
	CheckStatusAsyncTask(BaseActivity activity, List<CheckStatusType> statusOrderList, Callback callback) {
		this.activity = activity;
		this.statusOrderList = new ArrayList<CheckStatusType>(statusOrderList);
		this.callback = callback;
		
		initialize();
	}
	
	CheckStatusAsyncTask(BaseActivity activity, CheckStatusType checkStatus, Callback callback) {
		this.activity = activity;
		this.statusOrderList = Arrays.asList(new CheckStatusType[] {checkStatus});
		this.callback = callback;
		
		initialize();
	}
	
	private void initialize() {
		factory = new CheckStatusSimpleFactory(activity, this);
		executeList = new ArrayList<CheckStatus>();
		
		for(CheckStatusType type : statusOrderList) {
			CheckStatus status = factory.getInstance(type);
			if(status != null) {
				executeList.add(status);
			}
		}
		
		executeList.add(factory.getInstance(CheckStatusType.FINISH));
	}
	
	/*
	 * ����k�|�A�I�sUI thread�h����onProgressUpdate()
	 */
	
	void publishProgress(CheckStatus status) {
		super.publishProgress(status);
	}
	
	/**
	 * �������
	 */
	
	void stopRunning() {
		Log.d("CheckStatusThread.stopRunning()", "stopRunning");
		
		for(CheckStatus status : executeList) {
			if(!status.isStopRunning()) {
				status.stopRunning();
			}
		}
	}
	
	/**
	 * �O�_���b����
	 * @return 
	 */
	
	public boolean isStopRunning() {
		boolean result = true;
		
		for(CheckStatus status : executeList) {
			result &= status.isStopRunning();
		}

		return result;
	}
	
	/**
	 * �j������A�i���Y�XException
	 */
	
	@Override
    protected void onCancelled() { //Two-phase Termination
		Log.d("CheckStatusThread.onCancelled()", "cancelled");
		stopRunning();
		
		super.onCancelled();
    }

	
	/*
	 * ��UI Thread����
	 */
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		
		/*
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("�еy�a����");
		progressDialog.setMessage("Ū����...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		*/
	}
	
	/*
	 * �DUI Thread����
	 */
	
	@Override
	protected CheckStatusType doInBackground(Void... arg0) {	
		CheckStatusType lastCheckStatusType = null;
		
		for(CheckStatus status : executeList) {
			Log.d(this.getClass().toString(), "Next Status: " + status.getClass().toString());
			if(!status.isStopRunning()) {
				status.checkStatus();
				lastCheckStatusType = status.getType();
			}
		}
		
		return lastCheckStatusType;
	}
	
	/*
	 * ��UI Thread����
	 */
	
	@Override
	protected void onProgressUpdate(CheckStatus... progs) {
		if(progs.length != 1) {
			return;
		}
		
		CheckStatus status = progs[0];
		status.onProgressUpdate();
	}
	
	
	/*
	 * ��UI Thread����
	 */
	
	@Override
	protected void onPostExecute(CheckStatusType lastCheckStatusType) {
		super.onPostExecute(lastCheckStatusType);
		
		for(CheckStatus status : executeList) {
			if(!status.isStopRunning()) {
				status.stopRunning();
			}
		}
		
		/*
		if(progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
		*/
		
		
		// �T�{�̫��ˬd�����A�O�_�M�]�w���@��
		boolean result = CheckStatusType.FINISH.equals(lastCheckStatusType);
		
		
		callback.onComplete(result, lastCheckStatusType);
	}
	
	/*
	 * �n�D�h�X���ε{��
	 */
	
	void exitApplication() {
		Log.d(this.getClass().toString(), "exit application");
		activity.exitApplication();
	}
	
	/*
	 * ����A���P��Android�����|�����P������覡
	 */
	
	public void run() {
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			Log.d(this.getClass().toString(), "Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB");
			executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		}
		else {
			Log.d(this.getClass().toString(), "Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB");
			execute();
		}
	}
	
}
