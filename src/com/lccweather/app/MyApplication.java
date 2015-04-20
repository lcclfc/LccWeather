package com.lccweather.app;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

public class MyApplication extends Application{

	private int shareCityCount = 0;
    private SharedPreferences prefs;
    SharedPreferences.Editor editor;
	
	@Override
	public void onCreate() {
		super.onCreate();
		prefs = getSharedPreferences("weatherInfo", MODE_PRIVATE);
		editor = prefs.edit();
		if(prefs.getInt("city_count", -1) >=0 ){
			shareCityCount = prefs.getInt("city_count", 0);
		}else{
			editor.putInt("city_count", 0);
			editor.commit();
		}
	}

	public int getShareCityCount() {
		shareCityCount = prefs.getInt("city_count", 0);
		return shareCityCount;
	}

	public void setShareCityCount(int shareCityCount) {
		editor.putInt("city_count", shareCityCount);
		this.shareCityCount = shareCityCount;
	}  
	
	public void addCityCount(){
		shareCityCount += 1;
		editor.putInt("city_count", shareCityCount);
		editor.commit();
	}
	
	public void cutCityCount(){
		shareCityCount -= 1;
		editor.putInt("city_count", shareCityCount);
		editor.commit();
	}
}
