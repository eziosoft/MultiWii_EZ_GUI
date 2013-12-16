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

//This is template which can be used to create new activities

package com.ezio.multiwii.advanced;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class AUXControlActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	RadioButton AUX1_LO;
	RadioButton AUX1_MID;
	RadioButton AUX1_HI;

	RadioButton AUX2_LO;
	RadioButton AUX2_MID;
	RadioButton AUX2_HI;

	RadioButton AUX3_LO;
	RadioButton AUX3_MID;
	RadioButton AUX3_HI;

	RadioButton AUX4_LO;
	RadioButton AUX4_MID;
	RadioButton AUX4_HI;

	CheckBox CBEnableControl;

	int[] CH8 = { 0, 0, 0, 0, 0, 0, 0, 0 };

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			app.Frequentjobs();

			if (CBEnableControl.isChecked()) {
				if (AUX1_LO.isChecked())
					CH8[4] = 1100;
				if (AUX1_MID.isChecked())
					CH8[4] = 1500;
				if (AUX1_HI.isChecked())
					CH8[4] = 1800;

				if (AUX2_LO.isChecked())
					CH8[5] = 1100;
				if (AUX2_MID.isChecked())
					CH8[5] = 1500;
				if (AUX2_HI.isChecked())
					CH8[5] = 1800;

				if (AUX3_LO.isChecked())
					CH8[6] = 1100;
				if (AUX3_MID.isChecked())
					CH8[6] = 1500;
				if (AUX3_HI.isChecked())
					CH8[6] = 1800;

				if (AUX4_LO.isChecked())
					CH8[7] = 1100;
				if (AUX4_MID.isChecked())
					CH8[7] = 1500;
				if (AUX4_HI.isChecked())
					CH8[7] = 1800;

				app.mw.SendRequestMSP_SET_RAW_RC(CH8);
				
			}

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		app.ForceLanguage();
		
		setContentView(R.layout.aux_control);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		AUX1_LO = (RadioButton) findViewById(R.id.RadioButtonAUX1LO);
		AUX1_MID = (RadioButton) findViewById(R.id.RadioButtonAUX1MID);
		AUX1_HI = (RadioButton) findViewById(R.id.RadioButtonAUX1HI);

		AUX2_LO = (RadioButton) findViewById(R.id.RadioButtonAUX2LO);
		AUX2_MID = (RadioButton) findViewById(R.id.RadioButtonAUX2MID);
		AUX2_HI = (RadioButton) findViewById(R.id.RadioButtonAUX2HI);

		AUX3_LO = (RadioButton) findViewById(R.id.RadioButtonAUX3LO);
		AUX3_MID = (RadioButton) findViewById(R.id.RadioButtonAUX3MID);
		AUX3_HI = (RadioButton) findViewById(R.id.RadioButtonAUX3HI);

		AUX4_LO = (RadioButton) findViewById(R.id.RadioButtonAUX4LO);
		AUX4_MID = (RadioButton) findViewById(R.id.RadioButtonAUX4MID);
		AUX4_HI = (RadioButton) findViewById(R.id.RadioButtonAUX4HI);

		CBEnableControl = (CheckBox) findViewById(R.id.checkBoxEnableControl);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.AUXControl));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getSupportActionBar().setTitle(getString(R.string.AUXControl));


	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

}