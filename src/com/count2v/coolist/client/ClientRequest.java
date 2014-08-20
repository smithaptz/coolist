package com.count2v.coolist.client;

import android.util.Log;

public class ClientRequest<T> {
	private static long CurrentRequestId = 0;
	public final long REQUEST_ID;
	
	ServerCommunicator.Callback<T> callback;
	RequestExecutor<T> requestExecutor;
	
	interface RequestExecutor<T> {
		ConnectionStatus<T> exeRequest();
		String getQuery();
	}
	
	interface RequestCallback {
		void onComplete(long requestId, int statusCode);
	}
	
	ClientRequest(ServerCommunicator.Callback<T> callback, RequestExecutor<T> requestExecutor) {
		this.callback = callback;
		this.requestExecutor = requestExecutor;
		
		CurrentRequestId++;
		REQUEST_ID = CurrentRequestId;
	}
	
	/*
	 * �ѩI�s����k��Thread�Ӱ���Request
	 */
	
	void executeOnDefaultThread() {
		ConnectionStatus<T> connctionStatus = requestExecutor.exeRequest();
		
		
		Log.d(this.getClass().toString(), "executeOnDefaultThread()");
			
		int statusCode = connctionStatus.getStatus();
		T element = connctionStatus.getElement();
			
		callback.onComplete(element, statusCode);
	}
	
	/*
	 * �Ұ�AsyncTask�Ӱ���Request
	 */
	
	void execute() {
		RequestAsyncTask<T> task = new RequestAsyncTask<T>(REQUEST_ID, requestExecutor, callback);
		task.run();
	}
	
	/*
	 * �Ұ�AsyncTask�Ӱ���Request
	 * ������|�I�srequestCallback
	 */
	
	void executeWithReport(RequestCallback requestCallback) {
		RequestAsyncTask<T> task = new RequestAsyncTask<T>(REQUEST_ID, requestExecutor, callback);
		task.runWithReport(requestCallback);
	}
	
	@Override
	public String toString() {
		return "Request: " + requestExecutor.getQuery();
	}
	
}
