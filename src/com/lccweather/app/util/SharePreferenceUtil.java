package com.lccweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SharePreferenceUtil {

	private SharedPreferences sharePreferences;
	private SharedPreferences.Editor editor;
	
	public SharePreferenceUtil(Context context,String fileName){
		sharePreferences = 
				context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		editor = sharePreferences.edit();
	}
	
	public void setSelectedCity(){
		editor.putBoolean("selected_city", true);
	}
	
	public void setCityName(String cityName){
		editor.putString("city_Name", cityName);
	}
	
	public void setWeatherCode(){
		
	}
	
	public void saveCityInfo(){
		
	}
	
}
