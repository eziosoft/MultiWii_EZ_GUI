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
package com.ezio.multiwii.radio;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.App;
import com.ezio.multiwii.R;

public class RadioActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	StickView SV1;
	StickView SV2;
	ProgressBar pb1;
	ProgressBar pb2;
	ProgressBar pb3;
	ProgressBar pb4;

	TextView TextViewRadioInfo;

	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			if (app.RadioMode == 2) {
				SV1.SetPosition(app.mw.rcYaw, app.mw.rcThrottle);
				SV2.SetPosition(app.mw.rcRoll, app.mw.rcPitch);
			}

			if (app.RadioMode == 1) {
				SV1.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
				SV2.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
			}

			pb1.setProgress((int) (app.mw.rcAUX1 - 1000));
			pb2.setProgress((int) (app.mw.rcAUX2 - 1000));
			pb3.setProgress((int) (app.mw.rcAUX3 - 1000));
			pb4.setProgress((int) (app.mw.rcAUX4 - 1000));

			String a = "Throttle:" + String.valueOf(app.mw.rcThrottle);
			a += "\nYaw:" + String.valueOf(app.mw.rcYaw);

			a += "\nRoll:" + String.valueOf(app.mw.rcRoll);
			a += "\nPitch:" + String.valueOf(app.mw.rcPitch);

			a += "\nAUX1:" + String.valueOf(app.mw.rcAUX1);
			a += "\nAUX2:" + String.valueOf(app.mw.rcAUX2);
			a += "\nAUX3:" + String.valueOf(app.mw.rcAUX3);
			a += "\nAUX4:" + String.valueOf(app.mw.rcAUX4);

			TextViewRadioInfo.setText(a);

			app.Frequentjobs();

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

		}
	};

	private void SetTextViewColorOnOFF(boolean state, TextView TV, String title) {
		if (state) {
			TV.setBackgroundColor(getResources().getColor(R.color.Green));
			TV.setTextColor(Color.BLACK);
			TV.setText(title);
		} else {
			TV.setBackgroundColor(getResources().getColor(R.color.Red));
			TV.setTextColor(Color.WHITE);
			TV.setText(title);
		}
	}

	private void SetTextViewColorOnOFF(boolean state, TextView TV) {
		if (state) {
			TV.setBackgroundColor(getResources().getColor(R.color.Green));
			TV.setTextColor(Color.BLACK);

		} else {
			TV.setBackgroundColor(getResources().getColor(R.color.Red));
			TV.setTextColor(Color.WHITE);

		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.radio_layout);

		app = (App) getApplication();

		SV1 = (StickView) findViewById(R.id.StickView1);
		SV2 = (StickView) findViewById(R.id.StickView2);

		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		pb2 = (ProgressBar) findViewById(R.id.progressBar2);
		pb3 = (ProgressBar) findViewById(R.id.progressBar3);
		pb4 = (ProgressBar) findViewById(R.id.progressBar4);

		TextViewRadioInfo = (TextView) findViewById(R.id.textViewRadioInfo);

		app.Say(getString(R.string.RadioMode) + " "
				+ String.valueOf(app.RadioMode));

	}

	@Override
	protected void onPause() {
		super.onPause();
		killme = true;
		mHandler.removeCallbacks(update);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();

		// if (app.Protocol > 20) {
		// AccActiveTV.setVisibility(View.GONE);
		// BaroActiveTV.setVisibility(View.GONE);
		// GpsActiveTV.setVisibility(View.GONE);
		// MagnetoActiveTV.setVisibility(View.GONE);
		//
		// }
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
	}

}
