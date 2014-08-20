package com.count2v.coolist.contact;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.core.BaseActivity;

public class ContactActivity extends BaseActivity {
	private ViewGroup mainLayout;
	//private TextView phoneNumTextView;
	private TextView emailAddressTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setView();
		setListener();
	}
	
	private void setView() {
		mainLayout = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.activity_contact, null);
		
		setContentView(mainLayout);
		
		//phoneNumTextView = (TextView) findViewById(R.id.contactTextView03);
		//emailAddressTextView = (TextView) findViewById(R.id.contactTextView05);
		
		emailAddressTextView = (TextView) findViewById(R.id.contactTextView03);
		
//		SpannableString phoneNum = new SpannableString(phoneNumTextView.getText());
//		phoneNum.setSpan(new UnderlineSpan(), 0, phoneNum.length(), 0);
//		phoneNumTextView.setText(phoneNum);
		
		SpannableString emailAddress = new SpannableString(emailAddressTextView.getText());
		emailAddress.setSpan(new UnderlineSpan(), 0, emailAddress.length(), 0);
		emailAddressTextView.setText(emailAddress);

	}
	
	private void setListener() {
//		phoneNumTextView.setOnClickListener(new View.OnClickListener() {
//			
//			@Override
//			public void onClick(View v) {
//				String phoneNum = phoneNumTextView.getText().toString();
//				Intent intent = new Intent(Intent.ACTION_CALL);
//				intent.setData(Uri.parse("tel:" + phoneNum));
//				
//				try {
//					startActivity(intent);
//				} catch(Exception e) {
//					Toast.makeText(ContactActivity.this, "無法撥打電話", Toast.LENGTH_LONG).show();
//					e.printStackTrace();
//				}
//			}
//		});
		
		emailAddressTextView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String emailAddress = emailAddressTextView.getText().toString();
				Intent i = new Intent(Intent.ACTION_SEND);
				i.setType("message/rfc822");
				i.putExtra(Intent.EXTRA_EMAIL  , new String[]{emailAddress});
				try {
				    startActivity(Intent.createChooser(i, "請選擇寄送電子郵件的應用程式"));
				} catch (android.content.ActivityNotFoundException ex) {
				    Toast.makeText(ContactActivity.this, "找不到收發電子郵件的應用程式", Toast.LENGTH_LONG).show();
				}
			}
		});
		
	}

}
