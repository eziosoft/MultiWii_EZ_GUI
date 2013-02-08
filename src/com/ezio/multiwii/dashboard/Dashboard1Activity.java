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
package com.ezio.multiwii.dashboard;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.ezio.multiwii.App;
import com.ezio.multiwii.R;

public class Dashboard1Activity extends Activity {

	private boolean killme = false;

	App app;
	PitchRollView PRVp;
	PitchRollView PRVr;
	CompassView compass;
	CompassView myCompass;
	PitchRollCircleView pitchRollCircle;
	TextView baro;

	TextView BattVoltageTV;
	TextView PowerSumTV;

	Handler mHandler = new Handler();

	float myAzimuth = 0;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frsky.ProcessSerialData(false);

			myAzimuth = (float) (app.sensors.GetHeading);
			if (app.D) {
				app.mw.angy = app.sensors.GetPitch;
				app.mw.angx = app.sensors.GetRoll;
			}

			PRVp.SetAngle(app.mw.angy);
			PRVr.SetAngle(app.mw.angx);

			pitchRollCircle.SetRollPitch(app.mw.angx, app.mw.angy);

			if (app.MagMode == 1) {
				compass.SetHeading(-app.mw.head);
				compass.SetText("");

			} else {
				compass.SetHeading(myAzimuth - app.mw.head);
				compass.SetText("FRONT");
			}

			myCompass.SetHeading(myAzimuth);

			baro.setText(String.format("%.2f", app.mw.alt));
			BattVoltageTV.setText(String.valueOf((float) (app.mw.bytevbat / 10.0)));
			PowerSumTV.setText(String.valueOf(app.mw.pMeterSum));

			app.Frequentjobs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard1_layout);

		app = (App) getApplication();

		PRVp = (PitchRollView) findViewById(R.id.PRVp);
		PRVp.SetColor(Color.GREEN);

		PRVr = (PitchRollView) findViewById(R.id.PRVr);
		PRVr.SetColor(Color.GREEN);

		pitchRollCircle = (PitchRollCircleView) findViewById(R.id.PitchRollCircle);
		pitchRollCircle.SetColor(Color.GREEN);

		compass = (CompassView) findViewById(R.id.Mag);
		compass.SetColor(Color.GREEN, Color.YELLOW);

		myCompass = (CompassView) findViewById(R.id.CompassView02);
		myCompass.SetColor(Color.GRAY, Color.LTGRAY);
		myCompass.SetText("N");

		baro = (TextView) findViewById(R.id.textViewBaro);
		BattVoltageTV = (TextView) findViewById(R.id.TextViewBattVoltage);
		PowerSumTV = (TextView) findViewById(R.id.TextViewPowerSum);

	}

	@Override
	protected void onPause() {
		super.onPause();
		killme = true;
		mHandler.removeCallbacks(update);
		app.sensors.stop();

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		app.sensors.start();

		app.Say(getString(R.string.PitchRoll));

	}

}
