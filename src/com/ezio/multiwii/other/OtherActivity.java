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
package com.ezio.multiwii.other;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.R.id;
import com.ezio.multiwii.R.layout;
import com.ezio.multiwii.R.string;
import com.ezio.multiwii.app.App;

public class OtherActivity extends SherlockActivity {
	App app;
	// Handler mHandler = new Handler();

	EditText EditTextPowerMeterAlarm;
	EditText EditTextSelectSettings;
	EditText EditTextSetSerialBoudRate;

	Button ButtonRxBIND;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_layout);

		app = (App) getApplication();

		EditTextPowerMeterAlarm = (EditText) findViewById(R.id.editTextPowerMeterAlarm);
		EditTextSelectSettings = (EditText) findViewById(R.id.editTextSelectSettingNumber);
		ButtonRxBIND = (Button) findViewById(R.id.buttonRXBIND);

		EditTextSetSerialBoudRate = (EditText) findViewById(R.id.editTextSerialBoudRate);

	}

	@Override
	protected void onPause() {
		super.onPause();
		// mHandler.removeCallbacks(update);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		// mHandler.postDelayed(update, App.REFRESH_RATE);
		app.Say(getString(R.string.Other));

		app.mw.SendRequestGetMisc();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		EditTextPowerMeterAlarm.setText(String.valueOf(app.mw.intPowerTrigger));
		EditTextSelectSettings.setText(String.valueOf(app.mw.confSetting));

		// if ((app.mw.multiCapability & 1) > 0) {
		// ButtonRxBIND.setVisibility(Button.VISIBLE);
		// } else {
		// ButtonRxBIND.setVisibility(Button.GONE);
		// }

	}

	public void MagCalibrationOnClick(View v) {
		app.mw.SendRequestMagCalibration();
	}

	public void AccCalibrationOnClick(View v) {
		app.mw.SendRequestAccCalibration();
	}

	public void WritePowerMeterAlarmOnClick(View v) {
		app.mw.SendRequestSetandSaveMISC(Integer.parseInt(EditTextPowerMeterAlarm.getText().toString()));
	}

	public void WriteSelectSettingOnClick(View v) {
		app.mw.SendRequestSelectSetting(Integer.parseInt(EditTextSelectSettings.getText().toString()));
	}

	public void RXBINDOnClick(View v) {
		app.mw.SendRequestBIND();
	}

	public void SetSerialBoudRateOnClick(View v) {
		app.mw.SendRequestMSP_ENABLE_FRSKY();
		// app.mw.SendRequestMSP_SET_SERIAL_BAUDRATE(Integer.parseInt(EditTextSetSerialBoudRate.getText().toString()));
		app.mw.SendRequestMSP_SET_SERIAL_BAUDRATE(9600);
		app.bt.CloseSocket();
	}

}
