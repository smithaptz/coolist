package com.count2v.coolist.client;

public interface StatusType {
	int EXCEPTION = 2000;
	
	int RECEIVED_JSON_NOT_MATCH = 2002;
	int RECEIVED_NULL_DATA = 2003;
	
	int UNKNOWN_TRANSACTION_TYPE = 2100;
	int SHOP_NOT_AVAILABLE = 2101;
	int SHOP_ITEM_NOT_AVAILABLE = 2102;
	int USER_CANNOT_AFFORD_SHOP_ITEM = 2103;
	int QRCODE_DECODE_ERROR = 2104;
	int QRCODE_NOT_MATCH = 2105;
	int MD5_ENCODE_ERROR = 2106;
	
	int CONNECTION_SUCCESS = 200;
	int CONNECTION_TIMEOUT = -1;
	int CONNECTION_RESPOND_NOT_JSON = 2001;
	
	
	int NO_PERMISSION = 1001;
	int MISSING_ARGUMENT = 1002;
	
	int FACEBOOK_FAILED_OR_REPEATED = 1003;
	
	int ADVERTISEMENT_HAS_BEEN_READ = 1004;
	int ADVERTISEMENT_HAS_NOT_BEEN_READ = 1005;
	int ADVERTISEMENT_CAN_BE_READ = 1006;
	int ADVERTISEMENT_CAN_NOT_BE_READ = 1007;
	
	int FACEBOOK_ACCOUNT_NOT_QUALIFIED = 1011;
	
	int ADVERTISEMENT_APP_DOWNLOADED_EVENT_HAS_DONE = 1008;
	int ADVERTISEMENT_APP_DOWNLOADED_EVENT_NOT_EXIST = 1009;
	
	int USER_POINTS_NOT_ENOUGH = 1101;
	int SHOP_POINTS_NOT_ENOUGH = 1102;
	int USER_ALREADY_READ_THE_ADVERTISEMNT = 1103;
	int SHOP_ITEM_UNAVAILABLE = 1104;
	int COUPON_CODE_UNAVAILABLE = 1105;
}
