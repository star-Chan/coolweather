package com.coolweather.app.model;

public class WeatherInfo {

	
	private String current_data;
	private String weather_desp;
	private int weather_image;
	private String min_temp;
	private String max_temp;
	public String getCurrent_data() {
		return current_data;
	}
	public void setCurrent_data(String current_data) {
		this.current_data = current_data;
	}
	public String getWeather_desp() {
		return weather_desp;
	}
	public void setWeather_desp(String weather_desp) {
		this.weather_desp = weather_desp;
	}
	
	public int getWeather_image() {
		return weather_image;
	}
	public void setWeather_image(int weather_image) {
		this.weather_image = weather_image;
	}
	public String getMin_temp() {
		return min_temp;
	}
	public void setMin_temp(String min_temp) {
		this.min_temp = min_temp;
	}
	public String getMax_temp() {
		return max_temp;
	}
	public void setMax_temp(String max_temp) {
		this.max_temp = max_temp;
	}
	
}
