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

package com.ezio.multiwii.aux_pid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.CustomInputDialog;
import com.ezio.sec.Sec;

public class ServosActivity extends SherlockActivity {

	private boolean killme = false;
	final int ROWS = 8;
	final int COLS = 4;

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
		setContentView(R.layout.servo_conf_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getSupportActionBar().setTitle(getString(R.string.Servos));

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Servos));
		killme = false;
		// mHandler.postDelayed(update, app.RefreshRate);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		ServoReadOnClick();

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

	public void ServoCheckBoxOnClick(View v) {
		String name = getResources().getResourceEntryName(v.getId());
		Log.d("aaa", name.substring(3, 5));

		int i = Integer.parseInt(name.substring(3, 5)) - 1;
		for (int j = 0; j < COLS; j++) {
			String a = String.format("%02d", i + 1);
			String b = String.format("%02d", j + 1);
			int editTextId = getResources().getIdentifier("box" + a + b, "id", getPackageName());
			EditText et = (EditText) findViewById(editTextId);
			switch (j) {
			case 3:
				int checkbox1Id = getResources().getIdentifier("box" + a + String.format("%02d", j + 2), "id", getPackageName());
				CheckBox cb1 = (CheckBox) findViewById(checkbox1Id);
				int checkbox2Id = getResources().getIdentifier("box" + a + String.format("%02d", j + 3), "id", getPackageName());
				CheckBox cb2 = (CheckBox) findViewById(checkbox2Id);

				if (i < 2) {
					if (cb1.isChecked() && Integer.parseInt(et.getText().toString()) != 0) {
						et.setText(String.valueOf((Math.abs(Integer.parseInt(et.getText().toString()))) * -1));
					} else {
						et.setText(String.valueOf((Math.abs(Integer.parseInt(et.getText().toString())))));
					}
				}

				if (i == 2) {
					if (cb1.isChecked()) {
						et.setText("1");
					} else {
						et.setText("0");
					}
				}

				if (i > 2) {
					int x = (cb1.isChecked()) ? 1 : 0;
					int y = (cb2.isChecked()) ? 2 : 0;
					et.setText(String.valueOf(x + y));
				}
				break;
			}
		}
	}

	private void ServoWriteOnClick() {

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				String a = String.format("%02d", i + 1);
				String b = String.format("%02d", j + 1);
				int editTextId = getResources().getIdentifier("box" + a + b, "id", getPackageName());
				EditText et = (EditText) findViewById(editTextId);
				switch (j) {
				case 0:

					app.mw.ServoConf[i].Min = Integer.parseInt(et.getText().toString());
					break;
				case 1:

					app.mw.ServoConf[i].Max = Integer.parseInt(et.getText().toString());
					break;
				case 2:
					app.mw.ServoConf[i].MidPoint = Integer.parseInt(et.getText().toString());
					break;
				case 3:

					if (Integer.parseInt(et.getText().toString()) < 0) {
						app.mw.ServoConf[i].Rate = Integer.parseInt(et.getText().toString()) + 256;
					} else {
						app.mw.ServoConf[i].Rate = Integer.parseInt(et.getText().toString());
					}
					break;
				}
			}
		}

		app.mw.SendRequestMSP_SET_SERVO_CONF();
	}

	public void ServoReadOnClick() {
		app.mw.SendRequestMSP_SERVO_CONF();

		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(app.loggingON);
		for (int i = 0; i < 8; i++) {

			// Check the boundaries, if no servos are defined in config.h then
			// the servo variables remain uninitailised in EEPROM so expect
			// gibberis
			if (app.mw.ServoConf[i].Max == 0)
				app.mw.ServoConf[i].Max = 2000;
			if ((app.mw.ServoConf[i].Min < 900) || (app.mw.ServoConf[i].Min > 2100))
				app.mw.ServoConf[i].Min = 1000;
			if (app.mw.ServoConf[i].MidPoint == 0)
				app.mw.ServoConf[i].MidPoint = 1500;
			if ((app.mw.ServoConf[i].Max < 900) || (app.mw.ServoConf[i].Max > 2100))
				app.mw.ServoConf[i].Max = 2000;
//			if (app.mw.ServoConf[i].Rate == 0)
//				app.mw.ServoConf[i].Rate = 100;
			if ((app.mw.ServoConf[i].MidPoint < 1000) || (app.mw.ServoConf[i].MidPoint > 2000))
				app.mw.ServoConf[i].MidPoint = 1500;
		}
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				String a = String.format("%02d", i + 1);
				String b = String.format("%02d", j + 1);
				int editTextId = getResources().getIdentifier("box" + a + b, "id", getPackageName());
				EditText et = (EditText) findViewById(editTextId);
				//
				et.setFocusable(false);
				//
				switch (j) {
				case 0:
					et.setText(String.valueOf(app.mw.ServoConf[i].Min));
					break;
				case 1:
					et.setText(String.valueOf(app.mw.ServoConf[i].Max));
					break;
				case 2:
					et.setText(String.valueOf(app.mw.ServoConf[i].MidPoint));
					break;
				case 3:
					int checkbox1Id = getResources().getIdentifier("box" + a + String.format("%02d", j + 2), "id", getPackageName());
					CheckBox cb1 = (CheckBox) findViewById(checkbox1Id);
					int checkbox2Id = getResources().getIdentifier("box" + a + String.format("%02d", j + 3), "id", getPackageName());
					CheckBox cb2 = (CheckBox) findViewById(checkbox2Id);

					if (app.mw.ServoConf[i].Rate > 127) {

						et.setText(String.valueOf(app.mw.ServoConf[i].Rate - 256));
					} else {
						et.setText(String.valueOf(app.mw.ServoConf[i].Rate));
					}

					if (i < 2) {
						cb2.setVisibility(View.INVISIBLE);
						if (app.mw.ServoConf[i].Rate > 127) {
							cb1.setChecked(true);
						} else {
							cb1.setChecked(false);
						}
					}

					if (i == 2) {
						cb2.setVisibility(View.INVISIBLE);
						if (app.mw.ServoConf[i].Rate == 1) {
							cb1.setChecked(true);
						} else {
							cb1.setChecked(false);
						}
					}

					if (i > 2) {
						if (app.mw.ServoConf[i].Rate == 1) {
							cb1.setChecked(true);
							cb2.setChecked(false);
						}

						if (app.mw.ServoConf[i].Rate == 2) {
							cb1.setChecked(false);
							cb2.setChecked(true);
						}

						if (app.mw.ServoConf[i].Rate == 3) {
							cb1.setChecked(true);
							cb2.setChecked(true);
						}

						if (app.mw.ServoConf[i].Rate > 3 || app.mw.ServoConf[i].Rate < 0) {
							cb1.setChecked(false);
							cb2.setChecked(false);
						}
					}

					break;
				}
			}
		}
	}

	public void OpenInfoOnClick(View v) {
		app.OpenInfoOnClick(v);
	}

	public void ShowCustomDialogOnClick(View vv) {
		CustomInputDialog.ShowCustomDialogOnClick(vv, this);
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_servoconf, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuReadServo) {
			ServoReadOnClick();
			return true;
		}

		if (item.getItemId() == R.id.MenuSaveServo) {

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					ServoWriteOnClick();
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

		return false;
	}

	// ///menu end//////
}
