package com.count2v.coolist.util;


import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class FlingDetector {
	private Context context;
	private FlingListener flingListener;
	private ConcurrentHashMap<View, GestureDetector> flingObserverMap = new ConcurrentHashMap<View, GestureDetector>();
	
	public FlingDetector(Context context, FlingListener flingListener) {
		this.context = context;
		this.flingListener = flingListener;
	}
	
	public Context getContext() {
		return context;
	}
	
	public boolean onFlingEvent(MotionEvent event, View view) {
		GestureDetector gestureDetector;
		
	
		if(flingObserverMap.containsKey(view)) {
			gestureDetector = flingObserverMap.get(view);
		} else {
			gestureDetector = instanceGestureDetector(view);
			flingObserverMap.put(view, gestureDetector);
		}
			
		return gestureDetector.onTouchEvent(event);
	}
	
	public void removeListener(View view) {
		flingObserverMap.remove(view);
	}
	
	private GestureDetector instanceGestureDetector(final View view) {
		
		return new GestureDetector(getContext(), new FlingDetectorListener() {

			@Override
			protected void onFlingLeft() {
				FlingDetector.this.flingListener.onFlingLeft(view);
			}

			@Override
			protected void onFlingRight() {
				FlingDetector.this.flingListener.onFlingRight(view);
			}
			
			@Override
			protected void onFlingUp() {
				FlingDetector.this.flingListener.onFlingUp(view);
			}

			@Override
			protected void onFlingDown() {
				FlingDetector.this.flingListener.onFlingDown(view);
			}
			
		});
	}
	
	public static class FlingListener {
		public void onFlingLeft(View view) {
			
		}
		public void onFlingRight(View view) {
			
		}
		
		public void onFlingUp(View view) {
			
		}
		
		public void onFlingDown(View view) {
			
		}
	}
	
}
