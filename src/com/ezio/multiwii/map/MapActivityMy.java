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
import android.view.WindowManager;

import com.ezio.multiwii.App;
import com.ezio.multiwii.R;
import com.ezio.multiwii.R.string;
import com.ezio.multiwii.notUsed.HttpCli;
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
	MapCirclesOverlay circles = new MapCirclesOverlay();

	private boolean killme = false;

	private GeoPoint GYou = new GeoPoint(0, 0);

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			if (app.GPSfromNet) {
				app.mw.GPS_latitude += random.nextInt(200) - 50;// for
				// simulation
				app.mw.GPS_longitude += random.nextInt(100) - 50;// for
				// simulation
			}

			GeoPoint g = new GeoPoint(app.mw.GPS_latitude / 10,
					app.mw.GPS_longitude / 10);
			CenterLocation(g);

			copter.Set(g, map((int) app.mw.head, 180, -180, 0, 360),
					app.mw.HomePosition, app.mw.bytevbat, app.mw.pMeterSum,
					app.mw.intPowerTrigger);
			circles.Set(app.mw.HomePosition, GYou);
			mapView.postInvalidate();

			app.Frequentjobs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, 1000);

			// Log.d("aaa", "aaa");

		}
	};

	int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		app = (App) getApplication();

		if (app.UseMapPublicAPI) {
			mapView = new MapView(this,
					app.MapAPIKeyPublic);
		} else {

			mapView = new MapView(this,
					app.MapAPIKeyDebug);
		}

		mapView.setClickable(true);
		mapView.setBuiltInZoomControls(true);

		setContentView(mapView);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		copter = new CopterOverlay(getApplicationContext());

		mapView.setBuiltInZoomControls(true);
		myMapController = mapView.getController();
		mapView.setSatellite(true);
		myMapController.setZoom(mapView.getMaxZoomLevel());

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		if (!app.GPSfromNet)
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
		mHandler.postDelayed(update, app.REFRESH_RATE);

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
		GYou = new GeoPoint((int) (location.getLatitude() * 1e6),
				(int) (location.getLongitude() * 1e6));

	
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
