package com.count2v.coolist.util;


import android.view.GestureDetector;
import android.view.MotionEvent;

abstract class FlingDetectorListener implements GestureDetector.OnGestureListener {
	public static final int DEFAULT_FLING_MIN_DISTANCE = 20;
	public static final int DEFAULT_FLING_MIN_VELOCITY = 0;
	
	public int flingMinDistance = DEFAULT_FLING_MIN_DISTANCE;
	public int flingMinVelocity = DEFAULT_FLING_MIN_VELOCITY; 
	
	public void setFlingMinDistance(int value) {
		flingMinDistance = value;
	}
	
	public void setFlingMinVelocity(int value) {
		flingMinVelocity = value;
	}
	
	public int getFlingMinDistance() {
		return flingMinDistance;
	}
	
	public int getFlingMinVelocity() {
		return flingMinVelocity;
	}

	protected abstract void onFlingLeft();
	protected abstract void onFlingRight();
	protected abstract void onFlingUp();
	protected abstract void onFlingDown();
	
	@Override
	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		boolean result = false;
		
		
		if(Math.abs(velocityX) > getFlingMinVelocity() && Math.abs(e1.getX() - e2.getX()) > getFlingMinDistance()) {
			if (e1.getX() > e2.getX()) {
				onFlingLeft();
			} else {
				onFlingRight();
			}
			
			result = true;
		}
		
		if(Math.abs(velocityY) > getFlingMinVelocity() && Math.abs(e1.getY() - e2.getY()) > getFlingMinDistance()) {
			if (e1.getY() > e2.getY()) {
				onFlingUp();
			} else {     
				onFlingDown();
			}
			
			result = true;
		}    
		
		
		
		return result;
	}

	@Override
	public void onLongPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
