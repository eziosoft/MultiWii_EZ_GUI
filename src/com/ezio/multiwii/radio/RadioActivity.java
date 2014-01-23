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

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class RadioActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	StickView SV1;
	StickView SV2;
	ProgressBar pb1;
	ProgressBar pb2;
	ProgressBar pb3;
	ProgressBar pb4;
	ProgressBar pb5;
	ProgressBar pb6;
	ProgressBar pb7;
	ProgressBar pb8;

	TextView TV1;
	TextView TV2;
	TextView TV3;
	TextView TV4;
	TextView TV5;
	TextView TV6;
	TextView TV7;
	TextView TV8;

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

			pb1.setProgress((int) (app.mw.rcThrottle - 1000));
			pb2.setProgress((int) (app.mw.rcPitch - 1000));
			pb3.setProgress((int) (app.mw.rcRoll - 1000));
			pb4.setProgress((int) (app.mw.rcYaw - 1000));
			pb5.setProgress((int) (app.mw.rcAUX1 - 1000));
			pb6.setProgress((int) (app.mw.rcAUX2 - 1000));
			pb7.setProgress((int) (app.mw.rcAUX3 - 1000));
			pb8.setProgress((int) (app.mw.rcAUX4 - 1000));

			TV1.setText(String.valueOf(app.mw.rcThrottle));
			TV2.setText(String.valueOf(app.mw.rcPitch));
			TV3.setText(String.valueOf(app.mw.rcRoll));
			TV4.setText(String.valueOf(app.mw.rcYaw));
			TV5.setText(String.valueOf(app.mw.rcAUX1));
			TV6.setText(String.valueOf(app.mw.rcAUX2));
			TV7.setText(String.valueOf(app.mw.rcAUX3));
			TV8.setText(String.valueOf(app.mw.rcAUX4));

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
		setContentView(R.layout.radio_layout);

		app = (App) getApplication();

		SV1 = (StickView) findViewById(R.id.StickView1);
		SV2 = (StickView) findViewById(R.id.StickView2);

		pb1 = (ProgressBar) findViewById(R.id.ProgressBar01);
		pb2 = (ProgressBar) findViewById(R.id.ProgressBar02);
		pb3 = (ProgressBar) findViewById(R.id.ProgressBar03);
		pb4 = (ProgressBar) findViewById(R.id.ProgressBar04);
		pb5 = (ProgressBar) findViewById(R.id.ProgressBar05);
		pb6 = (ProgressBar) findViewById(R.id.ProgressBar06);
		pb7 = (ProgressBar) findViewById(R.id.ProgressBar07);
		pb8 = (ProgressBar) findViewById(R.id.ProgressBar08);

		TV1 = (TextView) findViewById(R.id.TextView01);
		TV2 = (TextView) findViewById(R.id.TextView02);
		TV3 = (TextView) findViewById(R.id.TextView03);
		TV4 = (TextView) findViewById(R.id.TextView04);
		TV5 = (TextView) findViewById(R.id.TextView05);
		TV6 = (TextView) findViewById(R.id.TextView06);
		TV7 = (TextView) findViewById(R.id.TextView07);
		TV8 = (TextView) findViewById(R.id.TextView08);

		app.Say(getString(R.string.RadioMode) + " " + String.valueOf(app.RadioMode));
		getSupportActionBar().hide();

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

		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
	}

}
