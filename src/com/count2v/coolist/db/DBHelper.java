package com.count2v.coolist.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class DBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "coolist";
	private static final int DATABASE_VERSION = 2;
	
	public interface AdvertisementSchema {
		String TABLE_NAME = "advertisement";
		String ADVERTISEMENT_ID = "advertisement_id";
		String ADVERTISER_ID = "advertiser_id";
		String ADVERTISEMENT_NAME = "advertisement_name";
		String PICTURE_URL = "picture_url";
		String WEBSITE_URL = "website_url";
		String ICON_URL = "icon_url";
		String DESCRIPTION = "description";
		String POPULARITY = "popularity";
		
		String READ = "read";
		String AVAILABLE = "available";

		String READ_TIME = "read_time";
		String GAME_TYPE = "game_type";
		String GAME_POINTS = "game_points";
		
		String START_TIME = "start_time";
		String END_TIME = "end_time";
	}
	
	public interface FacebookAdvertisementEventSchema {
		String TABLE_NAME = "facebook_advertisement_event";
		String FACEBOOK_ADVERTISEMENT_EVENT_ID = "facebook_advertisement_event_id";
		String ADVERTISEMENT_ID = "advertisement_id";
		String ADVERTISER_ID = "advertiser_id";
		String PAGE_ID = "page_id";
		String POST_ID = "post_id";
		String TYPE = "type";
		String POINTS = "points";
		String DONE = "done";
	}
	
	public interface FacebookSharedPostInfoSchema {
		String TABLE_NAME = "facebook_shared_post_info";
		String FACEBOOK_ADVERTISEMENT_EVENT_ID = "facebook_advertisement_event_id";
		String ADVERTISEMENT_ID = "advertisement_id";
		String NAME = "name";
		String CAPTION = "caption";
		String DESCRIPTION = "description";
		String URL = "url";
		String PICTURE_URL = "picture_url";
		String PLACE = "place";
		String REF = "ref";
		String POPULARITY = "popularity";
	}
	
	public interface AdvertisementAppInfoSchema {
		String TABLE_NAME = "advertisement_app_info";
		String ADVERTISEMENT_APP_ID = "advertisemnt_app_id";
		String ADVERTISEMENT_ID = "advertisement_id";
		String ADVERTISER_ID = "advertiser_id";
		String ANDROID_MARKET_ID = "android_market_id";
		String NAME = "name";
		String POPUARITY = "popuarity";
		String POINTS = "points";
		String DONE = "done";
	}
	
	public interface ShopSchema {
		String TABLE_NAME = "shop";
		String SHOP_ID = "shop_id";
		String SHOP_NAME = "shop_name";
		String LOGO_URL = "logo_url";
		String WEBSITE_URL = "website_url";
		String PHONE = "phone";
		String ADDRESS = "address";
		String ZIP_CODE = "zip_code";
		String CITY = "city";
		String GPS_COORDINATES = "gps_coordinates";
		String MAP_URL = "map_url";
		String DESCRIPTION = "description";
		String DESCRIPTION_URL = "description_url";
		String POINTS = "points";
		String MIN_POINTS = "min_points";
		String AVAILABLE = "available";
		String NOT_USED = "not_used";
	}
	
	public interface ShopItemSchema {
		String TABLE_NAME = "shop_item";
		String SHOP_ITEM_ID = "shop_item_id";
		String SHOP_ID = "shop_id";
		String ICON_URL = "icon_url";
		String NAME = "name";
		String DESCRIPTION = "description";
		String TYPE = "type";
		String COUNT = "count";
		String POINTS = "points";
		String AVAILABLE = "available";
		String NOT_USED = "not_used";
	}
	
	
