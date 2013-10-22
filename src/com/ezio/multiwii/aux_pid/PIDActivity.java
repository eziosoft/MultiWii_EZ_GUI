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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.CustomInputDialog;

public class PIDActivity extends SherlockActivity {

	App app;
	// ActionBarSherlock actionBar;

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

	EditText RollPitchRate;
	EditText RollPitchRate2;
	EditText YawRate;

	EditText ThrottleMid;
	EditText ThrottleExpo;

	EditText RcRate;
	EditText RcExpo;

	EditText ThrottleRate;

	Spinner spinnerProfile;

	// used for write to eeprom
	float[] P;
	float[] I;
	float[] D;

	float confRC_RATE = 0, confRC_EXPO = 0, rollPitchRate = 0, yawRate = 0, dynamic_THR_PID = 0, throttle_MID = 0, throttle_EXPO = 0;

	// ///

	public void OpenInfoOnClick(View v) {
		app.OpenInfoOnClick(v);
	}

	public void SetPIDNames() {
		((TextView) findViewById(R.id.PIDName0)).setText(app.mw.PIDNames[0]);
		((TextView) findViewById(R.id.PIDName1)).setText(app.mw.PIDNames[1]);
		((TextView) findViewById(R.id.PIDName2)).setText(app.mw.PIDNames[2]);
		((TextView) findViewById(R.id.PIDName3)).setText(app.mw.PIDNames[3]);
		((TextView) findViewById(R.id.PIDName4)).setText(app.mw.PIDNames[4]);
		((TextView) findViewById(R.id.PIDName5)).setText(app.mw.PIDNames[5]);
		((TextView) findViewById(R.id.PIDName6)).setText(app.mw.PIDNames[6]);
		((TextView) findViewById(R.id.PIDName7)).setText(app.mw.PIDNames[7]);
		((TextView) findViewById(R.id.PIDName8)).setText(app.mw.PIDNames[8]);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pid_layout);

		getSupportActionBar().setTitle(getString(R.string.PID));

		app = (App) getApplication();
		// actionBar = getSherlock();

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

		RollPitchRate = (EditText) findViewById(R.id.editTextRatePitchRoll1);
		RollPitchRate2 = (EditText) findViewById(R.id.editTextRatePitchRoll2);
		YawRate = (EditText) findViewById(R.id.editTextRateYaw);

		ThrottleMid = (EditText) findViewById(R.id.editTextMIDThrottle);
		ThrottleExpo = (EditText) findViewById(R.id.editTextEXPOThrottle);

		RcRate = (EditText) findViewById(R.id.editTextRate2PitchRoll);
		RcExpo = (EditText) findViewById(R.id.editTextEXPOPitchRoll);

		ThrottleRate = (EditText) findViewById(R.id.editTextTPA);

		loadProfileFiles();

