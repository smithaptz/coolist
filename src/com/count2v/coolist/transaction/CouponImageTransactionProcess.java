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
		dialog.setTitle("�`�N");
		dialog.setMessage("�I�����Ӫ��e��������ѰӮa�T�{�A�@���I��������N�|�ߨ覩�I�C" +
				"\n\n�Ӯa�G" + shopData.getName() + "\n�ӫ~�G" + shopItemData.getName() + 
				"\n�I�ơG" + shopItemData.getPoints());
		dialog.setPositiveButton("�T�w", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				transactionProcess();
			}
		});
		dialog.setNeutralButton("����", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				transactionActivity.finish();
				Toast.makeText(transactionActivity, "�I���w����", Toast.LENGTH_LONG).show();
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
			setTransactionFail("�Ȱ��A��");
			String errorMsg = "�Ȱ��A��, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.UNKNOWN_TRANSACTION_TYPE);
			
			return;
		}
		
		if(!isUserAfford()) {
			setTransactionFail("�I�Ƥ����I");
			String errorMsg = "�ϥΪ��I�Ƥ���, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + 
					shopItemData.getType() + ", points: " + shopItemData.getPoints() + ", userPoints: " + 
					transactionActivity.getUserPoints();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.USER_CANNOT_AFFORD_SHOP_ITEM);
			
			return;
		}

		executeTransactionRequest();
	}
	
	private void executeTransactionRequest() {
		// ��ܵ��ݰT��:
		showWaitingDialog();
		// �ǰe�I���T���ܦ��A��:
		ServerCommunicator.execute(transactionReqeust);
	}
	
	@Override
	public String getSuccessConfirmMessage() {
		return "�O�_���}�I�����ӡH�T�{���ᦹ�I����N�P���C";
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
					setTransactionFail("�I�Ƥ���");
					errorMsg = "�ϥΪ��I�Ƥ���";
				} else if(statusCode == StatusType.SHOP_POINTS_NOT_ENOUGH) {
					setTransactionFail("�Ȱ��A��");
					errorMsg = "���a�I�Ƥ���";
				} else if(statusCode == StatusType.CONNECTION_TIMEOUT) {
					setTransactionFail("�s�u�O�ɡA�Э��s�I��");
					errorMsg = "�s�u�O��";
				} else if(statusCode == StatusType.SHOP_ITEM_UNAVAILABLE)  {
					setTransactionFail("�Ȱ��A��");
					errorMsg = "�I�����ؼƶq����";
				} else if(statusCode == StatusType.COUPON_CODE_UNAVAILABLE) {
					setTransactionFail("�Ȱ��A��");
					errorMsg = "�I�����X�ƶq����";
				} else {
					setTransactionFail("���~�N�X�G" + statusCode);
					errorMsg = "�I������";
				}
				
				ServerCommunicator.postErrorMessage(transactionReqeust, errorMsg, statusCode);
			}
			dismissWaitingDialog();
		}
	};
	

}
