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

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class MiscActivity extends SherlockActivity {

	private boolean killme = false;

	// intPowerTrigger1
	// conf.minthrottle
	// MAXTHROTTLE
	// MINCOMMAND
	// conf.failsafe_throttle
	// plog.arm
	// plog.lifetime
	// conf.mag_declination
	// conf.vbatscale
	// conf.vbatlevel_warn1
	// conf.vbatlevel_warn2
	// conf.vbatlevel_crit

	EditText ETPowerTrigger;
	EditText ETMinThrottle;
	EditText ETMaxThrottle;
	EditText ETMinCommand;
	EditText ETFailSafeThrottle;
	EditText ETArmCount;
	EditText ETLifeTime;
	EditText ETMagDeclination;
	EditText ETVBatScale;
	EditText ETLevelWarn1;
	EditText ETLevelWarn2;
	EditText ETLevelCrit;

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			app.Frequentjobs();

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
		app.ConnectionBug();
		setContentView(R.layout.misc_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		ETPowerTrigger = (EditText) findViewById(R.id.EditTextPowerTrigger);
		ETMinThrottle = (EditText) findViewById(R.id.EditTextMinThrottle);
		ETMaxThrottle = (EditText) findViewById(R.id.EditTextMaxThrottle);
		ETMinCommand = (EditText) findViewById(R.id.EditTextMinCommand);
		ETFailSafeThrottle = (EditText) findViewById(R.id.EditTextFailsafeThrottle);
		ETArmCount = (EditText) findViewById(R.id.EditTextArmedCount);
		ETLifeTime = (EditText) findViewById(R.id.EditTextLiveTime);
		ETMagDeclination = (EditText) findViewById(R.id.EditTextMagneticDeclination);
		ETVBatScale = (EditText) findViewById(R.id.EditTextVBatScale);
		ETLevelWarn1 = (EditText) findViewById(R.id.EditTextBatLevelWarn1);
		ETLevelWarn2 = (EditText) findViewById(R.id.EditTextBatLevelWarn2);
		ETLevelCrit = (EditText) findViewById(R.id.editTextBatLevelCrit);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Motors));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		switch (app.mw.confSetting) {
		case 0:
			((RadioButton) findViewById(R.id.radioSelectSetting0)).setChecked(true);
			break;
		case 1:
			((RadioButton) findViewById(R.id.radioSelectSetting1)).setChecked(true);
			break;
		case 2:
			((RadioButton) findViewById(R.id.radioSelectSetting2)).setChecked(true);
			break;
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	private void ReadAndDisplay() {
		app.mw.SendRequestMSP_MISC();

		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void SelectSettingSetOnClick(View v) {

		int a = 0;
		if (((RadioButton) findViewById(R.id.radioSelectSetting0)).isChecked())
			a = 0;
		if (((RadioButton) findViewById(R.id.radioSelectSetting1)).isChecked())
			a = 1;
		if (((RadioButton) findViewById(R.id.radioSelectSetting2)).isChecked())
			a = 2;

		app.mw.SendRequestMSP_SELECT_SETTING(a);
		Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT);

	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_misc, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuRead) {
			ReadAndDisplay();
			return true;
		}

		if (item.getItemId() == R.id.MenuSave) {
			// SetOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.MenuSharePID) {
			// ShareIt();
			return true;
		}

		return false;
	}

	// ///menu end//////

}
