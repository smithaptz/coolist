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
	 * �}�l���A�ˬd�A�`�N�I���i��UI thread�Ӱ���
	 */

	void checkStatus() {
		
		// �ˬd�Ӫ��A
		checkProcess();
		
		if(isStopRunning()) {
			return;
		}
		
		// �����ˬd����
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
	 * �Ѧ��w�q�ˬd���A���y�{
	 */
	
	abstract void checkProcess();
	
	/**
	 * ���UI�A�`�N�I�������UI thread�Ӱ���
	 * ���A��s�ɡA�]�|�I�s����k�n�D��sUI
	 */
	abstract void displayUI();
	
	/**
	 * ����UI�A�`�N�I�������UI thread�Ӱ���
	 */
	abstract void dismissUI();
	
	/*
	 * �Ǧ^BaseActivity
	 */
	
	protected BaseActivity getBaseActivity() {
		return activity;
	}
	
	/*
	 * �ˬd����
	 */

	protected void finish() {
		Log.d(this.getClass().toString(), "finish");
		if(!isStopRunning()) {
			stopRunning = true;
		}
		publishNewProgress();
	}
	
	/**
	 * ����Task.onProgressUpdate()�ɡA�|�өI�s����k
	 * �]�O����Status���̫�@�ӨB�J
	 */
	
	void onProgressUpdate() {
		if(!isStopRunning()) {
			// ��sUI
			displayUI();
			return;
		} 
		
		dismissUI();
		
		// �q�����A�w�����A�������bcheckProcess()��Thread
		synchronized(this) {
			Log.d(this.getClass().toString(), "notify()");
			notify();
		}

	}
	
	/*
	 * �����ˬd
	 */
	
	public void stopRunning() {
		Log.d(this.getClass().toString(), "stopRunning");
		stopRunning = true;
		publishNewProgress();
	}
	
	/*
	 * �O�_�w�����ˬd���A (�]�t�����ˬd)
	 */
	
	public boolean isStopRunning() {
		return stopRunning;
	}
	
	/**
	 * �q��Task�h����UI
	 */
	
	protected void publishNewProgress() {
		task.publishProgress(this);
	}
	
	/**
	 * ����Ҧ������A�ˬd
	 */
	
	protected void stopRunningAll() {
		Log.d(this.getClass().toString(), "stopRunningAll");
		task.stopRunning();
	}
	
	/*
	 * �n�D�����{�����ε{��
	 */
	
	protected void exitApplication() {
		Log.d(this.getClass().toString(), "exit application");
		task.exitApplication();
	}
	
	abstract CheckStatusType getType();
	
}
