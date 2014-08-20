package com.count2v.coolist.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class DefaultImageLoadingListener extends SimpleImageLoadingListener {
	static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		
		if(loadedImage == null) {
			return;
		}
		
		ImageView imageView = (ImageView) view;
		boolean firstDisplay = !displayedImages.contains(imageUri);
		if(firstDisplay) {
			FadeInBitmapDisplayer.animate(imageView, 500);
			displayedImages.add(imageUri);
		}
	}
}
