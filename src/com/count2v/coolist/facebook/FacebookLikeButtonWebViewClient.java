package com.count2v.coolist.facebook;

import com.count2v.coolist.R;
import com.count2v.coolist.core.BaseActivity;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

@SuppressLint("SetJavaScriptEnabled")
public class FacebookLikeButtonWebViewClient extends WebViewClient {
	public static final String[] ALLOWED_URL_CONTENTS = new String[] {
		"facebook.com/login.php",
		"facebook.com/dialog/",
		"facebook.com/plugins/"
	};
	
	private BaseActivity activty;
	private ViewGroup viewGroup; //main layout
	private WebView webView;
	private WebView newPageWebView;
	private View currentView;
	private String indexUrl;
	
	private static WebView setWebViewProperties(WebView webView) {
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
		
		return webView;
	}
	
	public static WebView setupFacebookWebView(BaseActivity activty, ViewGroup viewGroup, WebView webView) {
		setWebViewProperties(webView);
		webView.setWebViewClient(new FacebookLikeButtonWebViewClient(activty, viewGroup, webView));
		return webView;
	}
	
	private FacebookLikeButtonWebViewClient(BaseActivity activty, ViewGroup viewGroup, WebView webView) {
		this.activty = activty;
		this.viewGroup = viewGroup;
		this.webView = webView;
		currentView = viewGroup;
	}
	
	private BaseActivity getBaseActivity() {
		return activty;
	}
	
	private ViewGroup getMainView() {
		return viewGroup;
	}
	
	private WebView getDefaultWebView() {
		return webView;
	}
	
	private void setIndexUrl(String url) {
		indexUrl = url;
	}
	
	private String getIndexUrl() {
		return indexUrl;
	}
	
	private void setView(View view) {
		getBaseActivity().setContentView(view);
		currentView = view;
	}
	
	private View getCurrentView() {
		return currentView;
	}
	
	private void setBackKeyListener(final WebView webView) {
		webView.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_BACK) {
					backToMainView();
					
					Toast.makeText(getBaseActivity(), "back key", Toast.LENGTH_SHORT).show();
					return true;
				}
				
				return false;
			}
		});
	}
	
	public boolean isOnMainView() {
		return getCurrentView() == getMainView();
	}
	
	public void backToMainView() {
		setView(getMainView());
		getDefaultWebView().loadUrl(getIndexUrl());
		
		if(newPageWebView != null) {
			newPageWebView.destroy();
		}
		newPageWebView = null;
	}
	
	
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		
		if(url.contains("facebook.com/login.php")) {
			Log.d(this.getClass().toString(), "Login");
			if(view == getDefaultWebView()) {
				newPageWebView = (WebView) LayoutInflater.from(getBaseActivity()).inflate(R.layout.webview_layout, null);
				setWebViewProperties(newPageWebView);
				setBackKeyListener(newPageWebView);
				newPageWebView.setWebViewClient(this);
				newPageWebView.loadUrl(url);
				setView(newPageWebView);
				return true;
				
			}
		} 
		
		return false;
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {  
		if(getIndexUrl() == null) {
			setIndexUrl(url);
		}
		
		for(String allowedUrlContent : ALLOWED_URL_CONTENTS) {
			if(url.contains(allowedUrlContent)) {
				return;
			}
		}
		
		view.stopLoading();
	}
	
	
	
	@Override
	public void onPageFinished(WebView view, String url) {
		Log.d(this.getClass().toString(), url);
		
		
		if(url.contains("facebook.com/plugins/close_popup.php")) {
			Log.d(this.getClass().toString(), "contains");
			backToMainView();
		}
	}
	
	
	@Override
	public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
		Log.d(this.getClass().toString(), "errorCode: " + errorCode);
	}
	
	public static String getFacebookLikeButtonUrl(String pageId) {
		return "http://www.facebook.com/plugins/like.php?href=" +
				"https%3A%2F%2Fwww.facebook.com%2F" + pageId + 
				"&width=450&height=35&colorscheme=light" +
				"&layout=standard&action=like&show_faces=false" +
				"&send=false&appId=142733639266956";
	}
	
}
