package com.lccweather.app.receiver;

import com.lccweather.app.service.AutoUpateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUpdateReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context,AutoUpateService.class);
		context.startActivity(i);
		
	}

}
