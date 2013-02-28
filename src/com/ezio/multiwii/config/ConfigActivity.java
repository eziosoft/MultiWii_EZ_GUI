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
package com.ezio.multiwii.config;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.App;
import com.ezio.multiwii.R;
import com.ezio.multiwii.mw.BT;

public class ConfigActivity extends SherlockActivity {

	App app;
	RadioButton Mode1;
	RadioButton Mode2;

	RadioButton Protocol20;
	RadioButton Protocol21;

	RadioButton MagMode1;
	RadioButton MagMode2;

	CheckBox CheckBoxTTS;
	CheckBox CheckBoxUseOfflineMap;

	TextView MacAddressBTTV;
	TextView MacAddressBTFrskyTV;

	CheckBox CheckBoxConnectOnStart;

	CheckBox CheckBoxAltCorrection;

	CheckBox CheckBoxDisableBTonExit;

	CheckBox CheckBoxCopyFrskyToMW;
	
	CheckBox CheckBoxReverseRollDirection;

	RadioButton RadioNotForce;
	RadioButton RadioForceEnglish;
	RadioButton RadioForceGerman;
	RadioButton RadioForceHungarian;
	RadioButton RadioForcePolish;

	EditText EditTextPeriodicSpeaking;
	EditText EditTextVoltageAlarm;
	EditText EditTextRefreshRate;

	private static final int REQUEST_CONNECT_DEVICE_MULTIWII = 1;
	private static final int REQUEST_CONNECT_DEVICE_FRSKY = 2;

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(BT.TAG, "onActivityResult " + resultCode);
		switch (requestCode) {

		case REQUEST_CONNECT_DEVICE_MULTIWII:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				app.MacAddress = address;
				MacAddressBTTV.setText("MAC:" + app.MacAddress);
			}
			break;

