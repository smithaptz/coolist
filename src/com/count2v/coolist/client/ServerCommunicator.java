package com.count2v.coolist.client;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.util.Log;

import com.count2v.coolist.client.data.AdvertisementAppInfoData;
import com.count2v.coolist.client.data.AdvertisementData;
import com.count2v.coolist.client.data.AdvertisementRecordData;
import com.count2v.coolist.client.data.AdvertiserData;
import com.count2v.coolist.client.data.CheckData;
import com.count2v.coolist.client.data.ConsumptionRecordData;
import com.count2v.coolist.client.data.EventCheckData;
import com.count2v.coolist.client.data.FacebookAdvertisementEventData;
import com.count2v.coolist.client.data.FacebookEventRecordData;
import com.count2v.coolist.client.data.FacebookSharedPostInfoData;
import com.count2v.coolist.client.data.LoginCheckData;
import com.count2v.coolist.client.data.ServerAnnouncementData;
import com.count2v.coolist.client.data.ServerStatusData;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.client.data.TransactionResultData;
import com.count2v.coolist.client.data.VersionData;
import com.count2v.coolist.net.HttpJSONHandler;


public class ServerCommunicator {
	public static final String ORDER_BY_LATEST = "latest";
	public static final String ORDER_BY_USER_FAVORTIE = "favorite";
	public static final String ORDER_BY_POPULAR = "popular";
	public static final String OS = "Android";
	
	//private final static String SERVER_URL = "http://140.112.90.29/";
	//private final static String SERVER_URL = "http://122.116.171.187/";
	//private final static String SERVER_URL = "http://192.168.1.28/";
	private final static String SERVER_URL = "http://www.count2v.com/";
	//private final static String SERVER_URL = "http://www.count2v.com/test/";
	private final static String SERVER_BASE_URL = SERVER_URL + "sdg_one2print_main.php?";
	
	private static final String SUCCESS = "success";
	private static final String ERROR_CODE = "error_code";
	private static final String LOG = "log";
	
	
	
	public interface Callback<T> {
		void onComplete(T element, int statusCode);
	}
	
	public static void execute(ClientRequest<?> request) {
		request.execute();
	}
		
	/*
	 * 不保證回傳順序
	 * 
	 */
	
	public static void execute(List<ClientRequest<?>> requestList) {
		for(ClientRequest<?> request : requestList) {
			request.execute();
		}
	}
	
	public static void executeOnDefaultThread(ClientRequest<?> request) {
		request.executeOnDefaultThread();
	}
	
	public static void executeOnDefaultThread(List<ClientRequest<?>> requestList) {
		for(ClientRequest<?> request : requestList) {
			request.executeOnDefaultThread();
		}
	}
	
	
	private static String getEncodeString(String s) {
		/*
		String result = null;
		
		try {
			result = URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return result;
		*/
		return s;
	}
	
