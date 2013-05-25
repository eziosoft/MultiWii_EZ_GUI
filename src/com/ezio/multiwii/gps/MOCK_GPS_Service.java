package com.ezio.multiwii.gps;

import java.util.Random;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.ezio.multiwii.app.App;
import com.google.android.maps.GeoPoint;

public class MOCK_GPS_Service extends Service {

	private boolean killme = false;
	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			Log.d("aaaService", "Service running...");
			if (app.D) {
				app.mw.GPS_latitude += random.nextInt(200) - 50;// for
				// simulation
				app.mw.GPS_longitude += random.nextInt(100) - 50;// for
				// simulation
				app.mw.GPS_fix = 1;
				// app.mw.alt++;

				app.mw.head++;
			}

			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);

			app.sensors.setMOCKLocation(app.mw.GPS_latitude / Math.pow(10, 7), app.mw.GPS_longitude / Math.pow(10, 7), app.mw.alt, app.mw.head);

			 app.Frequentjobs();

			// app.mw.SendRequest();
			// app.mw.SendRequestMSP_RAW_GPS();
			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, 1000);

		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		app = (App) getApplication();
		if (app.sensors.isMockEnabled()) {
			app.sensors.initMOCKLocation();
			mHandler.postDelayed(update, app.RefreshRate);
		}else
		{
			stopSelf();
		}

	}
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(null);
		killme = true;
		app.sensors.ClearMOCKLocation();
		super.onDestroy();
		
	}

}
