package com.count2v.coolist.check;

import com.count2v.coolist.core.BaseActivity;

public class FinishStatus extends CheckStatus {

	public FinishStatus(BaseActivity activity, CheckStatusAsyncTask task) {
		super(activity, task);
		// TODO Auto-generated constructor stub
	}

	@Override
	void checkProcess() {
		finish();
	}

	@Override
	void displayUI() {
		// TODO Auto-generated method stub
		
	}

	@Override
	void dismissUI() {
		// TODO Auto-generated method stub
		
	}

	@Override
	CheckStatusType getType() {
		// TODO Auto-generated method stub
		return CheckStatusType.FINISH;
	}

}
