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
	 * 由呼叫此方法的Thread來執行Request
	 */
	
	void executeOnDefaultThread() {
		ConnectionStatus<T> connctionStatus = requestExecutor.exeRequest();
		
		
		Log.d(this.getClass().toString(), "executeOnDefaultThread()");
			
		int statusCode = connctionStatus.getStatus();
		T element = connctionStatus.getElement();
			
		callback.onComplete(element, statusCode);
	}
	
	/*
	 * 啟動AsyncTask來執行Request
	 */
	
	void execute() {
		RequestAsyncTask<T> task = new RequestAsyncTask<T>(REQUEST_ID, requestExecutor, callback);
		task.run();
	}
	
	/*
	 * 啟動AsyncTask來執行Request
	 * 完成後會呼叫requestCallback
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
