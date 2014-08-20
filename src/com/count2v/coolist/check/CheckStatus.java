package com.count2v.coolist.check;

import com.count2v.coolist.core.BaseActivity;

import android.util.Log;

abstract class CheckStatus {
	private CheckStatusAsyncTask task;
	private BaseActivity activity;
	private boolean stopRunning = false;
	
	public CheckStatus(BaseActivity activity, CheckStatusAsyncTask task) {
		this.task = task;
		this.activity = activity;
	}
	
	/**
	 * 開始狀態檢查，注意！不可由UI thread來執行
	 */

	void checkStatus() {
		
		// 檢查該狀態
		checkProcess();
		
		if(isStopRunning()) {
			return;
		}
		
		// 等待檢查完成
		synchronized(this) {
			try {
				Log.d(this.getClass().toString(), "wait()");
				this.wait();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/*
	 * 由此定義檢查狀態的流程
	 */
	
	abstract void checkProcess();
	
	/**
	 * 顯示UI，注意！必須交由UI thread來執行
	 * 狀態更新時，也會呼叫此方法要求更新UI
	 */
	abstract void displayUI();
	
	/**
	 * 關閉UI，注意！必須交由UI thread來執行
	 */
	abstract void dismissUI();
	
	/*
	 * 傳回BaseActivity
	 */
	
	protected BaseActivity getBaseActivity() {
		return activity;
	}
	
	/*
	 * 檢查完成
	 */

	protected void finish() {
		Log.d(this.getClass().toString(), "finish");
		if(!isStopRunning()) {
			stopRunning = true;
		}
		publishNewProgress();
	}
	
	/**
	 * 執行Task.onProgressUpdate()時，會來呼叫此方法
	 * 也是結束Status的最後一個步驟
	 */
	
	void onProgressUpdate() {
		if(!isStopRunning()) {
			// 更新UI
			displayUI();
			return;
		} 
		
		dismissUI();
		
		// 通知狀態已完成，結束正在checkProcess()的Thread
		synchronized(this) {
			Log.d(this.getClass().toString(), "notify()");
			notify();
		}

	}
	
	/*
	 * 停止檢查
	 */
	
	public void stopRunning() {
		Log.d(this.getClass().toString(), "stopRunning");
		stopRunning = true;
		publishNewProgress();
	}
	
	/*
	 * 是否已停止檢查狀態 (包含完成檢查)
	 */
	
	public boolean isStopRunning() {
		return stopRunning;
	}
	
	/**
	 * 通知Task去改變UI
	 */
	
	protected void publishNewProgress() {
		task.publishProgress(this);
	}
	
	/**
	 * 停止所有的狀態檢查
	 */
	
	protected void stopRunningAll() {
		Log.d(this.getClass().toString(), "stopRunningAll");
		task.stopRunning();
	}
	
	/*
	 * 要求關閉程式應用程式
	 */
	
	protected void exitApplication() {
		Log.d(this.getClass().toString(), "exit application");
		task.exitApplication();
	}
	
	abstract CheckStatusType getType();
	
}
