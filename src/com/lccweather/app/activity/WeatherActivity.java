package com.lccweather.app.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lccweather.app.R;
import com.lccweather.app.util.HttpCallbackListener;
import com.lccweather.app.util.HttpUtil;
import com.lccweather.app.util.Utility;

public class WeatherActivity extends Activity{

	private LinearLayout weatherLinearLayout;
	private TextView cityNameView;
	private TextView publishTimeView;
	private TextView weatherTimeView;
	private TextView weatherDescView;
	private TextView temp1View;
	private TextView temp2View;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_activity);
		weatherLinearLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
		cityNameView = (TextView)findViewById(R.id.city_name);
		publishTimeView = (TextView)findViewById(R.id.publish_time);
		weatherTimeView = (TextView)findViewById(R.id.weather_time);
		weatherDescView = (TextView)findViewById(R.id.weather_desc);
		temp1View = (TextView)findViewById(R.id.lowest_temp);
		temp2View = (TextView)findViewById(R.id.highest_temp);
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			publishTimeView.setText("ͬ����...");
			weatherLinearLayout.setVisibility(View.INVISIBLE);
			cityNameView.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		}else{
			showWeather();
		}
		
	}
	
	/**
	 * ��ѯ�ؼ���������Ӧ����������
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode){
		String address = "http://www.weather.com.cn/data/list3/city" + 
	countyCode +".xml";
		queryFromServer(address,"countyCode");
	}
	/**
	 * ��ѯ������Ϣ
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode){
		String address = "http://www.weather.com.cn/data/cityinfo/"+
	weatherCode+".html";
		queryFromServer(address, "weatherCode");
	}
	
	/**
	 * ���ݴ���ĵ�ַ������ȥ���������ѯ�������Ż���������Ϣ
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address,final String type){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array != null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				}else if("weatherCode".equals(type)){
					Log.d("myTag", response);
					Utility.handleWeatherReponse(WeatherActivity.this, response);
					runOnUiThread(new Runnable(){
						@Override
						public void run() {
							showWeather();
						}
					});
					}
				
			}
			
			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable(){
					@Override
					public void run() {
						publishTimeView.setText("ͬ��ʧ��");
					}
				});
				
			}
		});
	}
	
	/**
	 * ��sharePreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
	 */
	private void showWeather(){
		SharedPreferences sharePreferences = 
				PreferenceManager.getDefaultSharedPreferences(this);
		cityNameView.setText(sharePreferences.getString("city_Name", ""));
		publishTimeView.setText("����"+sharePreferences.getString("publish_time", "")+"����");
		weatherTimeView.setText(sharePreferences.getString("current_time", ""));
		weatherDescView.setText(sharePreferences.getString("weather_desc", ""));
		temp1View.setText(sharePreferences.getString("temp2", ""));
		temp2View.setText(sharePreferences.getString("temp1", ""));
		weatherLinearLayout.setVisibility(View.VISIBLE);
		cityNameView.setVisibility(View.VISIBLE);
	}
	
	

}
