package com.count2v.coolist.transaction;

import java.util.HashMap;
import java.util.Map;

import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.client.data.TransactionResultData;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class CouponImageTransactionProcess extends TransactionProcess {
	private static final String EXTRA_CONTENT_LINK = "link";
	
	private ClientRequest<TransactionResultData> transactionReqeust;
	
	private TransactionActivity transactionActivity;
	private ShopData shopData;
	private ShopItemData shopItemData;
	
	private WebView contentWebView;

	public CouponImageTransactionProcess(
			TransactionActivity transactionActivity, ShopData shopData,
			ShopItemData shopItemData) {
		super(transactionActivity, shopData, shopItemData);
		
		this.transactionActivity = transactionActivity;
		this.shopData = shopData;
		this.shopItemData = shopItemData;
		
		initialize();
	}
	
	private void initialize() {
		transactionReqeust = ServerCommunicator.requestSetConsumptionRecord(shopItemData.getShopId(), 
				shopItemData.getShopItemId(), transactionListener);
		
		contentWebView = new WebView(transactionActivity);
		contentWebView.setWebViewClient(new WebViewClient());
		contentWebView.getSettings().setJavaScriptEnabled(true);
		contentWebView.getSettings().setAppCacheEnabled(true);
		contentWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	}
	
	private void showConfirmDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(transactionActivity);
		dialog.setTitle("注意");
		dialog.setMessage("兌換明細的畫面必須交由商家確認，一旦兌換完畢後將會立刻扣點。" +
				"\n\n商家：" + shopData.getName() + "\n商品：" + shopItemData.getName() + 
				"\n點數：" + shopItemData.getPoints());
		dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				transactionProcess();
			}
		});
		dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				transactionActivity.finish();
				Toast.makeText(transactionActivity, "兌換已取消", Toast.LENGTH_LONG).show();
			}
		});
		dialog.show();
	}

	@Override
	public void startTransaction() {
		showConfirmDialog();
	}

	private void transactionProcess() {
		
		if(!isOnService()) {
			setTransactionFail("暫停服務");
			String errorMsg = "暫停服務, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.UNKNOWN_TRANSACTION_TYPE);
			
			return;
		}
		
		if(!isUserAfford()) {
			setTransactionFail("點數不足！");
			String errorMsg = "使用者點數不足, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + 
					shopItemData.getType() + ", points: " + shopItemData.getPoints() + ", userPoints: " + 
					transactionActivity.getUserPoints();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.USER_CANNOT_AFFORD_SHOP_ITEM);
			
			return;
		}

		executeTransactionRequest();
	}
	
	private void executeTransactionRequest() {
		// 顯示等待訊息:
		showWaitingDialog();
		// 傳送兌換訊息至伺服器:
		ServerCommunicator.execute(transactionReqeust);
	}
	
	@Override
	public String getSuccessConfirmMessage() {
		return "是否離開兌換明細？確認任後此兌換券將銷毀。";
	}
	
	private ServerCommunicator.Callback<TransactionResultData> transactionListener = 
			new ServerCommunicator.Callback<TransactionResultData>() {

		@Override
		public void onComplete(TransactionResultData element, int statusCode) {
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				if(element != null) {
					setTransactionSuccess(element.getSerialCode(), getTime());
					
					Map<String, String> extraInfoMap = element.getExtraInfoMap();
					if(extraInfoMap != null && extraInfoMap.containsKey(EXTRA_CONTENT_LINK)) {
						contentWebView.loadUrl(extraInfoMap.get(EXTRA_CONTENT_LINK));
						setContentDetailView(contentWebView);
					}
					
				} else {
					setTransactionUnconfirm(getTime());
					ServerCommunicator.postErrorMessage(transactionReqeust, "Received null data", 
							StatusType.RECEIVED_NULL_DATA);
				}
				
			} else if(statusCode == StatusType.RECEIVED_NULL_DATA) {
				setTransactionUnconfirm(getTime());
				ServerCommunicator.postErrorMessage(transactionReqeust, "Received null data", 
						StatusType.RECEIVED_NULL_DATA);
			} else {
				String errorMsg = null;
				if(statusCode == StatusType.USER_POINTS_NOT_ENOUGH) {
					setTransactionFail("點數不足");
					errorMsg = "使用者點數不足";
				} else if(statusCode == StatusType.SHOP_POINTS_NOT_ENOUGH) {
					setTransactionFail("暫停服務");
					errorMsg = "店家點數不足";
				} else if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					setTransactionFail("連線逾時，請重新兌換");
					errorMsg = "連線逾時";
				} else if(statusCode == StatusType.SHOP_ITEM_UNAVAILABLE)  {
					setTransactionFail("暫停服務");
					errorMsg = "兌換項目數量不足";
				} else if(statusCode == StatusType.COUPON_CODE_UNAVAILABLE) {
					setTransactionFail("暫停服務");
					errorMsg = "兌換條碼數量不足";
				} else {
					setTransactionFail("錯誤代碼：" + statusCode);
					errorMsg = "兌換失敗";
				}
				
				ServerCommunicator.postErrorMessage(transactionReqeust, errorMsg, statusCode);
			}
			dismissWaitingDialog();
		}
	};
	

}