		case REQUEST_CONNECT_DEVICE_FRSKY:
			// When DeviceListActivity returns with a device to connect
			if (resultCode == Activity.RESULT_OK) {
				String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				app.MacAddressFrsky = address;
				MacAddressBTFrskyTV.setText("MAC:" + app.MacAddressFrsky);
			}
			break;
		}
	}

	public void SelectBTdevice(View v) {
		Intent serverIntent = null;
		serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_MULTIWII);
	}

	public void SelectFrskyDevice(View v) {
		Intent serverIntent = null;
		serverIntent = new Intent(this, DeviceListActivity.class);
		startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_FRSKY);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.config_layout);

		app = (App) getApplication();

		Mode1 = (RadioButton) findViewById(R.id.radioButton1);
		Mode2 = (RadioButton) findViewById(R.id.radioButton2);
		Protocol20 = (RadioButton) findViewById(R.id.radioButtonProtocol20);
		Protocol21 = (RadioButton) findViewById(R.id.radioButtonProtocol21);
		MagMode1 = (RadioButton) findViewById(R.id.radioButtonMagMode1);
		MagMode2 = (RadioButton) findViewById(R.id.radioButtonMagMode2);
		CheckBoxTTS = (CheckBox) findViewById(R.id.checkBoxTTS);
		MacAddressBTTV = (TextView) findViewById(R.id.textViewMacAddress);
		MacAddressBTFrskyTV = (TextView) findViewById(R.id.textViewMacAddressFrsky);
		CheckBoxConnectOnStart = (CheckBox) findViewById(R.id.checkBoxConnectOnStart);
		CheckBoxAltCorrection = (CheckBox) findViewById(R.id.checkBoxAltCorrection);
		CheckBoxDisableBTonExit = (CheckBox) findViewById(R.id.checkBoxDisableBTonExit);
		RadioNotForce = (RadioButton) findViewById(R.id.RadioDontForce);
		RadioForceEnglish = (RadioButton) findViewById(R.id.radioForceEnglish);
		RadioForceGerman = (RadioButton) findViewById(R.id.radioForceGerman);
		RadioForceHungarian = (RadioButton) findViewById(R.id.radioForceHungarian);
		RadioForcePolish = (RadioButton) findViewById(R.id.radioForcePolish);
		EditTextPeriodicSpeaking = (EditText) findViewById(R.id.editTextPeriodicSpeaking);
		EditTextVoltageAlarm = (EditText) findViewById(R.id.editTextVoltageAlarm);
		CheckBoxUseOfflineMap = (CheckBox) findViewById(R.id.checkBoxUseOfflineMap);
		EditTextRefreshRate = (EditText) findViewById(R.id.editTextRefreshRate);
		CheckBoxCopyFrskyToMW = (CheckBox) findViewById(R.id.checkBoxCopyFrskyToMW);
		CheckBoxReverseRollDirection=(CheckBox)findViewById(R.id.checkBoxReverseRollDirection);

	}

	@Override
	protected void onPause() {
		SaveSettingsOnClick(null);
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();

		if (app.RadioMode == 1) {
			Mode1.setChecked(true);
		} else {
			Mode2.setChecked(true);
		}

		if (app.Protocol == 200) {
			Protocol20.setChecked(true);
		} else {
			Protocol21.setChecked(true);
		}

		if (app.MagMode == 1) {
			MagMode1.setChecked(true);
		} else {
			MagMode2.setChecked(true);
		}

		CheckBoxTTS.setChecked(app.TextToSpeach);
		CheckBoxConnectOnStart.setChecked(app.ConnectOnStart);
		CheckBoxAltCorrection.setChecked(app.AltCorrection);
		CheckBoxDisableBTonExit.setChecked(app.DisableBTonExit);
		CheckBoxCopyFrskyToMW.setChecked(app.CopyFrskyToMW);
		CheckBoxUseOfflineMap.setChecked(app.UseOfflineMaps);
		CheckBoxReverseRollDirection.setChecked(app.ReverseRoll);
		
		
		MacAddressBTTV.setText("MAC:" + app.MacAddress);
		MacAddressBTFrskyTV.setText("MAC:" + app.MacAddressFrsky);

		RadioNotForce.setChecked(app.ForceLanguage.equals(""));
		RadioForceEnglish.setChecked(app.ForceLanguage.equals("en"));
		RadioForceGerman.setChecked(app.ForceLanguage.equals("de"));
		RadioForceHungarian.setChecked(app.ForceLanguage.equals("hu"));
		RadioForcePolish.setChecked(app.ForceLanguage.equals("pl"));
		

		EditTextPeriodicSpeaking.setText(String.valueOf(app.PeriodicSpeaking / 1000));

		EditTextVoltageAlarm.setText(String.valueOf(app.VoltageAlarm));
		EditTextRefreshRate.setText(String.valueOf(app.RefreshRate));

		app.Say(getString(R.string.Config));

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	public void SaveSettingsOnClick(View v) {

		if (Mode1.isChecked()) {
			app.RadioMode = 1;
		} else {
			app.RadioMode = 2;
		}

		if (Protocol20.isChecked()) {
			app.Protocol = 200;
		}

		if (Protocol21.isChecked()) {
			app.Protocol = 210;
		}

		if (MagMode1.isChecked()) {
			app.MagMode = 1;
		} else {
			app.MagMode = 2;
		}

		app.TextToSpeach = CheckBoxTTS.isChecked();
		app.ConnectOnStart = CheckBoxConnectOnStart.isChecked();
		app.AltCorrection = CheckBoxAltCorrection.isChecked();
		app.DisableBTonExit = CheckBoxDisableBTonExit.isChecked();
		app.UseOfflineMaps = CheckBoxUseOfflineMap.isChecked();
		app.CopyFrskyToMW = CheckBoxCopyFrskyToMW.isChecked();
		app.ReverseRoll=CheckBoxReverseRollDirection.isChecked();

		if (RadioNotForce.isChecked())
			app.ForceLanguage = "";
		if (RadioForceEnglish.isChecked())
			app.ForceLanguage = "en";
		if (RadioForceGerman.isChecked())
			app.ForceLanguage = "de";
		if (RadioForceHungarian.isChecked())
			app.ForceLanguage = "hu";
		if (RadioForcePolish.isChecked())
			app.ForceLanguage = "pl";

		app.PeriodicSpeaking = Integer.parseInt(EditTextPeriodicSpeaking.getText().toString()) * 1000;

		app.VoltageAlarm = Float.parseFloat(EditTextVoltageAlarm.getText().toString());

		app.RefreshRate = Integer.parseInt(EditTextRefreshRate.getText().toString());

		app.SaveSettings(false);

		app.SelectProtocol();

	}
}
