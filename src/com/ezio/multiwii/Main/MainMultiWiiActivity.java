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
package com.ezio.multiwii.Main;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.ezio.multiwii.AboutActivity;
import com.ezio.multiwii.AdvancedActivity;
import com.ezio.multiwii.App;
import com.ezio.multiwii.CheckBoxesActivity;
import com.ezio.multiwii.GPSActivity;
import com.ezio.multiwii.LogActivity;
import com.ezio.multiwii.OtherActivity;
import com.ezio.multiwii.PIDActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.config.ConfigActivity;
import com.ezio.multiwii.dashboard.Dashboard1Activity;
import com.ezio.multiwii.dashboard.Dashboard2Activity;
import com.ezio.multiwii.frsky.FrskyActivity;
import com.ezio.multiwii.graph.GraphsActivity;
import com.ezio.multiwii.map.MapActivityMy;
import com.ezio.multiwii.mapoffline.MapOfflineActivityMy;
import com.ezio.multiwii.motors.MotorsActivity;
import com.ezio.multiwii.mw.BT;
import com.ezio.multiwii.radio.RadioActivity;
import com.google.ads.AdRequest;
import com.google.ads.AdSize;
import com.google.ads.AdView;
import com.viewpagerindicator.TitlePageIndicator;

public class MainMultiWiiActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	TextView TVData;
	TextView TVinfo;

	private Handler mHandler = new Handler();

	// Button ButtonPID;
	// Button ButtonOther;

	ActionBarSherlock actionBar;

	AdView adView;
	public static final String MY_PUBLISHER_ID = "a15030365bc09b4";

	private void adMobConfig() {
		// request TEST ads to avoid being disabled for clicking your own ads
		AdRequest adRequest = new AdRequest();

		// test mode on EMULATOR
		adRequest.addTestDevice(AdRequest.TEST_EMULATOR);

		// test mode on DEVICE (this example code must be replaced with your
		// device uniquq ID)

		adRequest.addTestDevice("5A831EB94F5A7B11BB055E09E217A0DE");

		// create a Banner Ad
		adView = new AdView(this, AdSize.BANNER, MY_PUBLISHER_ID);

		// call the main layout from xml
		LinearLayout mainLayout = (LinearLayout) findViewById(R.id.linearLayoutData);

		// add the Banner Ad to our main layout
		mainLayout.addView(adView);
		// mainLayout.addView(TVData);

		// Initiate a request to load an ad in TEST mode. The test mode will
		// work only on emulators and your specific test device, the users will
		// get real ads.
		adView.loadAd(adRequest);

	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.d("aaa", "MAIN ON CREATE");
		requestWindowFeature(Window.FEATURE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiwii_main_layout3);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		MyPagerAdapter adapter = new MyPagerAdapter(this);

		adapter.SetTitles(new String[] { getString(R.string.page1),
				getString(R.string.page2), getString(R.string.page3) });
		final LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_1,
				(ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_2,
				(ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_3,
				(ViewGroup) null, false));
		viewPager.setAdapter(adapter);
		viewPager.setAdapter(adapter);

		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		titleIndicator.setViewPager(viewPager);

		app = (App) getApplication();

		// actionBar = getSherlock();
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		TVData = (TextView) findViewById(R.id.textViewData);
		TVinfo = (TextView) findViewById(R.id.TextViewInfo);

		// ButtonPID = (Button) findViewById(R.id.buttonPID);
		// ButtonOther = (Button) findViewById(R.id.buttonOther);

		if (app.ShowADS)
			adMobConfig();

	}

	@Override
	protected void onDestroy() {
		Close(null);
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();

	}

	@Override
	public void onResume() {
		Log.d("aaa", "MAIN ON RESUME");
		app.ForceLanguage();
		super.onResume();

		killme = false;

		if (app.MacAddress.equals("")) {
			TVData.setText(getString(R.string.MacNotSet));
		} else {
			TVData.setText("");
			if (!app.bt.Connected)
				TVData.setText(getString(R.string.InfoNotConnected));
		}

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(),
					0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		TVinfo.setText(getString(R.string.app_name) + " " + app_ver + "."
				+ String.valueOf(app_ver_code));

		if (app.bt.Connected) {

			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}

			mHandler.postDelayed(update, 100);
			Log.d(BT.TAG, "OnResume if connected");

		}

	}

	@Override
	public void onPause() {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		super.onPause();

	}

	public void Close(View v) {
		try {
			mHandler.removeCallbacksAndMessages(null);
			app.bt.CloseSocket();
			app.BTFrsky.CloseSocket();
		}

		catch (Exception e) {

		}

	}

	public void Connect(String MacAddress) {
		if (!app.MacAddress.equals("")) {
			app.bt.Connect(app.MacAddress);
			app.Say(getString(R.string.menu_connect));
		} else {
			Toast.makeText(
					getApplicationContext(),
					"Wrong MAC address. Go to Config and select correct device",
					Toast.LENGTH_LONG).show();
		}
		try {
			mHandler.removeCallbacksAndMessages(null);
		} catch (Exception e) {

		}
	}

	public void ConnectFrsky(String MacAddress) {
		if (!app.MacAddress.equals("")) {
			app.BTFrsky.Connect(app.MacAddressFrsky);
			app.Say(getString(R.string.Connect_frsky));
		} else {
			Toast.makeText(
					getApplicationContext(),
					"Wrong MAC address. Go to Config and select correct device",
					Toast.LENGTH_LONG).show();
		}
		try {
			mHandler.removeCallbacksAndMessages(null);
		} catch (Exception e) {

		}
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			TVData.setText("");
			log("version", app.mw.version);
			log("multiType", app.mw.MultiTypeName[app.mw.multiType] + "("
					+ String.valueOf(app.mw.multiType) + ")");

			log("cycleTime", app.mw.cycleTime);
			log("i2cError", app.mw.i2cError);

			log("gx", app.mw.gx);
			log("gy", app.mw.gy);
			log("gz", app.mw.gz);

			log("ax", app.mw.ax);
			log("ay", app.mw.ay);
			log("az", app.mw.az);

			log("magx", app.mw.magx);
			log("magy", app.mw.magy);
			log("magz", app.mw.magz);

			log("baro", app.mw.baro);
			log("alt", app.mw.alt);
			log("head", app.mw.head);

			log("angx", app.mw.angx);
			log("angy", app.mw.angy);
			log("bytevbat", app.mw.bytevbat);
			log("pMeterSum", app.mw.pMeterSum);

			log("nunchukPresent", app.mw.nunchukPresent);
			log("AccPresent", app.mw.AccPresent);
			log("BaroPresent", app.mw.BaroPresent);
			log("MagnetoPresent", app.mw.MagPresent);
			log("GPSPresent", app.mw.GPSPresent);
			log("SonarPresent", app.mw.SonarPresent);

			log("present", app.mw.present);
			log("mode", app.mw.mode);
			log("levelMode", app.mw.levelMode);

			log("byteThrottle_EXPO", app.mw.byteThrottle_EXPO);
			log("byteThrottle_MID", app.mw.byteThrottle_MID);

			log("GPS_fix", app.mw.GPS_fix);
			log("GPS_numSat", app.mw.GPS_numSat);
			log("GPS_update", app.mw.GPS_update);
			log("GPS_directionToHome", app.mw.GPS_directionToHome);
			log("GPS_distanceToHome", app.mw.GPS_distanceToHome);
			log("GPS_altitude", app.mw.GPS_altitude);
			log("GPS_speed", app.mw.GPS_speed);
			log("GPS_latitude", app.mw.GPS_latitude);
			log("GPS_longitude", app.mw.GPS_longitude);

			log("rcThrottle", app.mw.rcThrottle);
			log("rcYaw", app.mw.rcYaw);
			log("rcPitch", app.mw.rcPitch);
			log("rcRoll", app.mw.rcRoll);
			log("rcAUX1", app.mw.rcAUX1);
			log("rcAUX2", app.mw.rcAUX2);
			log("rcAUX3", app.mw.rcAUX3);
			log("rcAUX4", app.mw.rcAUX4);

			log("debug1", app.mw.debug1);
			log("debug2", app.mw.debug2);
			log("debug3", app.mw.debug3);
			log("debug4", app.mw.debug4);

			log("MSP_DEBUGMSG", app.mw.DebugMSG);

			for (int i = 0; i < app.mw.mot.length; i++) {
				log("Motor" + String.valueOf(i + 1), app.mw.mot[i]);
			}

			for (int i = 0; i < app.mw.PIDITEMS; i++) {
				log("P=" + String.valueOf(app.mw.byteP[i]) + " I="
						+ String.valueOf(app.mw.byteI[i]) + " D",
						app.mw.byteD[i]);
			}

			log("versionMisMatch", app.mw.versionMisMatch);

			app.frsky.ProcessSerialData(false);
			setSupportProgress((int) map(app.frsky.TxRSSI, 0, 110, 0, 10000));

			app.Frequentjobs();
			app.mw.SendRequest();
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);
		}

	};

	float map(float x, float in_min, float in_max, float out_min, float out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	private void log(String co, int wartosc) {
		TVData.append(co + "=" + String.valueOf(wartosc) + "\n");
	}

	private void log(String co, float wartosc) {
		TVData.append(co + "=" + String.valueOf(wartosc) + "\n");
	}

	private void log(String co, String wartosc) {
		TVData.append(co + "=" + (wartosc) + "\n");
	}

	// //buttons/////////////////////////////////////
	public void RadioOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), RadioActivity.class));
	}

	public void ConfigOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), ConfigActivity.class));
	}

	public void LoggingOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), LogActivity.class));
	}

	public void GPSOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), GPSActivity.class));

	}

	public void MotorsOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), MotorsActivity.class));
	}

	public void PIDOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), PIDActivity.class));
	}

	public void OtherOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), OtherActivity.class));
	}

	public void FrskyOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), FrskyActivity.class));
	}

	public void AUXOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(),
				CheckBoxesActivity.class));
	}

	public void Dashboard1OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(),
				Dashboard1Activity.class));
	}

	public void Dashboard2OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(),
				Dashboard2Activity.class));
	}

	public void MapOnClick(View v) {
		killme = true;
		if (app.UseOfflineMaps) {
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(),
					MapOfflineActivityMy.class));
		} else {
			killme = true;
			mHandler.removeCallbacksAndMessages(null);
			startActivity(new Intent(getApplicationContext(),
					MapActivityMy.class));
		}
	}

	public void AboutOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	public void GraphsOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), GraphsActivity.class));
	}

	public void AdvancedOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(),
				AdvancedActivity.class));
	}

	public void DonateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		Intent browserIntent = new Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EZ88MU3VKXSGG&lc=GB&item_name=MultiWiiAllinOne&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
		startActivity(browserIntent);
	}

	public void RateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id="
				+ getApplicationContext().getPackageName()));
		startActivity(intent);
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {
			if (app.DisableBTonExit) {

				app.bt.BTDisable();
			}

			app.mw.CloseLoggingFile();
			Close(null);
			System.exit(0);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect) {

			Connect(app.MacAddress);

			mHandler.postDelayed(update, 100);
			return true;
		}

		if (item.getItemId() == R.id.menu_connect_frsky) {

			ConnectFrsky(app.MacAddressFrsky);

			mHandler.postDelayed(update, 100);

			setSupportProgressBarVisibility(true);

			return true;
		}

		if (item.getItemId() == R.id.menu_disconnect) {
			app.Say(getString(R.string.menu_disconnect));
			app.bt.ConnectionLost = false;
			app.BTFrsky.ConnectionLost = false;
			Close(null);
			return true;
		}

		if (item.getItemId() == R.id.menu_vote) {
			RateOnClick(null);

			return true;
		}

		if (item.getItemId() == R.id.menuDonate) {
			DonateOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuOther) {
			OtherOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuConfig) {
			ConfigOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuAbout) {
			AboutOnClick(null);
			return true;
		}

		if (item.getItemId() == R.id.menuAdvanced) {
			AdvancedOnClick(null);
		}
		return false;
	}

	// ///menu end//////
}