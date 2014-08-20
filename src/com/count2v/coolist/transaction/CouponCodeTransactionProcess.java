package com.count2v.coolist.transaction;

import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.client.data.TransactionResultData;

public class CouponCodeTransactionProcess extends TransactionProcess {
	private static final String EXTRA_CONTENT_COUPON_CODE = "number";
	
	private ClientRequest<TransactionResultData> transactionReqeust;
	
	private TransactionActivity transactionActivity;
	private ShopData shopData;
	private ShopItemData shopItemData;
	
	private ViewGroup transactionTextContentView;
	private TextView contentTextView;

	public CouponCodeTransactionProcess(
			TransactionActivity transactionActivity, ShopData shopData,
			ShopItemData shopItemData) {
		super(transactionActivity, shopData, shopItemData);
		
		this.transactionActivity = transactionActivity;
		this.shopData = shopData;
		this.shopItemData = shopItemData;
		
		initialize();
		setView();
	}
	
	private void initialize() {
		transactionReqeust = ServerCommunicator.requestSetConsumptionRecord(shopItemData.getShopId(), 
				shopItemData.getShopItemId(), transactionListener);
	}
	
	private void setView() {
		transactionTextContentView = (ViewGroup) LayoutInflater.from(transactionActivity).
				inflate(R.layout.transaction_text_content, null, false);
		contentTextView = (TextView) transactionTextContentView.findViewById(
				R.id.transactionTextContentTextView01);
		
		transactionTextContentView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
	}
	
	private void showConfirmDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(transactionActivity);
		dialog.setTitle("�Щ��");
		dialog.setMessage("�ڭ̱N�B�~�H�e�ӫ~�Ǹ��ܱz�ҨϥΪ�Facebook�H�c�C\n");
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
	
	private ServerCommunicator.Callback<TransactionResultData> transactionListener = 
			new ServerCommunicator.Callback<TransactionResultData>() {

		@Override
		public void onComplete(TransactionResultData element, int statusCode) {
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				if(element != null) {
					setTransactionSuccess(element.getSerialCode(), getTime());
					
					Map<String, String> extraInfoMap = element.getExtraInfoMap();
					if(extraInfoMap != null && extraInfoMap.containsKey(EXTRA_CONTENT_COUPON_CODE)) {
						contentTextView.setText("�Ǹ��G" + extraInfoMap.get(EXTRA_CONTENT_COUPON_CODE));
					} else {
						contentTextView.setText("�ڭ̱N�H�e�Ǹ��ܱzFacebook�ҨϥΪ��H�c");
					}
					
				} else {
					contentTextView.setText("�ڭ̱N�H�e�Ǹ��ܱzFacebook�ҨϥΪ��H�c");
					setTransactionUnconfirm(getTime());
					ServerCommunicator.postErrorMessage(transactionReqeust, "Received null data", 
							StatusType.RECEIVED_NULL_DATA);
				}
				
				setContentDetailView(transactionTextContentView);
				
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
