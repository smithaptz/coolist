package com.count2v.coolist.net;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import android.util.Log;

import com.count2v.coolist.client.ConnectionStatus;
import com.count2v.coolist.client.StatusType;




public class HttpJSONHandler {
	public static final int TIMEOUT = 9000;
	
	private static CookieStore cookieStore = new BasicCookieStore();
	
	
	public static ConnectionStatus<JSONArray> getJSON(String url) {
		// ** Log.d(HttpJSONHandler.class.toString(), "url: " + url);
		ConnectionStatus<String> status = doGet(url);
		int statusCode = status.getStatus();
		String data = status.getElement();
		
		//Log.d(HttpJSONHandler.class.toString(), "data: " + data);
		
		if(statusCode != StatusType.CONNECTION_SUCCESS) {
			// ** Log.d(HttpJSONHandler.class.toString(), "doGet(url) failed");
			return new ConnectionStatus<JSONArray>(null, statusCode);
		}
		
		if(data == null || data.length() == 0) {
			return new ConnectionStatus<JSONArray>(null, StatusType.RECEIVED_NULL_DATA);
		}
		
		// 全都變成JSON ARRAY
		
		if(data.startsWith("{") && data.endsWith("}")) {
			data = "[" + data + "]";
		}
		
		JSONArray result = null;
		//Log.d("JSONHandler.getJSON() from " + url, data);
		
	    try {
	    	result = new JSONArray(data);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	statusCode = StatusType.CONNECTION_RESPOND_NOT_JSON;
	    }
	    
	    
	    return new ConnectionStatus<JSONArray>(result, statusCode);
	}
		
	private static ConnectionStatus<String> doGet(String url) {
		String result = null;	
		
		DefaultHttpClient httpClient = new DefaultHttpClient();
		httpClient.setCookieStore(cookieStore);
		
		HttpGet httpGet = new HttpGet(url);
		
		
		
		HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), TIMEOUT);
		
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(httpResponse == null) {
			// ** Log.d(HttpJSONHandler.class.toString(), "Connection timeout");
			return new ConnectionStatus<String>(null, StatusType.CONNECTION_TIMEOUT);
		}
		
		StatusLine statusLine = httpResponse.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		if (statusCode == 200) {
			try {
				result = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		} else {
			// ** Log.d(HttpJSONHandler.class.toString(), "statusCode: " + statusCode);
		}
		
		return new ConnectionStatus<String>(result, statusCode);
	} 
	
	/*
	public static ConnectionStatus<JSONArray> postJSON(String url, JSONObject data) {
		return postJSON(url, data.toString());
	}
	
	public static ConnectionStatus<JSONArray> postJSON(String url, String data)  {
		ConnectionStatus<JSONArray> result = null;

	    HttpPost httPost = new HttpPost(url);
	    
	    HttpConnectionParams.setConnectionTimeout(httpClient.getParams(), 3000);
		HttpConnectionParams.setSoTimeout(httpClient.getParams(), 5000);
	    
	    StringEntity stringEntity = null;
	    httPost.setEntity(stringEntity);
	    HttpResponse httpResponse = null;
	    String retSrc = null;
	    
		try {
			stringEntity = new StringEntity(data, HTTP.UTF_8);
			httpResponse = httpClient.execute(httPost);
			
			if(httpResponse == null) {
				// ** Log.d(ConnectivityJSONHandler.class.toString(), "Connection timeout");
				return new ConnectionStatus<JSONArray>(null, 
						ConnectionStatus.CONNECTION_TIMEOUT);
			}
			
			StatusLine statusLine = httpResponse.getStatusLine();
			int statusCode = statusLine.getStatusCode();
			
			if(statusCode != 200) {
				// ** Log.d(ConnectivityJSONHandler.class.toString(), "doGet(url) failed");
				return new ConnectionStatus<JSONArray>(null, statusCode);
			}
			
			retSrc = EntityUtils.toString(httpResponse.getEntity(), HTTP.UTF_8);
			
			if(retSrc.startsWith("{") && retSrc.endsWith("}")) {
				retSrc = "[" + retSrc + "]";
			}
			
			result = new ConnectionStatus<JSONArray>(new JSONArray(retSrc), 
					ConnectionStatus.CONNECTION_SUCCESS);
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ClientProtocolException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	    
	    return result;
	}
	*/
}
