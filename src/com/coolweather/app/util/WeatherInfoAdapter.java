package com.coolweather.app.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import com.coolweather.app.R;
import com.coolweather.app.model.WeatherInfo;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WeatherInfoAdapter extends ArrayAdapter<WeatherInfo> {
	
	private int resourceId;
	private static final int WEATHER_IMAGE=0;
	private Drawable drawable1;
	private String urladd;
	private WeatherInfo weatherInfo;
//	private ListView listView;
//	private LinearLayout layout;
	private ViewHolder viewHolder;
	
	private Handler handler=new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case WEATHER_IMAGE:
				drawable1=(Drawable)msg.obj;
//				listView.setVisibility(View.VISIBLE);
//				layout.setVisibility(View.VISIBLE);
				
			}
		}
	};

	public WeatherInfoAdapter(Context context, int textViewResourceId,
			List<WeatherInfo> objects) {
		super(context, textViewResourceId, objects);
		resourceId=textViewResourceId;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		weatherInfo=getItem(position);
		urladd="http://files.heweather.com/cond_icon/"+weatherInfo.getWeather_image()+".png";
		View view;
		
		if(convertView==null){
			view=LayoutInflater.from(getContext()).inflate(resourceId, null);
			viewHolder=new ViewHolder();
			viewHolder.current_date=(TextView)view.findViewById(R.id.current_data);
			viewHolder.weather_desp=(TextView)view.findViewById(R.id.weather_desp);
			viewHolder.weather_image=(ImageView)view.findViewById(R.id.weather_image);
			viewHolder.minTemp=(TextView)view.findViewById(R.id.temp1);
			viewHolder.maxTemp=(TextView)view.findViewById(R.id.temp2);
			view.setTag(viewHolder);
			
		}else{
			view=convertView;
			viewHolder=(ViewHolder)view.getTag();
		}
//		listView=(ListView)view.findViewById(R.id.weatherInfo_list);
//		listView.setVisibility(View.INVISIBLE);
//		layout=(LinearLayout)view.findViewById(R.layout.weather_list);
//		layout.setVisibility(View.INVISIBLE);
	
		new Thread(new Runnable() {
			Drawable drawable = null;
			@Override
			public void run() {
				drawable = loadImageFromNetwork(urladd);
		        Message message=new Message();
		        message.what=WEATHER_IMAGE;
		        message.obj=drawable;
		        handler.sendMessage(message);
			}

			private Drawable loadImageFromNetwork(String urladd) {
				Drawable drawable = null;
				try {
					drawable = Drawable.createFromStream(new URL(urladd).openStream(),
							weatherInfo.getWeather_image()+".jpg");
				} catch (IOException e) {
					Log.d("test", e.getMessage());
				}
				if (drawable == null) {
					Log.d("test", "null drawable");
				} else {
					Log.d("test", "not null drawable");
				}
				return drawable;
			}
		}).start();
		
		viewHolder.current_date.setText(weatherInfo.getCurrent_data());
		viewHolder.weather_desp.setText(weatherInfo.getWeather_desp());
		viewHolder.weather_image.setImageDrawable(drawable1);
//		File file=new File("D:/weatherpictures/"+weatherInfo.getWeather_image()+".png");
//		Uri uri=Uri.fromFile(file);
//		viewHolder.weather_image.setImageResource(R.drawable.aaa);
		viewHolder.minTemp.setText(weatherInfo.getMin_temp()+"¡æ");
		viewHolder.maxTemp.setText(weatherInfo.getMax_temp()+"¡æ");
		
		return view;
	}
	
	

	class ViewHolder{
		TextView current_date;
		TextView weather_desp;
		ImageView weather_image;
		TextView minTemp;
		TextView maxTemp;
	}

}
