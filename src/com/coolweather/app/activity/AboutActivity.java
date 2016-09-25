package com.coolweather.app.activity;

import com.coolweather.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class AboutActivity extends Activity {

	private ListView listView;
	private String[] text = { "������ƣ���ŷ����",
			"���������������ϵͳ������ʵʱ��������������֧��ȫ�������ء��е�������ѯ", "�汾�ţ�V1.0",
			"�������ڣ�2016��12��25��", "�����ߣ�C.star",
			"ע�⣺�������Ȩ�鿪�������У�δ���������������ý������������ҵ��;��" };

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
