package com.count2v.coolist.facebook;



import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.count2v.coolist.R;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.facebook.widget.UserSettingsFragment;


public class LoginFacebook extends FragmentActivity {	
	public static final String NEW_USER = "newUser";
	
	private static boolean newUser = false;
	
    private UserSettingsFragment userSettingsFragment;
    private boolean askReadPermissions = true;
    //private boolean askPublishPermissions = true;
    
    
    

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_facebook_login);

        FragmentManager fragmentManager = getSupportFragmentManager();
        userSettingsFragment = (UserSettingsFragment) fragmentManager.findFragmentById(R.id.login_fragment);
        userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                Log.d("LoginUsingLoginFragmentActivity", String.format("New session state: %s", state.toString()));
                /*
                if (session.isOpened()) {
                	if(!FacebookPermissions.hasReadPermissions(session)) {
                		if(!askReadPermissions) {
                			askReadPermissions = true;
                			session.close();
                			return;
                		}
                		
                		askReadPermissions = false; 
                		FacebookPermissions.askUserReadPermissions(LoginFacebook.this, session);
                	} else if(!FacebookPermissions.hasPublishPermissions(session)) {
                		if(!askPublishPermissions) {
                			askPublishPermissions = true;
                			session.close();
                			return;
                		}
                		
                		askPublishPermissions = false;
                		FacebookPermissions.askUserPublishPermissions(LoginFacebook.this, session);
                	} else {
                		//getUser();
                		LoginFacebook.this.finish();
                	}
                }
                */
                
                if (session.isOpened()) {
                	if(!FacebookPermissions.hasReadPermissions(session)) {
                		if(!askReadPermissions) {
                			askReadPermissions = true;
                			session.close();
                			return;
                		}
                		newUser = true;
                		askReadPermissions = false; 
                		FacebookPermissions.askUserReadPermissions(LoginFacebook.this, session);
                	} else {
                		if(newUser) {
                			Intent intent = new Intent();
                			intent.putExtra(NEW_USER, true);
                			LoginFacebook.this.setResult(RESULT_OK, intent);
                		}
                		
                		LoginFacebook.this.finish();
                	}
                }
            }
        });
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        userSettingsFragment.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    public static void getUser() {
    	Request request = Request.newMeRequest(Session.getActiveSession(), new Request.GraphUserCallback() {
			
			@Override
			public void onCompleted(GraphUser user, Response response) {
				if(user != null) {
					Log.d("LoginUsingLoginFragmentActivity", "user.getId(): " + user.getId() + "\n user.getUsername(): " + 
							user.getUsername() + "\n user.getName(): " + user.getName() + "\n user.getBirthday(): " + 
							user.getBirthday() +"\n user.getLink(): " + user.getLink() + "\n gender: " + user.getProperty("gender") + 
							"\n education: " + user.getProperty("education") + "\n email: " + user.getProperty("email") );
					
				}
				
			}
		});
    	request.executeAsync();
    }
    
}
