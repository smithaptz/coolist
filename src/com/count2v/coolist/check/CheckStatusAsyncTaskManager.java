package com.count2v.coolist.check;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.util.Log;

import com.count2v.coolist.check.CheckStatusAsyncTask.Callback;
import com.count2v.coolist.core.BaseActivity;

/*
 * Manager���ت��O���F��Activity�Ȱ���(�ponPause())�A
 * �i�H�N�X�U�Ҧ���CheckStatusTask��������A
 * �t�@�ӥت��O�i�H����RequestUIHandler�ҵo�X��CheckStatusTask
 * ��Activity����Ҧ���CheckStatusTask
 */

public class CheckStatusAsyncTaskManager {
	private List<CheckStatusAsyncTask> taskList = Collections.synchronizedList(
			new ArrayList<CheckStatusAsyncTask>());
	private static ConcurrentHashMap<BaseActivity, CheckStatusAsyncTaskManager> taskManagerMap = new 
			ConcurrentHashMap<BaseActivity, CheckStatusAsyncTaskManager>();
	
	private BaseActivity activity;
	
	/*
	 * ����Singleton�A�C��BaseActivty�u�|����ۦP��CheckStatusAsyncTaskManager
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
	 * ���ëغc��
	 */
	
	private CheckStatusAsyncTaskManager(BaseActivity activity) {
		this.activity = activity;
	}
	
	/*
	 * ���oBaseActivity
	 */
	
	private BaseActivity getBaseActivtiy() {
		return activity;
	}
	
	/*
	 * ���oCheckStatusAsyncTask
	 */
	
	public CheckStatusAsyncTask instanceCheckStatusTask(List<CheckStatusType> statusOrderList, Callback callback) {
		CheckStatusAsyncTask result = new CheckStatusAsyncTask(getBaseActivtiy(), statusOrderList,  callback);
		taskList.add(result);
		
		Log.d(this.getClass().toString(), "activity: " + activity + ", taskList.size(): " + taskList.size() + 
				", taskList: " + taskList + ", map: " + taskManagerMap);
		
		return result;
	}
	
	/*
	 * ���oCheckStatusAsyncTask
	 */
	
	public CheckStatusAsyncTask instanceCheckStatusTask( CheckStatusType checkStatus, Callback callback) {
		CheckStatusAsyncTask result = new CheckStatusAsyncTask(getBaseActivtiy(), checkStatus,  callback);
		taskList.add(result);
		
		Log.d(this.getClass().toString(), "activity: " + activity + ", taskList.size(): " + taskList.size() + 
				", taskList: " + taskList + ", map: " + taskManagerMap);
		
		return result;
	}
	
	/*
	 * ������檬�A�ˬd
	 */
	
	public void stopRunning(CheckStatusAsyncTask task) {
		if(!task.isStopRunning()) {
			task.stopRunning();
		}
		
		taskList.remove(task);
	}
	
	/*
	 * �������Ҧ������A�ˬd
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