/*
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}
*/
	
	public DBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}


	@Override
	public void onCreate(SQLiteDatabase db) {
		String DATABASE_CREATE_ADVERTISEMENT_TABLE = "CREATE TABLE IF NOT EXISTS " + AdvertisementSchema.TABLE_NAME + 
				"(" + AdvertisementSchema.ADVERTISEMENT_ID + " INTEGER PRIMARY KEY NOT NULL, " +
				AdvertisementSchema.ADVERTISER_ID + " INTEGER, " +
				AdvertisementSchema.ADVERTISEMENT_NAME + " TEXT, " +
				AdvertisementSchema.PICTURE_URL + " TEXT, " +
				AdvertisementSchema.WEBSITE_URL + " TEXT, " +
				AdvertisementSchema.ICON_URL + " TEXT, " +
				
				AdvertisementSchema.DESCRIPTION + " TEXT, " +
				AdvertisementSchema.POPULARITY + " INTEGER, " +
				
				AdvertisementSchema.READ + " INTEGER, " + 
				AdvertisementSchema.AVAILABLE + " INTEGER, " + 
				
				AdvertisementSchema.READ_TIME + " INTEGER, " + 
				AdvertisementSchema.GAME_TYPE + " INTEGER, " +
				AdvertisementSchema.GAME_POINTS + " INTEGER, " +
				
				
				AdvertisementSchema.START_TIME + " TEXT, " +
				AdvertisementSchema.END_TIME + " TEXT)";
		
		
		String DATABASE_CREATE_FACEBOOK_ADVERTISEMENT_EVENT_TABLE = "CREATE TABLE IF NOT EXISTS " + 
				FacebookAdvertisementEventSchema.TABLE_NAME + 
				"(" + FacebookAdvertisementEventSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID + " INTEGER PRIMARY KEY  NOT NULL, " +
				FacebookAdvertisementEventSchema.ADVERTISEMENT_ID + " INTEGER, " +
				FacebookAdvertisementEventSchema.ADVERTISER_ID + " INTEGER, " +
				FacebookAdvertisementEventSchema.PAGE_ID + " TEXT, " +
				FacebookAdvertisementEventSchema.POST_ID + " TEXT, " +
				FacebookAdvertisementEventSchema.TYPE + " INTEGER, " +
				FacebookAdvertisementEventSchema.POINTS + " INTEGER, " +
				FacebookAdvertisementEventSchema.DONE + " INTEGER)";
		
		String DATABASE_CREATE_FACEBOOK_SHARED_POST_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " + 
				FacebookSharedPostInfoSchema.TABLE_NAME + 
				"(" + "_id" + " INTEGER PRIMARY KEY  NOT NULL, " +
				FacebookSharedPostInfoSchema.FACEBOOK_ADVERTISEMENT_EVENT_ID + " INTEGER, " +
				FacebookSharedPostInfoSchema.ADVERTISEMENT_ID + " INTEGER, " +
				FacebookSharedPostInfoSchema.NAME + " TEXT, " +
				FacebookSharedPostInfoSchema.CAPTION + " TEXT, " +
				FacebookSharedPostInfoSchema.DESCRIPTION + " TEXT, " +
				FacebookSharedPostInfoSchema.URL + " TEXT, " +
				FacebookSharedPostInfoSchema.PICTURE_URL + " TEXT, " +
				FacebookSharedPostInfoSchema.PLACE + " TEXT, " +
				FacebookSharedPostInfoSchema.REF + " TEXT, " +
				FacebookSharedPostInfoSchema.POPULARITY + " INTEGER)";
		
		String DATABASE_CREATE_ADVERTISEMENT_APP_INFO_TABLE = "CREATE TABLE IF NOT EXISTS " + 
				AdvertisementAppInfoSchema.TABLE_NAME + 
				"(" + AdvertisementAppInfoSchema.ADVERTISEMENT_APP_ID + " INTEGER PRIMARY KEY  NOT NULL, " +
				AdvertisementAppInfoSchema.ADVERTISEMENT_ID + " INTEGER, " +
				AdvertisementAppInfoSchema.ADVERTISER_ID + " INTEGER, " +
				AdvertisementAppInfoSchema.ANDROID_MARKET_ID + " TEXT, " +
				AdvertisementAppInfoSchema.NAME + " TEXT, " +
				AdvertisementAppInfoSchema.POPUARITY + " INTEGER, " +
				AdvertisementAppInfoSchema.POINTS + " INTEGER, " +
				AdvertisementAppInfoSchema.DONE + " INTEGER)";
		
		String DATABASE_CREATE_SHOP_TABLE = "CREATE TABLE IF NOT EXISTS " + ShopSchema.TABLE_NAME + 
				"(" + ShopSchema.SHOP_ID + " INTEGER PRIMARY KEY NOT NULL, " +
				ShopSchema.SHOP_NAME + " TEXT, " +
				ShopSchema.LOGO_URL + " TEXT, " +
				ShopSchema.WEBSITE_URL + " TEXT, " +
				ShopSchema.PHONE + " TEXT, " +
				ShopSchema.ADDRESS + " TEXT," +
				ShopSchema.ZIP_CODE + " TEXT, " +
				ShopSchema.CITY + " TEXT, " +
				ShopSchema.GPS_COORDINATES + " TEXT, " +
				ShopSchema.MAP_URL + " TEXT, " +
				ShopSchema.DESCRIPTION + " TEXT, " +
				ShopSchema.DESCRIPTION_URL + " TEXT, " +
				ShopSchema.POINTS + " INTEGER, " + 
				ShopSchema.MIN_POINTS + " INTEGER, " +
				ShopSchema.AVAILABLE + " INTEGER, " +
				ShopSchema.NOT_USED + " INTEGER)";
		
		String DATABASE_CREATE_SHOP_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " + ShopItemSchema.TABLE_NAME + 
				"(" + ShopItemSchema.SHOP_ITEM_ID + " INTEGER PRIMARY KEY NOT NULL, " +
				ShopItemSchema.SHOP_ID + " INTEGER, " +
				ShopItemSchema.ICON_URL + " TEXT, " +
				ShopItemSchema.NAME + " TEXT, " +
				ShopItemSchema.DESCRIPTION + " TEXT, " +
				ShopItemSchema.TYPE + " INTEGER, " + 
				ShopItemSchema.COUNT + " INTEGER, " + 
				ShopItemSchema.POINTS + " INTEGER, " +
				ShopItemSchema.AVAILABLE + " INTEGER, " + 
				ShopItemSchema.NOT_USED + " INTEGER)";
		

		
		
		db.execSQL(DATABASE_CREATE_ADVERTISEMENT_TABLE);
		db.execSQL(DATABASE_CREATE_FACEBOOK_ADVERTISEMENT_EVENT_TABLE);
		db.execSQL(DATABASE_CREATE_FACEBOOK_SHARED_POST_INFO_TABLE);
		db.execSQL(DATABASE_CREATE_ADVERTISEMENT_APP_INFO_TABLE);
		db.execSQL(DATABASE_CREATE_SHOP_TABLE);
		db.execSQL(DATABASE_CREATE_SHOP_ITEM_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + AdvertisementSchema.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FacebookAdvertisementEventSchema.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + FacebookSharedPostInfoSchema.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + AdvertisementAppInfoSchema.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ShopSchema.TABLE_NAME);
		db.execSQL("DROP TABLE IF EXISTS " + ShopItemSchema.TABLE_NAME);
		
        onCreate(db);
	}

}
