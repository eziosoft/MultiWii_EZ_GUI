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
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;
import com.ezio.multiwii.R;
import com.ezio.multiwii.about.AboutActivity;
import com.ezio.multiwii.about.InfoActivity;
import com.ezio.multiwii.advanced.AdvancedActivity;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.aux_pid.AUXActivity;
import com.ezio.multiwii.aux_pid.PIDActivity;
import com.ezio.multiwii.aux_pid.ServosActivity;
import com.ezio.multiwii.config.ConfigActivity;
import com.ezio.multiwii.dashboard.Dashboard1Activity;
import com.ezio.multiwii.dashboard.Dashboard2Activity;
import com.ezio.multiwii.dashboard.Dashboard4Activity;
import com.ezio.multiwii.dashboard.dashboard3.Dashboard3Activity;
import com.ezio.multiwii.frsky.FrskyActivity;
import com.ezio.multiwii.gps.GPSActivity;
import com.ezio.multiwii.gps.MOCK_GPS_Service;
import com.ezio.multiwii.graph.GraphsActivity;
import com.ezio.multiwii.helpers.Functions;
import com.ezio.multiwii.log.LogActivity;
import com.ezio.multiwii.motors.MotorsActivity;
import com.ezio.multiwii.other.OtherActivity;
import com.ezio.multiwii.radio.RadioActivity;
import com.ezio.multiwii.raw.RawDataActivity;
import com.ezio.multiwii.waypoints.MapWaypointsActivity;
import com.ezio.multiwii.waypoints.WaypointActivity;
import com.ezio.sec.Sec;
import com.viewpagerindicator.TitlePageIndicator;

public class MainMultiWiiActivity extends SherlockActivity {

	private boolean killme = false;

	App app;

	// TextView TVinfo;
	TextView TVInfo;

	private Handler mHandler = new Handler();

	ActionBarSherlock actionBar;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		app = (App) getApplication();

