package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.WeatherInfo;
import com.coolweather.app.service.AutoUpdateService;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;
import com.coolweather.app.util.WeatherInfoAdapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WeatherActivity extends Activity implements OnClickListener {

	private ListView weatherInfoList;
	private LinearLayout weatherInfoLayout;
	/*
	 * 用于显示同步天气的进度
	 */
	private ProgressDialog progressDialog;
	/*
	 * 用于显示城市名
	 */
	private TextView cityNameText;
	/*
	 * 用于显示发布时间
	 */
	private TextView publishText;
	/*
	 * 用于显示天气描述信息
	 */
	private TextView weatherDespText;
	/*
	 * 用于显示气温1
	 */
	private TextView temp1Text;
	/*
	 * 用于显示气温2
	 */
	private TextView temp2Text;
	/*
	 * 用于显示当前日期
	 */
	private TextView currentDateText;

	/*
	 * 切换城市按钮
	 */
	private Button switchCity;
	/*
	 * 更新天气按钮
	 */
	private Button refreshWeather;

	/*
	 * 获取天气列表信息，显示几天后的天气状况
	 */
	List<WeatherInfo> infos = new ArrayList<WeatherInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		Log.d("TAG", "天气启用了oncreate方法。");
		weatherInfoLayout = (LinearLayout) findViewById(R.id.weather_info_layout);
		weatherInfoList = (ListView) findViewById(R.id.weatherInfo_list);
		cityNameText = (TextView) findViewById(R.id.city_name);
		publishText = (TextView) findViewById(R.id.publish_text);
		weatherDespText = (TextView) findViewById(R.id.weather_desp);
		temp1Text = (TextView) findViewById(R.id.temp1);
		temp2Text = (TextView) findViewById(R.id.temp2);
		currentDateText = (TextView) findViewById(R.id.current_data);
		String countyCode = getIntent().getStringExtra("county_code");
		if (!TextUtils.isEmpty(countyCode)) {
			// 有县级代号时就去查询天气
			// publishText.setText("同步中...");
			showProgressDialog();
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// 没有县级代号就直接显示本地天气
			showWeather(infos);
		}

		switchCity = (Button) findViewById(R.id.switch_city);
		refreshWeather = (Button) findViewById(R.id.refresh_weather);
		switchCity.setOnClickListener(this);
		refreshWeather.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		String weatherCode = prefs.getString("weather_code", "");
		refreshWeatherInfo(weatherCode);
		showWeather(infos);
		Log.d("TAG", infos.toString());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.switch_city:
			Intent intent = new Intent(this, ChooseAreaActivity.class);
			intent.putExtra("from_weather_activity", true);
			startActivity(intent);
			finish();
			break;
		case R.id.refresh_weather:
			// publishText.setText("刷新同步中...");
			showProgressDialog();
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this);
			String weatherCode = prefs.getString("weather_code", "");
			if (!TextUtils.isEmpty(weatherCode)) {
				refreshWeatherInfo(weatherCode);
			}
			break;
		default:
			break;
		}
	}

	/*
	 * 查询县级代号所对应的天气代号
	 */
	private void queryWeatherCode(String countyCode) {
		String address = "http://www.weather.com.cn/data/list3/city"
				+ countyCode + ".xml";
		queryFromServer(address, "countyCode");
	}

	private void queryFromServer(final String address, final String type) {
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(final String response) {
				if ("countyCode".equals(type)) {
					if (!TextUtils.isEmpty(response)) {
						String[] array = response.split("\\|");
						if (array != null && array.length == 2) {
							String weatherCode = array[1];
							queryWeatherInfo(weatherCode);
						}
					}
				} else if ("weatherCode".equals(type)) {

					infos = Utility.handleWeatherResponse(WeatherActivity.this,
							response);
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							showWeather(infos);
						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						closeProgressDialog();
						publishText.setText("同步失败...");
						ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo mNetworkInfo = mConnectivityManager
								.getActiveNetworkInfo();
						if (mNetworkInfo == null) {
							Toast.makeText(WeatherActivity.this, "当前没有网络",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	/*
	 * 查询天气代号所对应的天气
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "https://api.heweather.com/x3/weather?cityid=CN"
				+ weatherCode + "&key=b37e2d400e814e82b2926d680aed42d6";
		queryFromServer(address, "weatherCode");
	}

	/*
	 * 刷新天气代号所对应的天气
	 */
	private void refreshWeatherInfo(String weatherCode) {
		String address = "https://api.heweather.com/x3/weather?cityid="
				+ weatherCode + "&key=b37e2d400e814e82b2926d680aed42d6";
		queryFromServer(address, "weatherCode");
	}

	// private List<WeatherInfo> weatherList=new ArrayList<WeatherInfo>();
	/*
	 * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
	 */
	public void showWeather(List<WeatherInfo> weatherList) {
		closeProgressDialog();
		// List<WeatherInfo> weatherList=new ArrayList<WeatherInfo>();
		WeatherInfoAdapter adapter = new WeatherInfoAdapter(
				WeatherActivity.this, R.layout.weather_list, weatherList);
		weatherInfoList.setAdapter(adapter);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		cityNameText.setText(prefs.getString("city_name", ""));

		publishText.setText(prefs.getString("publishDate", "") + "发布");

		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		// intent.putExtra("cityName", prefs.getString("city_name", ""));
		// intent.putExtra("date", prefs.getString("publishDate", ""));
		startService(intent);
	}

	/*
	 * 显示进度对话框
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("同步天气中...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * 关闭进度对话框
	 */
	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			Toast.makeText(this, "该软件已是最新版本", Toast.LENGTH_SHORT).show();
			break;
		case R.id.about:
			Intent intent = new Intent(WeatherActivity.this,
					AboutActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
		return true;
	}

	private long exitTime = 0;
	/*
	 * moveTaskToBack()方法：在activity中调用 moveTaskToBack (boolean
	 * nonRoot)方法即可将activity 退到后台，注意不是finish()退出。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		// 按两次退出程序或在后台运行
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序,酷我天气将在后台运行",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				/**
				 * 会调用：onPause()和onStop()
				 */
				moveTaskToBack(true);
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
