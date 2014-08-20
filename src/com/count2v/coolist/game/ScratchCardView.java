package com.count2v.coolist.game;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.count2v.coolist.R;
import com.count2v.coolist.core.BaseActivity;

public class ScratchCardView extends View {
	public static final float DEFAULT_FINISH_PROGRESS = 0.99F;
	
	private int paintSize;
	private float paintDesize = 8.0F;
	
	private BaseActivity baseActivity;
	private GameListener gameListener;
	
	private Paint mBitmapPaint;
	
	private Paint paint;
	private Path path;
	private Canvas canvas;
	
	private Bitmap coverBitmap;
	private Bitmap contentBitmap;
	
	private float mX;
	private float mY;
	
	private float contentOffsetX;
	private float contentOffsetY;
	
	private float progress;
	
	public interface GameListener {
		void onProgress(float progress);
		void onFinish();
	}

	public ScratchCardView(BaseActivity baseActivity, GameListener gameListener) {
		super(baseActivity);
		
		this.baseActivity = baseActivity;
		this.gameListener = gameListener;
	}
	
	public void onDestroy() {
		coverBitmap.recycle();
		contentBitmap.recycle();
	}
	
	public void start(Bitmap bitmap, int width, int height) {	
		
		/*
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.outWidth = bitmap.getWidth();
		options.outHeight = bitmap.getHeight();
		options.inSampleSize = calculateInSampleSize(options, getWidth(), getHeight());
		
		byte[] contentBitmapBytes = BitmapToBytes(bitmap);
		contentBitmap = BitmapFactory.decodeByteArray(contentBitmapBytes, 0, contentBitmapBytes.length, options);

		options = new BitmapFactory.Options();
		options.inMutable = true;
		options.outWidth = getWidth();
		options.outHeight = getHeight();
		coverBitmap = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.scratch_off_cover, options);
		*/
		
		paintSize = (int) (Math.min(width, height) / paintDesize);
		
//		BitmapFactory.Options options = new BitmapFactory.Options();
//		options = new BitmapFactory.Options();
//		options.inMutable = true;
//		Bitmap tempCoverBitmap = BitmapFactory.decodeResource(baseActivity.getResources(), R.drawable.scratch_off_cover, options);
		
		Bitmap tempCoverBitmap = decodeMutableBitmapFromResourceId(baseActivity, R.drawable.scratch_off_cover);
		
		
		// 維持來源圖片相同的長寬比：
		float widthScale = (float) width / (float) bitmap.getWidth();
		float heightScale = (float) height / (float) bitmap.getHeight();
		
		float scale = (widthScale < heightScale) ? widthScale : heightScale;
		
		int scaleWidth = (int) (bitmap.getWidth() * scale);
		int scaleHeight = (int) (bitmap.getHeight() * scale);
		
		contentOffsetX = (width - scaleWidth) / 2;
		contentOffsetY = (height - scaleHeight) / 2;
		
		contentBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth, scaleHeight, true);
		coverBitmap =  Bitmap.createScaledBitmap(tempCoverBitmap, width, height, true);
		
		tempCoverBitmap.recycle();
		
		canvas = new Canvas(coverBitmap);
		
