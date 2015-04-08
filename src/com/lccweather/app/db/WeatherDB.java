package com.lccweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lccweather.app.model.City;
import com.lccweather.app.model.County;
import com.lccweather.app.model.Province;


public class WeatherDB {

	public static final String DB_NAME = "cool_weather";//数据库名称
	public static final int VERSION = 1;
	private static WeatherDB weatherDB;
	private SQLiteDatabase db;
	
	public WeatherDB(Context context){
		WeatherOpenHelper dbHelper = 
				new WeatherOpenHelper(context, DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}
	
	public synchronized static WeatherDB getInstance(Context context){
		if(weatherDB == null){
			weatherDB = new WeatherDB(context);
		}
		return weatherDB;
	}
	
	/**
	 * 将province实例保存到数据库
	 * @param province
	 */
	public void saveProvince(Province province){
		if(province != null){
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}
	
	/**
	 * 从数据库中读取省份信息
	 * @return
	 */
	public List<Province> loadProvinces(){
		List<Province> provinceList = new ArrayList<Province>();
		Cursor cursor = db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			while(cursor.moveToNext()){
				Province province = new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(
						cursor.getColumnIndex("province_name")));
				province.setProvinceCode(cursor.getString(
						cursor.getColumnIndex("province_code")));
				provinceList.add(province);
			}
		}
		if(cursor != null){
			cursor.close();
		}
		return provinceList;
		
	}
	
	public void saveCity(City city){
		if(city !=null){
			ContentValues values = new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	public List<City> loadCities(int provinceId){
		List<City> cityList = new ArrayList<City>();
		Cursor cursor = db.query("City", null, "province_id = ?", 
				new String[]{String.valueOf(provinceId)}, null, null, null);
		if(cursor.moveToFirst()){
			while(cursor.moveToNext()){
				City city = new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(
						cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
				cityList.add(city);
			}
		}
		if(cursor != null){
			cursor.close();
		}
		return cityList;
	}
	
	public void saveCounty(County county){
		if(county != null){
			ContentValues values = new ContentValues();
			values.put("county_name", county.getCountyName());
			values.put("county_code", county.getCountyCode());
			values.put("city_id",county.getCityId());
			db.insert("county", null, values);
		}
	}
	public List<County> loadCounty(int cityId){
		List<County> countyList = new ArrayList<County>();
		Cursor cursor = db.query("county", null, "city_id = ?", 
				new String[]{String.valueOf(cityId)}, null, null, null);
		if(cursor.moveToFirst()){
			while(cursor.moveToNext()){
				County county = new County();
				county.setId(cursor.getInt(cursor.getColumnIndex("id")));
				county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
				county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
				county.setCityId(cityId);
				
				countyList.add(county);
			}
		}
		if(cursor != null){
			cursor.close();
		}
		return countyList;
	}
	
}
