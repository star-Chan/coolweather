package com.coolweather.app.receiver;



import com.coolweather.app.R;
import com.coolweather.app.util.NetWorkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class NetWorkStatusReceiver extends BroadcastReceiver {

	private static int isNetWorkConnected;
//	private static boolean isNetWorkConnected1;
//	private static boolean isWifiConnected;
//	private static boolean isMobileConnected;
	 public NetWorkStatusReceiver() {
		 
	  }
	 
	  @Override
	  public void onReceive(Context context, Intent intent) {
	    String action = intent.getAction();
	    if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//	      Toast.makeText(context, "network changed", Toast.LENGTH_LONG).show();
	      View view=LayoutInflater.from(context).inflate(R.layout.choose_area, null);
	      
	      isNetWorkConnected = NetWorkUtils.getAPNType(context);
	      
	      if(isNetWorkConnected==0){
	        	Toast.makeText(context, "当前没有网络关闭了", Toast.LENGTH_SHORT).show();
	        }
	        if(isNetWorkConnected==1){
	        	Toast.makeText(context, "打开了wifi网络", Toast.LENGTH_SHORT).show();
	        }
	        if(isNetWorkConnected==2){
	        	Toast.makeText(context, "打开了3G网络", Toast.LENGTH_SHORT).show();
	        	
	        }
	        if(isNetWorkConnected==3){
	        	Toast.makeText(context, "打开了2G网络", Toast.LENGTH_SHORT).show();
	        }
	     
	    }
	  }

}
