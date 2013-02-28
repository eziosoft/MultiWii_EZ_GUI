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

import com.ezio.multiwii.App;
import com.ezio.multiwii.R;
import com.ezio.multiwii.R.string;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

public class Dashboard2Activity extends Activity {
	App app;
	Handler mHandler = new Handler();
	Dashboard2View v;
	private boolean killme = false;

	private Runnable update = new Runnable() {
		@Override
		public void run() {
			app.mw.ProcessSerialData(app.loggingON);
			app.frsky.ProcessSerialData(false);

			String state = "";
			for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
				if (app.mw.ActiveModes[i]) {
					state += " " + app.mw.buttonCheckboxLabel[i];
				}
			}

			int a = 1; //used for reverce roll
			if (app.ReverseRoll) {
				a = -1;
			}
			
			float gforce = (float) Math.sqrt(app.mw.ax * app.mw.ax + app.mw.ay * app.mw.ay + app.mw.az * app.mw.az) / app.mw._1G;
			v.Set(app.mw.GPS_numSat, app.mw.GPS_distanceToHome, app.mw.GPS_directionToHome, app.mw.GPS_speed, app.mw.GPS_altitude, app.mw.alt, app.mw.GPS_latitude, app.mw.GPS_longitude, app.mw.angy, a * app.mw.angx, app.mw.head, gforce, state, app.mw.bytevbat, app.mw.pMeterSum, app.mw.intPowerTrigger, app.frsky.TxRSSI, app.frsky.RxRSSI);

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
		app = (App) getApplication();

		v = new Dashboard2View(getApplicationContext());
		setContentView(v);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(update);
		killme = true;
	}

	@Override
	protected void onResume() {
		app.ForceLanguage();
		super.onResume();

		app.ConnectionBug();

		app.Say(getString(R.string.dashboard2));
		mHandler.postDelayed(update, app.RefreshRate);
		killme = false;

	}
}
