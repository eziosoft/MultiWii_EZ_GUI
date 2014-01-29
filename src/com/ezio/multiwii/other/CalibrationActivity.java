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
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class CalibrationActivity extends SherlockActivity {
	App app;
	Handler mHandler = new Handler();
	private boolean killme = false;

	Button ButtonRxBIND;

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
		setContentView(R.layout.calibration_layout);

		app = (App) getApplication();

		getSupportActionBar().setTitle(getString(R.string.Calibration));

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
		app.Say(getString(R.string.Calibration));
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		Read();

		if (!app.mw.multi_Capability.RXBind) {
			((Button) findViewById(R.id.buttonRXBIND)).setEnabled(false);
		}

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	private void Read() {
		app.mw.SendRequestMSP_MISC();

		try {
			Thread.sleep(600);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		app.mw.ProcessSerialData(false);

	}

	public void ResetOnClick(View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(getString(R.string.ResetALLnotonlyPIDparamstodefault)).setCancelable(false).setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				app.mw.SendRequestMSP_RESET_CONF();
				Toast.makeText(getApplicationContext(), getString(R.string.Done), Toast.LENGTH_LONG).show();

			}
		}).setNegativeButton(getString(R.string.No), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}

	public void MSP_MISC_CONFreadOnClick(View v) {
		Read();
	}

	public void MagCalibrationOnClick(View v) {

		if (app.mw.communication.Connected) {
			app.mw.SendRequestMSP_MAG_CALIBRATION();
			ShowCountDown(getString(R.string.MagCalibration), getString(R.string.MagCalibrationDialogInfo), 30);
		}

	}

	public void AccCalibrationOnClick(View v) {
		if (app.mw.communication.Connected) {
			app.mw.SendRequestMSP_ACC_CALIBRATION();
			ShowCountDown(getString(R.string.AccCalibration), getString(R.string.ACCcalibrationDialogInfo), 10);
		}

	}

	public void RXBINDOnClick(View v) {
		app.mw.SendRequestMSP_BIND();
	}

	public void SetSerialBoudRateOnClick(View v) {
		app.mw.SendRequestMSP_ENABLE_FRSKY();
		app.mw.SendRequestMSP_SET_SERIAL_BAUDRATE(9600);
		app.commMW.Close();
	}

	private void ShowCountDown(String title, final String message, int time_s) {
		final ProgressDialog alertDialog = new ProgressDialog(this);
		alertDialog.setTitle(title);
		alertDialog.setMessage(message + "(" + "30" + "s)");
		alertDialog.setMax(time_s);
		alertDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

		alertDialog.show(); //

		new CountDownTimer(time_s * 1000, 1000) {
			@Override
			public void onTick(long millisUntilFinished) {
				alertDialog.setMessage(message + "(" + String.valueOf((millisUntilFinished / 1000) + "s)"));
				alertDialog.setProgress((int) (millisUntilFinished / 1000));
			}

			@Override
			public void onFinish() {
				alertDialog.dismiss();
			}
		}.start();
	}

}
