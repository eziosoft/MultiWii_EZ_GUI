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
import com.ezio.multiwii.app.App;

public class OtherActivity extends SherlockActivity {
	App app;
	// Handler mHandler = new Handler();

	EditText EditTextPowerMeterAlarm;
	EditText EditTextSelectSettings;
	EditText EditTextSetSerialBoudRate;

	EditText EditTextMinThrottle;
	EditText EditTextMaxThrottle;
	EditText EditTextMinCommand;
	EditText EditTextMidRC;

	EditText EditTextVBatsScale;
	EditText EditTextBatWarning1;
	EditText EditTextBatWarning2;
	EditText EditTextBatCritical;

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

		EditTextMinThrottle = (EditText) findViewById(R.id.editTextMinThrottle);
		EditTextMaxThrottle = (EditText) findViewById(R.id.editTextMaxThrottle);
		EditTextMinCommand = (EditText) findViewById(R.id.editTextMinCommand);
		EditTextMidRC = (EditText) findViewById(R.id.editTextMidRC);

		EditTextVBatsScale = (EditText) findViewById(R.id.EditTextVBatScale);
		EditTextBatWarning1 = (EditText) findViewById(R.id.EditTextBatWarning1);
		EditTextBatWarning2 = (EditText) findViewById(R.id.EditTextBatWarning2);
		EditTextBatCritical = (EditText) findViewById(R.id.EditTextBatCritical);

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
		app.mw.SendRequestMSP_MISC_CONF();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		EditTextPowerMeterAlarm.setText(String.valueOf(app.mw.intPowerTrigger));
		EditTextSelectSettings.setText(String.valueOf(app.mw.confSetting));

		EditTextMinThrottle.setText(String.valueOf(app.mw.minthrottle));
		EditTextMaxThrottle.setText(String.valueOf(app.mw.maxthrottle));
		EditTextMinCommand.setText(String.valueOf(app.mw.mincommand));
		EditTextMidRC.setText(String.valueOf(app.mw.midrc));

		EditTextVBatsScale.setText(String.valueOf(app.mw.vbatscale));
		EditTextBatWarning1.setText(String.valueOf(app.mw.vbatlevel_warn1));
		EditTextBatWarning2.setText(String.valueOf(app.mw.vbatlevel_warn2));
		EditTextBatCritical.setText(String.valueOf(app.mw.vbatlevel_crit));

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
		app.comm.Close();
	}

}