		SetPIDNames();

	}

	private void loadProfileFiles() {
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
							l.add(f.getName().replace(".mwi", ""));
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

		ReadOnClick(null);
	}

	public void ReadOnClick(View v) {
		app.mw.SendRequestMSP_PID_MSP_RC_TUNING();
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

		ShowData();

	}

	void ShareIt() {

		try {

			Log.d("aaa", "File to send:" + Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString());
			File myFile = new File(Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString());
			// MimeTypeMap mime = MimeTypeMap.getSingleton();
			// String ext =
			// myFile.getName().substring(myFile.getName().lastIndexOf(".") +
			// 1);
			// String type = mime.getMimeTypeFromExtension(ext);
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
		confRC_RATE = Float.parseFloat(RcRate.getText().toString().replace(",", "."));
		confRC_EXPO = Float.parseFloat(RcExpo.getText().toString().replace(",", "."));
		rollPitchRate = Float.parseFloat(RollPitchRate.getText().toString().replace(",", "."));
		yawRate = Float.parseFloat(YawRate.getText().toString().replace(",", "."));
		dynamic_THR_PID = Float.parseFloat(ThrottleRate.getText().toString().replace(",", "."));
		throttle_MID = Float.parseFloat(ThrottleMid.getText().toString().replace(",", "."));
		throttle_EXPO = Float.parseFloat(ThrottleExpo.getText().toString().replace(",", "."));

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

		if (v != null) {
			app.mw.SendRequestMSP_SET_PID(confRC_RATE, confRC_EXPO, rollPitchRate, yawRate, dynamic_THR_PID, throttle_MID, throttle_EXPO, P, I, D);
			app.mw.SendRequestMSP_EEPROM_WRITE();
			Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_SHORT).show();
		} else {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.Continue)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int id) {

					app.mw.SendRequestMSP_SET_PID(confRC_RATE, confRC_EXPO, rollPitchRate, yawRate, dynamic_THR_PID, throttle_MID, throttle_EXPO, P, I, D);
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

	}

	public void LoadProfilePIDOnClick(View v) {
		try {
			if (spinnerProfile.getCount() > 0)
				readFromXML("/MultiWiiLogs/" + spinnerProfile.getSelectedItem().toString() + ".mwi");
		} catch (InvalidPropertiesFormatException e) {

			e.printStackTrace();
		} catch (IOException e) {

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

		RollPitchRate.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rollpitch.rate"))));
		RollPitchRate2.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rollpitch.rate"))));

		YawRate.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.yaw.rate"))));

		ThrottleMid.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.mid"))));
		ThrottleExpo.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.expo"))));

		RcRate.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.rate"))));
		RcExpo.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.expo"))));

		ThrottleRate.setText(String.format("%.2f", Float.valueOf(properties.getProperty("rc.throttle.rate"))));

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

		I1.setText(String.format("%.3f", (float) app.mw.byteI[0] / 1000.0));
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

		RollPitchRate.setText(String.format("%.2f", (float) app.mw.byteRollPitchRate / 100.0));
		RollPitchRate2.setText(String.format("%.2f", (float) app.mw.byteRollPitchRate / 100.0));

		YawRate.setText(String.format("%.2f", (float) app.mw.byteYawRate / 100.0));

		ThrottleMid.setText(String.format("%.2f", (float) app.mw.byteThrottle_MID / 100.0));
		ThrottleExpo.setText(String.format("%.2f", (float) app.mw.byteThrottle_EXPO / 100.0));

		RcRate.setText(String.format("%.2f", (float) app.mw.byteRC_RATE / 100.0));
		RcExpo.setText(String.format("%.2f", (float) app.mw.byteRC_EXPO / 100.0));

		ThrottleRate.setText(String.format("%.2f", (float) app.mw.byteDynThrPID / 100.0));
	}

	public void SaveProfileOnClick(View v) {

		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.EnterFileName));
		// alert.setMessage(getString(R.string.Profile));

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		if (spinnerProfile.getCount() > 0) {
			input.setText(spinnerProfile.getSelectedItem().toString());
		}
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.Save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					SaveToXml(input.getText().toString());
				} catch (InvalidPropertiesFormatException e) {

					e.printStackTrace();
				} catch (IOException e) {

					e.printStackTrace();
				}

				loadProfileFiles();

			}
		});

		alert.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();

	}

	private void SaveToXml(String fileName) throws InvalidPropertiesFormatException, IOException {

		confRC_RATE = Float.parseFloat(RcRate.getText().toString().replace(",", "."));
		confRC_EXPO = Float.parseFloat(RcExpo.getText().toString().replace(",", "."));
		rollPitchRate = Float.parseFloat(RollPitchRate.getText().toString().replace(",", "."));
		yawRate = Float.parseFloat(YawRate.getText().toString().replace(",", "."));
		dynamic_THR_PID = Float.parseFloat(ThrottleRate.getText().toString().replace(",", "."));
		throttle_MID = Float.parseFloat(ThrottleMid.getText().toString().replace(",", "."));
		throttle_EXPO = Float.parseFloat(ThrottleExpo.getText().toString().replace(",", "."));

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

		File sdcard = Environment.getExternalStorageDirectory();
		File file = new File(sdcard, "/MultiWiiLogs/" + fileName + ".mwi");
		Properties properties = new Properties();
		FileOutputStream fos = new FileOutputStream(file);

		for (int i = 0; i < app.mw.PIDITEMS; i++) {
			properties.setProperty("pid." + i + ".p", String.valueOf(P[i]).replace(",", "."));
			properties.setProperty("pid." + i + ".i", String.valueOf(I[i]).replace(",", "."));
			properties.setProperty("pid." + i + ".d", String.valueOf(D[i]).replace(",", "."));
		}

		properties.setProperty("rc.rollpitch.rate", RollPitchRate.getText().toString().replace(",", "."));
		properties.setProperty("rc.yaw.rate", YawRate.getText().toString().replace(",", "."));
		properties.setProperty("rc.throttle.mid", ThrottleMid.getText().toString().replace(",", "."));
		properties.setProperty("rc.throttle.expo", ThrottleExpo.getText().toString().replace(",", "."));
		properties.setProperty("rc.rate", RcRate.getText().toString().replace(",", "."));
		properties.setProperty("rc.expo", RcExpo.getText().toString().replace(",", "."));
		properties.setProperty("rc.throttle.rate", ThrottleRate.getText().toString().replace(",", "."));

		properties.storeToXML(fos, new Date().toString());

		Toast.makeText(getApplicationContext(), getString(R.string.Settingssaved), Toast.LENGTH_SHORT).show();

	}

	public void ShowCustomDialogOnClick(final View vv) {
		CustomInputDialog.ShowCustomDialogOnClick(vv, this);
	}

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

		if (item.getItemId() == R.id.MenuSharePID) {
			ShareIt();
			return true;
		}

		return false;
	}

	// ///menu end//////

}
