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

//This is template which can be used to create new activities

package com.ezio.multiwii.notUsed;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class TemplateActivity extends Activity {

	private boolean		killme		= false;

	App app;
	Handler				mHandler	= new Handler();

	private Runnable	update		= new Runnable() {
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
		app = (App) getApplication();
		app.ForceLanguage();
		app.ConnectionBug();
		setContentView(R.layout.motor_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Motors));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

}
