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
package com.ezio.multiwii.log;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class LogActivity extends SherlockActivity {

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.Frequentjobs();
			app.mw.SendRequest();
			mHandler.postDelayed(update, app.RefreshRate);

			Log.d(app.TAG, "loop " + this.getClass().getName());

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.logging_layout);

		app = (App) getApplication();
		app.Say(getString(R.string.Logging));

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mHandler.removeCallbacks(update);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		app.ForceLanguage();
	}

	public void ConvertToKMLOnClick(View v) {
		try {
			Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.eziosoft.multiwii.kmlconverter");
			startActivity(LaunchIntent);
		} catch (Exception e) {
			Intent goToMarket = null;
			goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.eziosoft.multiwii.kmlconverter"));
			startActivity(goToMarket);
		}
	}

	public void StartLoggingOnClick(View v) {
		app.mw.CreateNewLogFile();
		app.loggingON = true;
		mHandler.postDelayed(update, app.RefreshRate);

		Toast.makeText(getApplicationContext(), getString(R.string.Loggingstarted), Toast.LENGTH_LONG).show();

	}

	public void StopLoggingOnClick(View v) {
		app.loggingON = false;
		app.mw.CloseLoggingFile();

		Toast.makeText(getApplicationContext(), getString(R.string.Loggingstopedandsaved), Toast.LENGTH_LONG).show();

	}

}
