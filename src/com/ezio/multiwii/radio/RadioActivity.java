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
package com.ezio.multiwii.radio;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.App;
import com.ezio.multiwii.R;

public class RadioActivity extends SherlockActivity {

	private boolean			killme		= false;
	
	App					app;
	StickView			SV1;
	StickView			SV2;
	ProgressBar			pb1;
	ProgressBar			pb2;
	ProgressBar			pb3;
	ProgressBar			pb4;

	TextView			AccActiveTV;
	TextView			BaroActiveTV;
	TextView			MagnetoActiveTV;
	TextView			GpsActiveTV;
	TextView			SonarActiveTV;

	TextView			LEVELTV;						// 1
	TextView			BAROTV;						// 2
	TextView			MAGTV;							// 3
	TextView			CAMSTABTV;						// 4
	TextView			CAMTRIGTV;						// 5
	TextView			ARMTV;							// 6
	TextView			GPS_HOMETV;					// 7
	TextView			GPS_HOLDTV;					// 8
	TextView			PASSTHRUTV;					// 9
	TextView			HEADFREETV;					// 10
	TextView			BEEPERTV;						// 11
	TextView			LEDMAXTV;						// 12
	TextView			LLIGHTSTV;						// 13
	TextView			HEADADJTV;						// 14

	Handler				mHandler	= new Handler();

	private Runnable	update		= new Runnable() {
										@Override
										public void run() {

											app.mw.ProcessSerialData(app.loggingON);

											if (app.RadioMode == 2) {
												SV1.SetPosition(app.mw.rcYaw, app.mw.rcThrottle);
												SV2.SetPosition(app.mw.rcRoll, app.mw.rcPitch);
											}

											if (app.RadioMode == 1) {
												SV1.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
												SV2.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
											}

											pb1.setProgress((int) (app.mw.rcAUX1 - 1000));
											pb2.setProgress((int) (app.mw.rcAUX2 - 1000));
											pb3.setProgress((int) (app.mw.rcAUX3 - 1000));
											pb4.setProgress((int) (app.mw.rcAUX4 - 1000));

											SetTextViewColorOnOFF(app.mw.I2cAccActive, AccActiveTV);
											SetTextViewColorOnOFF(app.mw.I2cBaroActive, BaroActiveTV);
											SetTextViewColorOnOFF(app.mw.I2cMagnetoActive, MagnetoActiveTV);
											SetTextViewColorOnOFF(app.mw.GPSActive, GpsActiveTV);

											SetTextViewColorOnOFF(app.mw.ActiveModes[0], LEVELTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[1], BAROTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[2], MAGTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[3], CAMSTABTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[4], CAMTRIGTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[5], ARMTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[6], GPS_HOMETV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[7], GPS_HOLDTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[8], PASSTHRUTV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[9], HEADFREETV);
											SetTextViewColorOnOFF(app.mw.ActiveModes[10], BEEPERTV);
											// added in 2.1

											if (app.Protocol > 210) {
												SetTextViewColorOnOFF(app.mw.ActiveModes[11], LEDMAXTV);
												SetTextViewColorOnOFF(app.mw.ActiveModes[12], LLIGHTSTV);
												SetTextViewColorOnOFF(app.mw.ActiveModes[13], HEADADJTV);
											}
											// /////////////////////////////////////////////

											app.Frequentjobs();

											app.mw.SendRequest();
											if (!killme)mHandler.postDelayed(update, app.REFRESH_RATE);

										}
									};

	private void SetTextViewColorOnOFF(boolean state, TextView TV) {
		if (state) {
			TV.setBackgroundColor(getResources().getColor(R.color.Green));
			TV.setTextColor(Color.BLACK);
		}
		else {
			TV.setBackgroundColor(getResources().getColor(R.color.Red));
			TV.setTextColor(Color.WHITE);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.radio_layout);

		app = (App) getApplication();

		SV1 = (StickView) findViewById(R.id.StickView1);
		SV2 = (StickView) findViewById(R.id.StickView2);

		pb1 = (ProgressBar) findViewById(R.id.progressBar1);
		pb2 = (ProgressBar) findViewById(R.id.progressBar2);
		pb3 = (ProgressBar) findViewById(R.id.progressBar3);
		pb4 = (ProgressBar) findViewById(R.id.progressBar4);

		AccActiveTV = (TextView) findViewById(R.id.textViewAccActive);
		BaroActiveTV = (TextView) findViewById(R.id.textViewBaroActive);
		GpsActiveTV = (TextView) findViewById(R.id.textViewGpsActive);
		MagnetoActiveTV = (TextView) findViewById(R.id.textViewMagnetoActive);
		SonarActiveTV = (TextView) findViewById(R.id.TextViewSonarActive);
		
		SonarActiveTV.setVisibility(View.GONE);

		LEVELTV = (TextView) findViewById(R.id.TextViewLEVEL);
		BAROTV = (TextView) findViewById(R.id.TextViewBARO);
		MAGTV = (TextView) findViewById(R.id.TextViewMAG);
		CAMSTABTV = (TextView) findViewById(R.id.TextViewCAMSTAB);
		CAMTRIGTV = (TextView) findViewById(R.id.TextViewCAMTRIG);
		ARMTV = (TextView) findViewById(R.id.TextViewARM);
		GPS_HOMETV = (TextView) findViewById(R.id.TextViewGPS_HOME);
		GPS_HOLDTV = (TextView) findViewById(R.id.TextViewGPS_HOLD);
		PASSTHRUTV = (TextView) findViewById(R.id.TextViewPASSTHRU);
		HEADFREETV = (TextView) findViewById(R.id.TextViewHEADFREE);
		BEEPERTV = (TextView) findViewById(R.id.TextViewBEEPER);
		LEDMAXTV = (TextView) findViewById(R.id.TextViewLEDMAX);
		LLIGHTSTV = (TextView) findViewById(R.id.TextViewLLIGHTS);
		HEADADJTV = (TextView) findViewById(R.id.TextViewHEADADJ);

		app.Say(getString(R.string.RadioMode) + " " + String.valueOf(app.RadioMode));

	}

	@Override
	protected void onPause() {
		super.onPause();
		killme=true;
		mHandler.removeCallbacks(update);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();

		if (app.Protocol > 20) {
			AccActiveTV.setVisibility(View.GONE);
			BaroActiveTV.setVisibility(View.GONE);
			GpsActiveTV.setVisibility(View.GONE);
			MagnetoActiveTV.setVisibility(View.GONE);

		}
		killme=false;
		mHandler.postDelayed(update, app.REFRESH_RATE);
	}

}
