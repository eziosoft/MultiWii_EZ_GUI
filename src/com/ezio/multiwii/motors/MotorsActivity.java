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
package com.ezio.multiwii.motors;

import java.util.Random;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class MotorsActivity extends SherlockActivity {
	
	private boolean		killme		= false;

	App					app;
	Handler				mHandler	= new Handler();

	final Random		myRandom	= new Random();

	ProgressBar			M1;
	ProgressBar			M2;
	ProgressBar			M3;
	ProgressBar			M4;
	ProgressBar			M5;
	ProgressBar			M6;
	ProgressBar			M7;
	ProgressBar			M8;

	ProgressBar			S1;
	ProgressBar			S2;
	ProgressBar			S3;
	ProgressBar			S4;
	ProgressBar			S5;
	ProgressBar			S6;
	ProgressBar			S7;
	ProgressBar			S8;

	TextView			TVM1;
	TextView			TVM2;
	TextView			TVM3;
	TextView			TVM4;
	TextView			TVM5;
	TextView			TVM6;
	TextView			TVM7;
	TextView			TVM8;

	private Runnable	update		= new Runnable() {
										@Override
										public void run() {

											app.mw.ProcessSerialData(app.loggingON);

											if (app.mw.mot[0] >= 1000)
												M1.setProgress((int) app.mw.mot[0] - 1000);
											if (app.mw.mot[1] >= 1000)
												M2.setProgress((int) app.mw.mot[1] - 1000);
											if (app.mw.mot[2] >= 1000)
												M3.setProgress((int) app.mw.mot[2] - 1000);
											if (app.mw.mot[3] >= 1000)
												M4.setProgress((int) app.mw.mot[3] - 1000);
											if (app.mw.mot[4] >= 1000)
												M5.setProgress((int) app.mw.mot[4] - 1000);
											if (app.mw.mot[5] >= 100)
												M6.setProgress((int) app.mw.mot[5] - 1000);
											if (app.mw.mot[6] >= 1000)
												M7.setProgress((int) app.mw.mot[6] - 1000);
											if (app.mw.mot[7] >= 1000)
												M8.setProgress((int) app.mw.mot[7] - 1000);

											TVM1.setText(Integer.toString((int) app.mw.mot[0]));
											TVM2.setText(Integer.toString((int) app.mw.mot[1]));
											TVM3.setText(Integer.toString((int) app.mw.mot[2]));
											TVM4.setText(Integer.toString((int) app.mw.mot[3]));
											TVM5.setText(Integer.toString((int) app.mw.mot[4]));
											TVM6.setText(Integer.toString((int) app.mw.mot[5]));
											TVM7.setText(Integer.toString((int) app.mw.mot[6]));
											TVM8.setText(Integer.toString((int) app.mw.mot[7]));

											if (app.mw.servo[0] >= 1000)
												S1.setProgress((int) app.mw.servo[0] - 1000);
											if (app.mw.servo[1] >= 1000)
												S2.setProgress((int) app.mw.servo[1] - 1000);
											if (app.mw.servo[2] >= 1000)
												S3.setProgress((int) app.mw.servo[2] - 1000);
											if (app.mw.servo[3] >= 1000)
												S4.setProgress((int) app.mw.servo[3] - 1000);
											if (app.mw.servo[4] >= 1000)
												S5.setProgress((int) app.mw.servo[4] - 1000);
											if (app.mw.servo[5] >= 1000)
												S6.setProgress((int) app.mw.servo[5] - 1000);
											if (app.mw.servo[6] >= 1000)
												S7.setProgress((int) app.mw.servo[6] - 1000);
											if (app.mw.servo[7] >= 1000)
												S8.setProgress((int) app.mw.servo[7] - 1000);

											app.Frequentjobs();

											app.mw.SendRequest(app.MainRequestMethod);
											if (!killme)mHandler.postDelayed(update, app.RefreshRate);

											if(app.D)		Log.d(app.TAG, "loop "+this.getClass().getName());
										}
									};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.motor_layout);
		getSupportActionBar().setTitle(getString(R.string.Motors));
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// motorsView = (MotorView) findViewById(R.id.motorView1);
		// motorsView.SetMotorsCount(4, true);

		M1 = (ProgressBar) findViewById(R.id.progressBar1);
		M2 = (ProgressBar) findViewById(R.id.progressBar2);
		M3 = (ProgressBar) findViewById(R.id.progressBar3);
		M4 = (ProgressBar) findViewById(R.id.progressBar4);
		M5 = (ProgressBar) findViewById(R.id.progressBar5);
		M6 = (ProgressBar) findViewById(R.id.progressBar6);
		M7 = (ProgressBar) findViewById(R.id.progressBar7);
		M8 = (ProgressBar) findViewById(R.id.progressBar8);

		TVM1 = (TextView) findViewById(R.id.textViewM1);
		TVM2 = (TextView) findViewById(R.id.textViewM2);
		TVM3 = (TextView) findViewById(R.id.textViewM3);
		TVM4 = (TextView) findViewById(R.id.textViewM4);
		TVM5 = (TextView) findViewById(R.id.textViewM5);
		TVM6 = (TextView) findViewById(R.id.textViewM6);
		TVM7 = (TextView) findViewById(R.id.textViewM7);
		TVM8 = (TextView) findViewById(R.id.textViewM8);

		S1 = (ProgressBar) findViewById(R.id.ProgressBar9);
		S2 = (ProgressBar) findViewById(R.id.ProgressBar10);
		S3 = (ProgressBar) findViewById(R.id.ProgressBar11);
		S4 = (ProgressBar) findViewById(R.id.ProgressBar12);
		S5 = (ProgressBar) findViewById(R.id.ProgressBar13);
		S6 = (ProgressBar) findViewById(R.id.ProgressBar14);
		S7 = (ProgressBar) findViewById(R.id.ProgressBar15);
		S8 = (ProgressBar) findViewById(R.id.ProgressBar16);

		M1.setMax(1000);
		M2.setMax(1000);
		M3.setMax(1000);
		M4.setMax(1000);
		M5.setMax(1000);
		M6.setMax(1000);
		M7.setMax(1000);
		M8.setMax(1000);

		S1.setMax(1000);
		S2.setMax(1000);
		S3.setMax(1000);
		S4.setMax(1000);
		S5.setMax(1000);
		S6.setMax(1000);
		S7.setMax(1000);
		S8.setMax(1000);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme=true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Motors));
		killme=false;
		mHandler.postDelayed(update, app.RefreshRate);

	}

}
