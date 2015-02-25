package com.lockpattern.app;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Window;



public class WelcomeActivity extends FragmentActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		//�ӳ�����
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				SharedPreferences sp = getSharedPreferences("sp" , Context.MODE_PRIVATE);
				String passwordStr = sp.getString("password", "");
				
				if(TextUtils.isEmpty(passwordStr)){ //������ڻ�û���������룬�Ǿ�����MainActivity����ȥ��������
					startActivity( new Intent(WelcomeActivity.this , MainActivity.class) );
					finish();
				}
				else{ //���֮ǰ�Ѿ����ù����룬�Ϳ�ʼ����������
					getSupportFragmentManager().beginTransaction().replace(android.R.id.content , PasswordFragment.newInstance(PasswordFragment.TYPE_CHECK)).commit();
				}
			}
			
		}, 1000);
		
		
		
		
	}//onCreate()��������
	
}
