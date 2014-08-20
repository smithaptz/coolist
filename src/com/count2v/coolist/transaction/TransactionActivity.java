package com.count2v.coolist.transaction;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.check.CheckStatusType;
import com.count2v.coolist.client.ClientRequest;
import com.count2v.coolist.client.ServerCommunicator;
import com.count2v.coolist.client.StatusType;
import com.count2v.coolist.client.data.ShopData;
import com.count2v.coolist.client.data.ShopItemData;
import com.count2v.coolist.contact.ContactActivity;
import com.count2v.coolist.core.BaseActivity;
import com.count2v.coolist.core.RequestUIHandler;
import com.count2v.coolist.db.DBAccessor;

public class TransactionActivity extends BaseActivity {

	private long shopItemId;
	private ShopData shopData;
	private ShopItemData shopItemData;
	
	private TransactionProcess transactionProcess;
	
	private String serialNum;
	private String transactionTime;
	
	private DBAccessor dbAccessor;
	
	private ViewGroup transactionProcessView;
	private ViewGroup transactionSuccessView;
	private ViewGroup transactionFailView;
	private ViewGroup transactionContentView;
	
	private TextView successSerialNumTextView;
	private TextView successShopNameTextView;
	private TextView successShopItemTextView;
	private TextView successTransactionTimeTextView;
	private TextView successUserNameTextView;
	private TextView successTransactionPointsTextView;
	private TextView successUserPointsTextView;
	
	private TextView successContentTextView;
	private TextView successConfirmTextView;
	
	private TextView failTextView;
	
	private TextView failContactTextView;
	private TextView failConfirmTextView;
	
	private TextView contentShopItemTextView;
	private TextView contentSerialNumTextView;
	private TextView contentConfirmTextView;
	
	private ViewGroup contentDetailView;
	
	private ProgressDialog waitingDialog;
	
	private AlertDialog authFailDialog;
	
	
	
