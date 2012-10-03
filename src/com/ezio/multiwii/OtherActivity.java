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

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class OtherActivity extends SherlockActivity {
	App			app;
	// Handler mHandler = new Handler();

	EditText	PowerMeterAlarm;

	public void MagCalibrationOnClick(View v) {
		app.mw.SendRequestMagCalibration();
	}

	public void AccCalibrationOnClick(View v) {
		app.mw.SendRequestAccCalibration();
	}

	// private Runnable update = new Runnable() {
	// @Override
	// public void run() {
	//
	// {
	// app.mw.ProcessSerialData(app.loggingON);
	//
	// }
	//
	// mHandler.postDelayed(update, App.REFRESH_RATE);
	//
	// }
	// };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_layout);

		app = (App) getApplication();

		PowerMeterAlarm = (EditText) findViewById(R.id.editTextPowerMeterAlarm);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// mHandler.removeCallbacks(update);

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		app.ForceLanguage();
		// mHandler.postDelayed(update, App.REFRESH_RATE);
		app.Say(getString(R.string.Other));

		app.mw.SendRequestGetMisc();
		try {
			Thread.sleep(300);
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		PowerMeterAlarm.setText(String.valueOf(app.mw.intPowerTrigger));

	}

	public void WritePowerMeterAlarmOnClick(View v) {
		app.mw.SendRequestSetandSaveMISC(Integer.parseInt(PowerMeterAlarm.getText().toString()));
	}

}
