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
	 * @param statusOrderList : 為檢查狀態的順序
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
	 * 此方法會再呼叫UI thread去執行onProgressUpdate()
	 */
	
	void publishProgress(CheckStatus status) {
		super.publishProgress(status);
	}
	
	/**
	 * 停止執行
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
	 * 是否正在執行
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
	 * 強制中止執行，可能擲出Exception
	 */
	
	@Override
    protected void onCancelled() { //Two-phase Termination
		Log.d("CheckStatusThread.onCancelled()", "cancelled");
		stopRunning();
		
		super.onCancelled();
    }

	
	/*
	 * 由UI Thread執行
	 */
	
	@Override
	protected void onPreExecute() {		
		super.onPreExecute();
		
		/*
		progressDialog = new ProgressDialog(activity);
		progressDialog.setTitle("請稍帶片刻");
		progressDialog.setMessage("讀取中...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		*/
	}
	
	/*
	 * 非UI Thread執行
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
	 * 由UI Thread執行
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
	 * 由UI Thread執行
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
		
		
		// 確認最後檢查的狀態是否和設定的一樣
		boolean result = CheckStatusType.FINISH.equals(lastCheckStatusType);
		
		
		callback.onComplete(result, lastCheckStatusType);
	}
	
	/*
	 * 要求退出應用程式
	 */
	
	void exitApplication() {
		Log.d(this.getClass().toString(), "exit application");
		activity.exitApplication();
	}
	
	/*
	 * 執行，不同的Android版本會有不同的執行方式
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
