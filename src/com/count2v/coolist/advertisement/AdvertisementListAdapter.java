package com.count2v.coolist.advertisement;

import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import com.count2v.coolist.R;
import com.count2v.coolist.client.FacebookAdvertisementEventType;
import com.count2v.coolist.util.DefaultImageLoadingListener;
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
import android.widget.ProgressBar;
import android.widget.TextView;


class AdvertisementListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<AdvertisementListItem> itemList;
	private ArrayList<Long> pendingItemList;
	
	private ImageLoaderConfiguration config;
	private ImageLoader imageLoader;
	private DisplayImageOptions displayOptions;
	private ImageLoadingListener imageLoadingListner;
	
	private Drawable iconLoadingDrawable;
	
	private Drawable facebookLikePageDoneDrawable;
	private Drawable facebookLikePageUndoneDrawable;
	private Drawable facebookSharedPostDoneDrawable;
	private Drawable facebookSharedPostUndoneDrawable;
	private Drawable downloadAppDoneDrawable;
	private Drawable downloadAppUndoneDrawable;
	

	public AdvertisementListAdapter(Context context, List<AdvertisementListItem> itemList) {
		this.context = context;
		this.itemList = new ArrayList<AdvertisementListItem>(itemList);
		
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
		.showImageForEmptyUri(R.drawable.default_icon)
		.showImageOnFail(R.drawable.default_icon)
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
		
		imageLoadingListner = new DefaultImageLoadingListener();
		
		setDrawable();
	}
	
	private void setDrawable() {
		iconLoadingDrawable = context.getResources().getDrawable(R.drawable.loading);
		
		facebookLikePageDoneDrawable =  context.getResources().getDrawable(R.drawable.like_done);
		facebookLikePageUndoneDrawable = context.getResources().getDrawable(R.drawable.like_undone);
		
		facebookSharedPostDoneDrawable = context.getResources().getDrawable(R.drawable.share_done);
		facebookSharedPostUndoneDrawable = context.getResources().getDrawable(R.drawable.share_undone);
		
		downloadAppDoneDrawable = context.getResources().getDrawable(R.drawable.download_done);
		downloadAppUndoneDrawable = context.getResources().getDrawable(R.drawable.download_undone);
	}
	
	public void setItemList(List<AdvertisementListItem> itemList) {
		this.itemList = new ArrayList<AdvertisementListItem>(itemList);
	}
	
	public void setItemListWithPreviousPendingStatus(List<AdvertisementListItem> itemList) {
		storePendingStatus();
		this.itemList = new ArrayList<AdvertisementListItem>(itemList);
		restorePendingStatus();
	}
	
	private void storePendingStatus() {
		pendingItemList = new ArrayList<Long>();
		
		for(AdvertisementListItem item : itemList) {
			if(item.isPending()) {
				pendingItemList.add(item.getAdvertisementId());
			}
		}
	}
	
	private void restorePendingStatus() {
		for(long advertisementId : pendingItemList) {
			setItemPendingStatus(advertisementId, true);
		}
	}
	
	public void setItemPendingStatus(long itemId, boolean pending) {
		
		for(AdvertisementListItem item : itemList) {
			if(item.getAdvertisementId() == itemId) {
				item.setPending(pending);
			}
		}
	}
	
	public boolean isItemPending(long itemId) {
		for(AdvertisementListItem item : itemList) {
			if(item.getAdvertisementId() == itemId) {
				return item.isPending();
			}
		}
		
		return false;
	}
	
	
	
	private Context getContext() {
		return context;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public AdvertisementListItem getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return itemList.get(position).getAdvertisementId();
	}
	
	public int getItemPosition(long itemId) {
		int position = -1;
		
		for(AdvertisementListItem item : itemList) {
			position++;
			if(item.getAdvertisementId() == itemId) {
				return position;
			}
		}
		
		return -1;
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
		private ImageView lconImageView;
		private ImageView eventImageView01;
		private ImageView eventImageView02;
		private ImageView eventImageView03;
		private ImageView eventImageView04;
		private TextView titleTextView;
		private TextView remainPointsTextView;
		
		private ProgressBar pendingProgressBar;

		
		private AdvertisementListItem currentItem;
		
		public ViewHolder() {
			setView();
		}
	
		private void setView() {
			view = LayoutInflater.from(getContext()).inflate(
					com.count2v.coolist.R.layout.advertisement_list_item, null);
			lconImageView = (ImageView) view.findViewById(R.id.advertisementListItemImageView01);
			
			eventImageView01 = (ImageView) view.findViewById(R.id.advertisementListItemImageView02);
			eventImageView02 = (ImageView) view.findViewById(R.id.advertisementListItemImageView03);
			eventImageView03 = (ImageView) view.findViewById(R.id.advertisementListItemImageView04);
			eventImageView04 = (ImageView) view.findViewById(R.id.advertisementListItemImageView05);
			titleTextView = (TextView) view.findViewById(R.id.advertisementListItemTextView01);
			remainPointsTextView = (TextView) view.findViewById(R.id.advertisementListItemTextView02);
			
			pendingProgressBar = (ProgressBar) view.findViewById(R.id.advertisementListItemProgressBar01);
			
			// no sign up event for now
			eventImageView03.setVisibility(View.GONE);
			
			
			
		}
		
		public void updateView(AdvertisementListItem listItem) {
			currentItem = listItem;
			
			lconImageView.setImageDrawable(iconLoadingDrawable);
			imageLoader.displayImage(currentItem.getIconUrl(), lconImageView, displayOptions, imageLoadingListner);
			titleTextView.setText(listItem.getTitle());
			remainPointsTextView.setText(String.valueOf(listItem.getRemainPoints()));
			
			if(currentItem.isPending()) {
				if(pendingProgressBar.getVisibility() != View.VISIBLE) {
					pendingProgressBar.setVisibility(View.VISIBLE);
				}
				
				if(remainPointsTextView.getVisibility() != View.GONE) {
					remainPointsTextView.setVisibility(View.GONE);
				}
			} else {
				if(pendingProgressBar.getVisibility() != View.GONE) {
					pendingProgressBar.setVisibility(View.GONE);
				}
				
				if(remainPointsTextView.getVisibility() != View.VISIBLE) {
					remainPointsTextView.setVisibility(View.VISIBLE);
				}
			}
			
			Set<Integer> facebookEventSet = currentItem.getFacebookEventSet();
			Set<Integer> facebookUndoneEventSet = currentItem.getFacebookUndoneEventSet();
			
			
			if(facebookEventSet.contains(FacebookAdvertisementEventType.LIKE_FAN_PAGE)) {
				if(eventImageView01.getVisibility() != View.VISIBLE) {
					eventImageView01.setVisibility(View.VISIBLE);
				}
				if(facebookUndoneEventSet.contains(FacebookAdvertisementEventType.LIKE_FAN_PAGE)) {
					eventImageView01.setImageDrawable(facebookLikePageUndoneDrawable);
				} else {
					eventImageView01.setImageDrawable(facebookLikePageDoneDrawable);
				}
			} else {
				if(eventImageView01.getVisibility() == View.VISIBLE) {
					eventImageView01.setVisibility(View.GONE);
				}
			}
			
			if(facebookEventSet.contains(FacebookAdvertisementEventType.SHARE_POST)) {
				if(eventImageView02.getVisibility() != View.VISIBLE) {
					eventImageView02.setVisibility(View.VISIBLE);
				}
				if(facebookUndoneEventSet.contains(FacebookAdvertisementEventType.SHARE_POST)) {
					eventImageView02.setImageDrawable(facebookSharedPostUndoneDrawable);
				} else {
					eventImageView02.setImageDrawable(facebookSharedPostDoneDrawable);
				}
			} else {
				if(eventImageView02.getVisibility() == View.VISIBLE) {
					eventImageView02.setVisibility(View.GONE);
				}
			}
			
			if(listItem.hasDownloadEvent()) {
				if(eventImageView04.getVisibility() != View.VISIBLE) {
					eventImageView04.setVisibility(View.VISIBLE);
				}
				if(listItem.isDownloadEventDone()) {
					eventImageView04.setImageDrawable(downloadAppDoneDrawable);
				} else {
					eventImageView04.setImageDrawable(downloadAppUndoneDrawable);
				}
			} else {
				if(eventImageView04.getVisibility() == View.VISIBLE) {
					eventImageView04.setVisibility(View.GONE);
				}
			}
			
			
		}
		
		
		public View getView() {
			return view;
		}
		
	}



}
