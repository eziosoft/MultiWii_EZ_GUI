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
package com.ezio.multiwii;

import it.sephiroth.android.wheel.view.Wheel;
import it.sephiroth.android.wheel.view.Wheel.OnScrollListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.SyncStateContract.Helpers;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.helpers.Functions;

public class PIDActivity extends SherlockActivity {

	App app;
	ActionBarSherlock actionBar;

	EditText P1;
	EditText P2;
	EditText P3;
	EditText P4;
	EditText P5;
	EditText P6;
	EditText P7;
	EditText P8;
	EditText P9;

	EditText D1;
	EditText D2;
	EditText D3;
	EditText D4;
	EditText D5;
	EditText D6;
	EditText D7;
	EditText D8;
	EditText D9;

	EditText I1;
	EditText I2;
	EditText I3;
	EditText I4;
	EditText I5;
	EditText I6;
	EditText I7;
	EditText I8;
	EditText I9;

	EditText RatePitchRoll1;
	EditText RatePitchRoll2;
	EditText RateYaw;

	EditText MIDThrottle;
	EditText EXPOThrottle;

	EditText RATE2PitchRoll;
	EditText EXPOPitchRoll;

	EditText TPA;

	Spinner spinnerProfile;

	// used for write
	float[] P;
	float[] I;
	float[] D;

	float confRC_RATE = 0, confRC_EXPO = 0, rollPitchRate = 0, yawRate = 0, dynamic_THR_PID = 0, throttle_MID = 0, throttle_EXPO = 0;

