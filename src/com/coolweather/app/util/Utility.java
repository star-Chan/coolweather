package com.coolweather.app.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.coolweather.app.db.CoolWeatherDB;
import com.coolweather.app.model.City;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.model.WeatherInfo;

public class Utility {

	// 解析和处理服务器返回的省级数据
	public synchronized static boolean handleProvincesResponse(
			CoolWeatherDB coolWeatherDB, String response) {
		if (!TextUtils.isEmpty(response)) {
			String[] allProvinces = response.split(",");
			if (allProvinces != null && allProvinces.length > 0) {
				for (String p : allProvinces) {
					String[] array = p.split("\\|");
					Province province = new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}

	// 解析和处理服务器返回的市级数据
	public synchronized static boolean handleCitiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int provinceId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCities = response.split(",");
			if (allCities != null && allCities.length > 0) {
				for (String p : allCities) {
					String[] array = p.split("\\|");
					City city = new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					coolWeatherDB.saveCity(city);
				}
				return true;
			}
		}
		return false;
	}

	// 解析和处理服务器返回的县级数据
	public synchronized static boolean handleCountiesResponse(
			CoolWeatherDB coolWeatherDB, String response, int cityId) {
		if (!TextUtils.isEmpty(response)) {
			String[] allCounties = response.split(",");
			if (allCounties != null && allCounties.length > 0) {
				for (String p : allCounties) {
					String[] array = p.split("\\|");
					County county = new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		return false;
	}

	private static WeatherInfo weatherInfo=null;
	/*
	 * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
	 */
	public static List<WeatherInfo> handleWeatherResponse(Context context, String response) {
		String cityName=null;
		String publishDate=null;
		String weather_desp=null;
		int weather_image;
		String date=null;
		String minTemp=null;
		String maxTemp=null;
		String weatherCode=null;
		
		List<WeatherInfo> weatherInfoList=new ArrayList<WeatherInfo>();
		try {
			String count = "";
			JSONObject jsonObject = new JSONObject(response);
			JSONArray jsonArray = jsonObject
					.getJSONArray("HeWeather data service 3.0");
			if (jsonArray.length() > 0) {
				JSONObject object = jsonArray.getJSONObject(0);
				JSONObject object1 = object.getJSONObject("basic");
				cityName = object1.getString("city");
				weatherCode=object1.getString("id");
				JSONObject updateObject=object1.getJSONObject("update");
				publishDate=updateObject.getString("loc");
				JSONArray array = object.getJSONArray("daily_forecast");
				for (int i = 0; i < array.length(); i++) {
					JSONObject array2 = array.getJSONObject(i);
					JSONObject jsonObject2 = array2.getJSONObject("tmp");
					JSONObject condObject=array2.getJSONObject("cond");
					weather_image=condObject.getInt("code_d");
					weather_desp=condObject.getString("txt_d");
					maxTemp = jsonObject2.getString("max");
					minTemp = jsonObject2.getString("min");
					// JSONObject object2=array2.getJSONObject("data");
					date = array2.getString("date");

					count = count + publishDate+"\n" + cityName + " "+weather_desp+" "+ minTemp + " " + maxTemp
							+ " " + date;
					Log.i("AAA", count);
					
					weatherInfo=new WeatherInfo();
					weatherInfo.setCurrent_data(date);
					weatherInfo.setWeather_desp(weather_desp);
					weatherInfo.setWeather_image(weather_image);
					weatherInfo.setMin_temp(minTemp);
					weatherInfo.setMax_temp(maxTemp);
					
					weatherInfoList.add(weatherInfo);
					
					saveWeatherInfo1(context,cityName,publishDate,weatherCode);
				}

			}
			// saveWeatherInfo(context, cityName, weatherCode, , temp2,
			// weatherDesp, publishTime);
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return weatherInfoList;
	}

	public static void saveWeatherInfo1(Context context, String cityName,
			String publishDate,String weatherCode) {
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("publishDate", publishDate);
		editor.putString("weather_code", weatherCode);
		editor.commit();
		
	}

	

	/*
	 * 将服务器返回的所有的天气信息存储到SharedPreference文件中。
	 */
	public static void saveWeatherInfo(Context context, String cityName,
			String weatherCode, String temp1, String temp2, String weatherDesp,
			String publishTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
		SharedPreferences.Editor editor = PreferenceManager
				.getDefaultSharedPreferences(context).edit();
		editor.putBoolean("city_selected", true);
		editor.putString("city_name", cityName);
		editor.putString("weather", weatherCode);
		editor.putString("temp1", temp1);
		editor.putString("temp2", temp2);
		editor.putString("weather_desp", weatherDesp);
		editor.putString("publish_time", publishTime);
		editor.putString("current_date", sdf.format(new Date()));
		editor.commit();
	}

}
