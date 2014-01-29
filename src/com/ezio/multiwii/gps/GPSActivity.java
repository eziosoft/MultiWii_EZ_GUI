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
package com.ezio.multiwii.gps;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class GPSActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	TextView GPS_distanceToHomeTV;
	TextView GPS_directionToHomeTV;
	TextView GPS_numSatTV;
	TextView GPS_fixTV;
	TextView GPS_updateTV;

	TextView GPS_altitudeTV;
	TextView GPS_speedTV;
	TextView GPS_latitudeTV;
	TextView GPS_longitudeTV;

	TextView PhoneLatitudeTV;
	TextView PhoneLongtitudeTV;
	TextView PhoneAltitudeTV;
	TextView PhoneSpeedTV;
	TextView PhoneNumSatTV;
	TextView DeclinationTV;
	TextView PhoneAccuracyTV;

	TextView FollowMeInfoTV;

	TextView PhoneHeadingTV;
	TextView HeadingTV;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);

			float lat = (float) (app.mw.GPS_latitude / Math.pow(10, 7));
			float lon = (float) (app.mw.GPS_longitude / Math.pow(10, 7));

			GPS_distanceToHomeTV.setText(String.valueOf(app.mw.GPS_distanceToHome));
			GPS_directionToHomeTV.setText(String.valueOf(app.mw.GPS_directionToHome));

			switch (app.mw.GPS_numSat) {
			case 0:
				GPS_numSatTV.setBackgroundColor(Color.RED);
				break;
			case 1:
				GPS_numSatTV.setBackgroundColor(Color.RED);
				break;
			case 2:
				GPS_numSatTV.setBackgroundColor(Color.RED);
				break;
			case 3:
				GPS_numSatTV.setBackgroundColor(Color.YELLOW);
				break;
			case 4:
				GPS_numSatTV.setBackgroundColor(Color.YELLOW);
			default:
				GPS_numSatTV.setBackgroundColor(Color.GREEN);
				break;
			}

			GPS_numSatTV.setText(String.valueOf(app.mw.GPS_numSat));

			if (app.mw.GPS_fix == 0) {
				GPS_fixTV.setText(getString(R.string.Searching));
				GPS_fixTV.setBackgroundColor(Color.RED);
			} else {
				GPS_fixTV.setText(getString(R.string.GPSFixOK));
				GPS_fixTV.setBackgroundColor(Color.GREEN);
			}

			if (app.mw.GPS_update % 2 == 0) {
				// GPS_updateTV.setVisibility(View.VISIBLE);
				GPS_updateTV.setBackgroundResource(R.drawable.green_light);
			} else {
				// GPS_updateTV.setVisibility(View.INVISIBLE);
				GPS_updateTV.setBackgroundResource(R.drawable.red_light);
			}

			GPS_altitudeTV.setText(String.valueOf(app.mw.GPS_altitude));
			GPS_speedTV.setText(String.valueOf(app.mw.GPS_speed));

			GPS_latitudeTV.setText(String.valueOf(lat));
			GPS_longitudeTV.setText(String.valueOf(lon));

			PhoneLatitudeTV.setText(String.valueOf((float) app.sensors.PhoneLatitude));
			PhoneLongtitudeTV.setText(String.valueOf((float) app.sensors.PhoneLongitude));
			PhoneAltitudeTV.setText(String.valueOf((int) app.sensors.PhoneAltitude));
			PhoneSpeedTV.setText(String.valueOf(app.sensors.PhoneSpeed));
			PhoneNumSatTV.setText(String.valueOf(app.sensors.PhoneNumSat));
			PhoneAccuracyTV.setText(String.valueOf(app.sensors.PhoneAccuracy));
			DeclinationTV.setText(String.valueOf(app.sensors.Declination));
			PhoneHeadingTV.setText(String.valueOf(app.sensors.Heading));
			HeadingTV.setText(String.valueOf(app.mw.head));

			app.Frequentjobs();

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			Log.d(app.TAG, "loop " + this.getClass().getName());

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.gps_layout);

		getSupportActionBar().setTitle(getString(R.string.GPSInfo));

		app = (App) getApplication();

		GPS_distanceToHomeTV = (TextView) findViewById(R.id.TextViewGPS_distanceToHome);
		GPS_directionToHomeTV = (TextView) findViewById(R.id.TextViewGPS_directionToHome);
		GPS_numSatTV = (TextView) findViewById(R.id.TextViewGPS_numSat);
		GPS_fixTV = (TextView) findViewById(R.id.TextViewGPS_fix);
		GPS_updateTV = (TextView) findViewById(R.id.TextViewGPS_update);

		GPS_altitudeTV = (TextView) findViewById(R.id.TextViewGPS_altitude);
		GPS_speedTV = (TextView) findViewById(R.id.TextViewGPS_speed);
		GPS_latitudeTV = (TextView) findViewById(R.id.TextViewGPS_latitude);
		GPS_longitudeTV = (TextView) findViewById(R.id.TextViewGPS_longitude);

		PhoneLatitudeTV = (TextView) findViewById(R.id.textViewPhoneLatitude);
		PhoneLongtitudeTV = (TextView) findViewById(R.id.textViewPhoneLongitude);
		PhoneAltitudeTV = (TextView) findViewById(R.id.TextViewPhoneAltitude);
		PhoneSpeedTV = (TextView) findViewById(R.id.textViewPhoneSpeed);
		PhoneNumSatTV = (TextView) findViewById(R.id.textViewPhoneNumSat);
		PhoneAccuracyTV = (TextView) findViewById(R.id.textViewPhoneAccuracy);

		DeclinationTV = (TextView) findViewById(R.id.textViewDeclination);
		FollowMeInfoTV = (TextView) findViewById(R.id.textViewFollowMeInfo);
		PhoneHeadingTV = (TextView) findViewById(R.id.TextViewPhoneHead);
		HeadingTV = (TextView) findViewById(R.id.TextViewHeading);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(update);
		killme = true;
		app.sensors.stopMagACC();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();

		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		app.Say(getString(R.string.GPS));
		app.sensors.startMagACC();

	}

}