	// ///

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pid);

		app = (App) getApplication();
		actionBar = getSherlock();

		P = new float[app.mw.PIDITEMS];
		I = new float[app.mw.PIDITEMS];
		D = new float[app.mw.PIDITEMS];

		P1 = (EditText) findViewById(R.id.P1);
		P2 = (EditText) findViewById(R.id.P2);
		P3 = (EditText) findViewById(R.id.P3);
		P4 = (EditText) findViewById(R.id.P4);
		P5 = (EditText) findViewById(R.id.P5);
		P6 = (EditText) findViewById(R.id.P6);
		P7 = (EditText) findViewById(R.id.P7);
		P8 = (EditText) findViewById(R.id.P8);
		P9 = (EditText) findViewById(R.id.P9);

		D1 = (EditText) findViewById(R.id.D1);
		D2 = (EditText) findViewById(R.id.D2);
		D3 = (EditText) findViewById(R.id.D3);
		D4 = (EditText) findViewById(R.id.D4);
		D5 = (EditText) findViewById(R.id.D5);
		D6 = (EditText) findViewById(R.id.D6);
		D7 = (EditText) findViewById(R.id.D7);
		D8 = (EditText) findViewById(R.id.D8);
		D9 = (EditText) findViewById(R.id.D9);

		I1 = (EditText) findViewById(R.id.I1);
		I2 = (EditText) findViewById(R.id.I2);
		I3 = (EditText) findViewById(R.id.I3);
		I4 = (EditText) findViewById(R.id.I4);
		I5 = (EditText) findViewById(R.id.I5);
		I6 = (EditText) findViewById(R.id.I6);
		I7 = (EditText) findViewById(R.id.I7);
		I8 = (EditText) findViewById(R.id.I8);
		I9 = (EditText) findViewById(R.id.I9);

		RatePitchRoll1 = (EditText) findViewById(R.id.editTextRatePitchRoll1);
		RatePitchRoll2 = (EditText) findViewById(R.id.editTextRatePitchRoll2);
		RateYaw = (EditText) findViewById(R.id.editTextRateYaw);

		MIDThrottle = (EditText) findViewById(R.id.editTextMIDThrottle);
		EXPOThrottle = (EditText) findViewById(R.id.editTextEXPOThrottle);

		RATE2PitchRoll = (EditText) findViewById(R.id.editTextRate2PitchRoll);
		EXPOPitchRoll = (EditText) findViewById(R.id.editTextEXPOPitchRoll);

		TPA = (EditText) findViewById(R.id.editTextTPA);

		File folder = new File(Environment.getExternalStorageDirectory() + "/MultiWiiLogs");
		boolean success = false;
		if (!folder.exists()) {
			success = folder.mkdir();
		} else {
			success = true;
		}

		if (success) {
			File sdCardRoot = Environment.getExternalStorageDirectory();
			File yourDir = new File(sdCardRoot, "MultiWiiLogs");
			ArrayList<String> l = new ArrayList<String>();

			if (yourDir.listFiles() != null) {
				for (File f : yourDir.listFiles()) {
					if (f.isFile())
						if (f.getName().contains("mwi"))
							l.add(f.getName());
				}
			}
			spinnerProfile = (Spinner) findViewById(R.id.spinnerProfile);
			ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, l);

			spinnerProfile.setAdapter(aa);
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.PID));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		while (app.bt.available() > 0) {
			app.mw.ProcessSerialData(false);
		}
		ShowData();
	}

	public void ReadOnClick(View v) {
		app.mw.SendRequestGetPID();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		ShowData();

	}

	public void ResetOnClick(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.ResetALLnotonlyPIDparamstodefault)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				app.mw.SendRequestResetSettings();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				ReadOnClick(null);
				Toast.makeText(getApplicationContext(), getString(R.string.ValuesaresettodefaultPressreadbuttonnow), Toast.LENGTH_LONG).show();

			}
		}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	void ShareIt() {

		try {

			Log.d("aaa", "File to send:" + Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString());
			File myFile = new File(Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString());
			MimeTypeMap mime = MimeTypeMap.getSingleton();
			String ext = myFile.getName().substring(myFile.getName().lastIndexOf(".") + 1);
			String type = mime.getMimeTypeFromExtension(ext);
			Intent sharingIntent = new Intent("android.intent.action.SEND");
			sharingIntent.setType("*/*");
			sharingIntent.putExtra("android.intent.extra.STREAM", Uri.fromFile(myFile));
			startActivity(Intent.createChooser(sharingIntent, "Share using"));
		} catch (Exception e) {
			Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
		}

	}

	public void SetOnClick(View v) {

		// Log.d("aaaaa",RATE2PitchRoll.getText().toString());
		confRC_RATE = Float.parseFloat(RATE2PitchRoll.getText().toString().replace(",", "."));
		confRC_EXPO = Float.parseFloat(EXPOPitchRoll.getText().toString().replace(",", "."));
		rollPitchRate = Float.parseFloat(RatePitchRoll1.getText().toString().replace(",", "."));
		yawRate = Float.parseFloat(RateYaw.getText().toString().replace(",", "."));
		dynamic_THR_PID = Float.parseFloat(TPA.getText().toString().replace(",", "."));
		throttle_MID = Float.parseFloat(MIDThrottle.getText().toString().replace(",", "."));
		throttle_EXPO = Float.parseFloat(EXPOThrottle.getText().toString().replace(",", "."));

		P[0] = Float.parseFloat(P1.getText().toString().replace(",", "."));
		P[1] = Float.parseFloat(P2.getText().toString().replace(",", "."));
		P[2] = Float.parseFloat(P3.getText().toString().replace(",", "."));
		P[3] = Float.parseFloat(P4.getText().toString().replace(",", "."));
		P[4] = Float.parseFloat(P5.getText().toString().replace(",", "."));
		P[5] = Float.parseFloat(P6.getText().toString().replace(",", "."));
		P[6] = Float.parseFloat(P7.getText().toString().replace(",", "."));
		P[7] = Float.parseFloat(P8.getText().toString().replace(",", "."));
		P[8] = Float.parseFloat(P9.getText().toString().replace(",", "."));

		I[0] = Float.parseFloat(I1.getText().toString().replace(",", "."));
		I[1] = Float.parseFloat(I2.getText().toString().replace(",", "."));
		I[2] = Float.parseFloat(I3.getText().toString().replace(",", "."));
		I[3] = Float.parseFloat(I4.getText().toString().replace(",", "."));
		I[4] = Float.parseFloat(I5.getText().toString().replace(",", "."));
		I[5] = Float.parseFloat(I6.getText().toString().replace(",", "."));
		I[6] = Float.parseFloat(I7.getText().toString().replace(",", "."));
		I[7] = Float.parseFloat(I8.getText().toString().replace(",", "."));
		I[8] = Float.parseFloat(I9.getText().toString().replace(",", "."));

		D[0] = Float.parseFloat(D1.getText().toString().replace(",", "."));
		D[1] = Float.parseFloat(D2.getText().toString().replace(",", "."));
		D[2] = Float.parseFloat(D3.getText().toString().replace(",", "."));
		D[3] = Float.parseFloat(D4.getText().toString().replace(",", "."));
		D[4] = Float.parseFloat(D5.getText().toString().replace(",", "."));
		D[5] = Float.parseFloat(D6.getText().toString().replace(",", "."));
		D[6] = Float.parseFloat(D7.getText().toString().replace(",", "."));
		D[7] = Float.parseFloat(D8.getText().toString().replace(",", "."));
		D[8] = Float.parseFloat(D9.getText().toString().replace(",", "."));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				app.mw.SendRequestSetPID(confRC_RATE, confRC_EXPO, rollPitchRate, yawRate, dynamic_THR_PID, throttle_MID, throttle_EXPO, P, I, D);

				app.mw.SendRequestWriteToEEprom();

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

	public void LoadProfilePIDOnClick(View v) {
		try {
			if (spinnerProfile.getCount() > 0)
				readFromXML("/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString());
		} catch (InvalidPropertiesFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void readFromXML(String fileName) throws InvalidPropertiesFormatException, IOException {

		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, fileName);
		Properties properties = new Properties();
		FileInputStream fis = new FileInputStream(file);
		properties.loadFromXML(fis);

		for (int i = 0; i < app.mw.PIDITEMS; i++) {
			P[i] = Float.parseFloat(properties.getProperty("pid." + i + ".p"));
			I[i] = Float.parseFloat(properties.getProperty("pid." + i + ".i"));
			D[i] = Float.parseFloat(properties.getProperty("pid." + i + ".d"));
		}

		P1.setText(String.format("%.1f", P[0]));
		P2.setText(String.format("%.1f", P[1]));
		P3.setText(String.format("%.1f", P[2]));
		P4.setText(String.format("%.1f", P[3]));
		P5.setText(String.format("%.2f", P[4]));
		P6.setText(String.format("%.1f", P[5]));
		P7.setText(String.format("%.1f", P[6]));
		P8.setText(String.format("%.1f", P[7]));
		P9.setText(String.format("%.1f", P[8]));

		I1.setText(String.format("%.3f", I[0]));
		I2.setText(String.format("%.3f", I[1]));
		I3.setText(String.format("%.3f", I[2]));
		I4.setText(String.format("%.3f", I[3]));
		I5.setText(String.format("%.1f", I[4]));
		I6.setText(String.format("%.2f", I[5]));
		I7.setText(String.format("%.2f", I[6]));
		I8.setText(String.format("%.3f", I[7]));
		I9.setText(String.format("%.3f", I[8]));

		D1.setText(String.format("%.0f", D[0]));
		D2.setText(String.format("%.0f", D[1]));
		D3.setText(String.format("%.0f", D[2]));
		D4.setText(String.format("%.0f", D[3]));
		D5.setText(String.format("%.0f", D[4]));
		D6.setText(String.format("%.3f", D[5]));
		D7.setText(String.format("%.3f", D[6]));
		D8.setText(String.format("%.0f", D[7]));
		D9.setText(String.format("%.3f", D[8]));

		RatePitchRoll1.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rollpitch.rate"))));
		RatePitchRoll2.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rollpitch.rate"))));

		RateYaw.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.yaw.rate"))));

		MIDThrottle.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.mid"))));
		EXPOThrottle.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.expo"))));

		RATE2PitchRoll.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rate"))));
		EXPOPitchRoll.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.expo"))));

		TPA.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.rate"))));

	}

	private void ShowData() {

		P1.setText(String.format("%.1f", (float) app.mw.byteP[0] / 10.0));
		P2.setText(String.format("%.1f", (float) app.mw.byteP[1] / 10.0));
		P3.setText(String.format("%.1f", (float) app.mw.byteP[2] / 10.0));
		P4.setText(String.format("%.1f", (float) app.mw.byteP[3] / 10.0));
		P5.setText(String.format("%.2f", (float) app.mw.byteP[4] / 100.0));
		P6.setText(String.format("%.1f", (float) app.mw.byteP[5] / 10.0));
		P7.setText(String.format("%.1f", (float) app.mw.byteP[6] / 10.0));
		P8.setText(String.format("%.1f", (float) app.mw.byteP[7] / 10.0));
		P9.setText(String.format("%.1f", (float) app.mw.byteP[8] / 10.0));

		I1.setText(String.format("%.3f", (float) I[0] / 1000.0));
		I2.setText(String.format("%.3f", (float) app.mw.byteI[1] / 1000.0));
		I3.setText(String.format("%.3f", (float) app.mw.byteI[2] / 1000.0));
		I4.setText(String.format("%.3f", (float) app.mw.byteI[3] / 1000.0));
		I5.setText(String.format("%.1f", (float) app.mw.byteI[4] / 100.0));
		I6.setText(String.format("%.2f", (float) app.mw.byteI[5] / 100.0));
		I7.setText(String.format("%.2f", (float) app.mw.byteI[6] / 100.0));
		I8.setText(String.format("%.3f", (float) app.mw.byteI[7] / 1000.0));
		I9.setText(String.format("%.3f", (float) app.mw.byteI[8] / 1000.0));

		D1.setText(String.format("%.0f", (float) app.mw.byteD[0]));
		D2.setText(String.format("%.0f", (float) app.mw.byteD[1]));
		D3.setText(String.format("%.0f", (float) app.mw.byteD[2]));
		D4.setText(String.format("%.0f", (float) app.mw.byteD[3]));
		D5.setText(String.format("%.0f", (float) app.mw.byteD[4]));
		D6.setText(String.format("%.3f", (float) app.mw.byteD[5] / 1000.0));
		D7.setText(String.format("%.3f", (float) app.mw.byteD[6] / 1000.0));
		D8.setText(String.format("%.0f", (float) app.mw.byteD[7]));
		D9.setText(String.format("%.3f", (float) app.mw.byteD[8]));

		RatePitchRoll1.setText(String.format("%.2f", (float) app.mw.byteRollPitchRate / 100.0));
		RatePitchRoll2.setText(String.format("%.2f", (float) app.mw.byteRollPitchRate / 100.0));

		RateYaw.setText(String.format("%.2f", (float) app.mw.byteYawRate / 100.0));

		MIDThrottle.setText(String.format("%.2f", (float) app.mw.byteThrottle_MID / 100.0));
		EXPOThrottle.setText(String.format("%.2f", (float) app.mw.byteThrottle_EXPO / 100.0));

		RATE2PitchRoll.setText(String.format("%.2f", (float) app.mw.byteRC_RATE / 100.0));
		EXPOPitchRoll.setText(String.format("%.2f", (float) app.mw.byteRC_EXPO / 100.0));

		TPA.setText(String.format("%.2f", (float) app.mw.byteDynThrPID / 100.0));
	}

	// ////////////////////////dialog

	public void TVOnClick0_250(View v) {
		CustomDialog(v, 0.25f);
	}

	public void TVOnClick2_50(View v) {
		CustomDialog(v, 2.5f);
	}
	
	public void TVOnClick1(View v) {
		CustomDialog(v, 1);
	}

	public void TVOnClick5(View v) {
		CustomDialog(v, 5);
	}

	public void TVOnClick20(View v) {
		CustomDialog(v, 20);
	}

	public void TVOnClick25(View v) {
		CustomDialog(v, 25);
	}
	
	public void TVOnClick100(View v) {
		CustomDialog(v, 100);
	}

	void CustomDialog(final View v, final float maxValue) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.custom_dialog, (ViewGroup) findViewById(R.id.your_dialog_root_element));

		Wheel w = (Wheel) layout.findViewById(R.id.wheel1);
		w.setValue(Functions.map(Float.parseFloat(((EditText) v).getText().toString().replace(",", ".")), 0f, maxValue, -1f, 1f), false);

		final TextView tv = (TextView) layout.findViewById(R.id.text);

		w.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStarted(Wheel view, float value, int roundValue) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScrollFinished(Wheel view, float value, int roundValue) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(Wheel view, float value, int roundValue) {
				float v;
				v = Functions.map(value, -1, 1, 0, maxValue);
				tv.setText(String.valueOf(v));

			}
		});

		alertDialogBuilder.setView(layout);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				((EditText) v).setText(String.valueOf(tv.getText().toString()));
			}
		});
		alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				dialog.cancel();
			}
		});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();

	}

	// /////////////////end dialog

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_pid, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuReadPID) {
			ReadOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.MenuSavePID) {
			SetOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.MenuResetPID) {
			ResetOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.MenuSharePID) {
			ShareIt();
			return true;
		}

		return false;
	}

	// ///menu end//////

}
