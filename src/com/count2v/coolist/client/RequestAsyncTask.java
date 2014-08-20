package com.count2v.coolist.client;

import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

class RequestAsyncTask<T> extends AsyncTask<Void, Void, ConnectionStatus<T>> {
	private long requestId;
	private ClientRequest.RequestExecutor<T> requestExecutor;
	private ClientRequest.RequestCallback requestCallback;
	private ServerCommunicator.Callback<T> callback;
	
	private boolean report = false;

	public RequestAsyncTask(long requestId, ClientRequest.RequestExecutor<T> requestExecutor, ServerCommunicator.Callback<T> callback) {
		Log.d(this.getClass().toString(), "new RequestAsyncTask");
		this.requestId = requestId;
		this.requestExecutor = requestExecutor;
		this.callback = callback;
	}
	
	@Override
	protected ConnectionStatus<T> doInBackground(Void... params) {
		Log.d(this.getClass().toString(), "doInBackground");
		return requestExecutor.exeRequest();
	}
	
	@Override
	protected void onPostExecute(ConnectionStatus<T> connctionStatus) {
		Log.d(this.getClass().toString(), "onPostExecute");
		
		int statusCode = connctionStatus.getStatus();
		T element = connctionStatus.getElement();
		
		callback.onComplete(element, statusCode);
		
		if(report) {
			requestCallback.onComplete(requestId, statusCode);
		}
	}
	
	
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
	
	public void runWithReport(ClientRequest.RequestCallback requestCallback) {
		this.requestCallback = requestCallback;
		report = true;
		run();
	}

}
