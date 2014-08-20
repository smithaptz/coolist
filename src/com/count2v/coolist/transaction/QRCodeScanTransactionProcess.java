package com.count2v.coolist.transaction;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.client.data.TransactionResultData;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents.Scan;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

public class QRCodeScanTransactionProcess extends TransactionProcess {
	private static final int REQUEST_CODE = 1;
	
	private ClientRequest<TransactionResultData> transactionReqeust;
	
	private TransactionActivity transactionActivity;
	private ShopData shopData;
	private ShopItemData shopItemData;
	
	private String validString;
	
	private boolean MD5EncodeError = false;

	public QRCodeScanTransactionProcess(
			TransactionActivity transactionActivity, ShopData shopData, ShopItemData shopItemData) {
		super(transactionActivity, shopData, shopItemData);
		
		this.transactionActivity = transactionActivity;
		this.shopData = shopData;
		this.shopItemData = shopItemData;
		
		initialize();
	}
	
	private void initialize() {
		validString = generateMD5String("id=" + shopData.getShopId());
		transactionReqeust = ServerCommunicator.requestSetConsumptionRecord(shopItemData.getShopId(), 
				shopItemData.getShopItemId(), transactionListener);
				
	}
	
	private void showConfirmDialog() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(transactionActivity);
		dialog.setTitle("�`�N");
		dialog.setMessage("���U�T�w��A�б��۱��y���w���G�����X�H�����I���C" + "\n\n" + 
				"ĵ�i�G�I�����Ӫ��e��������ѰӮa�T�{�A�@���I��������N�|�ߨ覩�I�C" +
				"\n\n�Ӯa�G" + shopData.getName() + "\n�ӫ~�G" + shopItemData.getName() + 
				"\n�I�ơG" + shopItemData.getPoints());
		dialog.setPositiveButton("�T�w", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				transactionProcess();
				Toast.makeText(transactionActivity, "�б��y���w���G�����X�H�����I��", Toast.LENGTH_LONG).show();
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
		if(MD5EncodeError) {
			setTransactionFail("���~�N�X: " + StatusType.MD5_ENCODE_ERROR);
			String errorMsg = "MD5�s�X���~, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.MD5_ENCODE_ERROR);
			return;
		}
		
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
		
		scanQRCode();	
		//executeTransactionRequest();
	}
	
	
//	private void scanQRCode() {
//		Log.d(this.getClass().toString(), "start scanning QR code");
//		IntentIntegrator integrator = new IntentIntegrator(transactionActivity);
//		integrator.initiateScan(IntentIntegrator.QR_CODE_TYPES);
//	}
	
	
	
	private void scanQRCode() {
		String packageName = transactionActivity.getPackageName();
		
		Intent intent = new Intent("com.google.zxing.client.android.SCAN"); 
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		intent.setPackage(packageName);
        transactionActivity.startActivityForResult(intent, REQUEST_CODE);
	}
	

	@Override
	public boolean waitForAnotherActivityResult() {
		return true;
	}
	
	private boolean isQRCodeResultValid(String qrCodeResult) {
		return validString.equals(qrCodeResult);
	}
	
	private void executeTransactionRequest() {
		// ��ܵ��ݰT��:
		showWaitingDialog();
		// �ǰe�I���T���ܦ��A��:
		ServerCommunicator.execute(transactionReqeust);
	}

	/*
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

    	if(scanResult != null) {
    		String result = scanResult.getContents();
    		
    		if(result == null) {
    			setTransactionFail("�Э��s���y�G�����X");
    			String errorMsg = "�G�����X���ѥ���, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
    			ServerCommunicator.postErrorMessage(errorMsg, StatusType.QRCODE_DECODE_ERROR);
    			return;
        	}
    		
        	Log.d(this.getClass().toString(), "QR code result: " + result);

    		// �T�{QR code�O�_�M���a�N�X�@�P:
    		if(isQRCodeResultValid(result)) {
    			Log.d(this.getClass().toString(), "QRCode is valid");
    			executeTransactionRequest();
    			
    		} else {
    			Log.d(this.getClass().toString(), "QRCode is not match");
    			setTransactionFail("�G�����X���šA�нT�{�ҿ�Ӯa�O�_���T");
    			String errorMsg = "�G�����X����, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
    			ServerCommunicator.postErrorMessage(errorMsg, StatusType.QRCODE_NOT_MATCH);
    		}
    	}
		
	}
	*/
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode != REQUEST_CODE) {
			return;
		}
		
        if(resultCode == Activity.RESULT_OK) {
            String result = intent.getStringExtra("SCAN_RESULT");
            
            if(isQRCodeResultValid(result)) {
    			Log.d(this.getClass().toString(), "QRCode is valid");
    			executeTransactionRequest();
    		} else {
    			Log.d(this.getClass().toString(), "QRCode is not match");
    			setTransactionFail("�G�����X���šA�нT�{�ҿ�Ӯa�O�_���T");
    			String errorMsg = "�G�����X����, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
    			ServerCommunicator.postErrorMessage(errorMsg, StatusType.QRCODE_NOT_MATCH);
    		}
        } else {
        	setTransactionFail("�Э��s���y�G�����X");
			String errorMsg = "�G�����X���ѥ���, shop_item_id: " + shopItemData.getShopItemId() + ", type: " + shopItemData.getType();
			ServerCommunicator.postErrorMessage(errorMsg, StatusType.QRCODE_DECODE_ERROR);
			return;
        }
	}
	
	
	@SuppressLint("DefaultLocale")
	private String generateMD5String(final String toEncrypt) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("md5");
			digest.update(toEncrypt.getBytes());
		    final byte[] bytes = digest.digest();
		    final StringBuilder sb = new StringBuilder();
		    for (int i = 0; i < bytes.length; i++) {
		        sb.append(String.format("%02X", bytes[i]));
		        }
		        
		    return sb.toString().toLowerCase();
		} catch (NoSuchAlgorithmException e) {
			MD5EncodeError = true;
			e.printStackTrace();
			
			return "";
		}
	       
	}
	
	private ServerCommunicator.Callback<TransactionResultData> transactionListener = 
			new ServerCommunicator.Callback<TransactionResultData>() {

		@Override
		public void onComplete(TransactionResultData element, int statusCode) {
			if(statusCode == StatusType.CONNECTION_SUCCESS) {
				if(element != null) {
					setTransactionSuccess(element.getSerialCode(), getTime());
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