		invalidate();
		initializeView();
	}
	
	public void initializeView() {
		
		mBitmapPaint = new Paint(Paint.DITHER_FLAG);
		
		path = new Path();
	
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
		paint.setStrokeCap(Paint.Cap.ROUND);
		paint.setStrokeWidth(paintSize);
		
		setPaintMode();
		
	}
	
	
	
	/**
	 * 設置paint mode 可重疊與清除
	 */
	private void setPaintMode() {
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
		mBitmapPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
	}
	
	private void updateProgress() {
		int width = coverBitmap.getWidth();
		int height = coverBitmap.getHeight();
		
		int totalPixelCount = width * height;
		int touchPixelCount = 0;
		
		long startTime = System.currentTimeMillis();
		int touchPixel = Color.argb(0, 0, 0, 0);
		
		int delta = paintSize / 4;
		
		for(int i = 0; i < width; i += delta) {
			for(int j = 0; j < height; j += delta) {
				int pixel = coverBitmap.getPixel(i, j);
				
				if(pixel == touchPixel) {
					touchPixelCount++;
				}
			}
		}
		
		touchPixelCount *= delta * delta;
		
		long duration = System.currentTimeMillis() - startTime;
		
		progress = (float) touchPixelCount / (float) totalPixelCount;
		
		Log.d(this.getClass().toString(), "progress: " + progress + ", duration: " + duration);
		
		gameListener.onProgress(progress);
		
		if(progress >= DEFAULT_FINISH_PROGRESS) {
			gameListener.onFinish();
		}
	}
	
	
	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		
		// 畫出背景圖
		if(contentBitmap != null) {
			canvas.drawBitmap(contentBitmap, contentOffsetX, 
					contentOffsetY, mBitmapPaint);
		}
		
		
		//畫出前景(遭圖畫過
		if(coverBitmap != null){
			canvas.drawBitmap(coverBitmap, 0.0F, 0.0F, 
					mBitmapPaint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		float x = event.getX();
		float y = event.getY();
		
		switch (event.getAction()) {
		
		case MotionEvent.ACTION_DOWN:
			touchStart(x,y);
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(x,y);
			break;
		case MotionEvent.ACTION_UP:
			touchUp();
			break;
		}
		
		invalidate();
		
		return true;
	}
	
	
	private void touchMove(float x, float y) {
		float f1 = Math.abs(x - mX);
		float f2 = Math.abs(y - mY);
		
		if((f1 >= 0.05F) || (f2 >= 0.05F)) {
			path.quadTo(mX, mY, (x + mX) / 2.0F,
					(y + mY) / 2.0F);
			mX = x;
			mY = y;
		}
		
		if(canvas != null) {
			canvas.drawPath(path, paint);
		}
	}
	
	private void touchStart(float x, float y) {
		path.reset();
		path.moveTo(x, y);
		mX = x;
		mY = y;
		path.lineTo(x + 0.1F, 0.1F + y);
	}
	
	private void touchUp() {
		path.lineTo(mX, mY);
		
		if(canvas != null) {
			canvas.drawPath(path, paint);
		}
		
		path.reset();
		
		updateProgress();
	}
	
	private byte[] BitmapToBytes(Bitmap bitmap) {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
		return stream.toByteArray();
	}
	

	private static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;
	
	    if (height > reqHeight || width > reqWidth) {
	
	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);
	
	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	
	/**decodes a bitmap from a resource id. returns a mutable bitmap no matter what is the API level.<br/>
	might use the internal storage in some cases, creating temporary file that will be deleted as soon as it isn't finished*/
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static Bitmap decodeMutableBitmapFromResourceId(final Context context, final int bitmapResId) {
	    final BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	    if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB)
	        bitmapOptions.inMutable = true;
	    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), bitmapResId, bitmapOptions);
	    if (!bitmap.isMutable())
	        bitmap = convertToMutable(context, bitmap);
	    return bitmap;
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	public static Bitmap convertToMutable(final Context context, final Bitmap imgIn) {
	    final int width = imgIn.getWidth(), height = imgIn.getHeight();
	    final Config type = imgIn.getConfig();
	    File outputFile = null;
	    final File outputDir = context.getCacheDir();
	    try {
	        outputFile = File.createTempFile(Long.toString(System.currentTimeMillis()), null, outputDir);
	        outputFile.deleteOnExit();
	        final RandomAccessFile randomAccessFile = new RandomAccessFile(outputFile, "rw");
	        final FileChannel channel = randomAccessFile.getChannel();
	        final MappedByteBuffer map = channel.map(MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
	        imgIn.copyPixelsToBuffer(map);
	        imgIn.recycle();
	        final Bitmap result = Bitmap.createBitmap(width, height, type);
	        map.position(0);
	        result.copyPixelsFromBuffer(map);
	        channel.close();
	        randomAccessFile.close();
	        outputFile.delete();
	        return result;
	    } catch (final Exception e) {
	    } finally {
	        if (outputFile != null)
	            outputFile.delete();
	    }
	    return null;
	}
	

}
