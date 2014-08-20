package com.count2v.coolist.shop;

import java.util.ArrayList;

import java.util.List;

import com.count2v.coolist.util.DefaultImageLoadingListener;
import com.count2v.coolist.R;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


class ShopListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<ShopListItem> itemList;
	
	private ImageLoaderConfiguration config;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	private ImageLoadingListener imageLoadingListner;
	
	
	private Drawable iconLoadingDrawable;
	private Drawable serviceAvailableDrawable;
	private Drawable serviceUnavailableDrawable;
	
	

	
	public ShopListAdapter(Context context, List<ShopListItem> itemList) {
		this.context = context;
		this.itemList = new ArrayList<ShopListItem>(itemList);
		
		initialize();
	}
	
	private void initialize() {		
		config = new ImageLoaderConfiguration.Builder(context).build();
		imageLoader = ImageLoader.getInstance();
		
		if(!imageLoader.isInited()) {
			imageLoader.init(config);
		}
		
		displayOptions = new DisplayImageOptions.Builder()
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.showImageForEmptyUri(R.drawable.shop_icon_default)
		.showImageOnFail(R.drawable.shop_icon_default)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		imageLoadingListner = new DefaultImageLoadingListener();
		
		setDrawable();
	}
	
	private void setDrawable() {
		iconLoadingDrawable = context.getResources().getDrawable(R.drawable.shop_icon_default);
		
		serviceAvailableDrawable = context.getResources().getDrawable(R.drawable.service_available);
		serviceUnavailableDrawable = context.getResources().getDrawable(R.drawable.service_unavailable);
	}
	
	public void setItemList(List<ShopListItem> itemList) {
		this.itemList = new ArrayList<ShopListItem>(itemList);
	}
	
	protected Context getContext() {
		return context;
	}
	
	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public ShopListItem getItem(int index) {
		return itemList.get(index);
	}

	@Override
	public long getItemId(int index) {
		
		return itemList.get(index).getShopId() ;
	}
	
	public int getItemPosition(long itemId) {
		int position = -1;
		
		for(ShopListItem item : itemList) {
			position++;
			if(item.getShopId() == itemId) {
				return position;
			}
		}
		
		return -1;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
	
		if(convertView == null) {
			viewHolder = new ViewHolder();
			convertView = viewHolder.getView();
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.updateView(itemList.get(position));
		
		return viewHolder.getView();
	}

	
	
	private class ViewHolder {
		private View view;
		private ImageView iconImageView;
		private ImageView serviceImgageView;
		private TextView nameTextView;
		private TextView descriptionTextView;
		private TextView minPointRequiredTextView;
		
		private ShopListItem currentItem;
		
		public ViewHolder() {
			setView();
		}
	
		private void setView() {
			view = LayoutInflater.from(getContext()).inflate(
					com.count2v.coolist.R.layout.shop_list_item, null);
			iconImageView = (ImageView) view.findViewById(R.id.shopListItemImageView01);
			serviceImgageView = (ImageView) view.findViewById(R.id.shopListItemImageView02);
			nameTextView = (TextView) view.findViewById(R.id.shopListItemTextView01);
			descriptionTextView = (TextView) view.findViewById(R.id.shopListItemTextView02);
			minPointRequiredTextView = (TextView) view.findViewById(R.id.shopListItemTextView03);
			
		}
		
		
		public void updateView(ShopListItem listItem) {
			currentItem = listItem;
			iconImageView.setImageDrawable(iconLoadingDrawable);
			imageLoader.displayImage(listItem.getIconUrl(), iconImageView, displayOptions, imageLoadingListner);
			
			nameTextView.setText(currentItem.getName());
			descriptionTextView.setText(currentItem.getDescription());
			minPointRequiredTextView.setText(String.valueOf(currentItem.getMinRequiredPoints()));
			
			if(currentItem.isAvailable()) {
				serviceImgageView.setImageDrawable(serviceAvailableDrawable);
			} else {
				serviceImgageView.setImageDrawable(serviceUnavailableDrawable);
			}
		}
		
		public View getView() {
			return view;
		}
		
	}
}