	private static String getUTF8EncodeString(String s) {
		String result = null;
		
		try {
			result = URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static int intValueOf(String s) {
		if(s == null || "".equals(s)) {
			return -1;
		}
		
		return Integer.valueOf(s);
	}
	
	public static void postErrorMessage(String message, int errorCode) {
		postErrorMessage("LocalErrorCode: " + errorCode + ", Message: " + message);
	}
	
	public static void postErrorMessage(ClientRequest<?> request, int errorCode) {
		postErrorMessage("LocalErrorCode: " + errorCode + ", Query: " + request.toString());
	}
	
	public static void postErrorMessage(ClientRequest<?> request, String message, int errorCode) {
		postErrorMessage("LocalErrorCode: " + errorCode + ", Query: " + request.toString() + ", Message: " + message);
	}
	
	public static void postErrorMessage(String message) {
		Log.d(ServerCommunicator.class.toString(), "Trying to post error message to server: " + message);
		
		
		ClientRequest<CheckData> request = requestSetUserLog(getUTF8EncodeString(message), new Callback<CheckData>() {

			@Override
			public void onComplete(CheckData element, int statusCode) {
				if(statusCode == StatusType.CONNECTION_SUCCESS) {
					Log.d(ServerCommunicator.class.toString(), "postErrorMessage: successful");
				} else {
					Log.d(ServerCommunicator.class.toString(), "postErrorMessage: " + statusCode);
				}
			}
		});
		
		execute(request);
	}
	
	
	
	public static ClientRequest<LoginCheckData> requestLogin(String facebookAccessToken, Callback<LoginCheckData> callback) {
		String query = "login.php?fb_token=" +  facebookAccessToken;
		
		return instanceLoginRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestLogout(Callback<CheckData> callback) {
		String query = "logout.php";
		
		return instanceLogoutRequest(query, callback);
	}
	
	public static ClientRequest<Integer> requestGetUserPoints(Callback<Integer> callback) {
		String query = getEncodeString("function=getUserPoints");
		
		return instanceGetUserPointsRequest(query, callback);
	}
	
	public static ClientRequest<VersionData> requestGetVersion(Callback<VersionData> callback) {
		
		final String query = "function=getVersion" + "&os=" + OS + "&order=" + ORDER_BY_LATEST + "&limit=1";
				
		return new ClientRequest<VersionData>(callback, new ClientRequest.RequestExecutor<VersionData>() {

			@Override
			public ConnectionStatus<VersionData> exeRequest() {
				ConnectionStatus<List<VersionData>> connctionStatus = exeGetVersionRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<VersionData> element = connctionStatus.getElement();
				
				VersionData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<VersionData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
		
	}
	
	public static ClientRequest<ServerStatusData> requestGetServerStatus(Callback<ServerStatusData> callback) {
		final String query = "function=getServerStatus" + "&order=" + ORDER_BY_LATEST + "&limit=1";
		
		return new ClientRequest<ServerStatusData>(callback, new ClientRequest.RequestExecutor<ServerStatusData>() {

			@Override
			public ConnectionStatus<ServerStatusData> exeRequest() {
				ConnectionStatus<List<ServerStatusData>> connctionStatus = exeGetServerStatusRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<ServerStatusData> element = connctionStatus.getElement();
				
				ServerStatusData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<ServerStatusData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
		
	public static ClientRequest<ServerAnnouncementData> requestGetServerAnnouncement(Callback<ServerAnnouncementData> callback) {
		final String query = "function=getServerAnnouncements" + "&order=" + ORDER_BY_LATEST + "&limit=1";
		
		
		return new ClientRequest<ServerAnnouncementData>(callback, new ClientRequest.RequestExecutor<ServerAnnouncementData>() {

			@Override
			public ConnectionStatus<ServerAnnouncementData> exeRequest() {
				ConnectionStatus<List<ServerAnnouncementData>> connctionStatus = exeGetServerAnnouncementsRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<ServerAnnouncementData> element = connctionStatus.getElement();
				
				ServerAnnouncementData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<ServerAnnouncementData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	public static ClientRequest<List<ServerAnnouncementData>> requestGetServerAnnouncements(Callback<List<ServerAnnouncementData>> callback) {
		String query = "function=getServerAnnouncements";
		
		return instanceGetServerAnnouncementsRequest(query, callback);
	}
	
	public static ClientRequest<List<ServerAnnouncementData>> requestGetServerAnnouncements(long limit, Callback<List<ServerAnnouncementData>> callback) {
		String query = "function=getServerAnnouncements" + "&limit=" + limit;
		
		return instanceGetServerAnnouncementsRequest(query, callback);
	}
	
	public static ClientRequest<List<ServerAnnouncementData>> requestGetServerAnnouncements(String order, Callback<List<ServerAnnouncementData>> callback) {
		String query = "function=getServerAnnouncements" + "&order=" + order;
		
		return instanceGetServerAnnouncementsRequest(query, callback);
	}
	
	public static ClientRequest<List<ServerAnnouncementData>> requestGetServerAnnouncements(String order, long limit, 
			Callback<List<ServerAnnouncementData>> callback) {
		String query = "function=getServerAnnouncements"  + "&order=" + order +  "&limit=" + limit;
		
		return instanceGetServerAnnouncementsRequest(query, callback);
	}
		
	public static ClientRequest<ShopData> requestGetShop(long id, Callback<ShopData> callback) {
		final String query = "function=getShops" + "&id=" + id;
		
		return new ClientRequest<ShopData>(callback, new ClientRequest.RequestExecutor<ShopData>() {

			@Override
			public ConnectionStatus<ShopData> exeRequest() {
				ConnectionStatus<List<ShopData>> connctionStatus = exeGetShopsRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<ShopData> element = connctionStatus.getElement();
				
				ShopData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<ShopData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
		
	}
	
	public static ClientRequest<List<ShopData>> requestGetShops(Callback<List<ShopData>> callback) {
		String query = "function=getShops";
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShops(long limit, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShops(String order, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&order=" + order;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShops(String order, long limit, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&order=" + order + 
				"&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByCity(String city, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&city=" + city;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByCity(String city, long limit, 
			Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&city=" + city + 
				"&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByCity(String city, String order, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&city=" + city + 
				"&order=" + order;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByCity(String city, String order, long limit, 
			Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&city=" + city + 
				"&order=" + order + "&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByDistrict(String district, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&district=" + district;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByDistrict(String district, long limit, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&district=" + district + 
				"&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByDistrict(String district, String order, Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&district=" + district + 
				"&order=" + order;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopData>> requestGetShopsByDistrict(String district, String order, long limit, 
			Callback<List<ShopData>> callback) {
		String query = "function=getShops" + "&district=" + district + 
				"&order=" + order + "&limit=" + limit;
		
		return instanceGetShopsRequest(query, callback);
	}
	
	public static ClientRequest<List<ShopItemData>> requestGetShopItems(Callback<List<ShopItemData>> callback) {
		String query = "function=getItems";
		
		return instanceGetShopItemsRequest(query, callback);
	}
	
	public static ClientRequest<AdvertiserData> requestGetAdvertiser(long id, Callback<AdvertiserData> callback) {
		final String query = "function=getAdvertisers" + "&id=" + id;
		
		return new ClientRequest<AdvertiserData>(callback, new ClientRequest.RequestExecutor<AdvertiserData>() {

			@Override
			public ConnectionStatus<AdvertiserData> exeRequest() {
				ConnectionStatus<List<AdvertiserData>> connctionStatus = exeGetAdvertisersRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<AdvertiserData> element = connctionStatus.getElement();
				
				AdvertiserData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<AdvertiserData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
		
	}
	
	public static ClientRequest<List<AdvertiserData>> requestGetAdvertisers(Callback<List<AdvertiserData>> callback) {
		String query = "function=getAdvertisers";
		
		return instanceGetAdvertisersRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertiserData>> requestGetAdvertisers(long limit, Callback<List<AdvertiserData>> callback) {
		String query = "function=getAdvertisers" + "&limit=" + limit;
		
		return instanceGetAdvertisersRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecords(Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord";
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecords(long limit, Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&limit=" + limit;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecords(String order, Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&order=" + order;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecords(String order, long limit, 
			Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&order=" + order + "&limit=" + limit;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecordsByShopId(long shopId, 
			Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&shop_id=" + shopId;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecordsByShopId(long shopId, String order,
			Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&shop_id=" + shopId + "&order=" + order;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecordsByShopId(long shopId, long limit, 
			Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&shop_id=" + shopId + "&limit=" + limit;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<ConsumptionRecordData>> requestGetConsumptionRecordsByShopId(long shopId, String order, long limit, 
			Callback<List<ConsumptionRecordData>> callback) {
		String query = "function=getConsumptionRecord" + "&shop_id=" + shopId + "&order=" + order + "&limit=" + limit;
		
		return instanceGetConsumptionRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementRecordData>> requestGetAdvertisementRecords(Callback<List<AdvertisementRecordData>> callback) {
		String query = "function=getAdvertisementRecord";
		
		return instanceGetAdvertisementRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementRecordData>> requestGetAdvertisementRecords(long limit, 
			Callback<List<AdvertisementRecordData>> callback) {
		String query = "function=getAdvertisementRecord" + "&limit=" + limit;
		
		return instanceGetAdvertisementRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementRecordData>> requestGetAdvertisementRecords(String order, 
			Callback<List<AdvertisementRecordData>> callback) {
		String query = "function=getAdvertisementRecord" + "&order=" + order;
		
		return instanceGetAdvertisementRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementRecordData>> requestGetAdvertisementRecords(String order, long limit, 
			Callback<List<AdvertisementRecordData>> callback) {
		String query = "function=getAdvertisementRecord" + "&order=" + order + "&limit=" + limit;
		
		return instanceGetAdvertisementRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookEventRecordData>> requestGetFacebookEventRecords(Callback<List<FacebookEventRecordData>> callback) {
		String query = "function=getFacebookRecord";
		
		return instanceGetFacebookEventRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookEventRecordData>> requestGetFacebookEventRecords(long limit, 
			Callback<List<FacebookEventRecordData>> callback) {
		String query = "function=getFacebookRecord" + "&limit=" + limit;
		
		return instanceGetFacebookEventRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookEventRecordData>> requestGetFacebookEventRecords(String order, 
			Callback<List<FacebookEventRecordData>> callback) {
		String query = "function=getFacebookRecord" + "&order=" + order;
		
		return instanceGetFacebookEventRecordsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookEventRecordData>> requestGetFacebookEventRecords(String order, long limit, 
			Callback<List<FacebookEventRecordData>> callback) {
		String query = "function=getFacebookRecord" + "&order=" + order + "&limit=" + limit;
		
		return instanceGetFacebookEventRecordsRequest(query, callback);
	}
	
	public static ClientRequest<AdvertisementData> requestGetAdvertisement(long advertisementId, Callback<AdvertisementData> callback) {
		final String query = "function=getAdvertisements" + "&advertisement_id=" + advertisementId;
		
		return new ClientRequest<AdvertisementData>(callback, new ClientRequest.RequestExecutor<AdvertisementData>() {

			@Override
			public ConnectionStatus<AdvertisementData> exeRequest() {
				ConnectionStatus<List<AdvertisementData>> connctionStatus = exeGetAdvertisementsRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<AdvertisementData> element = connctionStatus.getElement();
				
				AdvertisementData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<AdvertisementData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
		
	}
	
	public static ClientRequest<List<AdvertisementData>> requestGetAdvertisements(Callback<List<AdvertisementData>> callback) {
		String query = "function=getAdvertisements";
		
		return instanceGetAdvertisementsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementData>> requestGetAdvertisements(long limit, Callback<List<AdvertisementData>> callback) {
		String query = "function=getAdvertisements" + "&limit=" + limit;
		
		return instanceGetAdvertisementsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementData>> requestGetAdvertisements(String order, Callback<List<AdvertisementData>> callback) {
		String query = "function=getAdvertisements" + "&order=" + order;
		
		return instanceGetAdvertisementsRequest(query, callback);
	}
	
	public static ClientRequest<List<AdvertisementData>> requestGetAdvertisements(String order, long limit, 
			Callback<List<AdvertisementData>> callback) {
		String query = "function=getAdvertisements" + "&order=" + order + "&limit=" + limit;
		
		return instanceGetAdvertisementsRequest(query, callback);
	}
	
	public static ClientRequest<FacebookAdvertisementEventData> requestGetFacebookAdvertisementEvent(
			long facebookAdvertisementEventId, Callback<FacebookAdvertisementEventData> callback) {
		final String query = "function=getFacebookAdvertisementEvent" + "&facebook_event_id=" + facebookAdvertisementEventId;
		
		return new ClientRequest<FacebookAdvertisementEventData>(callback, new ClientRequest.RequestExecutor<FacebookAdvertisementEventData>() {

			@Override
			public ConnectionStatus<FacebookAdvertisementEventData> exeRequest() {
				ConnectionStatus<List<FacebookAdvertisementEventData>> connctionStatus = exeGetFacebookAdvertisementEventsRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<FacebookAdvertisementEventData> element = connctionStatus.getElement();
				
				FacebookAdvertisementEventData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<FacebookAdvertisementEventData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
		
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEvents(
			Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent";
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEvents(long limit, 
			Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&limit=" + limit;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEvents(String order, 
			Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&order=" + order;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEvents(String order, long limit, 
			Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&order=" + order + "&limit=" + limit;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEventsByAdvertisementId(
			long advertisementId, Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&advertisement_id=" + advertisementId;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEventsByAdvertisementId(
			long advertisementId, long limit, Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&advertisement_id=" + advertisementId + "&limit=" + limit;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEventsByAdvertisementId(
			long advertisementId, String order, Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&advertisement_id=" + advertisementId + "&order=" + order;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookAdvertisementEventData>> requestGetFacebookAdvertisementEventsByAdvertisementId(
			long advertisementId, String order, long limit, Callback<List<FacebookAdvertisementEventData>> callback) {
		String query = "function=getFacebookAdvertisementEvent" + "&advertisement_id=" + advertisementId + 
				"&order=" + order + "&limit=" + limit;
		
		return instanceGetFacebookAdvertisementEventsRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookSharedPostInfoData>> requestGetFacebookSharedPostInfo(Callback<List<FacebookSharedPostInfoData>> callback) {
		String query = "function=getFacebookEvent4Info";
		
		return instanceGetFacebookSharedPostInfoRequest(query, callback);
	}
	
	public static ClientRequest<List<FacebookSharedPostInfoData>> requestGetFacebookSharedPostInfoByAdvertisementId(
			long advertisementId, Callback<List<FacebookSharedPostInfoData>> callback) {
		String query = "function=getFacebookEvent4Info" + "&advertisement_id=" + advertisementId;
		
		return instanceGetFacebookSharedPostInfoRequest(query, callback);
	}
	
	public static ClientRequest<FacebookSharedPostInfoData> requestGetFacebookSharedPostInfoByFacebookAdvertisementEventId(
			long facebookAdvertisementEventId, Callback<FacebookSharedPostInfoData> callback) {
		final String query = "function=getFacebookEvent4Info" + "&facebook_event_id=" + facebookAdvertisementEventId;
		
		return new ClientRequest<FacebookSharedPostInfoData>(callback, new ClientRequest.RequestExecutor<FacebookSharedPostInfoData>() {

			@Override
			public ConnectionStatus<FacebookSharedPostInfoData> exeRequest() {
				ConnectionStatus<List<FacebookSharedPostInfoData>> connctionStatus = exeGetFacebookSharedPostInfoRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<FacebookSharedPostInfoData> element = connctionStatus.getElement();
				
				FacebookSharedPostInfoData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<FacebookSharedPostInfoData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	
	
	public static ClientRequest<List<AdvertisementAppInfoData>> requestGetAdvertisementAppInfo(Callback<List<AdvertisementAppInfoData>> callback) {
		String query = "function=getAdvertisementAppInfo";
		
		return instanceGetAdvertisementAppInfoRequest(query, callback);
	}
	
	
	public static ClientRequest<AdvertisementAppInfoData> requestGetAdvertisementAppInfo(long advertisementAppId, 
			Callback<AdvertisementAppInfoData> callback) {
		final String query = "function=getAdvertisementAppInfo" + "&ad_app_id=" + advertisementAppId;
		
		return new ClientRequest<AdvertisementAppInfoData>(callback, new ClientRequest.RequestExecutor<AdvertisementAppInfoData>() {

			@Override
			public ConnectionStatus<AdvertisementAppInfoData> exeRequest() {
				ConnectionStatus<List<AdvertisementAppInfoData>> connctionStatus = exeGetAdvertisementAppInfoRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<AdvertisementAppInfoData> element = connctionStatus.getElement();
				
				AdvertisementAppInfoData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<AdvertisementAppInfoData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	public static ClientRequest<AdvertisementAppInfoData> requestGetAdvertisementAppInfoByAdvertisementId(long advertisementId, 
			Callback<AdvertisementAppInfoData> callback) {
		final String query = "function=getAdvertisementAppInfo" + "&advertisement_id=" + advertisementId;
		
		return new ClientRequest<AdvertisementAppInfoData>(callback, new ClientRequest.RequestExecutor<AdvertisementAppInfoData>() {

			@Override
			public ConnectionStatus<AdvertisementAppInfoData> exeRequest() {
				ConnectionStatus<List<AdvertisementAppInfoData>> connctionStatus = exeGetAdvertisementAppInfoRequest(query);
				int statusCode = connctionStatus.getStatus();
				List<AdvertisementAppInfoData> element = connctionStatus.getElement();
				
				AdvertisementAppInfoData data = null;
				
				if(statusCode == StatusType.CONNECTION_SUCCESS && element != null && 
						element.size() > 0) {
					data = element.get(0);
				}
				
				return new ConnectionStatus<AdvertisementAppInfoData>(data, statusCode);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	
	public static ClientRequest<EventCheckData> requestIsFacebookAdvertisementEventDone(long facebookAdvertisementEventId, 
			Callback<EventCheckData> callback) {
		String query = "function=isFacebookAdvertisementEventDone" + "&facebook_event_id=" + facebookAdvertisementEventId;
		
		return instanceIsFacebookAdvertisementEventDoneRequest(query, callback);
	}
	
	public static ClientRequest<EventCheckData> requestIsFacebookAdvertisementEventDone(long facebookAdvertisementEventId,
			String userPostId, Callback<EventCheckData> callback) {
		String query = "function=isFacebookAdvertisementEventDone" + "&facebook_event_id=" + facebookAdvertisementEventId + 
				"&user_post_id=" + userPostId;
		
		return instanceIsFacebookAdvertisementEventDoneRequest(query, callback);
	}
	
	public static ClientRequest<EventCheckData> requestIsFacebookAdvertisementEventDoneByAdvertisementId(long advertisementId, 
			String pageId, String postId, int type, Callback<EventCheckData> callback) {
		String query = "function=isFacebookAdvertisementEventDone" + "&advertisement_id=" + advertisementId + "&page_id=" + pageId +
				"&post_id=" + postId + "&event=" + type;
		
		return instanceIsFacebookAdvertisementEventDoneRequest(query, callback);
	}
	
	
	public static ClientRequest<CheckData> requestIsAdvertisementRead(long advertisementId, Callback<CheckData> callback) {
		String query = "function=isAdvertisementRead" + "&advertisement_id=" + advertisementId;
		
		return instanceBasicSetRequest(query, callback);
	}
	
	/*
	public static ClientRequest<CheckData> requestSetConsumptionRecord(long shopId, int points, Callback<CheckData> callback) {
		String query = "function=setConsumptionRecord" + "&shop_id=" + shopId + "&points=" + points;
		
		return instanceBasicSetRequest(query, callback);
	}
	*/
	
	
	
	public static ClientRequest<TransactionResultData> requestSetConsumptionRecord(long shopId, long shopItemId, Callback<TransactionResultData> callback) {
		String query = "function=setConsumptionRecord" + "&shop_id=" + shopId + "&item_id=" + shopItemId;
		
		return instanceSetConsumptionRecordRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestSetFavoriteAdvertisement(long advertisementId, Callback<CheckData> callback) {
		String query = "function=setFavoriteAdvertisement" + "&advertisement_id=" + advertisementId;
		
		return instanceBasicSetRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestRemoveFavoriteAdvertisement(long advertisementId, Callback<CheckData> callback) {
		String query = "function=removeFavoriteAdvertisement" + "&advertisement_id=" + advertisementId;
		
		return instanceBasicSetRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestSetReadAdvertisement(long advertisementId, Callback<CheckData> callback) {
		String query = "function=setReadAdvertisement" + "&advertisement_id=" + advertisementId;
		
		return instanceBasicSetRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestSetAppDownloaded(long advertisementAppId, 
			Callback<CheckData> callback) {
		String query = "function=setAppDownloaded" + "&ad_app_id=" + advertisementAppId;
		
		return instanceBasicSetRequest(query, callback);
	}
	
	public static ClientRequest<CheckData> requestSetUserLog(String log, Callback<CheckData> callback) {
		String query = "function=setUserLog" + "&log=" + log;
		
		return instanceBasicSetRequest(query, callback);
	}
	
		
	private static ClientRequest<LoginCheckData> instanceLoginRequest(final String query, Callback<LoginCheckData> callback) {
		return new ClientRequest<LoginCheckData>(callback, new ClientRequest.RequestExecutor<LoginCheckData>() {

			@Override
			public ConnectionStatus<LoginCheckData> exeRequest() {
				return exeLoginRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<CheckData> instanceLogoutRequest(final String query, Callback<CheckData> callback) {
		return new ClientRequest<CheckData>(callback, new ClientRequest.RequestExecutor<CheckData>() {

			@Override
			public ConnectionStatus<CheckData> exeRequest() {
				return exeLogoutRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<Integer> instanceGetUserPointsRequest(final String query, Callback<Integer> callback) {		
		return new ClientRequest<Integer>(callback, new ClientRequest.RequestExecutor<Integer>() {

			@Override
			public ConnectionStatus<Integer> exeRequest() {
				return exeGetUserPointsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<VersionData>> instanceGetVersionRequest(final String query, Callback<List<VersionData>> callback) {
		return new ClientRequest<List<VersionData>>(callback, new ClientRequest.RequestExecutor<List<VersionData>>() {

			@Override
			public ConnectionStatus<List<VersionData>> exeRequest() {
				return exeGetVersionRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<ServerStatusData>> instanceGetServerStatusRequest(final String query, Callback<List<ServerStatusData>> callback) {
		return new ClientRequest<List<ServerStatusData>>(callback, new ClientRequest.RequestExecutor<List<ServerStatusData>>() {

			@Override
			public ConnectionStatus<List<ServerStatusData>> exeRequest() {
				return exeGetServerStatusRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<ServerAnnouncementData>> instanceGetServerAnnouncementsRequest(final String query, 
			Callback<List<ServerAnnouncementData>> callback) {
		return new ClientRequest<List<ServerAnnouncementData>>(callback, new ClientRequest.RequestExecutor<List<ServerAnnouncementData>>() {

			@Override
			public ConnectionStatus<List<ServerAnnouncementData>> exeRequest() {
				return exeGetServerAnnouncementsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<ShopData>> instanceGetShopsRequest(final String query, Callback<List<ShopData>> callback) {
		return new ClientRequest<List<ShopData>>(callback, new ClientRequest.RequestExecutor<List<ShopData>>() {

			@Override
			public ConnectionStatus<List<ShopData>> exeRequest() {
				return exeGetShopsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<ShopItemData>> instanceGetShopItemsRequest(final String query, Callback<List<ShopItemData>> callback) {
		return new ClientRequest<List<ShopItemData>>(callback, new ClientRequest.RequestExecutor<List<ShopItemData>>() {

			@Override
			public ConnectionStatus<List<ShopItemData>> exeRequest() {
				return exeGetShopItemsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<AdvertiserData>> instanceGetAdvertisersRequest(final String query, Callback<List<AdvertiserData>> callback) {
		return new ClientRequest<List<AdvertiserData>>(callback, new ClientRequest.RequestExecutor<List<AdvertiserData>>() {

			@Override
			public ConnectionStatus<List<AdvertiserData>> exeRequest() {
				return exeGetAdvertisersRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<AdvertisementData>> instanceGetAdvertisementsRequest(final String query, Callback<List<AdvertisementData>> callback) {
		return new ClientRequest<List<AdvertisementData>>(callback, new ClientRequest.RequestExecutor<List<AdvertisementData>>() {

			@Override
			public ConnectionStatus<List<AdvertisementData>> exeRequest() {
				return exeGetAdvertisementsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<FacebookAdvertisementEventData>> instanceGetFacebookAdvertisementEventsRequest(final String query, 
			Callback<List<FacebookAdvertisementEventData>> callback) {
		return new ClientRequest<List<FacebookAdvertisementEventData>>(callback, new ClientRequest.RequestExecutor<List<FacebookAdvertisementEventData>>() {

			@Override
			public ConnectionStatus<List<FacebookAdvertisementEventData>> exeRequest() {
				return exeGetFacebookAdvertisementEventsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<FacebookSharedPostInfoData>> instanceGetFacebookSharedPostInfoRequest(final String query, 
			Callback<List<FacebookSharedPostInfoData>> callback) {
		return new ClientRequest<List<FacebookSharedPostInfoData>>(callback, new ClientRequest.RequestExecutor<List<FacebookSharedPostInfoData>>() {

			@Override
			public ConnectionStatus<List<FacebookSharedPostInfoData>> exeRequest() {
				return exeGetFacebookSharedPostInfoRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<AdvertisementAppInfoData>> instanceGetAdvertisementAppInfoRequest(final String query, 
			Callback<List<AdvertisementAppInfoData>> callback) {
		return new ClientRequest<List<AdvertisementAppInfoData>>(callback, new ClientRequest.RequestExecutor<List<AdvertisementAppInfoData>>() {

			@Override
			public ConnectionStatus<List<AdvertisementAppInfoData>> exeRequest() {
				return exeGetAdvertisementAppInfoRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<ConsumptionRecordData>> instanceGetConsumptionRecordsRequest(final String query, 
			Callback<List<ConsumptionRecordData>> callback) {
		return new ClientRequest<List<ConsumptionRecordData>>(callback, new ClientRequest.RequestExecutor<List<ConsumptionRecordData>>() {

			@Override
			public ConnectionStatus<List<ConsumptionRecordData>> exeRequest() {
				return exeGetConsumptionRecordsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	

	
	private static ClientRequest<List<AdvertisementRecordData>> instanceGetAdvertisementRecordsRequest(final String query, 
			Callback<List<AdvertisementRecordData>> callback) {
		return new ClientRequest<List<AdvertisementRecordData>>(callback, new ClientRequest.RequestExecutor<List<AdvertisementRecordData>>() {

			@Override
			public ConnectionStatus<List<AdvertisementRecordData>> exeRequest() {
				return exeGetAdvertisementRecordsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<List<FacebookEventRecordData>> instanceGetFacebookEventRecordsRequest(final String query, 
			Callback<List<FacebookEventRecordData>> callback) {
		return new ClientRequest<List<FacebookEventRecordData>>(callback, new ClientRequest.RequestExecutor<List<FacebookEventRecordData>>() {

			@Override
			public ConnectionStatus<List<FacebookEventRecordData>> exeRequest() {
				return exeGetFacebookEventRecordsRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<EventCheckData> instanceIsFacebookAdvertisementEventDoneRequest(final String query, 
			Callback<EventCheckData> callback) {
		return new ClientRequest<EventCheckData>(callback, new ClientRequest.RequestExecutor<EventCheckData>() {

			@Override
			public ConnectionStatus<EventCheckData> exeRequest() {
				return exeIsFacebookAdvertisementEventDoneRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<CheckData> instanceBasicSetRequest(final String query, 
			Callback<CheckData> callback) {
		return new ClientRequest<CheckData>(callback, new ClientRequest.RequestExecutor<CheckData>() {

			@Override
			public ConnectionStatus<CheckData> exeRequest() {
				return exeBasicSetRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	private static ClientRequest<TransactionResultData> instanceSetConsumptionRecordRequest(final String query, 
			Callback<TransactionResultData> callback) {
		return new ClientRequest<TransactionResultData>(callback, new ClientRequest.RequestExecutor<TransactionResultData>() {

			@Override
			public ConnectionStatus<TransactionResultData> exeRequest() {
				return exeSetConsumptionRecordRequest(query);
			}
			
			@Override
			public String getQuery() {
				return query;
			}
		});
	}
	
	
	
	private static ConnectionStatus<LoginCheckData> exeLoginRequest(String query) {
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null || 
				jsonArray.length() == 0) {
			return new ConnectionStatus<LoginCheckData>(null, statusCode);
		}
		
		LoginCheckData result = null;

		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<LoginCheckData>(null, statusCode);
				}
			}
			
			result = new LoginCheckData();
			result.setSuccess("1".equals(jsonObject.getString(SUCCESS)));
			result.setErrorCode(intValueOf(jsonObject.getString(ERROR_CODE)));
			result.setLog(jsonObject.getString(LOG));
			result.setUserName(jsonObject.getString("user_name"));
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<LoginCheckData>(result, statusCode);
	}

	private static ConnectionStatus<CheckData> exeLogoutRequest(String query) {
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null || 
				jsonArray.length() == 0) {
			return new ConnectionStatus<CheckData>(null, statusCode);
		}
		
		
		CheckData result = null;

		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<CheckData>(null, statusCode);
				}
			}
			
			result = new CheckData();
			result.setSuccess("1".equals(jsonObject.getString(SUCCESS)));
			result.setErrorCode(intValueOf(jsonObject.getString(ERROR_CODE)));
			result.setLog(jsonObject.getString(LOG));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
	
		return new ConnectionStatus<CheckData>(result, statusCode);
	}
		
	private static ConnectionStatus<Integer> exeGetUserPointsRequest(String query) {
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		
		int statusCode = connectionStatus.getStatus();		
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null || 
				jsonArray.length() == 0) {
			return new ConnectionStatus<Integer>(null, statusCode);
		}
		
		Integer data = null;
		
		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<Integer>(null, statusCode);
				}
			}
			
			data = intValueOf(jsonObject.getString("points"));
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<Integer>(data, statusCode);
	}
	
	private static ConnectionStatus<List<VersionData>> exeGetVersionRequest(String query) {
		Log.d(ServerCommunicator.class.toString(), "exeGetVersionRequest");
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<VersionData>>(null, statusCode);
		}
		
		ArrayList<VersionData> result = new ArrayList<VersionData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<VersionData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				VersionData versionData = new VersionData();
				versionData.setId(jsonObject.getLong("id"));
				versionData.setAnnouncement(jsonObject.getString("announcement"));
				versionData.setDownloadUrl(jsonObject.getString("download_link"));
				versionData.setCurrentVersionCode(intValueOf(jsonObject.getString("current_version_code")));
				versionData.setCurrentVersionName(jsonObject.getString("current_version_name"));
				versionData.setMinimumVersionCode(intValueOf(jsonObject.getString("minimum_version_code")));
				versionData.setMinimumVersionName(jsonObject.getString("minimum_version_name"));
				versionData.setPublishDate(jsonObject.getString("published_date"));
				
				result.add(versionData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<VersionData>>(result, statusCode); 
	}
	
	private static ConnectionStatus<List<ServerStatusData>> exeGetServerStatusRequest(String query) {
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<ServerStatusData>>(null, statusCode);
		}
		
		ArrayList<ServerStatusData> result = new ArrayList<ServerStatusData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<ServerStatusData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ServerStatusData serverStatusData = new ServerStatusData();
				serverStatusData.setId(jsonObject.getLong("id"));
				serverStatusData.setStatus(intValueOf(jsonObject.getString("status")));
				serverStatusData.setUrl(jsonObject.getString("link"));
				
				result.add(serverStatusData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<ServerStatusData>>(result, statusCode); 
	}
	
	
	private static ConnectionStatus<List<ServerAnnouncementData>> exeGetServerAnnouncementsRequest(String query) {
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<ServerAnnouncementData>>(null, statusCode);
		}
		
		ArrayList<ServerAnnouncementData> result = new ArrayList<ServerAnnouncementData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<ServerAnnouncementData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ServerAnnouncementData serverAnnouncementData = new ServerAnnouncementData();
				serverAnnouncementData.setId(jsonObject.getLong("id"));
				serverAnnouncementData.setAnnouncement(jsonObject.getString("announcement"));
				serverAnnouncementData.setTimeStamp(jsonObject.getString("time_stamp"));
				
				result.add(serverAnnouncementData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<ServerAnnouncementData>>(result, statusCode); 
	}
	
	private static ConnectionStatus<List<ShopData>> exeGetShopsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<ShopData>>(null, statusCode);
		}
		
		
		
		ArrayList<ShopData> result = new ArrayList<ShopData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<ShopData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ShopData shopData = new ShopData();
				shopData.setShopId(jsonObject.getLong("shop_id"));
				shopData.setName(jsonObject.getString("name"));
				shopData.setWebsiteUrl(jsonObject.getString("website_link"));
				shopData.setDescription(jsonObject.getString("description"));
				shopData.setDescriptionUrl(jsonObject.getString("description_link"));
				shopData.setLogoUrl(jsonObject.getString("logo_link"));
				shopData.setPoints(intValueOf(jsonObject.getString("total_points")));
				shopData.setMinPoints(intValueOf(jsonObject.getString("min_points")));
				shopData.setZipCode(jsonObject.getString("zip_code"));
				shopData.setCity(jsonObject.getString("city"));
				shopData.setDistrict(jsonObject.getString("district"));
				shopData.setAddress(jsonObject.getString("address"));
				shopData.setPhone(jsonObject.getString("phone"));
				shopData.setGPSCoordinates(jsonObject.getString("gps"));
				shopData.setMapUrl(jsonObject.getString("map"));
				shopData.setAvailable("1".equals(jsonObject.getString("available")));
				shopData.setNotUsed("1".equals(jsonObject.getString("not_used")));
				
				result.add(shopData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<ShopData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<ShopItemData>> exeGetShopItemsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<ShopItemData>>(null, statusCode);
		}
		
		
		
		ArrayList<ShopItemData> result = new ArrayList<ShopItemData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<ShopItemData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ShopItemData shopItemData = new ShopItemData();
				shopItemData.setShopItemId(jsonObject.getLong("item_id"));
				shopItemData.setShopId(jsonObject.getLong("shop_id"));
				shopItemData.setIconUrl(jsonObject.getString("icon_link"));
				shopItemData.setName(jsonObject.getString("name"));
				shopItemData.setDescription(jsonObject.getString("description"));
				shopItemData.setType(intValueOf(jsonObject.getString("item_type")));
				shopItemData.setCount(intValueOf(jsonObject.getString("counts")));
				shopItemData.setPoints(intValueOf(jsonObject.getString("points")));
				shopItemData.setAvailable("1".equals(jsonObject.getString("available")));
				shopItemData.setNotUsed("1".equals(jsonObject.getString("not_used")));
				
				result.add(shopItemData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<ShopItemData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<AdvertiserData>> exeGetAdvertisersRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<AdvertiserData>>(null, statusCode);
		}
		
		
		
		ArrayList<AdvertiserData> result = new ArrayList<AdvertiserData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<AdvertiserData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				AdvertiserData advertiserData = new AdvertiserData();
				advertiserData.setAdvertiserId(jsonObject.getLong("advertiser_id"));
				advertiserData.setName(jsonObject.getString("name"));
				advertiserData.setWebsiteUrl(jsonObject.getString("website_link"));
				advertiserData.setPageUrl(jsonObject.getString("page_link"));
				advertiserData.setLogoUrl(jsonObject.getString("logo_link"));
				advertiserData.setDescription(jsonObject.getString("description"));
				
				
				result.add(advertiserData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<AdvertiserData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<AdvertisementData>> exeGetAdvertisementsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<AdvertisementData>>(null, statusCode);
		}
		
		
		
		ArrayList<AdvertisementData> result = new ArrayList<AdvertisementData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<AdvertisementData>>(null, statusCode);
					}
				}
			}
			
			long lastAdvertisementId = -1;
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				long advertisementId = jsonObject.getLong("advertisement_id");
				
				if(advertisementId == lastAdvertisementId) {
					continue;
				}
				
				AdvertisementData advertisementData = new AdvertisementData();
				advertisementData.setAdvertisementId(advertisementId);
				advertisementData.setAdvertiserId(jsonObject.getLong("advertiser_id"));
				advertisementData.setAdvertisementName(jsonObject.getString("advertisement_name"));
				advertisementData.setIconUrl(jsonObject.getString("icon_link"));
				advertisementData.setPictureUrl(jsonObject.getString("picture"));
				advertisementData.setWebsiteUrl(jsonObject.getString("website_link"));
				advertisementData.setPopularity(jsonObject.getLong("popularity"));
				advertisementData.setGameType(intValueOf(jsonObject.getString("game_type")));
				advertisementData.setGamePoints(intValueOf(jsonObject.getString("game_points")));
				advertisementData.setRead("1".equals(jsonObject.getString("read")));
				advertisementData.setAvailable("1".equals(jsonObject.getString("available")));
				//advertisementData.setReadTimes(intValueOf(jsonObject.getString("times")));
				advertisementData.setStartTime(jsonObject.getString("start_time"));
				advertisementData.setEndTime(jsonObject.getString("end_time"));
				
				result.add(advertisementData);
				
				lastAdvertisementId  = advertisementId;
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<AdvertisementData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<FacebookAdvertisementEventData>> exeGetFacebookAdvertisementEventsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<FacebookAdvertisementEventData>>(null, statusCode);
		}
		
		
		
		ArrayList<FacebookAdvertisementEventData> result = new ArrayList<FacebookAdvertisementEventData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<FacebookAdvertisementEventData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				FacebookAdvertisementEventData facebookAdvertisementEventData = new FacebookAdvertisementEventData();
				facebookAdvertisementEventData.setFacebookAdvertisementEventId(jsonObject.getLong("facebook_event_id"));
				facebookAdvertisementEventData.setAdvertisementId(jsonObject.getLong("advertisement_id"));
				facebookAdvertisementEventData.setAdvertiserId(jsonObject.getLong("advertiser_id"));
				facebookAdvertisementEventData.setType(intValueOf(jsonObject.getString("event")));
				facebookAdvertisementEventData.setPostId(jsonObject.getString("post_id"));
				facebookAdvertisementEventData.setPageId(jsonObject.getString("page_id"));
				facebookAdvertisementEventData.setPoints(intValueOf(jsonObject.getString("points")));
				facebookAdvertisementEventData.setDone("1".equals(jsonObject.getString("done")));
				
				result.add(facebookAdvertisementEventData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<FacebookAdvertisementEventData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<FacebookSharedPostInfoData>> exeGetFacebookSharedPostInfoRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<FacebookSharedPostInfoData>>(null, statusCode);
		}
		
		
		
		ArrayList<FacebookSharedPostInfoData> result = new ArrayList<FacebookSharedPostInfoData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<FacebookSharedPostInfoData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				FacebookSharedPostInfoData fcebookSharedPostInfoData = new FacebookSharedPostInfoData();
				fcebookSharedPostInfoData.setFacebookAdvertisementEventId(jsonObject.getLong("facebook_event_id"));
				fcebookSharedPostInfoData.setAdvertisementId(jsonObject.getLong("advertisement_id"));
				fcebookSharedPostInfoData.setName(jsonObject.getString("name"));
				fcebookSharedPostInfoData.setCaption(jsonObject.getString("caption"));
				fcebookSharedPostInfoData.setDescription(jsonObject.getString("description"));
				fcebookSharedPostInfoData.setUrl(jsonObject.getString("link"));
				fcebookSharedPostInfoData.setPictureUrl(jsonObject.getString("picture"));
				fcebookSharedPostInfoData.setPlace(jsonObject.getString("place"));
				fcebookSharedPostInfoData.setRef(jsonObject.getString("ref"));
				fcebookSharedPostInfoData.setPopularity(intValueOf(jsonObject.getString("popularity")));
				
				result.add(fcebookSharedPostInfoData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<FacebookSharedPostInfoData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<AdvertisementAppInfoData>> exeGetAdvertisementAppInfoRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<AdvertisementAppInfoData>>(null, statusCode);
		}
		
		
		
		ArrayList<AdvertisementAppInfoData> result = new ArrayList<AdvertisementAppInfoData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<AdvertisementAppInfoData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				AdvertisementAppInfoData advertisementAppInfoData = new AdvertisementAppInfoData();
				advertisementAppInfoData.setAdvertisementAppId(jsonObject.getLong("ad_app_id"));
				advertisementAppInfoData.setAdvertisementId(jsonObject.getLong("advertisement_id"));
				advertisementAppInfoData.setAdvertiserId(jsonObject.getLong("advertiser_id"));
				advertisementAppInfoData.setAndroidMarketId(jsonObject.getString("android_market_id"));
				advertisementAppInfoData.setDone("1".equals(jsonObject.getString("done")));
				advertisementAppInfoData.setName(jsonObject.getString("name"));
				advertisementAppInfoData.setPoints(intValueOf(jsonObject.getString("points")));
				advertisementAppInfoData.setPopuarity(intValueOf(jsonObject.getString("popularity")));
				
				result.add(advertisementAppInfoData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<AdvertisementAppInfoData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<ConsumptionRecordData>> exeGetConsumptionRecordsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<ConsumptionRecordData>>(null, statusCode);
		}
		
		
		
		ArrayList<ConsumptionRecordData> result = new ArrayList<ConsumptionRecordData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<ConsumptionRecordData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				ConsumptionRecordData consumptionRecordData = new ConsumptionRecordData();
				consumptionRecordData.setId(jsonObject.getLong("id"));
				consumptionRecordData.setShopId(jsonObject.getLong("shop_id"));
				consumptionRecordData.setPoints(intValueOf(jsonObject.getString("points")));
				consumptionRecordData.setTimeStamp(jsonObject.getString("time_stamp"));
				
				
				result.add(consumptionRecordData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<List<ConsumptionRecordData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<AdvertisementRecordData>> exeGetAdvertisementRecordsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<AdvertisementRecordData>>(null, statusCode);
		}
		
		
		
		ArrayList<AdvertisementRecordData> result = new ArrayList<AdvertisementRecordData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<AdvertisementRecordData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				AdvertisementRecordData advertisementRecordData = new AdvertisementRecordData();
				advertisementRecordData.setId(jsonObject.getLong("id"));
				advertisementRecordData.setAdvertisementId(jsonObject.getLong("advertisement_id"));
				advertisementRecordData.setPoints(intValueOf(jsonObject.getString("points")));
				advertisementRecordData.setTimeStamp(jsonObject.getString("time_stamp"));
				
				result.add(advertisementRecordData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}

		return new ConnectionStatus<List<AdvertisementRecordData>>(result, statusCode);
	}
	
	private static ConnectionStatus<List<FacebookEventRecordData>> exeGetFacebookEventRecordsRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<List<FacebookEventRecordData>>(null, statusCode);
		}
		
		ArrayList<FacebookEventRecordData> result = new ArrayList<FacebookEventRecordData>();
		
		try {
			if(jsonArray.length() == 1) {
				JSONObject jsonObject = jsonArray.getJSONObject(0);
				if(!jsonObject.isNull(ERROR_CODE)) {
					int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
					if(errorCode != 0) {
						statusCode = 1000 + errorCode;
						return new ConnectionStatus<List<FacebookEventRecordData>>(null, statusCode);
					}
				}
			}
			
			for(int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				FacebookEventRecordData facebookEventRecordData = new FacebookEventRecordData();
				facebookEventRecordData.setId(jsonObject.getLong("id"));
				facebookEventRecordData.setAdvertisementId(jsonObject.getLong("advertisement_id"));
				facebookEventRecordData.setType(intValueOf(jsonObject.getString("type")));
				facebookEventRecordData.setPageId(jsonObject.getString("page_id"));
				facebookEventRecordData.setPostId(jsonObject.getString("post_id"));
				facebookEventRecordData.setPoints(intValueOf(jsonObject.getString("points")));
				facebookEventRecordData.setTimeStamp(jsonObject.getString("time_stamp"));
				result.add(facebookEventRecordData);
			}	
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}

		return new ConnectionStatus<List<FacebookEventRecordData>>(result, statusCode);
	}
	
	private static ConnectionStatus<EventCheckData> exeIsFacebookAdvertisementEventDoneRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null) {
			return new ConnectionStatus<EventCheckData>(null, statusCode);
		}
		
		
		
		EventCheckData result = null;
		
		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<EventCheckData>(null, statusCode);
				}
			}
			
			result = new EventCheckData();
			result.setSuccess("1".equals(jsonObject.getString(SUCCESS)));
			result.setErrorCode(intValueOf(jsonObject.getString(ERROR_CODE)));
			result.setLog(jsonObject.getString(LOG));
			result.setRepeated("1".equals(jsonObject.getString("repeated")));
			result.setDone("1".equals(jsonObject.getString("done")));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<EventCheckData>(result, statusCode);
	}
	
	/*
	 * setConsumptionRecord
	 * setFavoriteAdvertisement
	 * setReadAdvertisement
	 * isAdvertisementRead
	 * removeFavoriteAdvertisement
	 * 
	 */
	
	private static ConnectionStatus<CheckData> exeBasicSetRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null || 
				jsonArray.length() == 0) {
			return new ConnectionStatus<CheckData>(null, statusCode);
		}
		
		
		
		CheckData result = null;

		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<CheckData>(null, statusCode);
				}
			}
			
			result = new CheckData();
			result.setSuccess("1".equals(jsonObject.getString(SUCCESS)));
			result.setErrorCode(intValueOf(jsonObject.getString(ERROR_CODE)));
			result.setLog(jsonObject.getString(LOG));
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<CheckData>(result, statusCode);
	}
	

	private static ConnectionStatus<TransactionResultData> exeSetConsumptionRecordRequest(String query) {
		
		
		ConnectionStatus<JSONArray> connectionStatus = HttpJSONHandler.getJSON(SERVER_BASE_URL + getEncodeString(query));
		int statusCode = connectionStatus.getStatus();
		JSONArray jsonArray = connectionStatus.getElement();
		
		if(statusCode != StatusType.CONNECTION_SUCCESS || jsonArray == null || 
				jsonArray.length() == 0) {
			return new ConnectionStatus<TransactionResultData>(null, statusCode);
		}
		
		
		
		TransactionResultData result = null;

		try {
			JSONObject jsonObject = jsonArray.getJSONObject(0);
			
			if(!jsonObject.isNull(ERROR_CODE)) {
				int errorCode = intValueOf(jsonObject.getString(ERROR_CODE));
				if(errorCode != 0) {
					statusCode = 1000 + errorCode;
					return new ConnectionStatus<TransactionResultData>(null, statusCode);
				}
			}
			
			result = new TransactionResultData();
			result.setSuccess("1".equals(jsonObject.getString(SUCCESS)));
			result.setErrorCode(intValueOf(jsonObject.getString(ERROR_CODE)));
			result.setLog(jsonObject.getString(LOG));
			result.setSerialCode(jsonObject.getString("serial_code"));
			result.setType(intValueOf(jsonObject.getString("item_type")));
			
			JSONObject extraInfo = new JSONObject(jsonObject.getString("extra"));
			
			HashMap<String, String> extraInfoMap = new HashMap<String, String>();
			
			@SuppressWarnings("unchecked")
			Iterator<String> iterator = extraInfo.keys();
			
			while(iterator.hasNext()) {
				String key = iterator.next();
				String value = extraInfo.getString(key);
				extraInfoMap.put(key, value);
			}
			
			result.setExtraInfoMap(extraInfoMap);
			
			
		} catch (JSONException e) {
			e.printStackTrace();
			statusCode = StatusType.RECEIVED_JSON_NOT_MATCH;
		}
		
		return new ConnectionStatus<TransactionResultData>(result, statusCode);
	}
	
}
