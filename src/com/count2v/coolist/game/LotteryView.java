package com.count2v.coolist.game;

import java.util.ArrayList;
import java.util.Iterator;

import com.count2v.coolist.game.base.GameView;
import com.count2v.coolist.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


public class LotteryView extends GameView implements View.OnTouchListener {
	private static final float DEFAULT_THICKNESS = 100.0F;
	
	private static Context mContext;
	
	/**���e�j�p*/
	private float globalThickness = DEFAULT_THICKNESS;
	private float paintDesize = 6.0F;
	
	/**�e��bitmap*/
	private Bitmap backgroundBitmap;
	/**�I��bitmap*/
	private Bitmap foregroundBitmap;
	/**�Q���e�e�L�Ȧsbitmap*/
	private Bitmap mBitmap;
	/**���paint*/
	private Paint mBitmapPaint;
	/**���e�e�Lpaint*/
	private Paint mPaint;
	/**���e�e�Lpath*/
	private Path mPath;
	private Canvas mCanvas;
	private float mX;
	private float mY;
	
	private int lastPointX;
	private int lastPointY;
	
	/**
	 * �x�s�I��϶�
	 */
	//public HashMap<Rect,Boolean> retangles = new HashMap<Rect,Boolean>();
	public ArrayList<TouchRectData> touchRectDataList = new ArrayList<TouchRectData>();
	
	
	/**
	 * �]�m�O�_�}�l�I��
	 * */
	private boolean startFlag = false;
//	private GameListener mGameListener;
	
	public LotteryView(Context paramContext) {
		super(paramContext);
		mContext = paramContext;
		//initialView();
	}

	public LotteryView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		mContext = paramContext;
		//initialView();
	}

	public LotteryView(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
		mContext = paramContext;
		//initialView();
	}

	/**
	 * �b�e���W���ʵe�X�I
	 * @param x ����x�y���I
	 * @param y ����y�y���I
	 */
	private void touch_move(float x, float y) {
		float f1 = Math.abs(x - this.mX);
		float f2 = Math.abs(y - this.mY);
		if ((f1 >= 0.05F) || (f2 >= 0.05F)) {
			this.mPath.quadTo(this.mX, this.mY, (x + this.mX) / 2.0F,
					(y + this.mY) / 2.0F);
			this.mX = x;
			this.mY = y;
		}
		if(this.mCanvas != null) {
			this.mCanvas.drawPath(this.mPath, this.mPaint);

		}
		
					
		setRectTouch(lastPointX, lastPointY, (int) x, (int) y);
		
		lastPointX = (int) x;
		lastPointY = (int) y;
//		this.mCanvas.drawPath(this.mPath, this.mBitmapPaint);
//		Log.i("test","percent"+getTouchPercentage()+"");
		
	}
	/**
	 * �}�l�I��
	 * @param paramFloat1
	 * @param paramFloat2
	 */
	private void touch_start(float x, float y) {
		this.mPath.reset();
		this.mPath.moveTo(x, y);
		this.mX = x;
		this.mY = y;
		this.mPath.lineTo(x + 0.1F, 0.1F + y);
		
		
		setRectTouch((int) x, (int) y);
		
		lastPointX = (int) x;
		lastPointY = (int) y;
	}
	/**
	 * �����}�ù�
	 */
	private void touch_up() {
		this.mPath.lineTo(this.mX, this.mY);
		if(this.mCanvas!=null)
			this.mCanvas.drawPath(this.mPath, this.mPaint);
		this.mPath.reset();
		

	}
	/**
	 * �]�wpaint�i�л\
	 */
	public void initialPaintMode() {
		resetXfermode();
		setPaintMode();
	}

	public void cleanCanvas() {
		this.mCanvas = new Canvas(this.mBitmap);
		invalidate();
	}
	/**
	 * ��l�ƩҦ�paint�åBinitialPaintMode()
	 */
	public void initialView() {
		
		this.mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		
		this.mPath = new Path();
	
		this.mPaint = new Paint();
		this.mPaint.setAntiAlias(true);
		this.mPaint.setDither(true);
		this.mPaint.setStyle(Paint.Style.STROKE);
		this.mPaint.setStrokeJoin(Paint.Join.ROUND);
		this.mPaint.setStrokeCap(Paint.Cap.ROUND);
		this.mPaint.setStrokeWidth(globalThickness);
		
		initialPaintMode();
		
	}

	/**
	 * �e����s�ɰ���
	 */
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/**
		 * �e�X�I����
		 */
		if (this.backgroundBitmap != null) {
			canvas.drawBitmap(this.backgroundBitmap, 0.0F, 0.0F,
					this.mBitmapPaint);
			
		}
		/**
		 * �e�X�e��(�D�ϵe�L)
		 */
		if(mBitmap != null){
			canvas.drawBitmap(this.mBitmap, 0.0F, 0.0F,
					this.mBitmapPaint);
		}
