package com.count2v.coolist.facebook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.facebook.Session;


import android.app.Activity;
import android.util.Log;


public class FacebookPermissions {
	
	public static final String EMAIL = "email";
	public static final String USER_BIRTHDAY = "user_birthday";
	public static final String USER_INTERESTS = "user_interests";
	//public static final String USER_WORK_HISTORY = "user_work_history";
	//public static final String USER_EDUCATION_HISTORY = "user_education_history";
	public static final String USER_LIKES = "user_likes";
	public static final String READ_STREAM = "read_stream";
	public static final String PUBLISH_ACTIONS = "publish_actions";
	
	
	public static boolean hasReadPermissions(Session session) {
		boolean result = true;
		
		List<String> permissions = Arrays.asList(EMAIL, USER_BIRTHDAY, USER_INTERESTS, 
				USER_LIKES, READ_STREAM);
		
    	
    	ArrayList<String> askPermissions = new ArrayList<String>(); 
    	
    	
    	for(String permission : permissions) {
    		if(!session.getPermissions().contains(permission)) {
    			askPermissions.add(permission);
    			result = false;
    		}
    	}
    	
    	return result;
	}
	
	public static boolean hasPublishPermissions(Session session) {
		boolean result = true;
		
		List<String> permissions = Arrays.asList(PUBLISH_ACTIONS);
		
    	
    	ArrayList<String> askPermissions = new ArrayList<String>(); 
    	
    	
    	for(String permission : permissions) {
    		if(!session.getPermissions().contains(permission)) {
    			askPermissions.add(permission);
    			result = false;
    		}
    	}
    	
    	
    	return result;
	}
	
	public static boolean askUserReadPermissions(Activity activity, Session session) {
		boolean result = false;
		
		List<String> permissions = Arrays.asList(EMAIL, USER_BIRTHDAY, USER_INTERESTS, 
				USER_LIKES, READ_STREAM);
		
    	
    	ArrayList<String> askPermissions = new ArrayList<String>(); 
    	
    	Log.d(activity.getClass().toString(), "permissions: " + session.getPermissions());
    	
    	for(String permission : permissions) {
    		if(!session.getPermissions().contains(permission)) {
    			askPermissions.add(permission);
    			result = true;
    		}
    	}
    	
    	if(result) {
    		Log.d(activity.getClass().toString(), "ask user permissions: " + askPermissions);
    		session.requestNewReadPermissions(new Session.NewPermissionsRequest(activity, askPermissions));
    	}
    	
    	return result;
	}
	
	public static boolean askUserPublishPermissions(Activity activity, Session session) {
		boolean result = false;
		
		List<String> permissions = Arrays.asList(PUBLISH_ACTIONS);
		
    	
    	ArrayList<String> askPermissions = new ArrayList<String>(); 
    	
    	Log.d(activity.getClass().toString(), "permission: " + session.getPermissions());
    	
    	for(String permission : permissions) {
    		if(!session.getPermissions().contains(permission)) {
    			askPermissions.add(permission);
    			result = true;
    		}
    	}
    	
    	if(result) {
    		Log.d(activity.getClass().toString(), "ask user permissions: " + askPermissions);
    		session.requestNewPublishPermissions(new Session.NewPermissionsRequest(activity, askPermissions));
    	}
    	
    	
    	return result;
	}
}
