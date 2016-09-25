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
	 * ������ʾͬ�������Ľ���
	 */
	private ProgressDialog progressDialog;
	/*
	 * ������ʾ������
	 */
	private TextView cityNameText;
	/*
	 * ������ʾ����ʱ��
	 */
	private TextView publishText;
	/*
	 * ������ʾ����������Ϣ
	 */
	private TextView weatherDespText;
	/*
	 * ������ʾ����1
	 */
	private TextView temp1Text;
	/*
	 * ������ʾ����2
	 */
	private TextView temp2Text;
	/*
	 * ������ʾ��ǰ����
	 */
	private TextView currentDateText;

	/*
	 * �л����а�ť
	 */
	private Button switchCity;
	/*
	 * ����������ť
	 */
	private Button refreshWeather;

	/*
	 * ��ȡ�����б���Ϣ����ʾ����������״��
	 */
	List<WeatherInfo> infos = new ArrayList<WeatherInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.weather_layout);
		Log.d("TAG", "����������oncreate������");
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
			// ���ؼ�����ʱ��ȥ��ѯ����
			// publishText.setText("ͬ����...");
			showProgressDialog();
			weatherInfoLayout.setVisibility(View.INVISIBLE);
			cityNameText.setVisibility(View.INVISIBLE);
			queryWeatherCode(countyCode);
		} else {
			// û���ؼ����ž�ֱ����ʾ��������
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
			// publishText.setText("ˢ��ͬ����...");
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
	 * ��ѯ�ؼ���������Ӧ����������
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
						publishText.setText("ͬ��ʧ��...");
						ConnectivityManager mConnectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
						NetworkInfo mNetworkInfo = mConnectivityManager
								.getActiveNetworkInfo();
						if (mNetworkInfo == null) {
							Toast.makeText(WeatherActivity.this, "��ǰû������",
									Toast.LENGTH_SHORT).show();
						}
					}
				});
			}
		});
	}

	/*
	 * ��ѯ������������Ӧ������
	 */
	private void queryWeatherInfo(String weatherCode) {
		String address = "https://api.heweather.com/x3/weather?cityid=CN"
				+ weatherCode + "&key=b37e2d400e814e82b2926d680aed42d6";
		queryFromServer(address, "weatherCode");
	}

	/*
	 * ˢ��������������Ӧ������
	 */
	private void refreshWeatherInfo(String weatherCode) {
		String address = "https://api.heweather.com/x3/weather?cityid="
				+ weatherCode + "&key=b37e2d400e814e82b2926d680aed42d6";
		queryFromServer(address, "weatherCode");
	}

	// private List<WeatherInfo> weatherList=new ArrayList<WeatherInfo>();
	/*
	 * ��SharedPreferences�ļ��ж�ȡ�洢��������Ϣ������ʾ��������
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

		publishText.setText(prefs.getString("publishDate", "") + "����");

		weatherInfoLayout.setVisibility(View.VISIBLE);
		cityNameText.setVisibility(View.VISIBLE);
		Intent intent = new Intent(this, AutoUpdateService.class);
		// intent.putExtra("cityName", prefs.getString("city_name", ""));
		// intent.putExtra("date", prefs.getString("publishDate", ""));
		startService(intent);
	}

	/*
	 * ��ʾ���ȶԻ���
	 */
	private void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("ͬ��������...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	/*
	 * �رս��ȶԻ���
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
			Toast.makeText(this, "������������°汾", Toast.LENGTH_SHORT).show();
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
	 * moveTaskToBack()��������activity�е��� moveTaskToBack (boolean
	 * nonRoot)�������ɽ�activity �˵���̨��ע�ⲻ��finish()�˳���
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {	
		// �������˳�������ں�̨����
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����,�����������ں�̨����",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				/**
				 * ����ã�onPause()��onStop()
				 */
				moveTaskToBack(true);
			}
			return true;
		}
		
		return super.onKeyDown(keyCode, event);
	}

}
