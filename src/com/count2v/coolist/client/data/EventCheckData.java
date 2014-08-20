package com.count2v.coolist.client.data;

public class EventCheckData extends CheckData {
	private boolean repeated;
	private boolean done;
	
	public void setRepeated(boolean repeated) {
		this.repeated = repeated;
	}
	
	public void setDone(boolean done) {
		this.done = done;
	}
	
	public boolean isRepeated() {
		return repeated;
	}
	
	public boolean isDone() {
		return done;
	}
	
	@Override
	public String toString() {
		return "[success: " + isSuccess() + ", errorCode: " + getErrorCode() + ", log: "+ getLog() + 
				", repeated: " + isRepeated() + ", done: " + isDone() + "]";
	}
	
}
