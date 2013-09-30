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

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

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

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Servos));
		killme = false;
		// mHandler.postDelayed(update, app.RefreshRate);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	private void ServoCheckBoxOnClick(View v) {
		String name = getResources().getResourceEntryName(v.getId());
		Log.d("aaa", name.substring(3, 5));

		int i = Integer.parseInt(name.substring(3, 5)) - 1;
		for (int j = 0; j < COLS; j++) {
			String a = String.format("%02d", i + 1);
			String b = String.format("%02d", j + 1);
			int editTextId = getResources().getIdentifier("box" + a + b, "id", getPackageName());
			EditText et = (EditText) findViewById(editTextId);
			switch (j) {
			// case 0:
			// et.setText(String.valueOf(app.mw.ServoConf[i].Min));
			// break;
			// case 1:
			// et.setText(String.valueOf(app.mw.ServoConf[i].Max));
			// break;
			// case 2:
			// et.setText(String.valueOf(app.mw.ServoConf[i].MidPoint));
			// break;
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

		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				String a = String.format("%02d", i + 1);
				String b = String.format("%02d", j + 1);
				int editTextId = getResources().getIdentifier("box" + a + b, "id", getPackageName());
				EditText et = (EditText) findViewById(editTextId);
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
			ServoWriteOnClick();
			return true;
		}

		return false;
	}

	// ///menu end//////
}
