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

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.ezio.multiwii.App;
import com.ezio.multiwii.R;
import com.ezio.multiwii.helpers.Functions;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class MapActivityMy extends MapActivity implements LocationListener {

	// keys
	// String apiKey = "0AxI9Dd4w6Y_4upkSvwAfQDK1f8fXpsnCx07vyg"; // debug
	// String apiKey = "0AxI9Dd4w6Y-ERQuGVB0WKB4x4iZe3uD9HVpWYQ";

	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	MapView mapView;
	private MapController myMapController;
	// MyLocationOverlay myLocationOverlay;
	private LocationManager locationManager;
	private String provider;
	CopterOverlay copter;
	MapCirclesOverlay circles;

	private boolean killme = false;

	private GeoPoint GYou = new GeoPoint(0, 0);

	private int centerStep = 0;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frsky.ProcessSerialData(false);

			if (app.D) {
				app.mw.GPS_latitude += random.nextInt(200) - 50;// for
				// simulation
				app.mw.GPS_longitude += random.nextInt(100) - 50;// for
				// simulation
				app.mw.GPS_fix = 1;

				app.mw.head++;
			}

			GeoPoint g = new GeoPoint(app.mw.GPS_latitude / 10, app.mw.GPS_longitude / 10);

			if (centerStep >= 3) {
				if (app.mw.GPS_fix == 1 || app.mw.GPS_numSat > 0) {
					CenterLocation(g);
				} else {
					CenterLocation(GYou);
				}
				centerStep = 0;
			}
			centerStep++;

			String state = "";
			for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
				if (app.mw.ActiveModes[i]) {
					state += " " + app.mw.buttonCheckboxLabel[i];
				}
			}

			float gforce = (float) Math.sqrt(app.mw.ax * app.mw.ax + app.mw.ay * app.mw.ay + app.mw.az * app.mw.az) / app.mw._1G;
			// public void Set(GeoPoint copter, GeoPoint home, int satNum, float
			// distanceToHome, float directionToHome, float speed, float
			// gpsAltitude, float altitude, float lat, float lon, float pitch,
			// float roll, float azimuth, float gforce, String state, int vbat,
			// int powerSum, int powerTrigger, int txRSSI, int rxRSSI) {

			copter.Set(g, app.mw.Waypoints[0].getGeoPoint(), app.mw.Waypoints[16].getGeoPoint(), app.mw.GPS_numSat, app.mw.GPS_distanceToHome, app.mw.GPS_directionToHome, app.mw.GPS_speed, app.mw.GPS_altitude, app.mw.alt, app.mw.GPS_latitude, app.mw.GPS_longitude, app.mw.angy, app.mw.angx, Functions.map((int) app.mw.head, 180, -180, 0, 360), gforce, state, app.mw.bytevbat, app.mw.pMeterSum, app.mw.intPowerTrigger, app.frsky.TxRSSI, app.frsky.RxRSSI);
			mapView.postInvalidate();

			app.Frequentjobs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, 1000);

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
		myMapController.setZoom(mapView.getMaxZoomLevel());

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		if (!app.D)
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, false);
		// Location location = locationManager.getLastKnownLocation(provider);
		// GYou = new GeoPoint((int) (location.getLatitude() * 1e6), (int)
		// (location.getLongitude() * 1e6));

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
		locationManager.requestLocationUpdates(provider, 1000, 1, this);
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		app.Say(getString(R.string.Map));

	}

	@Override
	protected void onPause() {
		super.onPause();
		// myLocationOverlay.disableMyLocation();
		locationManager.removeUpdates(this);
		killme = true;
		mHandler.removeCallbacks(null);

	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(Location location) {
		GYou = new GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));

		// Log.d("aaa", String.valueOf(location.getLatitude()));

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

}
