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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class OtherActivity extends SherlockActivity {
	App app;
	Handler mHandler = new Handler();
	private boolean killme = false;

	EditText EditTextPowerMeterAlarm;
	EditText EditTextSelectSettings;
	EditText EditTextSetSerialBoudRate;

	EditText EditTextMinThrottle;
	EditText EditTextMaxThrottle;
	EditText EditTextMinCommand;
	EditText EditTextFailsafeThrottle;

	EditText EditTextVBatsScale;
	EditText EditTextBatWarning1;
	EditText EditTextBatWarning2;
	EditText EditTextBatCritical;
	TextView TextViewVolt;
	EditText EditTextDeclination;

	TextView textViewArmedCount;

	Button ButtonRxBIND;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			app.Frequentjobs();

			TextViewVolt.setText(String.valueOf(app.mw.bytevbat / 10f) + "V");

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

		}
	};

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
		EditTextFailsafeThrottle = (EditText) findViewById(R.id.editTextFailsafeThrottle);

		EditTextVBatsScale = (EditText) findViewById(R.id.EditTextVBatScale);
		EditTextBatWarning1 = (EditText) findViewById(R.id.EditTextBatWarning1);
		EditTextBatWarning2 = (EditText) findViewById(R.id.EditTextBatWarning2);
		EditTextBatCritical = (EditText) findViewById(R.id.EditTextBatCritical);
		TextViewVolt = (TextView) findViewById(R.id.textViewVolt);
		EditTextDeclination = (EditText) findViewById(R.id.editTextDeclination);
		textViewArmedCount = (TextView) findViewById(R.id.textViewArmedCount);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		app.Say(getString(R.string.Other));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Read();
	}

	private void Read() {
		app.mw.SendRequestMSP_MISC();

		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		EditTextPowerMeterAlarm.setText(String.valueOf(app.mw.intPowerTrigger));
		EditTextSelectSettings.setText(String.valueOf(app.mw.confSetting));

		EditTextMinThrottle.setText(String.valueOf(app.mw.minthrottle));
		EditTextMaxThrottle.setText(String.valueOf(app.mw.maxthrottle));
		EditTextMinCommand.setText(String.valueOf(app.mw.mincommand));
		EditTextFailsafeThrottle.setText(String.valueOf(app.mw.failsafe_throttle));

		EditTextVBatsScale.setText(String.valueOf(app.mw.vbatscale));
		EditTextBatWarning1.setText(String.valueOf(app.mw.vbatlevel_warn1));
		EditTextBatWarning2.setText(String.valueOf(app.mw.vbatlevel_warn2));
		EditTextBatCritical.setText(String.valueOf(app.mw.vbatlevel_crit));
		EditTextDeclination.setText(String.valueOf(app.mw.mag_decliniation));
		textViewArmedCount.setText(String.valueOf(app.mw.armedNum));
	}

	public void DeclinationTakeFromPhoneOnClick(View v) {
		EditTextDeclination.setText(String.valueOf(app.sensors.Declination));
	}

	public void MSP_MISC_CONFreadOnClick(View v) {
		Read();
	}

	public void MagCalibrationOnClick(View v) {
		app.mw.SendRequestMSP_MAG_CALIBRATION();
	}

	public void AccCalibrationOnClick(View v) {
		app.mw.SendRequestMSP_ACC_CALIBRATION();
	}

	public void MSP_SET_MISC_CONF_WriteOnClick(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				app.mw.SendRequestMSP_SET_MISC(Integer.parseInt(EditTextPowerMeterAlarm.getText().toString()), Integer.parseInt(EditTextMinThrottle.getText().toString()), Integer.parseInt(EditTextMaxThrottle.getText().toString()), Integer.parseInt(EditTextMinCommand.getText().toString()), Integer.parseInt(EditTextFailsafeThrottle.getText().toString()), Float.parseFloat(EditTextDeclination.getText().toString()), (byte) Integer.parseInt(EditTextVBatsScale.getText().toString()), (Float.parseFloat(EditTextBatWarning1.getText().toString())), (Float.parseFloat(EditTextBatWarning2.getText().toString())), (Float.parseFloat(EditTextBatCritical.getText().toString())));

				app.mw.SendRequestMSP_EEPROM_WRITE();

				Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT).show();

			}
		}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	public void WriteSelectSettingOnClick(View v) {
		app.mw.SendRequestMSP_SELECT_SETTING(Integer.parseInt(EditTextSelectSettings.getText().toString()));
	}

	public void RXBINDOnClick(View v) {
		app.mw.SendRequestMSP_BIND();
	}

	public void SetSerialBoudRateOnClick(View v) {
		app.mw.SendRequestMSP_ENABLE_FRSKY();
		// app.mw.SendRequestMSP_SET_SERIAL_BAUDRATE(Integer.parseInt(EditTextSetSerialBoudRate.getText().toString()));
		app.mw.SendRequestMSP_SET_SERIAL_BAUDRATE(9600);
		app.commMW.Close();
	}

}
