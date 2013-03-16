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
package com.ezio.multiwii.raw;

import com.ezio.multiwii.R;
import com.ezio.multiwii.R.id;
import com.ezio.multiwii.R.layout;
import com.ezio.multiwii.R.string;
import com.ezio.multiwii.app.App;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class RawDataActivity extends Activity {
	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	TextView TVData;
	TextView TVMWInfo;
	View FlashUpdate;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frsky.ProcessSerialData(false);
			app.Frequentjobs();

			displayData();

			if (FlashUpdate.getVisibility() == View.VISIBLE) {
				FlashUpdate.setVisibility(View.INVISIBLE);
			} else {
				FlashUpdate.setVisibility(View.VISIBLE);
			}

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if(app.D)	Log.d(app.TAG, "loop " + this.getClass().getName());
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		app.ForceLanguage();
		app.ConnectionBug();
		setContentView(R.layout.raw_data_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		TVData = (TextView) findViewById(R.id.textViewData);
		TVMWInfo = (TextView) findViewById(R.id.textViewMWInfo);
		FlashUpdate = (View) findViewById(R.id.UpdateFlash);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.RawData));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		if (app.MacAddress.equals("")) {
			TVData.setText(getString(R.string.MacNotSet));
		} else {
			TVData.setText("");
			if (!app.bt.Connected)
				TVData.setText(getString(R.string.InfoNotConnected));
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	private void displayData() {

		String t = new String();
		if (app.mw.BaroPresent == 1)
			t += "BARO ";
		if (app.mw.GPSPresent == 1)
			t += "GPS ";
		if (app.mw.SonarPresent == 1)
			t += "SONAR ";
		if (app.mw.MagPresent == 1)
			t += "MAG ";
		if (app.mw.AccPresent == 1)
			t += "ACC";
		TVMWInfo.setText("MW Version:" + String.valueOf(app.mw.version) + "\n" + "MultiType:" + app.mw.MultiTypeName[app.mw.multiType] + "\n" + "CycleTime:" + String.valueOf(app.mw.cycleTime) + "\n" + "i2cError:" + String.valueOf(app.mw.i2cError) + "\n" + t + "\n MSP_ver:" + String.valueOf(app.mw.MSPversion));

		TVData.setText("");

		log("gx", app.mw.gx);
		log("gy", app.mw.gy);
		log("gz", app.mw.gz);

		log("ax", app.mw.ax);
		log("ay", app.mw.ay);
		log("az", app.mw.az);

		log("magx", app.mw.magx);
		log("magy", app.mw.magy);
		log("magz", app.mw.magz);

		log("baro", app.mw.baro);
		log("alt", app.mw.alt);
		log("vario", app.mw.vario);
		log("head", app.mw.head);

		log("angx", app.mw.angx);
		log("angy", app.mw.angy);
		log("bytevbat", app.mw.bytevbat);
		log("pMeterSum", app.mw.pMeterSum);

		log("nunchukPresent", app.mw.nunchukPresent);
		log("AccPresent", app.mw.AccPresent);
		log("BaroPresent", app.mw.BaroPresent);
		log("MagnetoPresent", app.mw.MagPresent);
		log("GPSPresent", app.mw.GPSPresent);
		log("SonarPresent", app.mw.SonarPresent);

		log("present", app.mw.present);
		log("mode", app.mw.mode);
		log("levelMode", app.mw.levelMode);

		log("byteThrottle_EXPO", app.mw.byteThrottle_EXPO);
		log("byteThrottle_MID", app.mw.byteThrottle_MID);

		log("GPS_fix", app.mw.GPS_fix);
		log("GPS_numSat", app.mw.GPS_numSat);
		log("GPS_update", app.mw.GPS_update);
		log("GPS_directionToHome", app.mw.GPS_directionToHome);
		log("GPS_distanceToHome", app.mw.GPS_distanceToHome);
		log("GPS_altitude", app.mw.GPS_altitude);
		log("GPS_speed", app.mw.GPS_speed);
		log("GPS_latitude", app.mw.GPS_latitude);
		log("GPS_longitude", app.mw.GPS_longitude);
		log("GPS_ground_course", app.mw.GPS_ground_course);

		log("rcThrottle", app.mw.rcThrottle);
		log("rcYaw", app.mw.rcYaw);
		log("rcPitch", app.mw.rcPitch);
		log("rcRoll", app.mw.rcRoll);
		log("rcAUX1", app.mw.rcAUX1);
		log("rcAUX2", app.mw.rcAUX2);
		log("rcAUX3", app.mw.rcAUX3);
		log("rcAUX4", app.mw.rcAUX4);

		log("debug1", app.mw.debug1);
		log("debug2", app.mw.debug2);
		log("debug3", app.mw.debug3);
		log("debug4", app.mw.debug4);

		log("MSP_DEBUGMSG", app.mw.DebugMSG);

		for (int i = 0; i < app.mw.mot.length; i++) {
			log("Motor" + String.valueOf(i + 1), app.mw.mot[i]);
		}

		for (int i = 0; i < app.mw.PIDITEMS; i++) {
			log("P=" + String.valueOf(app.mw.byteP[i]) + " I=" + String.valueOf(app.mw.byteI[i]) + " D", app.mw.byteD[i]);
		}

		log("confSetting", app.mw.confSetting);
		log("multiCapability", app.mw.multiCapability);

		log("rssi", app.mw.rssi);

		log("---", 0);

		log("EZ-Gui Protocol", app.mw.EZGUIProtocol);

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}
		log("App version", getString(R.string.app_name) + " " + app_ver + "." + String.valueOf(app_ver_code));

		log("versionMisMatch", app.mw.versionMisMatch);
		log("1G", app.mw._1G);
		log("CHECKBOXITEMS", app.mw.CHECKBOXITEMS);
		log("PIDITEMS", app.mw.PIDITEMS);
		log("timer1", app.mw.timer1);
		log("timer2", app.mw.timer2);
		log("bt.Connected", String.valueOf(app.mw.bt.Connected));
		log("bt.ConnectionLost", String.valueOf(app.mw.bt.ConnectionLost));
		log("bt.ReconnectTry", String.valueOf(app.mw.bt.ReconnectTry));
		log("AppStartCounter", String.valueOf(app.AppStartCounter));
		log("DonationButtonPressed", String.valueOf(app.DonateButtonPressed));

		for (String s : app.mw.buttonCheckboxLabel) {
			log("buttonCheckboxLabel", s);

		}

	}

	private void log(String co, int wartosc) {
		TVData.append(co + "=" + String.valueOf(wartosc) + "\n");
	}

	private void log(String co, float wartosc) {
		TVData.append(co + "=" + String.valueOf(wartosc) + "\n");
	}

	private void log(String co, String wartosc) {
		TVData.append(co + "=" + (wartosc) + "\n");
	}

	void ShareIt() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		String shareBody = TVMWInfo.getText().toString() + "\n\n" + TVData.getText().toString();

		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "MultiWii Raw Data");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}

	public void SendOnClick(View v) {
		ShareIt();
	}

	public void EnableDebugOnClick(View v) {
		app.D = true;
		// for testing
		if (app.D) {
			Toast.makeText(getApplicationContext(), "Debug version", Toast.LENGTH_LONG).show();
			app.Say("Debug version");
			app.mw.GPS_longitude = 23654111;
			app.mw.GPS_latitude = 488547500;
		}

	}
}