		Log.d("aaa", "MAIN ON CREATE");
		requestWindowFeature(Window.FEATURE_PROGRESS);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.multiwii_main_layout3);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		MyPagerAdapter adapter = new MyPagerAdapter(this);

		adapter.SetTitles(new String[] { getString(R.string.page1), getString(R.string.page2), getString(R.string.page3) });
		final LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_1, (ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_2, (ViewGroup) null, false));
		adapter.AddView(inflater.inflate(R.layout.multiwii_main_layout3_3, (ViewGroup) null, false));

		TVInfo = (TextView) adapter.views.get(0).findViewById(R.id.textViewInfoFirstPage);

		viewPager.setAdapter(adapter);

		TitlePageIndicator titleIndicator = (TitlePageIndicator) findViewById(R.id.indicator);
		titleIndicator.setViewPager(viewPager);

		getSupportActionBar().setDisplayShowTitleEnabled(false);

		// TVinfo = (TextView) findViewById(R.id.TextViewInfo);

		if ((app.AppStartCounter % 10 == 0 && app.DonateButtonPressed == 0)) {
			if (Sec.VerifyDeveloperID(Sec.GetDeviceID(getApplicationContext()), Sec.TestersIDs) || Sec.Verify(getApplicationContext(), "D.3")) {
			} else {
				killme = true;
				mHandler.removeCallbacksAndMessages(null);
				startActivity(new Intent(getApplicationContext(), InfoActivity.class));
			}
		}

		app.AppStartCounter++;
		app.SaveSettings(true);

		setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onResume() {
		Log.d("aaa", "MAIN ON RESUME");
		app.ForceLanguage();
		super.onResume();

		killme = false;

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		TVInfo.setText(getString(R.string.app_name) + " " + app_ver + "." + String.valueOf(app_ver_code));

		if (app.commMW.Connected || app.commFrsky.Connected) {

			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}

			mHandler.postDelayed(update, 100);
			// Log.d(BT_old.TAG, "OnResume if connected");

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
			app.commMW.Close();
			app.commFrsky.Close();
		}

		catch (Exception e) {

		}

	}

	public void Connect(String MacAddress) {
		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_SERIAL_FTDI || app.CommunicationTypeMW == App.COMMUNICATION_TYPE_SERIAL_OTHERCHIPS) {
			app.commMW.Connect(app.SerialPortBaudRateMW);
		}

		if (app.CommunicationTypeMW == App.COMMUNICATION_TYPE_BT) {
			if (!app.MacAddress.equals("")) {
				app.commMW.Connect(app.MacAddress);
				app.Say(getString(R.string.menu_connect));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {
			}
		}
	}

	public void ConnectFrsky(String MacAddress) {
		if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_SERIAL_FTDI) {
			app.commMW.Connect(app.SerialPortBaudRateFrSky);
		}

		if (app.CommunicationTypeFrSky == App.COMMUNICATION_TYPE_BT) {
			if (!app.MacAddressFrsky.equals("")) {
				app.commFrsky.Connect(app.MacAddressFrsky);
				app.Say(getString(R.string.Connect_frsky));
			} else {
				Toast.makeText(getApplicationContext(), "Wrong MAC address. Go to Config and select correct device", Toast.LENGTH_LONG).show();
			}
			try {
				mHandler.removeCallbacksAndMessages(null);
			} catch (Exception e) {

			}
		}
	}

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.frskyProtocol.ProcessSerialData(false);
			setSupportProgress((int) Functions.map(app.frskyProtocol.TxRSSI, 0, 110, 0, 10000));

			String t = new String();
			if (app.mw.BaroPresent == 1)
				t += "BARO ";
			if (app.mw.GPSPresent == 1)
				t += "GPS ";
			if (app.mw.SonarPresent == 1)
				t += "SONAR ";
			if (app.mw.MagPresent == 1)
				t += "MAG ";
			if (app.mw.AccPresent == 1)
				t += "ACC";
			TVInfo.setText("MultiWii " + String.valueOf(app.mw.version/100f) + "\n" + app.mw.MultiTypeName[app.mw.multiType] + "\n" + t);

			app.Frequentjobs();
			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());
		}

	};

	// //buttons/////////////////////////////////////

	public void RawDataOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), RawDataActivity.class));
	}

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
		startActivity(new Intent(getApplicationContext(), AUXActivity.class));
	}

	public void Dashboard1OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), Dashboard1Activity.class));
	}

	public void Dashboard2OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), Dashboard2Activity.class));
	}

	public void Dashboard3OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), Dashboard3Activity.class));
	}

	public void Dashboard4OnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), Dashboard4Activity.class));
	}

	public void NewMapOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), MapWaypointsActivity.class).putExtra("WAYPOINT", false));
	}

	public void WaypointsMapOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), MapWaypointsActivity.class).putExtra("WAYPOINT", true));

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
		startActivity(new Intent(getApplicationContext(), AdvancedActivity.class));
	}

	public void DonateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=EZ88MU3VKXSGG&lc=GB&item_name=MultiWiiAllinOne&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donate_SM%2egif%3aNonHosted"));
		startActivity(browserIntent);
	}

	public void RateOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
		startActivity(intent);
	}

	public void TestOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), WaypointActivity.class));
	}

	public void CommunityMapOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://maps.google.com/maps?q=http:%2F%2Fezio.ovh.org%2Fkml2.php&hl=pl&sll=48.856612,2.366095&sspn=0.015614,0.042272&t=h&z=3")));
	}

	public void ServosOnClick(View v) {
		killme = true;
		mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), ServosActivity.class));
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		menu.findItem(R.id.menu_connect_frsky).setVisible(app.FrskySupport);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_exit) {

			try {
				stopService(new Intent(getApplicationContext(), MOCK_GPS_Service.class));
			} catch (Exception e) {
				// TODO: handle exception
			}

			if (app.DisableBTonExit) {
				app.commMW.Disable();
				app.commFrsky.Disable();
			}

			app.sensors.stop();
			app.mw.CloseLoggingFile();
			app.notifications.Cancel(99);
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
			app.commMW.ConnectionLost = false;
			app.commFrsky.ConnectionLost = false;
			Close(null);
			return true;
		}

		return false;
	}

	// ///menu end//////

}