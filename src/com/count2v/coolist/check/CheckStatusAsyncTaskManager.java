package com.count2v.coolist.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

import com.count2v.coolist.check.CheckStatusAsyncTask.Callback;
import com.count2v.coolist.core.BaseActivity;

/*
 * Manager的目的是為了當Activity暫停時(如onPause())，
 * 可以將旗下所有的CheckStatusTask全部停止，
 * 另一個目的是可以控制RequestUIHandler所發出的CheckStatusTask
 * 讓Activity控制所有的CheckStatusTask
 */

public class CheckStatusAsyncTaskManager {
	private List<CheckStatusAsyncTask> taskList = Collections.synchronizedList(
			new ArrayList<CheckStatusAsyncTask>());
	private static ConcurrentHashMap<BaseActivity, CheckStatusAsyncTaskManager> taskManagerMap = new 
			ConcurrentHashMap<BaseActivity, CheckStatusAsyncTaskManager>();
	
	private BaseActivity activity;
	
	/*
	 * 類似Singleton，每個BaseActivty只會拿到相同的CheckStatusAsyncTaskManager
	 */
	
	public static CheckStatusAsyncTaskManager getInstance(BaseActivity activity) {
		if(taskManagerMap.containsKey(activity)) {
			return taskManagerMap.get(activity);
		}
		
		CheckStatusAsyncTaskManager taskManager = new CheckStatusAsyncTaskManager(activity);
		taskManagerMap.put(activity, taskManager);
		
		return taskManager;
	}
	
	/*
	 * 隱藏建構式
	 */
	
	private CheckStatusAsyncTaskManager(BaseActivity activity) {
		this.activity = activity;
	}
	
	/*
	 * 取得BaseActivity
	 */
	
	private BaseActivity getBaseActivtiy() {
		return activity;
	}
	
	/*
	 * 取得CheckStatusAsyncTask
	 */
	
	public CheckStatusAsyncTask instanceCheckStatusTask(List<CheckStatusType> statusOrderList, Callback callback) {
		CheckStatusAsyncTask result = new CheckStatusAsyncTask(getBaseActivtiy(), statusOrderList,  callback);
		taskList.add(result);
		
		Log.d(this.getClass().toString(), "activity: " + activity + ", taskList.size(): " + taskList.size() + 
				", taskList: " + taskList + ", map: " + taskManagerMap);
		
		return result;
	}
	
	/*
	 * 取得CheckStatusAsyncTask
	 */
	
	public CheckStatusAsyncTask instanceCheckStatusTask( CheckStatusType checkStatus, Callback callback) {
		CheckStatusAsyncTask result = new CheckStatusAsyncTask(getBaseActivtiy(), checkStatus,  callback);
		taskList.add(result);
		
		Log.d(this.getClass().toString(), "activity: " + activity + ", taskList.size(): " + taskList.size() + 
				", taskList: " + taskList + ", map: " + taskManagerMap);
		
		return result;
	}
	
	/*
	 * 停止執行狀態檢查
	 */
	
	public void stopRunning(CheckStatusAsyncTask task) {
		if(!task.isStopRunning()) {
			task.stopRunning();
		}
		
		taskList.remove(task);
	}
	
	/*
	 * 停止執行所有的狀態檢查
	 */
	
	public void stopRunningAll() {
		Log.d(this.getClass().toString(), "stopRunningAll, taskList.size(): " + taskList.size() + 
				", taskManagerMap.size()" + taskManagerMap.size());
		for(CheckStatusAsyncTask task : taskList) {
			if(!task.isStopRunning()) {
				task.stopRunning();
			}
		}
		taskList.clear();
		taskManagerMap.remove(getBaseActivtiy());
	}
}
