package com.lccweather.app.util;

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
}
