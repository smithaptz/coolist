package com.count2v.coolist.db;

import java.util.ArrayList;
import java.util.List;

import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;
import com.count2v.coolist.client.data.FacebookSharedPostInfoData;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.db.DBHelper.AdvertisementAppInfoSchema;
import com.count2v.coolist.db.DBHelper.AdvertisementSchema;
import com.count2v.coolist.db.DBHelper.FacebookAdvertisementEventSchema;
import com.count2v.coolist.db.DBHelper.FacebookSharedPostInfoSchema;
import com.count2v.coolist.db.DBHelper.ShopItemSchema;
import com.count2v.coolist.db.DBHelper.ShopSchema;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DBAccessor {
	private static DBAccessor dbAccessor;
	private static DBHelper dbHelper;
	

	private DBAccessor(Context context) {
		dbHelper = new DBHelper(context);
	}
	
	public static synchronized DBAccessor instance(Context context) {
		if(dbAccessor == null) {
			dbAccessor = new DBAccessor(context);
		}
		
		return dbAccessor;
	}
	
	public void resetAdvertisement(List<AdvertisementData> advertisementDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(AdvertisementSchema.TABLE_NAME, null, null);
			
			for(AdvertisementData data : advertisementDataList) {
				ContentValues values = getContentValues(data);
				db.insert(AdvertisementSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
	}
	
	public void resetFacebookAdvertisementEvent(List<FacebookAdvertisementEventData> facebookEventList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(FacebookAdvertisementEventSchema.TABLE_NAME, null, null);
			
			for(FacebookAdvertisementEventData data : facebookEventList) {
				ContentValues values = getContentValues(data);
				db.insert(FacebookAdvertisementEventSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
	}
	
	public void resetFacebookSharedPostInfo(List<FacebookSharedPostInfoData> facebookSharedPostInfoDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(FacebookSharedPostInfoSchema.TABLE_NAME, null, null);
			
			for(FacebookSharedPostInfoData data : facebookSharedPostInfoDataList) {
				ContentValues values = getContentValues(data);
				db.insert(FacebookSharedPostInfoSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
	}
	
	public void resetAdvertisementAppInfo(List<AdvertisementAppInfoData> advertisementAppInfoDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(AdvertisementAppInfoSchema.TABLE_NAME, null, null);
			
			for(AdvertisementAppInfoData data : advertisementAppInfoDataList) {
				ContentValues values = getContentValues(data);
				db.insert(AdvertisementAppInfoSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
	}
	
	
	public void resetShop(List<ShopData> shopDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(ShopSchema.TABLE_NAME, null, null);
			
			for(ShopData data : shopDataList) {
				ContentValues values = getContentValues(data);
				db.insert(ShopSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
		
	}
	
	public void resetShopItem(List<ShopItemData> shopItemDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			db.delete(ShopItemSchema.TABLE_NAME, null, null);
			
			for(ShopItemData data : shopItemDataList) {
				ContentValues values = getContentValues(data);
				db.insert(ShopItemSchema.TABLE_NAME, null, values);
			}
			db.close();
		}
		
	}
	
	public void setAdvertisement(List<AdvertisementData> advertisementDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			for(AdvertisementData data : advertisementDataList) {
				setAdvertisement(data, db);
			}
			db.close();
		}
	}
	
	public void setAdvertisement(AdvertisementData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setAdvertisement(data, db);
			db.close();
		}
	}
	
	private void setAdvertisement(AdvertisementData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(AdvertisementSchema.TABLE_NAME, null, values);
	}
	

	public void setFacebookAdvertisementEvent(List<FacebookAdvertisementEventData> facebookEventList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			for(FacebookAdvertisementEventData data : facebookEventList) {
				setFacebookAdvertisementEvent(data, db);
			}
			db.close();
		}
	}

	public void setFacebookAdvertisementEvent(FacebookAdvertisementEventData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setFacebookAdvertisementEvent(data, db);
			db.close();
		}
	}
	
	private void setFacebookAdvertisementEvent(FacebookAdvertisementEventData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(FacebookAdvertisementEventSchema.TABLE_NAME, null, values);
	}
	
	public void setFacebookSharedPostInfo(List<FacebookSharedPostInfoData> facebookSharedPostInfoDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			for(FacebookSharedPostInfoData data : facebookSharedPostInfoDataList) {
				setFacebookSharedPostInfo(data, db);
			}
			db.close();
		}
	}
	
	public void setFacebookSharedPostInfo(FacebookSharedPostInfoData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setFacebookSharedPostInfo(data, db);
			db.close();
		}
	}
	
	private void setFacebookSharedPostInfo(FacebookSharedPostInfoData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(FacebookSharedPostInfoSchema.TABLE_NAME, null, values);
	}
	
	public void setAdvertisementAppInfo(List<AdvertisementAppInfoData> advertisementAppInfoDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			
			for(AdvertisementAppInfoData data : advertisementAppInfoDataList) {
				setAdvertisementAppInfo(data, db);
			}
			db.close();
		}
	}
	
	public void setAdvertisementAppInfo(AdvertisementAppInfoData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setAdvertisementAppInfo(data, db);
			db.close();
		}
	}

	
	private void setAdvertisementAppInfo(AdvertisementAppInfoData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(AdvertisementAppInfoSchema.TABLE_NAME, null, values);
	}
	
	public void setShop(List<ShopData> shopDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			for(ShopData data : shopDataList) {
				setShop(data, db);
			}
			db.close();
		}
	}
	
	public void setShop(ShopData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setShop(data, db);
			db.close();
		}
	}
	
	private void setShop(ShopData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(ShopSchema.TABLE_NAME, null, values);
	}
	
	public void setShopItem(List<ShopItemData> shopItemDataList) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			for(ShopItemData data : shopItemDataList) {
				setShopItem(data, db);
			}
			db.close();
		}
	}
	
	public void setShopItem(ShopItemData data) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getWritableDatabase();
			setShopItem(data, db);
			db.close();
		}
	}
	
	private void setShopItem(ShopItemData data, SQLiteDatabase db) {
		ContentValues values = getContentValues(data);
		db.replace(ShopItemSchema.TABLE_NAME, null, values);
	}
	
	public List<AdvertisementData> getAdvertisementDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertisementSchema.TABLE_NAME, null);
			
			List<AdvertisementData> result = new ArrayList<AdvertisementData>();
			
			while(cursor.moveToNext()) {
				result.add(getAdvertisementData(cursor));
			}
			
			cursor.close();
			db.close();
		
			return result;
		}
	}
	
	public AdvertisementData getAdvertisementData(long advertisementId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertisementSchema.TABLE_NAME + " WHERE " + 
					AdvertisementSchema.ADVERTISEMENT_ID + " = " +  advertisementId, null);
			AdvertisementData result = (cursor.moveToFirst()) ? getAdvertisementData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<FacebookAdvertisementEventData> getFacebookEventDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookAdvertisementEventSchema.TABLE_NAME, null);
			
			List<FacebookAdvertisementEventData> result = new ArrayList<FacebookAdvertisementEventData>();
			
			while(cursor.moveToNext()) {
				result.add(getFacebookAdvertisementEventData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public FacebookAdvertisementEventData getFacebookEventData(long facebookAdvertisementEventId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookAdvertisementEventSchema.TABLE_NAME + " WHERE " + 
					FacebookAdvertisementEventSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID + " = " +  
					facebookAdvertisementEventId, null);
			FacebookAdvertisementEventData result = (cursor.moveToFirst()) ? getFacebookAdvertisementEventData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<FacebookAdvertisementEventData> getFacebookEventDataListByAdvertisementId(long advertisementId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookAdvertisementEventSchema.TABLE_NAME + " WHERE " + 
					FacebookAdvertisementEventSchema.ADVERTISEMENT_ID + " = " +  
					advertisementId, null);
			
			List<FacebookAdvertisementEventData> result = new ArrayList<FacebookAdvertisementEventData>();
			
			while(cursor.moveToNext()) {
				result.add(getFacebookAdvertisementEventData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<FacebookSharedPostInfoData> getFacebookSharedPostInfoDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookSharedPostInfoSchema.TABLE_NAME, null);
			
			List<FacebookSharedPostInfoData> result = new ArrayList<FacebookSharedPostInfoData>();
			
			while(cursor.moveToNext()) {
				result.add(getFacebookSharedPostInfoData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<FacebookSharedPostInfoData> getFacebookSharedPostInfoDataListByAdvertisementId(long advertisementId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookSharedPostInfoSchema.TABLE_NAME + " WHERE " + 
					FacebookSharedPostInfoSchema.ADVERTISEMENT_ID + " = " +  
					advertisementId, null);
			
			List<FacebookSharedPostInfoData> result = new ArrayList<FacebookSharedPostInfoData>();
			
			while(cursor.moveToNext()) {
				result.add(getFacebookSharedPostInfoData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public FacebookSharedPostInfoData getFacebookSharedPostInfoDataByFacebookAdvertisementEventId(long facebookAdvertisementEventId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + FacebookSharedPostInfoSchema.TABLE_NAME + " WHERE " + 
					FacebookSharedPostInfoSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID + " = " +  
					facebookAdvertisementEventId, null);
			FacebookSharedPostInfoData result = (cursor.moveToFirst()) ? getFacebookSharedPostInfoData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	
	public List<AdvertisementAppInfoData> getAdvertisementAppInfoDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertisementAppInfoSchema.TABLE_NAME, null);
			
			List<AdvertisementAppInfoData> result = new ArrayList<AdvertisementAppInfoData>();
			
			while(cursor.moveToNext()) {
				result.add(getAdvertisementAppInfoData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public AdvertisementAppInfoData getAdvertisementAppInfoData(long advertisementAppId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertisementAppInfoSchema.TABLE_NAME + " WHERE " + 
					AdvertisementAppInfoSchema.ADVERTISEMENT_APP_ID + " = " +  
					advertisementAppId, null);
			AdvertisementAppInfoData result = (cursor.moveToFirst()) ? getAdvertisementAppInfoData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public AdvertisementAppInfoData getAdvertisementAppInfoDataByAdvertisementId(long advertisementId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + AdvertisementAppInfoSchema.TABLE_NAME + " WHERE " + 
					AdvertisementAppInfoSchema.ADVERTISEMENT_ID + " = " +  
					advertisementId, null);
			AdvertisementAppInfoData result = (cursor.moveToFirst()) ? getAdvertisementAppInfoData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<ShopData> getShopDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + ShopSchema.TABLE_NAME, null);
			
			List<ShopData> result = new ArrayList<ShopData>();
			
			while(cursor.moveToNext()) {
				result.add(getShopData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public ShopData getShopData(long shopId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + ShopSchema.TABLE_NAME + " WHERE " + 
					ShopSchema.SHOP_ID + " = " +  shopId, null);
			ShopData result = (cursor.moveToFirst()) ? getShopData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<ShopItemData> getShopItemDataList() {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + ShopItemSchema.TABLE_NAME, null);
			
			List<ShopItemData> result = new ArrayList<ShopItemData>();
			
			while(cursor.moveToNext()) {
				result.add(getShopItemData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public List<ShopItemData> getShopItemDataListByShopId(long shopId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + ShopItemSchema.TABLE_NAME + " WHERE " + 
					ShopItemSchema.SHOP_ID + " = " +  shopId, null);
			
			List<ShopItemData> result = new ArrayList<ShopItemData>();
			
			while(cursor.moveToNext()) {
				result.add(getShopItemData(cursor));
			}
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	public ShopItemData getShopItemData(long shopItemId) {
		synchronized(this) {
			SQLiteDatabase db = dbHelper.getReadableDatabase();
			Cursor cursor = db.rawQuery("SELECT * FROM " + ShopItemSchema.TABLE_NAME + " WHERE " + 
					ShopItemSchema.SHOP_ITEM_ID + " = " +  shopItemId, null);
			ShopItemData result =  (cursor.moveToFirst()) ? getShopItemData(cursor) : null;
			
			cursor.close();
			db.close();
			
			return result;
		}
	}
	
	private AdvertisementData getAdvertisementData(Cursor cursor) {
		AdvertisementData data = new AdvertisementData();
		data.setAdvertisementId(cursor.getLong(cursor.getColumnIndex(AdvertisementSchema.ADVERTISEMENT_ID)));
		data.setAdvertiserId(cursor.getLong(cursor.getColumnIndex(AdvertisementSchema.ADVERTISER_ID)));
		data.setAdvertisementName(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.ADVERTISEMENT_NAME)));
		
		data.setIconUrl(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.ICON_URL)));
		data.setPictureUrl(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.PICTURE_URL)));
		data.setWebsiteUrl(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.WEBSITE_URL)));
		data.setPopularity(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.POPULARITY)));
		data.setGameType(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.GAME_TYPE)));
		data.setGamePoints(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.GAME_POINTS)));
		data.setRead(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.READ)) == 1);
		data.setAvailable(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.AVAILABLE)) == 1);
		data.setReadTimes(cursor.getInt(cursor.getColumnIndex(AdvertisementSchema.READ_TIME)));
		data.setStartTime(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.START_TIME)));
		data.setEndTime(cursor.getString(cursor.getColumnIndex(AdvertisementSchema.END_TIME)));
		
		
		return data;
	}
	
	private FacebookAdvertisementEventData getFacebookAdvertisementEventData(Cursor cursor) {
		FacebookAdvertisementEventData data = new FacebookAdvertisementEventData();
		data.setFacebookAdvertisementEventId(cursor.getLong(cursor.getColumnIndex(FacebookAdvertisementEventSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID)));
		data.setAdvertisementId(cursor.getLong(cursor.getColumnIndex(FacebookAdvertisementEventSchema.ADVERTISEMENT_ID)));
		data.setAdvertiserId(cursor.getLong(cursor.getColumnIndex(FacebookAdvertisementEventSchema.ADVERTISER_ID)));
		data.setType(cursor.getInt(cursor.getColumnIndex(FacebookAdvertisementEventSchema.TYPE)));
		data.setPostId(cursor.getString(cursor.getColumnIndex(FacebookAdvertisementEventSchema.POST_ID)));
		data.setPageId(cursor.getString(cursor.getColumnIndex(FacebookAdvertisementEventSchema.PAGE_ID)));
		data.setPoints(cursor.getInt(cursor.getColumnIndex(FacebookAdvertisementEventSchema.POINTS)));
		data.setDone(cursor.getInt(cursor.getColumnIndex(FacebookAdvertisementEventSchema.DONE)) == 1);
		
		
		return data;
	}
	
	private FacebookSharedPostInfoData getFacebookSharedPostInfoData(Cursor cursor) {
		FacebookSharedPostInfoData data = new FacebookSharedPostInfoData();
		data.setFacebookAdvertisementEventId(cursor.getLong(cursor.getColumnIndex(FacebookSharedPostInfoSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID)));
		data.setAdvertisementId(cursor.getLong(cursor.getColumnIndex(FacebookSharedPostInfoSchema.ADVERTISEMENT_ID)));
		data.setName(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.NAME)));
		data.setCaption(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.CAPTION)));
		data.setDescription(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.DESCRIPTION)));
		data.setUrl(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.URL)));
		data.setPictureUrl(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.PICTURE_URL)));
		data.setPlace(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.PLACE)));
		data.setRef(cursor.getString(cursor.getColumnIndex(FacebookSharedPostInfoSchema.REF)));
		data.setPopularity(cursor.getInt(cursor.getColumnIndex(FacebookSharedPostInfoSchema.POPULARITY)));
		
		
		return data;
	}
	
	private AdvertisementAppInfoData getAdvertisementAppInfoData(Cursor cursor) {
		AdvertisementAppInfoData data = new AdvertisementAppInfoData();
		data.setAdvertisementAppId(cursor.getLong(cursor.getColumnIndex(AdvertisementAppInfoSchema.ADVERTISEMENT_APP_ID)));
		data.setAdvertisementId(cursor.getLong(cursor.getColumnIndex(AdvertisementAppInfoSchema.ADVERTISEMENT_ID)));
		data.setAdvertiserId(cursor.getLong(cursor.getColumnIndex(AdvertisementAppInfoSchema.ADVERTISER_ID)));
		data.setAndroidMarketId(cursor.getString(cursor.getColumnIndex(AdvertisementAppInfoSchema.ANDROID_MARKET_ID)));
		data.setName(cursor.getString(cursor.getColumnIndex(AdvertisementAppInfoSchema.NAME)));
		data.setPopuarity(cursor.getInt(cursor.getColumnIndex(AdvertisementAppInfoSchema.POPUARITY)));
		data.setPoints(cursor.getInt(cursor.getColumnIndex(AdvertisementAppInfoSchema.POINTS)));
		data.setDone(cursor.getInt(cursor.getColumnIndex(AdvertisementAppInfoSchema.DONE)) == 1);
		
		
		return data;
	}
	
	private ShopData getShopData(Cursor cursor) {
		ShopData data = new ShopData();
		data.setShopId((cursor.getLong(cursor.getColumnIndex(ShopSchema.SHOP_ID))));
		data.setName((cursor.getString(cursor.getColumnIndex(ShopSchema.SHOP_NAME))));
		data.setWebsiteUrl((cursor.getString(cursor.getColumnIndex(ShopSchema.WEBSITE_URL))));
		data.setDescription((cursor.getString(cursor.getColumnIndex(ShopSchema.DESCRIPTION))));
		data.setDescriptionUrl((cursor.getString(cursor.getColumnIndex(ShopSchema.DESCRIPTION_URL))));
		data.setLogoUrl((cursor.getString(cursor.getColumnIndex(ShopSchema.LOGO_URL))));
		data.setMinPoints((cursor.getInt(cursor.getColumnIndex(ShopSchema.MIN_POINTS))));
		data.setPoints((cursor.getInt(cursor.getColumnIndex(ShopSchema.POINTS))));
		data.setZipCode((cursor.getString(cursor.getColumnIndex(ShopSchema.ZIP_CODE))));
		data.setCity((cursor.getString(cursor.getColumnIndex(ShopSchema.CITY))));
		data.setDistrict((cursor.getString(cursor.getColumnIndex(ShopSchema.DESCRIPTION))));
		data.setAddress((cursor.getString(cursor.getColumnIndex(ShopSchema.ADDRESS))));
		data.setPhone((cursor.getString(cursor.getColumnIndex(ShopSchema.PHONE))));
		data.setGPSCoordinates((cursor.getString(cursor.getColumnIndex(ShopSchema.GPS_COORDINATES))));
		data.setMapUrl((cursor.getString(cursor.getColumnIndex(ShopSchema.MAP_URL))));
		data.setAvailable(cursor.getInt(cursor.getColumnIndex(ShopSchema.AVAILABLE)) == 1);
		data.setNotUsed(cursor.getInt(cursor.getColumnIndex(ShopSchema.NOT_USED)) == 1);
		
		
		return data;
	}
	
	private ShopItemData getShopItemData(Cursor cursor) {
		ShopItemData data = new ShopItemData();
		data.setShopItemId((cursor.getLong(cursor.getColumnIndex(ShopItemSchema.SHOP_ITEM_ID))));
		data.setShopId((cursor.getLong(cursor.getColumnIndex(ShopItemSchema.SHOP_ID))));
		data.setName((cursor.getString(cursor.getColumnIndex(ShopItemSchema.NAME))));
		data.setDescription((cursor.getString(cursor.getColumnIndex(ShopItemSchema.DESCRIPTION))));
		data.setIconUrl((cursor.getString(cursor.getColumnIndex(ShopItemSchema.ICON_URL))));
		data.setType((cursor.getInt(cursor.getColumnIndex(ShopItemSchema.TYPE))));
		data.setCount((cursor.getInt(cursor.getColumnIndex(ShopItemSchema.COUNT))));
		data.setPoints((cursor.getInt(cursor.getColumnIndex(ShopItemSchema.POINTS))));
		data.setAvailable(cursor.getInt(cursor.getColumnIndex(ShopItemSchema.AVAILABLE)) == 1);
		data.setNotUsed(cursor.getInt(cursor.getColumnIndex(ShopItemSchema.NOT_USED)) == 1);
		
		
		return data;
	}
	
	
	
	private ContentValues getContentValues(AdvertisementData data) {
		ContentValues values = new ContentValues();
		values.put(AdvertisementSchema.ADVERTISEMENT_ID, data.getAdvertisementId());
		values.put(AdvertisementSchema.ADVERTISER_ID, data.getAdvertiserId());
		values.put(AdvertisementSchema.ADVERTISEMENT_NAME, data.getAdvertisementName());
		values.put(AdvertisementSchema.PICTURE_URL, data.getPictureUrl());
		values.put(AdvertisementSchema.WEBSITE_URL, data.getWebsiteUrl());
		values.put(AdvertisementSchema.ICON_URL, data.getIconUrl());
		values.put(AdvertisementSchema.DESCRIPTION, data.getDescription());
		values.put(AdvertisementSchema.POPULARITY, data.getPopularity());

		values.put(AdvertisementSchema.READ, data.isRead() ? 1 : 0);
		values.put(AdvertisementSchema.AVAILABLE, data.isAvailable() ? 1 : 0);
		
		values.put(AdvertisementSchema.READ_TIME, data.getReadTimes());
		values.put(AdvertisementSchema.GAME_TYPE, data.getGameType());
		values.put(AdvertisementSchema.GAME_POINTS, data.getGamePoints());
		
		values.put(AdvertisementSchema.START_TIME, data.getStartTime());
		values.put(AdvertisementSchema.END_TIME, data.getEndTime());
		
		return values;
	}
	
	private ContentValues getContentValues(FacebookAdvertisementEventData data) {
		ContentValues values = new ContentValues();
		values.put(FacebookAdvertisementEventSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID, data.getFacebookAdvertisementEventId());
		values.put(FacebookAdvertisementEventSchema.ADVERTISEMENT_ID, data.getAdvertisementId());
		values.put(FacebookAdvertisementEventSchema.ADVERTISER_ID, data.getAdverterId());
		values.put(FacebookAdvertisementEventSchema.PAGE_ID, data.getPageId());
		values.put(FacebookAdvertisementEventSchema.POST_ID, data.getPostId());
		values.put(FacebookAdvertisementEventSchema.TYPE, data.getType());
		values.put(FacebookAdvertisementEventSchema.POINTS, data.getPoints());
		values.put(FacebookAdvertisementEventSchema.DONE, data.isDone() ? 1 : 0);
		
		
		return values;
	}
	
	private ContentValues getContentValues(FacebookSharedPostInfoData data) {
		ContentValues values = new ContentValues();
		values.put(FacebookSharedPostInfoSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID, data.getFacebookAdvertisementEventId());
		values.put(FacebookSharedPostInfoSchema.ADVERTISEMENT_ID, data.getAdvertisementId());
		values.put(FacebookSharedPostInfoSchema.NAME, data.getName());
		values.put(FacebookSharedPostInfoSchema.CAPTION, data.getCaption());
		values.put(FacebookSharedPostInfoSchema.DESCRIPTION, data.getDescription());
		values.put(FacebookSharedPostInfoSchema.URL, data.getUrl());
		values.put(FacebookSharedPostInfoSchema.PICTURE_URL, data.getPictureUrl());
		values.put(FacebookSharedPostInfoSchema.PLACE, data.getPlace());
		values.put(FacebookSharedPostInfoSchema.REF, data.getRef());
		values.put(FacebookSharedPostInfoSchema.POPULARITY, data.getPopularity());
		
		return values;
	}
	
	
	
	private ContentValues getContentValues(AdvertisementAppInfoData data) {
		ContentValues values = new ContentValues();
		values.put(AdvertisementAppInfoSchema.ADVERTISEMENT_APP_ID, data.getAdvertisementAppId());
		values.put(AdvertisementAppInfoSchema.ADVERTISEMENT_ID, data.getAdvertisementId());
		values.put(AdvertisementAppInfoSchema.ADVERTISER_ID, data.getAdverterId());
		values.put(AdvertisementAppInfoSchema.ANDROID_MARKET_ID, data.getAndroidMarketId());
		values.put(AdvertisementAppInfoSchema.NAME, data.getName());
		values.put(AdvertisementAppInfoSchema.POPUARITY, data.getPopuarity());
		values.put(AdvertisementAppInfoSchema.POINTS, data.getPoints());
		values.put(AdvertisementAppInfoSchema.DONE, data.isDone() ? 1 : 0);
		
		
		return values;
	}
	
	private ContentValues getContentValues(ShopData data) {
		ContentValues values = new ContentValues();
		values.put(ShopSchema.SHOP_ID, data.getShopId());
		values.put(ShopSchema.SHOP_NAME, data.getName());
		values.put(ShopSchema.LOGO_URL, data.getLogoUrl());
		values.put(ShopSchema.WEBSITE_URL, data.getWebsiteUrl());
		values.put(ShopSchema.PHONE, data.getPhone());
		values.put(ShopSchema.ADDRESS, data.getAddress());
		values.put(ShopSchema.ZIP_CODE, data.getZipCode());
		values.put(ShopSchema.CITY, data.getCity());
		values.put(ShopSchema.GPS_COORDINATES, data.getGPSCoordinates());
		values.put(ShopSchema.MAP_URL, data.getMapUrl());
		values.put(ShopSchema.DESCRIPTION, data.getDescription());
		values.put(ShopSchema.DESCRIPTION_URL, data.getDescriptionUrl());
		values.put(ShopSchema.MIN_POINTS, data.getMinPoints());
		values.put(ShopSchema.POINTS, data.getPoints());
		values.put(ShopSchema.AVAILABLE, data.isAvailable() ? 1 : 0);
		values.put(ShopSchema.NOT_USED, data.isNotUsed() ? 1 : 0);
		
		
		return values;
	}
	
	private ContentValues getContentValues(ShopItemData data) {
		ContentValues values = new ContentValues();
		values.put(ShopItemSchema.SHOP_ITEM_ID, data.getShopItemId());
		values.put(ShopItemSchema.SHOP_ID, data.getShopId());
		values.put(ShopItemSchema.ICON_URL, data.getIconUrl());
		values.put(ShopItemSchema.NAME, data.getName());
		values.put(ShopItemSchema.DESCRIPTION, data.getDescription());
		values.put(ShopItemSchema.TYPE, data.getType());
		values.put(ShopItemSchema.COUNT, data.getCount());
		values.put(ShopItemSchema.POINTS, data.getPoints());
		values.put(ShopItemSchema.AVAILABLE, data.isAvailable() ? 1 : 0);
		values.put(ShopItemSchema.NOT_USED, data.isNotUsed() ? 1 : 0);
		
		
		return values;
	}
	
	
}
