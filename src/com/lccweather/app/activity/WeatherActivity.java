package com.lccweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lccweather.app.MyApplication;
import com.lccweather.app.MyPagerAdapter;
import com.lccweather.app.R;
import com.lccweather.app.util.HttpCallbackListener;
import com.lccweather.app.util.HttpUtil;
import com.lccweather.app.util.Utility;

public class WeatherActivity extends Activity implements OnClickListener{

	private LinearLayout weatherLinearLayout;
	private TextView cityNameView;
	private TextView publishTimeView;
	private TextView weatherTimeView;
	private TextView weatherDescView;
	private TextView temp1View;
	private TextView temp2View;
	private TextView sharePrefernceName;
	private TextView symbol;
	private Button switchCityButton;
	private Button refreshButton;
	private Button addCityButton;
	private ImageView imageView;
	
	private ViewPager viewPager;
	private View defaultView;
	private LayoutInflater layoutInflater;
	private MyPagerAdapter myPagerAdapter;
	private List<View> viewList;
	private View selectedView;
	private String selectedPrefs;
	
	private boolean isAddWeather;
	
	private MyApplication myApplication;
	private SharedPreferences defaultSharedPreferences;
	private SharedPreferences.Editor defaultEditor;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.wether_activity);
		
		myApplication = (MyApplication)getApplicationContext();
		defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		defaultEditor = defaultSharedPreferences.edit();
		
		viewPager = (ViewPager)findViewById(R.id.viewPager);
		layoutInflater = LayoutInflater.from(this);
		initViewList();
		myPagerAdapter = new MyPagerAdapter(viewList);
		viewPager.setAdapter(myPagerAdapter);
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				SharedPreferences sharePreferences = null;
				if(arg0 == 0){
					sharePreferences = 
							defaultSharedPreferences;
					selectedPrefs = "";
				}else{
					sharePreferences = 
							getSharedPreferences("cityWeather_"+arg0, MODE_PRIVATE);
				}
				cityNameView.setText(sharePreferences.getString("city_Name", ""));
				selectedView = viewList.get(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		cityNameView = (TextView)findViewById(R.id.city_name);
		switchCityButton = (Button)findViewById(R.id.switch_city);
		switchCityButton.setOnClickListener(this);
		refreshButton = (Button)findViewById(R.id.refresh_weather);
		refreshButton.setOnClickListener(this);
		addCityButton = (Button)findViewById(R.id.add_weather);
		addCityButton.setOnClickListener(this);
		
		String countyCode = getIntent().getStringExtra("county_code");
		
		if(!TextUtils.isEmpty(countyCode)){
			/*publishTimeView.setText("同步中...");
			weatherLinearLayout.setVisibility(View.INVISIBLE);*/
			cityNameView.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode,selectedPrefs);
		}else{
			showWeather();
		}
		
	}
	
	/**
	 * 初始化initViewList
	 */
	private void initViewList(){
		viewList = new ArrayList<View>();
		defaultView = layoutInflater.inflate(R.layout.weatherinfo, null);
		viewList.add(defaultView);
		
		int cityCount = myApplication.getShareCityCount();
		for(int i=0;i<cityCount;i++){
			View view = layoutInflater.inflate(R.layout.weatherinfo, null);
			viewList.add(view);
		}
		if(getIntent().getBooleanExtra("isFromWeatherActivityAdd", false)){
			isAddWeather = true;
			selectedView = viewList.get(viewList.size()-1);
			selectedPrefs = "cityWeather_"+(viewList.size()-1);
		}else{
			selectedView = defaultView;
			selectedPrefs = "";
		}
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.switch_city:
			Intent intent = new Intent(this,ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			isAddWeather = false;
			TextView prefNameView = (TextView)selectedView.findViewById(R.id.sharePreferenceName);
			((TextView)selectedView.findViewById(R.id.publish_time)).setText("同步中...");
			selectedPrefs = prefNameView.getText().toString();
			SharedPreferences prefs = null;
			if(selectedPrefs == null || selectedPrefs == ""){
				Log.d("myTag", selectedPrefs+"-------");
				prefs = PreferenceManager.getDefaultSharedPreferences(this);
			}else{
				prefs = getSharedPreferences(selectedPrefs, MODE_PRIVATE);
			}
			String weatherCode = prefs.getString("weather_code", "");
			if(!TextUtils.isEmpty(weatherCode)){
				queryWeatherInfo(weatherCode,selectedPrefs);
			}
			break;
		case R.id.add_weather:
			myApplication.addCityCount();
			Intent i = new Intent(this,ChooseAreaActivity.class);
			i.putExtra("from_weather_activity_add", true);
			startActivity(i);
			/*View view = layoutInflater.inflate(R.layout.weatherinfo, null);
			viewList.add(view);
			myPagerAdapter.notifyDataSetChanged();*/
			
			finish();
			break;
		}
		
	}
	/**
	 * 查询县级代号所对应的天气代号
	 * @param countyCode
	 */
	private void queryWeatherCode(String countyCode,String selectedPrefs){
		String address = "http://www.weather.com.cn/data/list3/city" + 
	countyCode +".xml";
		queryFromServer(address,"countyCode",selectedPrefs);
	}
	/**
	 * 查询天气信息
	 * @param weatherCode
	 */
	private void queryWeatherInfo(String weatherCode,String selectedPrefs){
		String address = "http://www.weather.com.cn/data/cityinfo/"+
	weatherCode+".html";
		queryFromServer(address, "weatherCode",selectedPrefs);
	}
	
	/**
	 * 根据传入的地址和类型去向服务器查询天气代号或者天气信息
	 * @param address
	 * @param type
	 */
	private void queryFromServer(final String address,final String type,final String selectedPrefs){
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				if("countyCode".equals(type)){
					if(!TextUtils.isEmpty(response)){
						String[] array = response.split("\\|");
						if(array != null && array.length == 2){
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode,selectedPrefs);
						}
					}
				}else if("weatherCode".equals(type)){
					Utility.handleWeatherReponse(WeatherActivity.this, response,myApplication,selectedPrefs);
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
						((TextView)selectedView.findViewById(R.id.publish_time))
						.setText("同步失败");
						if(isAddWeather){
							myApplication.cutCityCount();
						}
					}
				});
				
			}
		});
	}
	
	/**
	 * 从sharePreferences文件中读取存储的天气信息，并显示到界面上
	 */
	private void showWeather(){
		SharedPreferences sharePreferences = null;
		/*for(int i=1;i<=myApplication.getShareCityCount();i++){
			//sharePreferences = 
		}*/
		
		for(int i=0;i<viewList.size();i++){
			View view = viewList.get(i);
			weatherLinearLayout = (LinearLayout)view.findViewById(R.id.weather_info_layout);
			publishTimeView = (TextView)view.findViewById(R.id.publish_time);
			weatherTimeView = (TextView)view.findViewById(R.id.weather_time);
			weatherDescView = (TextView)view.findViewById(R.id.weather_desc);
			imageView = (ImageView)view.findViewById(R.id.weather_image);
			temp1View = (TextView)view.findViewById(R.id.lowest_temp);
			temp2View = (TextView)view.findViewById(R.id.highest_temp);
			symbol = (TextView)view.findViewById(R.id.symbol);
			sharePrefernceName = (TextView)view.findViewById(R.id.sharePreferenceName);
			if(i == 0){
				sharePreferences = PreferenceManager.getDefaultSharedPreferences(this);
				sharePrefernceName.setText("");
			}else{
				sharePreferences = 
						this.getSharedPreferences("cityWeather_"+i, MODE_PRIVATE);
				sharePrefernceName.setText("cityWeather_"+i);
			}

			publishTimeView.setText("今天"+sharePreferences.getString("publish_time", "")+"发布");
			weatherTimeView.setText(sharePreferences.getString("current_time", ""));
			weatherDescView.setText(sharePreferences.getString("weather_desc", ""));
			imageView.setImageResource(getDrawableId(sharePreferences.getString("weather_desc", "")));
			temp1View.setText(sharePreferences.getString("temp2", ""));
			temp2View.setText(sharePreferences.getString("temp1", ""));
			symbol.setText("~");
			weatherLinearLayout.setVisibility(View.VISIBLE);
		}
		cityNameView.setVisibility(View.VISIBLE);
		if(!isAddWeather){
			if(selectedPrefs == null || selectedPrefs == ""){
				cityNameView.setText(defaultSharedPreferences.getString("city_Name", ""));
			}else{
				cityNameView.setText(getSharedPreferences(selectedPrefs, MODE_PRIVATE)
						.getString("city_Name",""));
			}
			viewPager.setCurrentItem(viewList.indexOf(selectedView));
		}else{
			cityNameView.setText(sharePreferences.getString("city_Name", ""));
			viewPager.setCurrentItem(viewList.size()-1);
			selectedView = viewList.get(viewList.size()-1);
			selectedPrefs = "cityWeather_"+(viewList.size()-1);
		}
		myPagerAdapter.notifyDataSetChanged();
		/*publishTimeView.setText("今天"+sharePreferences.getString("publish_time", "")+"发布");
		weatherTimeView.setText(sharePreferences.getString("current_time", ""));
		weatherDescView.setText(sharePreferences.getString("weather_desc", ""));
		temp1View.setText(sharePreferences.getString("temp2", ""));
		temp2View.setText(sharePreferences.getString("temp1", ""));
		weatherLinearLayout.setVisibility(View.VISIBLE);
		cityNameView.setVisibility(View.VISIBLE);*/
	}
	
	/**
	 * 根据天气信息得到对应的图片
	 * @param weatherInfo
	 * @return
	 */
	private int getDrawableId(String weatherInfo){
		if("小雨".equals(weatherInfo)){
			return R.drawable.little_rain;
		}else if("小雨转多云".equals(weatherInfo)){
			return R.drawable.littlerain_heavyclound;
		}else if("晴转多云".equals(weatherInfo)){
			return R.drawable.sun_to_cloudy;
		}
		return 0;
	}

	
	
	

}
