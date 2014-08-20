package com.count2v.coolist.game.base;

public abstract class GameListener {
	/***
	 * 獲取遊戲完成百分比
	 * @param percent
	 */
	 public abstract void gameCurrentPercent(float percent);
	 /***
		 * 遊戲是否可以開始
		 * @param percent
		 */
	 public abstract void canGameStart(boolean canStart);
//	 /**
//	  * 遊戲結束觸發事件
//	  * @param canStart
//	  */
//	 public abstract void gameFinished(boolean canStart);
}
