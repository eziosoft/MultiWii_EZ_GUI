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
package com.ezio.multiwii.aux_pid;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class AUXActivity extends SherlockActivity {

	App app;
	private boolean killme = false;

	Handler mHandler = new Handler();

	TextView TextViewInfo;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			SetActiveStates();
			app.Frequentjobs();

			TextViewInfo.setText("Aux1:" + GetTextValueOfAux(app.mw.rcAUX1) + " " + String.valueOf((int) app.mw.rcAUX1) + " Aux2:" + GetTextValueOfAux(app.mw.rcAUX2) + " " + String.valueOf((int) app.mw.rcAUX2) + " Aux3:" + GetTextValueOfAux(app.mw.rcAUX3) + " " + String.valueOf((int) app.mw.rcAUX3) + " Aux4:" + GetTextValueOfAux(app.mw.rcAUX4) + " " + String.valueOf((int) app.mw.rcAUX4));

			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);
			Log.d(app.TAG, "loop " + this.getClass().getName());

		}
	};

	private String GetTextValueOfAux(float rcAux) {
		if (rcAux > 1600)
			return "H";
		if (rcAux < 1400)
			return "L";
		if (rcAux >= 1400 && rcAux <= 1600)
			return "M";
		return null;
	}

	void SetAllChexboxes() {
		for (int i = 0; i < app.mw.buttonCheckboxLabel.length; i++) {
			for (int j = 0; j < 12; j++) {
				SetCheckbox(i * 100 + j, app.mw.Checkbox[i][j]);
			}
		}
	}

	void ReadOnClick() {
		app.mw.SendRequestMSP_BOX();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		SetAllChexboxes();

	}

	void SetOnClick() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				for (int i = 0; i < app.mw.buttonCheckboxLabel.length; i++) {
					for (int j = 0; j < 12; j++) {
						app.mw.Checkbox[i][j] = GetCheckbox(i * 100 + j);
					}
				}
				app.mw.SendRequestMSP_SET_BOX();

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (App) getApplication();

		getSupportActionBar().setTitle(getString(R.string.SetCheckboxes));

		CreateGUI();

	}

	private void SetCheckbox(int ID, Boolean checked) {
		CheckBox ch = (CheckBox) findViewById(ID);
		Log.d("aaa", String.valueOf(ID) + "=" + String.valueOf(checked));
		ch.setChecked(checked);

	}

	private Boolean GetCheckbox(int ID) {
		CheckBox ch = (CheckBox) findViewById(ID);
		// Log.d("aaa", String.valueOf(ID) + "=" + String.valueOf(checked));
		// ch.setChecked(checked);
		return ch.isChecked();
	}

	private void SetActive(int ID, Boolean active) {
		TextView t = (TextView) findViewById(ID);
		if (active) {
			t.setBackgroundColor(Color.GREEN);
		} else {
			t.setBackgroundColor(getResources().getColor(R.color.tittle));
		}
	}

	private void SetActiveStates() {
		for (int j = 0; j < app.mw.buttonCheckboxLabel.length; j++) {
			SetActive(250 + j, app.mw.ActiveModes[j]);
		}
	}

	void CreateGUI() {
		TextViewInfo = new TextView(this);
		TextViewInfo.setGravity(Gravity.CENTER);
		TextViewInfo.setBackgroundResource(R.drawable.frame);
		TextViewInfo.setTextAppearance(getApplicationContext(), android.R.style.TextAppearance_DeviceDefault_Small);

		HorizontalScrollView horizontalSV = new HorizontalScrollView(this);
		ScrollView verticalSV = new ScrollView(this);

		LinearLayout linearL = new LinearLayout(this);

		TableLayout tableL = new TableLayout(this);
		tableL.setBackgroundResource(R.drawable.frame);

		// add info text
		TextView TVClickForInfo = new TextView(this);
		TVClickForInfo.setText(getString(R.string.ClickHereForMoreInfo));
		TVClickForInfo.setClickable(true);
		TVClickForInfo.setTextColor(getResources().getColor(R.color.link));
		TVClickForInfo.setTag("http://www.multiwii.com/forum/viewtopic.php?f=16&t=3011&p=30010&hilit=combining#p30010");
		TVClickForInfo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				app.OpenInfoOnClick(v);
			}
		});

		// adding tittles
		TableRow r1 = new TableRow(this);
		TextView tv1 = new TextView(this);
		for (int zz = 0; zz < 4; zz++) {
			if (zz == 0) {
				tv1 = new TextView(this);
				tv1.setText("");
				r1.addView(tv1);
			}

			tv1 = new TextView(this);
			tv1.setText("");
			tv1.setBackgroundColor(getResources().getColor(R.color.tittle));
			tv1.setTextColor(getResources().getColor(R.color.tittleText));
			r1.addView(tv1);

			tv1 = new TextView(this);
			tv1.setText("AUX " + String.valueOf(zz + 1));
			tv1.setBackgroundColor(getResources().getColor(R.color.tittle));
			tv1.setTextColor(getResources().getColor(R.color.tittleText));
			r1.addView(tv1);

			tv1 = new TextView(this);
			tv1.setText("");
			tv1.setBackgroundColor(getResources().getColor(R.color.tittle));
			tv1.setTextColor(getResources().getColor(R.color.tittleText));
			r1.addView(tv1);

			tv1 = new TextView(this);
			tv1.setText("");
			r1.addView(tv1);
		}
		tableL.addView(r1);

		r1 = new TableRow(this);
		for (int zz = 0; zz < 4; zz++) {
			if (zz == 0) {
				tv1 = new TextView(this);
				tv1.setText(" ");
				tv1.setGravity(Gravity.CENTER);
				r1.addView(tv1);
			}

			tv1 = new TextView(this);
			tv1.setText("L");
			tv1.setGravity(Gravity.CENTER);
			tv1.setBackgroundColor(getResources().getColor(R.color.smalltittle));
			tv1.setTextColor(getResources().getColor(R.color.smalltittleText));
			r1.addView(tv1);

			tv1 = new TextView(this);
			tv1.setText("M");
			tv1.setGravity(Gravity.CENTER);
			tv1.setBackgroundColor(getResources().getColor(R.color.smalltittle));
			tv1.setTextColor(getResources().getColor(R.color.smalltittleText));
			r1.addView(tv1);

			tv1 = new TextView(this);
			tv1.setText("H");
			tv1.setGravity(Gravity.CENTER);
			tv1.setBackgroundColor(getResources().getColor(R.color.smalltittle));
			tv1.setTextColor(getResources().getColor(R.color.smalltittleText));
			r1.addView(tv1);

			if (zz < 3) {
				tv1 = new TextView(this);
				tv1.setText(" ");
				tv1.setGravity(Gravity.CENTER);
				r1.addView(tv1);
			}
		}
		tableL.addView(r1);
		// titles end/////
		for (int j = 0; j < app.mw.buttonCheckboxLabel.length; j++) {
			TableRow r = new TableRow(this);
			TextView tv = new TextView(this);
			tv.setText(app.mw.buttonCheckboxLabel[j]);

			tv.setId(j + 250);

			tv.setBackgroundColor(getResources().getColor(R.color.tittle));
			tv.setTextColor(getResources().getColor(R.color.tittleText));
			r.addView(tv);

			int ID = 100 * j - 1;
			for (int i = 1; i <= 15; i++) {
				CheckBox c = new CheckBox(this);

				if (i % 4 == 0) {
					c.setVisibility(View.INVISIBLE);
				} else {
					ID++;
					c.setId(ID);
					// c.setText(String.valueOf(ID));
				}
				r.addView(c);
			}
			tableL.addView(r);

		}

		// l.addView(TextViewInfo);
		linearL.addView(tableL);
		verticalSV.addView(linearL);
		horizontalSV.addView(verticalSV);

		LinearLayout a = new LinearLayout(this);
		a.setOrientation(LinearLayout.VERTICAL);
		a.addView(TVClickForInfo);
		a.addView(TextViewInfo);
		a.addView(horizontalSV);
		setContentView(a);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.SetCheckboxes));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		ReadOnClick();

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_aux, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuReadCheckbox) {
			ReadOnClick();
			return true;
		}

		// if (item.getItemId() == R.id.MenuSetCheckbox) {
		// SetOnClick();
		// return true;
		// }

		if (item.getItemId() == R.id.MenuSaveCheckbox) {
			SetOnClick();
			return true;
		}

		return false;
	}

	// ///menu end//////

}
