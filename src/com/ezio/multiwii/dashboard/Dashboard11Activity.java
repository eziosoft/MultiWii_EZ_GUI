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

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.dashboard.dashboard3.HeadingView;

public class Dashboard11Activity extends SherlockActivity {

	private boolean killme = false;

	App app;
	PitchRollView PRVp;
	PitchRollView PRVr;
	HeadingView headingView;

	LinearLayout boxesLayout;

	TextView TVInfo;

	Handler mHandler = new Handler();

	float myAzimuth = 0;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);

			myAzimuth = (float) (app.sensors.Heading);
			if (app.D) {
				app.mw.angy = app.sensors.Pitch;
				app.mw.angx = app.sensors.Roll;
			}

			PRVp.Set(app.mw.angy);
			PRVr.Set(app.mw.angx);
			headingView.Set(app.mw.head);

			setActiveBoxes();

			String s = "";

			s += getString(R.string.Altitude) + ":" + String.valueOf(app.mw.alt) + "\n";
			s += getString(R.string.Battery) + ":" + String.valueOf((float) (app.mw.bytevbat / 10.0)) + "\n";
			s += getString(R.string.Satellites) + ":" + String.valueOf(app.mw.GPS_numSat) + "\n";
			s += getString(R.string.GPS_speed) + ":" + String.valueOf(app.mw.GPS_speed) + "\n";
			s += getString(R.string.CycleTime) + ":" + String.valueOf(app.mw.cycleTime) + "\n";
			TVInfo.setText(s);

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
		setContentView(R.layout.dashboard12);

		app = (App) getApplication();

		PRVp = (PitchRollView) findViewById(R.id.PitchView);
		PRVp.arrow = true;
		PRVp.init();

		PRVr = (PitchRollView) findViewById(R.id.RollView);

		TVInfo = (TextView) findViewById(R.id.textViewInfoD1);

		headingView = (HeadingView) findViewById(R.id.headingView1);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		getSupportActionBar().setTitle(getString(R.string.Dashboard1));

		getSupportActionBar().hide();

	}

	Button boxes[];

	void createBoxes() {
		boxes = new Button[app.mw.BoxNames.length];

		boxesLayout = (LinearLayout) findViewById(R.id.LinearLayoutBoxes);

		for (int i = 0; i < app.mw.BoxNames.length; i++) {
			boxes[i] = new Button(getApplicationContext());
			boxes[i].setText(app.mw.BoxNames[i]);
			// boxes[i].setBackgroundResource(R.drawable.button_0);

		}

		for (int i = 0; i < app.mw.BoxNames.length; i++) {
			boxesLayout.addView(boxes[i]);
		}

	}

	void setActiveBoxes() {
		for (int i = 0; i < app.mw.BoxNames.length; i++) {
			if (app.mw.ActiveModes[i]) {
				boxes[i].setBackgroundResource(R.drawable.button_0);
			} else {
				boxes[i].setBackgroundResource(R.drawable.button_01);
			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		killme = true;
		mHandler.removeCallbacks(update);
		// app.sensors.stopMagACC();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		app.Say(getString(R.string.Dashboard1));
		// app.sensors.startMagACC();

		createBoxes();

	}

}
