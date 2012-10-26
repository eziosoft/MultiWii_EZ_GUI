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
package com.ezio.multiwii;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

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

			Log.d("aaa", String.valueOf(app.mw.ax));

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
		TVMWInfo.setText("MW Version:" + String.valueOf(app.mw.version) + "\n"
				+ "MultiType:" + app.mw.MultiTypeName[app.mw.multiType] + "\n"
				+ "CycleTime:" + String.valueOf(app.mw.cycleTime) + "\n"
				+ "i2cError:" + String.valueOf(app.mw.i2cError));

		TVData.setText("");
		// log("version", app.mw.version);
		// log("multiType",
		// app.mw.MultiTypeName[app.mw.multiType] + "("
		// + String.valueOf(app.mw.multiType) + ")");
		//
		// log("cycleTime", app.mw.cycleTime);
		// log("i2cError", app.mw.i2cError);

		log("EZ-GUI Protocol", app.mw.EZGUIProtocol);
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
			log("P=" + String.valueOf(app.mw.byteP[i]) + " I="
					+ String.valueOf(app.mw.byteI[i]) + " D", app.mw.byteD[i]);
		}

		log("confSetting", app.mw.confSetting);
		log("multiCapability",app.mw.multiCapability);
		log("versionMisMatch", app.mw.versionMisMatch);

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

}
