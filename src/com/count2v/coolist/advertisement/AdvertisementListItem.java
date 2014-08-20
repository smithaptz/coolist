package com.count2v.coolist.advertisement;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;

class AdvertisementListItem {
	private AdvertisementData advertisementData;
	private AdvertisementAppInfoData advertisementAppInfoData;
	private List<FacebookAdvertisementEventData> facebookEventList;
	private int remainPoints;
	private int totalFacebookEventPoints;
	private HashSet<Integer> facebookEventSet;
	private HashSet<Integer> facebookUndoneEventSet;
	
	private boolean pending = false;
	
	
	public AdvertisementListItem(AdvertisementData advertisementData, 
			List<FacebookAdvertisementEventData> facebookEventList,
			AdvertisementAppInfoData advertisementAppInfoData) {
		this.advertisementData = advertisementData;
		this.advertisementAppInfoData = advertisementAppInfoData;
		this.facebookEventList = facebookEventList;

		initialize();
	}
	
	
	public AdvertisementListItem(AdvertisementData advertisementData, 
			List<FacebookAdvertisementEventData> facebookEventList) {
		this.advertisementData = advertisementData;
		this.facebookEventList = facebookEventList;
		
		initialize();
	}
	
	
	public AdvertisementListItem(AdvertisementData advertisementData) {
		this.advertisementData = advertisementData;
		
		initialize();
	}
	
		
	private void initialize() {
		remainPoints = 0;
		totalFacebookEventPoints = 0;
		
		facebookEventSet = new HashSet<Integer>();
		facebookUndoneEventSet = new HashSet<Integer>();

		if(isAdvertisementAvailable()) {
			remainPoints += advertisementData.getGamePoints();
		}
		
		if(advertisementAppInfoData != null && 
				!advertisementAppInfoData.isDone()) {
				remainPoints += advertisementAppInfoData.getPoints();
		}
		
		
		if(facebookEventList == null) {
			return;
		}
			

		for(FacebookAdvertisementEventData facebookEventData : facebookEventList) {

			if(facebookEventData.isTypeValid()) {
				int type = facebookEventData.getType();
				
				// 同一種類別只能有一項:
				if(!facebookEventSet.contains(type)) {
					facebookEventSet.add(type);
					
					int points = facebookEventData.getPoints();
					
					totalFacebookEventPoints += points;
					
					if(!facebookEventData.isDone()) {
						remainPoints += points;
						facebookUndoneEventSet.add(type);
					}
				}
			}
		}
	}
	
	public void setPending(boolean pending) {
		this.pending = pending;
	}

	
	public long getAdvertisementId() {
		return advertisementData.getAdvertisementId();
	}
	
	public int getRemainPoints() {
		return remainPoints;
	}
	
	public int getTotalPoints() {
		return advertisementData.getGamePoints() + totalFacebookEventPoints;
	}
	
	public int getTotalFacebookEventPoints() {
		return totalFacebookEventPoints;
	}
	
	public String getTitle() {
		return advertisementData.getAdvertisementName();
	}
	
	public Set<Integer> getFacebookEventSet() {
		return new HashSet<Integer>(facebookEventSet);
	}
	
	public Set<Integer> getFacebookUndoneEventSet() {
		return new HashSet<Integer>(facebookUndoneEventSet);
	}
	
	public String getIconUrl() {
		return advertisementData.getIconUrl();
	}
	
	public boolean isAdvertisementAvailable() {
		return advertisementData.getGameType() >= 0  && 
				(!advertisementData.isRead() || advertisementData.isAvailable());
	}
	
	public boolean hasDownloadEvent() {
		return advertisementAppInfoData != null;
	}
	
	public boolean isDownloadEventDone() {
		
		if(!hasDownloadEvent()) {
			return false;
		}
		
		return advertisementAppInfoData.isDone();
	}
	
	public boolean isPending() {
		return pending;
	}

}
