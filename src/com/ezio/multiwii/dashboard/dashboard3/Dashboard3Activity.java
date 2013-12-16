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
package com.ezio.multiwii.dashboard.dashboard3;

import java.util.Random;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.Functions;
import com.ezio.multiwii.waypoints.MapHelperClass;
import com.ezio.sec.Sec;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class Dashboard3Activity extends SherlockFragmentActivity {
	private boolean killme = false;

	MapHelperClass mapHelperClass;

	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	HorizonView horizonView;
	AltitudeView altitudeView;
	HeadingView headingView;
	VarioView varioView;

	TextView TextViewd31;
	TextView TextViewd32;
	TextView TextViewd33;
	TextView TextViewd34;
	TextView TextViewStatus;

	ProgressBar ProgressBarTx;
	ProgressBar ProgressBarRx;

	long timer1 = 0;
	boolean MoveMap = true;
	private long centerStep = 0;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			if (timer1 < System.currentTimeMillis()) {
				app.mw.ProcessSerialData(app.loggingON);
				app.frskyProtocol.ProcessSerialData(false);

				if (app.D) {
					app.mw.GPS_latitude += random.nextInt(200) - 50;// simulation
					app.mw.GPS_longitude += random.nextInt(100) - 50;// simulation
					app.mw.GPS_fix = 1;
					app.mw.head++;
				}

				String state = "";
				for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
					if (app.mw.ActiveModes[i]) {
						state += " " + app.mw.buttonCheckboxLabel[i];
					}
				}

				TextViewd31.setText(String.valueOf(app.mw.GPS_numSat));
				if (app.mw.GPS_update % 2 == 0) {
					TextViewd31.append("*");
				}
				TextViewd32.setText(String.valueOf(app.mw.bytevbat / 10f) + "V");

				TextViewd33.setText(String.valueOf(app.mw.GPS_distanceToHome) + "m");
				TextViewd34.setText(String.valueOf(app.mw.pMeterSum) + "/" + String.valueOf(app.mw.intPowerTrigger) + "(" + String.valueOf(Functions.map(app.mw.pMeterSum, 1, app.mw.intPowerTrigger, 100, 0)) + "%)");
				TextViewStatus.setText(state);

				if (app.frskyProtocol.RxRSSI > 0 || app.frskyProtocol.TxRSSI > 0) {
					ProgressBarRx.setVisibility(View.VISIBLE);
					ProgressBarTx.setVisibility(View.VISIBLE);
					ProgressBarRx.setProgress(app.frskyProtocol.RxRSSI);
					ProgressBarTx.setProgress(app.frskyProtocol.TxRSSI);
				} else {
					ProgressBarRx.setVisibility(View.GONE);
					ProgressBarTx.setVisibility(View.GONE);
				}
				app.Frequentjobs();
				app.mw.SendRequest(app.MainRequestMethod);

				timer1 = System.currentTimeMillis() + app.RefreshRate;

			}

			// ///////////////////////

			LatLng copterPositionLatLng = new LatLng(app.mw.GPS_latitude / Math.pow(10, 7), app.mw.GPS_longitude / Math.pow(10, 7));
			mapHelperClass.SetCopterLocation(copterPositionLatLng, app.mw.head, app.mw.alt);
			mapHelperClass.DrawFlightPath(copterPositionLatLng);
			mapHelperClass.PositionHoldMarker.setPosition(new LatLng(app.mw.Waypoints[16].Lat / Math.pow(10, 7), app.mw.Waypoints[16].Lon / Math.pow(10, 7)));
			mapHelperClass.HomeMarker.setPosition(new LatLng(app.mw.Waypoints[0].Lat / Math.pow(10, 7), app.mw.Waypoints[0].Lon / Math.pow(10, 7)));

			// Map centering
			if (MoveMap && centerStep < System.currentTimeMillis()) {
				if (app.mw.GPS_fix == 1) {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(copterPositionLatLng, app.MapZoomLevel, 0, app.mw.head)));
				} else {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(app.sensors.PhoneLatitude, app.sensors.PhoneLongitude), app.MapZoomLevel, 0, 0)));
				}
				centerStep = System.currentTimeMillis() + app.MapCenterPeriod * 1000;
			}

			int a = 1; // used for reverce roll in artificial horyzon
			if (app.ReverseRoll) {
				a = -1;
			}
			horizonView.Set(-app.mw.angx * a, -app.mw.angy * 1.5f);
			altitudeView.Set(app.mw.alt * 10);
			headingView.Set(app.mw.head);
			varioView.Set(app.mw.vario * 0.6f);
			// ///////////////////////

			if (!killme)
				mHandler.postDelayed(update, 50);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		app = (App) getApplication();
		app.ForceLanguage();
	
		setContentView(R.layout.dashboard3_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getSupportActionBar().hide();

		mapHelperClass = new MapHelperClass(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(), 5);

		horizonView = (HorizonView) findViewById(R.id.horizonView1);
		varioView = (VarioView) findViewById(R.id.varioView1);
		headingView = (HeadingView) findViewById(R.id.headingView1);
		altitudeView = (AltitudeView) findViewById(R.id.altitudeView1);

		TextViewd31 = (TextView) findViewById(R.id.TextViewd31);
		TextViewd32 = (TextView) findViewById(R.id.TextViewd32);
		TextViewd33 = (TextView) findViewById(R.id.TextViewd33);
		TextViewd34 = (TextView) findViewById(R.id.TextViewd34);
		TextViewStatus = (TextView) findViewById(R.id.textViewStatus);

		ProgressBarTx = (ProgressBar) findViewById(R.id.progressBarTx);
		ProgressBarRx = (ProgressBar) findViewById(R.id.progressBarRx);

		ProgressBarRx.setMax(110);
		ProgressBarTx.setMax(110);

		mapHelperClass.map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (app.mw.GPS_fix == 1)
					app.MapZoomLevel = (int) position.zoom;
			}
		});
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Dashboard3));
		killme = false;

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
		app.SaveSettings(true);
	}

}