	private boolean hasInitialized = false;
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putLong("shopItemId", -1);
	}
	
	
	
	@Override
	protected void onPause() {
		super.onPause();
		
		if(authFailDialog != null && authFailDialog.isShowing()) {
			authFailDialog.dismiss();
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		shopItemId = getIntent().getLongExtra("shopItemId", -1);
		
		if(shopItemId < 0) {
			Log.d(this.getClass().toString(), "cannot find shopItemId");
			this.finish();
		}
		
		initialize();
		setView();
		setListener();
	}
	
	
	private void initialize() {
		setCheckStatus(FULL_CHECK_STATUS_LIST);
		dbAccessor = DBAccessor.instance(this);
		shopItemData = dbAccessor.getShopItemData(shopItemId);
		shopData = dbAccessor.getShopData(shopItemData.getShopId());
	}
	
	private void setView() {
		transactionProcessView = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.transaction_process, null);
		transactionSuccessView = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.transaction_success, null);
		transactionFailView = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.transaction_fail, null);
		transactionContentView = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.transaction_content, null);
		
		setTransactionSuccessView();
		setTransactionFailView();
		setTransactionContentView();
		
		setWaitingDialog();
			
		setContentView(transactionProcessView);
		
		
	}
	
	private void setWaitingDialog() {
		waitingDialog = new ProgressDialog(this);
		waitingDialog.setCancelable(false);
		waitingDialog.setMessage("正在與伺服器連線中，請稍待片刻");
	}
	
	private void setTransactionSuccessView() {
		// set transactionSuccessView
		
		successSerialNumTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView02);
		successShopNameTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView03);
		successShopItemTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView04);
		successUserNameTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView05);
		successTransactionTimeTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView06);
		
		successTransactionPointsTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView07);
		successUserPointsTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView08);
		
		successContentTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView09);
		successConfirmTextView = (TextView) transactionSuccessView.findViewById(R.id.transactionSuccessTextView10);
		
		successShopNameTextView.setText("商家：" + shopData.getName());
		successShopItemTextView.setText("商品：" + shopItemData.getName());
		successUserNameTextView.setText("用戶：" + getUserName());
		successTransactionPointsTextView.setText("扣除點數：" + String.valueOf(shopItemData.getPoints()));
	}
	
	// set transactionFailView
	private void setTransactionFailView() {
		failTextView = (TextView) transactionFailView.findViewById(R.id.transactionFailTextView02);
		
		failContactTextView = (TextView) transactionFailView.findViewById(R.id.transactionFailTextView03);
		failConfirmTextView = (TextView) transactionFailView.findViewById(R.id.transactionFailTextView04);
		
	}
	
	// set transactionContentView
	private void setTransactionContentView() {
		contentShopItemTextView = (TextView) transactionContentView.findViewById(R.id.transactionContentTextView01);
		contentSerialNumTextView = (TextView) transactionContentView.findViewById(R.id.transactionContentTextView02);
		contentConfirmTextView = (TextView) transactionContentView.findViewById(R.id.transactionContentTextView03);
		
		contentDetailView = (ViewGroup) transactionContentView.findViewById(R.id.transactionContentLinearLayout01);
		
		contentShopItemTextView.setText(shopItemData.getName());
	}
	
	private void setListener() {
		setTransactionSuccessViewListener();
		setTransactionFailViewListener();
		setTransactionContentViewListener();
	}
	
	private void setTransactionSuccessViewListener() {
		successConfirmTextView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String confirmMsg = transactionProcess.getSuccessConfirmMessage();
				AlertDialog.Builder dialog = new AlertDialog.Builder(TransactionActivity.this);
				dialog.setTitle("請確認");
				dialog.setMessage(confirmMsg);
				dialog.setPositiveButton("確定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						TransactionActivity.this.finish();
					}
				});
				dialog.setNeutralButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
					}
				});
				dialog.show();
			}
		});
		
		// 進到兌換內容的頁面:
		successContentTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(transactionContentView);
			}
		});
	}
	
	private void setTransactionFailViewListener() {
		failConfirmTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TransactionActivity.this.finish();
				Toast.makeText(TransactionActivity.this, "兌換已取消", Toast.LENGTH_LONG).show();
			}
		});
		
		failContactTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(TransactionActivity.this, ContactActivity.class);
				startActivity(intent);
			}
		});
		
	}
	
	private void setTransactionContentViewListener() {
		// 回到兌換明細:
		contentConfirmTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setContentView(transactionSuccessView);
			}
		});
	}
	
	void showWaitingDialog() {
		if(!waitingDialog.isShowing()) {
			waitingDialog.show();
		}
	}
	
	void dismissWaitingDialog() {
		if(waitingDialog.isShowing()) {
			waitingDialog.dismiss();
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        return false;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(transactionProcess == null) {
			return;
		}
		
		if(transactionProcess.waitForAnotherActivityResult()) {
			transactionProcess.onActivityResult(requestCode, resultCode, intent);
		}
	}
	
	public void transactionFail(String result) {
		failTextView.setText(result);
		setContentView(transactionFailView);
	}
	
	public void transactionSuccess(String serialNum, String transactionTime) {
		this.serialNum = serialNum;
		this.transactionTime = transactionTime;
		
		setSerialNum(serialNum);
		setTransactionTime(transactionTime);
		setRemainPoints();
		
		setContentView(transactionSuccessView);
	}
	
	private void setSerialNum(String serialNum) {
		successSerialNumTextView.setText(serialNum);
		contentSerialNumTextView.setText(serialNum);
	}
	
	private void setTransactionTime(String time) {
		successTransactionTimeTextView.setText("時間：" + time);
	}
	
	private void setRemainPoints() {
		int remainPoints = getUserPoints() - shopItemData.getPoints();
		setUserPoints(remainPoints);
		successUserPointsTextView.setText("帳戶餘額：" + String.valueOf(remainPoints));
	}
	
	void setContentDetailView(View view) {
		contentDetailView.addView(view);
	}
	
	@Override
	protected void onCheckStatusComplete(boolean result, CheckStatusType lastCheckStatus) {
		super.onCheckStatusComplete(result, lastCheckStatus);
		
		
		if(hasInitialized) {
			return;
		}
		
		if(result) {
			hasInitialized = true;
			setCheckStatus(DEFAULT_CHECK_STATUS_LIST);
			
			transactionProcess = TransactionProcessFactory.instanceProcess(this, shopData, shopItemData);
			transactionProcess.startTransaction();
			
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("兌換取消");
			builder.setMessage("認證失敗！");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					TransactionActivity.this.finish();
				}
			});
			builder.setNeutralButton("聯絡我們", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					Intent intent = new Intent(TransactionActivity.this, ContactActivity.class);
					startActivity(intent);
					TransactionActivity.this.finish();
				}
			});
			authFailDialog = builder.create();
			authFailDialog.show();
			
		}
		
	}
	
	

}
