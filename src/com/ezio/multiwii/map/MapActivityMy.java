/*  MultiWii EZ-GUI
    Copyright (C) <2012>  Bartosz Szczygiel (eziosoft)

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.ezio.multiwii.map;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.Functions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapActivityMy extends MapActivity {

	// keys
	// String apiKey = "0AxI9Dd4w6Y_4upkSvwAfQDK1f8fXpsnCx07vyg"; // debug
	// String apiKey = "0AxI9Dd4w6Y-ERQuGVB0WKB4x4iZe3uD9HVpWYQ";

	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	MapView mapView;
	private MapController myMapController;
	// MyLocationOverlay myLocationOverlay;
	CopterOverlay copter;
	MapCirclesOverlay circles;

	private boolean killme = false;

	private long centerStep = 0;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);

			if (app.D) {
				app.mw.GPS_latitude += random.nextInt(200) - 50;// for
				// simulation
				app.mw.GPS_longitude += random.nextInt(100) - 50;// for
				// simulation
				app.mw.GPS_fix = 1;

				app.mw.head++;
			}

			GeoPoint g = new GeoPoint(app.mw.GPS_latitude / 10, app.mw.GPS_longitude / 10);

			if (centerStep < System.currentTimeMillis()) {
				if (app.mw.GPS_fix == 1 || app.mw.GPS_numSat > 0) {
					CenterLocation(g);
				} else {
					CenterLocation(app.sensors.geopointOnlineMapCurrentPosition);
				}
				centerStep = System.currentTimeMillis() + app.MapCenterPeriod * 1000;
			}

			String state = "";
			for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
				if (app.mw.ActiveModes[i]) {
					state += " " + app.mw.buttonCheckboxLabel[i];
				}
			}

			// float gforce = (float) Math.sqrt(app.mw.ax * app.mw.ax +
			// app.mw.ay * app.mw.ay + app.mw.az * app.mw.az) / app.mw._1G;

			copter.Set(g, app.mw.Waypoints[0].getGeoPoint(), app.mw.Waypoints[16].getGeoPoint(), app.mw.GPS_numSat, app.mw.GPS_distanceToHome, app.mw.GPS_directionToHome, app.mw.GPS_speed, app.mw.GPS_altitude, app.mw.alt, app.mw.GPS_latitude, app.mw.GPS_longitude, app.mw.angy, app.mw.angx, Functions.map((int) app.mw.head, 180, -180, 0, 360), state, app.mw.bytevbat, app.mw.pMeterSum, app.mw.intPowerTrigger, app.frskyProtocol.TxRSSI, app.frskyProtocol.RxRSSI);
			circles.Set(app.mw.Waypoints[0].getGeoPoint(), app.sensors.getNextPredictedLocationOnlineMap());
			mapView.postInvalidate();

			app.Frequentjobs();

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		app = (App) getApplication();

		if (!app.D) {
			mapView = new MapView(this, app.MapAPIKeyPublic);
		} else {

			mapView = new MapView(this, app.MapAPIKeyDebug);
		}

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		setContentView(mapView);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		copter = new CopterOverlay(getApplicationContext());
		circles = new MapCirclesOverlay(getApplicationContext());

		mapView.setBuiltInZoomControls(true);
		myMapController = mapView.getController();
		mapView.setSatellite(true);
		myMapController.setZoom((int) app.MapZoomLevel);

		mapView.getOverlays().add(copter);
		mapView.getOverlays().add(circles);

	}

	private void CenterLocation(GeoPoint centerGeoPoint) {
		myMapController.animateTo(centerGeoPoint);
	};

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		app.Say(getString(R.string.Map));

	}

	@Override
	protected void onPause() {
		super.onPause();
		killme = true;
		mHandler.removeCallbacks(null);
		app.MapZoomLevel = mapView.getZoomLevel();
		app.SaveSettings(true);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
