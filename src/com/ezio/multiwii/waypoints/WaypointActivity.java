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

package com.ezio.multiwii.waypoints;

import java.util.Iterator;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.GpsStatus.Listener;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ezio.multiwii.App;
import com.ezio.multiwii.R;

public class WaypointActivity extends Activity implements LocationListener {

	int PhoneNumSat = 0;
	double PhoneLatitude = 0;
	double PhoneLongitude = 0;
	double PhoneAltitude = 0;
	double PhoneSpeed = 0;
	int PhoneFix = 0;
	float PhoneAccuracy = 0;
	double Declination = 0;

	TextView TVData;
	TextView TVMWInfo;

	private LocationManager locationManager;
	private String provider;

	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frsky.ProcessSerialData(false);
			app.Frequentjobs();

			TVData.setText("");
			displayWPs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		app.ForceLanguage();
		app.ConnectionBug();
		setContentView(R.layout.waypoint_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		TVData = (TextView) findViewById(R.id.textViewData);
		TVMWInfo = (TextView) findViewById(R.id.textViewMWInfo);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		if (!app.GPSfromNet)
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, false);
		// Location location = locationManager.getLastKnownLocation(provider);
		locationManager.addGpsStatusListener(new Listener() {

			@Override
			public void onGpsStatusChanged(int event) {
				if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
					GpsStatus status = locationManager.getGpsStatus(null);
					Iterable<GpsSatellite> sats = status.getSatellites();
					Iterator<GpsSatellite> it = sats.iterator();

					PhoneNumSat = 0;
					while (it.hasNext()) {

						GpsSatellite oSat = (GpsSatellite) it.next();
						if (oSat.usedInFix())
							PhoneNumSat++;
					}
				}
				if (event == GpsStatus.GPS_EVENT_FIRST_FIX)
					PhoneFix = 1;
			}
		});

	}

	public void GetWPOnClick(View v) {
		app.mw.SendRequestGetWayPoint(0);
	}

	public void SetWPOnClick(View v) {

		app.mw.SendRequestMSP_SET_WP(new Waypoint(0, 1, 2, 3, 4));
	}

	void displayWPs() {
		for (Waypoint w : app.mw.Waypoints) {
			TVData.append("No:" + String.valueOf(w.Number) + " Lat:"
					+ String.valueOf(w.Lat) + " Lon:" + String.valueOf(w.Lon)
					+ " Alt:" + String.valueOf(w.Alt) + " NavFlag:"
					+ String.valueOf(w.NavFlag) + "\n");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		// app.Say(getString(R.string.Motors));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		locationManager.requestLocationUpdates(provider, 400, 1, this);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
		locationManager.removeUpdates(this);
	}

	@Override
	public void onLocationChanged(Location location) {
		PhoneLatitude = location.getLatitude();
		PhoneLongitude = location.getLongitude();
		PhoneAltitude = location.getAltitude();
		PhoneSpeed = location.getSpeed() * 100f;
		PhoneAccuracy = location.getAccuracy() * 100f;

		String s = "Lat: " + String.valueOf(PhoneLatitude) + " Lon:"
				+ String.valueOf(PhoneLongitude);
		s += "\nLat: " + String.valueOf((int) (PhoneLatitude * 1e7)) + " Lon:"
				+ String.valueOf((int) (PhoneLongitude * 1e7));
		TVMWInfo.setText(s);
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
