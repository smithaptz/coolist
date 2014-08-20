package com.count2v.coolist.tuition;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.count2v.coolist.R;
import com.count2v.coolist.core.BaseActivity;

public class TuitionActivity extends BaseActivity {
	private static final int[] drawableResourceIds = new int[] {
		R.drawable.tuition_1, R.drawable.tuition_2, R.drawable.tuition_3,
		R.drawable.tuition_4, R.drawable.tuition_5, R.drawable.tuition_6
	};
	
	private ViewGroup mainLayout;
	
	private ArrayList<Drawable> drawableList = new ArrayList<Drawable>();
	private ArrayList<View> viewList = new ArrayList<View>();
	
	private ViewPager viewPager;
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// recycle:
		for(Drawable drawable : drawableList) {
			drawable.setCallback(null);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initialize();
		setView();
		setListener();
	}
	
	@Override
	public void finish() {
		Toast.makeText(TuitionActivity.this, "快來闖關換商品吧！", Toast.LENGTH_LONG).show();
		super.finish();
	}
	
	private void initialize() {
		enableConnectionAllTheTime(false);
		
		for(int id : drawableResourceIds) {
			ImageView imageView = new ImageView(this);
			imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			imageView.setImageDrawable(this.getResources().getDrawable(id));
			viewList.add(imageView);
		}
		
		ViewGroup baseLayout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_base, null);
		TextView userNameFieldText = (TextView) baseLayout.findViewById(R.id.baseActivityTextView01);
		TextView userPointsFieldText = (TextView) baseLayout.findViewById(R.id.baseActivityTextView02);
		userNameFieldText.setText("用戶名稱 " + getUserName());
		userPointsFieldText.setText("剩餘點數 " + this.getUserPoints() + " 點");
		
		baseLayout.addView(LayoutInflater.from(this).inflate(R.layout.activity_main, null), new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		viewList.add(baseLayout);
	}
	
	private void setView() {
		mainLayout = (ViewGroup) LayoutInflater.from(this).
				inflate(R.layout.activity_tuition, null);
		mainLayout.setLongClickable(true);
		//setMainLayoutBackground(drawableList.get(0));
		
		setContentView(mainLayout);
		setUserInfoBarVisibility(View.GONE);

		
		viewPager = (ViewPager) findViewById(R.id.tuitionViewPager);
		viewPager.setAdapter(pagerAdapter);
	}
	
	private void setListener() {		
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int state) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				System.out.println("position: " + position + ", positionOffset: " + positionOffset + 
						", positionOffsetPixels: " + positionOffsetPixels);
				
				//finish();
				//overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
			}

			@Override
			public void onPageSelected(int position) {
				// TODO Auto-generated method stub
				if(position == viewList.size() - 1) {
					finish();
					overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
				}
			}
			
		});
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_BACK) {
	    	AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    	builder.setMessage("是否離開使用教學?");
			builder.setPositiveButton("確定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
				}
			});
			builder.show();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	private PagerAdapter pagerAdapter = new PagerAdapter() {
		
		@Override
		public void destroyItem(ViewGroup viewGroup, int position, Object object) {
			viewGroup.removeView(viewList.get(position));
		}


		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return viewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			// TODO Auto-generated method stub
			return arg0 == arg1;
		}
		
		@Override  
		public Object instantiateItem(ViewGroup viewGroup, int position) {
			System.out.println("position: " + position);
			viewGroup.addView(viewList.get(position));
			return viewList.get(position);
		}
	
		
	};
}
