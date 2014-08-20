package com.count2v.coolist.game.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public abstract class GameView extends View {
	
	public GameListener mGameListener;
	 
	public GameView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}


	public GameView(Context context, AttributeSet paramAttributeSet) {
		super(context, paramAttributeSet);
	}

	public GameView(Context context, AttributeSet paramAttributeSet,int paramInt) {
		super(context, paramAttributeSet, paramInt);

	}
	/**
	 * 接收遊戲事件listener
	 * @param mGameListener
	 */
	public void setOnGameListener(GameListener mGameListener){
		this.mGameListener = mGameListener;
	}
	
	 public abstract void setGameStart();
	 public abstract void setGameFinish();
	 public abstract void setGameReset();
	 public abstract void setGamePause();
	 public abstract void setGameResume();

	 
}
