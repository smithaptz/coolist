package com.count2v.coolist.game.base;

public abstract class GameListener {
	/***
	 * ����C�������ʤ���
	 * @param percent
	 */
	 public abstract void gameCurrentPercent(float percent);
	 /***
		 * �C���O�_�i�H�}�l
		 * @param percent
		 */
	 public abstract void canGameStart(boolean canStart);
//	 /**
//	  * �C������Ĳ�o�ƥ�
//	  * @param canStart
//	  */
//	 public abstract void gameFinished(boolean canStart);
}