//		
	}

	/**
	 * �e�����e���ܫ�
	 */
	public void onSizeChanged (int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);

		if(mGameListener != null)
			mGameListener.canGameStart(true);
		
	}
	/**
	 * �]�m�e���P�I��bitmap
	 * @param backgroundBitmap �I��bitmap
	 * @param foregroundBitmap �e��bitmap
	 */
	public LotteryView setBitmap(Bitmap backgroundBitmap, Bitmap foregroundBitmap) {
		
		startFlag = true;
//		/*
//		 * ��l�ƹ�
//		 */
//		this.backgroundBitmap = BitmapFactory.decodeResource(
//				getResources(), R.drawable.b);
//
//		this.foregroundBitmap = BitmapFactory.decodeResource(
//				getResources(), R.drawable.gray);
		
		if(foregroundBitmap == null) {
			this.foregroundBitmap = BitmapFactory.decodeResource(
					getResources(), R.drawable.scratch_off_cover);
		} else{
			this.foregroundBitmap = foregroundBitmap;
		}
		
		if(backgroundBitmap == null) {
			this.backgroundBitmap = BitmapFactory.decodeResource(
					getResources(), R.drawable.scratch_off_default);
		} else{
			this.backgroundBitmap = backgroundBitmap;
		}
		
		/*
		 * �N�ϩԦ���ŦX���e
		 */
		
		//float backgroundDisplayRatio = backgroundBitmap.getWidth() / backgroundBitmap.getHeight();
		
		
		
		this.foregroundBitmap = Bitmap.createScaledBitmap(
				this.foregroundBitmap, getWidth(),
				getHeight(), true);
		
		
		
		this.backgroundBitmap = Bitmap.createScaledBitmap(
				this.backgroundBitmap, getWidth(),
				getHeight(), false);
		
		/*
		 * �e�ܿù��W
		 */
		this.mBitmap = this.foregroundBitmap.copy(Bitmap.Config.ARGB_8888, true);
		this.mCanvas = new Canvas(this.mBitmap);
		invalidate();
		
		return this;
	}
	
	public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
		return false;
	}
	
	
	
	private void initialRectTouch() {
		if(!touchRectDataList.isEmpty()) {
			touchRectDataList.clear();
		}
		
		globalThickness = Math.max(Math.min(getHeight(), getWidth()) / paintDesize, DEFAULT_THICKNESS);
		
		/**�e�װ϶�*/
		int thick = (int) globalThickness;
		int zoneW =  (int) Math.ceil(getWidth() / globalThickness);
		int zoneH = (int) Math.ceil(getHeight() / globalThickness);
		
		System.out.println("getHeight(): " + getHeight() + ", getWidth(): " + getWidth() + ", zoneH: " + zoneH + ", zoneW: " + zoneW);
		
		for(int x = 0; x < zoneW; x++) {
			for(int y = 0; y < zoneH; y++) {
				Rect mRect = new Rect(x * thick, y * thick,
						(x + 1) * thick, (y + 1) * thick);
				touchRectDataList.add(new TouchRectData(mRect));
				Log.d(this.getClass().toString(), "retangle, x from " + (x * thick) + ", to " + ((x + 1) * thick) + 
						", y from " + (y * thick) + " to " + ((y + 1) * thick));
			}
		}
		Log.d(this.getClass().toString(), "touchRectDataList.size(): " + touchRectDataList.size());
	}
	/**
	 * ����I����m ��retangles.put(mRect, true)�]��true;	
	 * @param x
	 * @param y
	 */
	private void setRectTouch(int x, int y) {
		System.out.println("x: " + x + ", y: " + y);
		
		/**
		 * �N�Ҧ�rect���X���
		 */
		if(touchRectDataList == null) {
			return;
		}
		
		for(TouchRectData touchRectData : touchRectDataList) {
			if(touchRectData.hasTouched()) {
				continue;
			}
			
			Rect rect = touchRectData.getRect();
			
			//int radius = (int) (globalThickness / 8;
			int radius = 1;
			int rectStartX = x - radius;
			int rectStartY = y - radius;
			int rectEndX =  x + radius;
			int rectEndY =  y + radius;
			
			if(rect.intersects(rectStartX, rectStartY, rectEndX, rectEndY)) {
				Log.d(this.getClass().toString(), "intersect: x: " + x + ", y: " + y + " with radius: " + radius);
				touchRectData.setTouch();
				break;
			}
		}
		
	}
	
	private void setRectTouch(int startX, int startY, int endX, int endY) {
		System.out.println("startX: " + startX + ", startY: " + startY + ", endX: " + endX + ", endY: " + endY);
		
		double chunkLength = Math.sqrt((endX - startX) * (endX - startX) + (endY - startY) * (endY - startY)) / globalThickness;
		double chunkX =  (endX - startX) / chunkLength; // sine * globalThickness
		double chunkY = (endY - startY) / chunkLength; // cosine * globalThickness
		
		Log.d(this.getClass().toString(), "chunkLength: " + chunkLength + ", chunkX: " + chunkX + ", chunkY: " + chunkY);
		
		ArrayList<PointData> pointList = new ArrayList<PointData>();
		
		for(int i = 0; i < Math.ceil(chunkLength); i++) {
			int pointX = (int) (startX + chunkX * i);
			int pointY = (int) (startY + chunkY * i);
			pointList.add(new PointData(pointX, pointY));
			Log.d(this.getClass().toString(), "insertPoint, pointX: " + pointX + ", pointY: " + pointY);
		}
		
		pointList.add(new PointData(endX, endY));
		Log.d(this.getClass().toString(), "insertPoint, pointX: " + endX + ", pointY: " + endY);
		
		/**
		 * �N�Ҧ�rect���X���
		 */
		
		if(touchRectDataList == null) {
			return;
		}
		
		for(TouchRectData touchRectData : touchRectDataList) {
			if(touchRectData.hasTouched()) {
				continue;
			}
			
			Rect rect = touchRectData.getRect();
			Iterator<PointData> iterator = pointList.iterator();
			while(iterator.hasNext()) {
				PointData pointData = iterator.next();
				int radius = 1;
				//int radius = (int) globalThickness / 8;
				int rectStartX = pointData.getPointX() - radius;
				int rectStartY = pointData.getPointY() - radius;
				int rectEndX =  pointData.getPointX() + radius;
				int rectEndY =  pointData.getPointY() + radius;
				
				if(rect.intersects(rectStartX, rectStartY, rectEndX, rectEndY)) {
					Log.d(this.getClass().toString(), "rect x from " + rect.left + " to " + rect.right + ", y from " + rect.top + " to " + rect.bottom + 
							", intersect: x: " + pointData.getPointX() + ", y: " + 
				pointData.getPointY() + " with radius: " + radius);
					touchRectData.setTouch();
					break;
				}
			}
		}
		
	}
		
	
	/**
	 * �d�ݦʤ���
	 */
	public float getTouchPercentage() {
		if(touchRectDataList == null) {
			return 0;
		}
		 
		float counter = 0.0f;
		
		for(TouchRectData touchRectData : touchRectDataList) {
			if(touchRectData.hasTouched()) {
				counter++;	
			}
		}
		
//			Log.i("test","counter "+counter+"");
//			Log.i("test","retangles.size() "+retangles.size()+"");
		
		return counter / touchRectDataList.size() * 100.0f;
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		if(!startFlag)
			return false;
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN:
			touch_start(x,y);
			
			break;
		case MotionEvent.ACTION_MOVE:
			touch_move(x,y);
			
			break;
		case MotionEvent.ACTION_UP:
			touch_up();
			
			break;
		}
		
		if(mGameListener!=null)
			mGameListener.gameCurrentPercent(getTouchPercentage());
		
		invalidate();
		
		
		
		return true;
	}

	/**
	 * ���}�{����^��bitmap
	 */
	public void releaseBitmap() {
		if (!this.mBitmap.isRecycled())
			this.mBitmap.recycle();
		if (!this.backgroundBitmap.isRecycled())
			this.backgroundBitmap.recycle();
		if (!this.foregroundBitmap.isRecycled())
			this.foregroundBitmap.recycle();
	}

	/**
	 * �N�e��bitmap���m
	 */
	public void resetCover() {
		if (this.foregroundBitmap != null) {
			this.mBitmap = this.foregroundBitmap.copy(
					Bitmap.Config.ARGB_8888, true);
			this.mCanvas = new Canvas(this.mBitmap);
			invalidate();
			initialRectTouch();
		}
	}
	/**
	 * �Npaint mode���m
	 */
	public void resetXfermode() {
		this.mBitmapPaint.setXfermode(null);
		this.mPaint.setXfermode(null);
	}
	/**
	 * �]�mpaint mode �i���|�P�M��
	 */
	public void setPaintMode() {
		this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		this.mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
	
		
	}
	/**
	 * �ϥιw�]�I���϶i��C��
	 */
	@Override
	public void setGameStart() {
		// TODO Auto-generated method stub
		
		if(backgroundBitmap == null || foregroundBitmap == null) {
			setBitmap(backgroundBitmap, foregroundBitmap);
		}
		
		initialRectTouch();
		initialView();
	}
	/**
	 * �ϥιw�]�I���϶i��C��
	 * null�h�ϥιw�]
	 * @param backgroundBitmap �I��bitmap
	 * @param foregroundBitmap �e��bitmap
	 */
	
	public void setGameStart(Bitmap backgroundBitmap,Bitmap foregroundBitmap) {
		// TODO Auto-generated method stub
		setBitmap(backgroundBitmap, foregroundBitmap);
		initialRectTouch();
		initialView();
		
	}

	@Override
	public void setGameFinish() {
		// TODO Auto-generated method stub
		this.releaseBitmap();
//		this.resetCover();
	}

	@Override
	public void setGameReset() {
		// TODO Auto-generated method stub
		this.resetCover();
		startFlag = true;
	}

	@Override
	public void setGamePause() {
		// TODO Auto-generated method stub
		startFlag = false;
	}

	@Override
	public void setGameResume() {
		// TODO Auto-generated method stub
		startFlag = true;
	}
	
	private class PointData {
		private final int pointX;
		private final int pointY;
		
		public PointData(int pointX, int pointY) {
			this.pointX = pointX;
			this.pointY = pointY;
		}
		
		public int getPointX() {
			return pointX;
		}
		
		public int getPointY() {
			return pointY;
		}
		
	}
	
	private class TouchRectData {
		private final Rect rect;
		private boolean touch = false;
		
		public TouchRectData(Rect rect) {
			this.rect = rect;
		}
		
		public void setTouch() {
			this.touch = true;
		}
		
		public Rect getRect() {
			return rect;
		}
		
		public boolean hasTouched() {
			return touch;
		}
	}
}