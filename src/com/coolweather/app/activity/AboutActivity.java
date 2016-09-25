package com.coolweather.app.activity;

import com.coolweather.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AboutActivity extends Activity {

	private ListView listView;
	private String[] text = { "软件名称：酷欧天气",
			"软件描述：本天气系统可用于实时更新最新天气，支持全国各个县、市的天气查询", "版本号：V1.0",
			"发布日期：2016年12月25号", "开发者：C.star",
			"注意：本软件版权归开发者所有，未经开发者允许，不得将本软件用于商业用途。" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.about_layout);
		listView = (ListView) findViewById(R.id.about_text);
		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				AboutActivity.this, android.R.layout.simple_list_item_1, text);
		listView.setAdapter(arrayAdapter);
	}
}
