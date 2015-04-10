package com.lccweather.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.lccweather.app.db.WeatherDB;
import com.lccweather.app.model.City;
import com.lccweather.app.model.County;
import com.lccweather.app.model.Province;

public class Utility {

	/**
	 * 解析和处理服务器返回的省级数据
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleProvinceResponse(
			WeatherDB db,String response){
		if(!TextUtils.isEmpty(response)){
			String[] allProvince = response.split(",");
			if(allProvince != null && allProvince.length>0){
				for(String p :allProvince){
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					db.saveProvince(province);
					
				}
				return true;
			}
		}
		return false;
		
	}
	
	/**
	 * 解析和处理服务器返回的市级数据
	 * @return
	 */
	public synchronized static boolean handleCityResponse(
			WeatherDB db,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCity = response.split(",");
			if(allCity != null && allCity.length>0){
				for(String c : allCity){
					String[] array = c.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					db.saveCity(city);
					
				}
				return true;
			}
		}
		
		return false;
		
	}
	
	/**
	 * 解析和处理服务器返回的县级数据
	 * @param db
	 * @param response
	 * @return
	 */
	public synchronized static boolean handleCountyResponse(
			WeatherDB db,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCounty = response.split(",");
			if(allCounty != null && allCounty.length>0){
				for(String c : allCounty){
					String[] array = c.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					db.saveCounty(county);
				}
				return true;
			}
		}
		
		return false;
		
	}
	/**
	 * 解析服务器返回来的json数据并将解析出的数据存储到本地
	 * @param context
	 * @param response
	 */
	public static void handleWeatherReponse(Context context,String response){
		try {
			JSONObject jsonObject = new JSONObject(response);
			JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");
			String cityName = weatherInfo.getString("city");
			String weatherCode = weatherInfo.getString("cityid");
			String temp1 = weatherInfo.getString("temp1");
			String temp2 = weatherInfo.getString("temp2");
			String weatherDesc = weatherInfo.getString("weather");
			String publishTime = weatherInfo.getString("ptime");

			saveWeatherInfo(context, cityName, weatherCode, temp1, temp2, 
					weatherDesc, publishTime);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 保存天气信息到SharePreference文件中
	 * @param context
	 * @param cityName
	 * @param weatherCode
	 * @param temp1
	 * @param temp2
	 * @param weatherDesc
	 * @param publishTime
	 */
	private static void saveWeatherInfo(Context context,String cityName,
			String weatherCode,String temp1,String temp2,
			String weatherDesc,String publishTime){
		SharedPreferences.Editor editor = 
				PreferenceManager.getDefaultSharedPreferences(context).edit();
		SimpleDateFormat simpleformat = 
				new SimpleDateFormat("yyyy年MM月dd日",Locale.CHINA);
		editor.putBoolean("selected_city", true);
		editor.putString("city_Name", cityName);
		editor.putString("weather_code", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desc", weatherDesc);
		editor.putString("publish_time", publishTime);
		editor.putString("current_time", simpleformat.format(new Date()));
		editor.commit();
	}
}
