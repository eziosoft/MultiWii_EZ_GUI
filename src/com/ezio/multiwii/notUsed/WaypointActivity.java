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
package com.ezio.multiwii.notUsed;

import android.os.Bundle;
import android.os.Handler;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.App;

public class WaypointActivity extends SherlockActivity {
	private boolean			killme		= false;
	
	App					app;
	Handler				mHandler	= new Handler();

	private Runnable	update		= new Runnable() {
										@Override
										public void run() {

											app.mw.ProcessSerialData(app.loggingON);

											app.Frequentjobs();

											;
											if (!killme)mHandler.postDelayed(update, 1000);

										}
									};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.maplayout);

		app = (App) getApplication();

	}

	@Override
	protected void onResume() {
		super.onResume();
		killme=false;
		mHandler.postDelayed(update, app.RefreshRate);
		// app.Speak(getString(R.string.Map));

	}

	@Override
	protected void onPause() {
		super.onPause();
		killme=true;
		mHandler.removeCallbacks(null);
	}

}
