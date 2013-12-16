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

import java.text.NumberFormat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.CustomInputDialog;
import com.ezio.sec.Sec;

public class MiscActivity extends SherlockActivity {

	private boolean killme = false;

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
	EditText ETBatteryVoltage;

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			app.Frequentjobs();

			ETBatteryVoltage.setText(String.valueOf((float) (app.mw.bytevbat / 10.0)));

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
		ETBatteryVoltage = (EditText) findViewById(R.id.EditTextBatteryVoltage);

		getSupportActionBar().setTitle(getString(R.string.Misc));

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Misc));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

		if (app.mw.multi_Capability.ByMis) {
			((TextView) findViewById(R.id.TextViewMinCommand)).setText(getString(R.string.FSRTHaltitude));
			((EditText) findViewById(R.id.EditTextMinCommand)).setEnabled(true);
		}

		ReadAndDisplay();

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

		if (Sec.VerifyDeveloperID(Sec.GetDeviceID(getApplicationContext()), Sec.TestersIDs) || Sec.Verify(getApplicationContext(), "D..3")) {
			mHandler.postDelayed(update, app.RefreshRate);
		} else {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

			dlgAlert.setTitle(getString(R.string.Locked));
			dlgAlert.setMessage(getString(R.string.DoYouWantToUnlock));

			// dlgAlert.setPositiveButton(getString(R.string.Yes), null);
			dlgAlert.setCancelable(false);
			dlgAlert.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.ezio.ez_gui_unlocker");
						startActivity(LaunchIntent);
					} catch (Exception e) {
						Intent goToMarket = null;
						goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ezio.ez_gui_unlocker"));
						startActivity(goToMarket);
					}
					finish();
				}
			});
			dlgAlert.setNegativeButton(getString(R.string.No), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			dlgAlert.create().show();
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

		app.mw.ProcessSerialData(app.loggingON);

		ETPowerTrigger.setText(String.valueOf(app.mw.intPowerTrigger));
		ETMinThrottle.setText(String.valueOf(app.mw.minthrottle));
		ETMaxThrottle.setText(String.valueOf(app.mw.maxthrottle));
		ETMinCommand.setText(String.valueOf(app.mw.mincommand));
		ETFailSafeThrottle.setText(String.valueOf(app.mw.failsafe_throttle));
		ETArmCount.setText(String.valueOf(app.mw.ArmCount));
		ETLifeTime.setText(String.valueOf(app.mw.LifeTime));
		ETMagDeclination.setText(String.valueOf(app.mw.mag_decliniation));
		ETVBatScale.setText(String.valueOf(app.mw.vbatscale));
		ETLevelWarn1.setText(String.valueOf(app.mw.vbatlevel_warn1));
		ETLevelWarn2.setText(String.valueOf(app.mw.vbatlevel_warn2));
		ETLevelCrit.setText(String.valueOf(app.mw.vbatlevel_crit));

	}

	private void Save() {
		int powerTrigger = Integer.parseInt(ETPowerTrigger.getText().toString());
		int MinThrottle = Integer.parseInt(ETMinThrottle.getText().toString());
		int MaxThrottle = Integer.parseInt(ETMaxThrottle.getText().toString());
		int MinCommand = Integer.parseInt(ETMinCommand.getText().toString());
		int FailSafhrottle = Integer.parseInt(ETFailSafeThrottle.getText().toString());
		// int ArmCount = Integer.parseInt(ETArmCount.getText().toString());
		// int Lifime = Integer.parseInt(ETLifeTime.getText().toString());
		float MagDeclination = Float.parseFloat(ETMagDeclination.getText().toString());
		int VBatScale = Integer.parseInt(ETVBatScale.getText().toString());
		float LevelWarn1 = Float.parseFloat(ETLevelWarn1.getText().toString());
		float LevelWarn2 = Float.parseFloat(ETLevelWarn2.getText().toString());
		float LevelCrit = Float.parseFloat(ETLevelCrit.getText().toString());

		app.mw.SendRequestMSP_SET_MISC(powerTrigger, MinThrottle, MaxThrottle, MinCommand, FailSafhrottle, MagDeclination, VBatScale, LevelWarn1, LevelWarn2, LevelCrit);

		Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT).show();
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
		Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT).show();

	}

	public void GetDeclinationOnClick(View v) {
		final NumberFormat format = NumberFormat.getNumberInstance();
		format.setMinimumFractionDigits(1);
		format.setMaximumFractionDigits(1);
		format.setGroupingUsed(false);
		ETMagDeclination.setText(format.format(app.sensors.Declination));
	}

	public void ShowCustomDialogOnClick(View vv) {
		CustomInputDialog.ShowCustomDialogOnClick(vv, this);
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

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					Save();
					Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT).show();

				}
			}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();

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
