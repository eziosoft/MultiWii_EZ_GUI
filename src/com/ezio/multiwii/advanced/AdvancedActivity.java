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
package com.ezio.multiwii.advanced;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.gps.MOCK_GPS_Service;

public class AdvancedActivity extends Activity {

	App app;
	CheckBox CheckBoxFollowMe;
	CheckBox CheckBoxFollowHeading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_layout);
		app = (App) getApplication();

		CheckBoxFollowMe = (CheckBox) findViewById(R.id.checkBoxFollowMe);
		CheckBoxFollowHeading = (CheckBox) findViewById(R.id.checkBoxFollowHeading);

	}

	@Override
	protected void onResume() {
		super.onResume();
		CheckBoxFollowMe.setChecked(app.FollowMeEnable);
		CheckBoxFollowHeading.setChecked(app.FollowHeading);
		
		try {
			stopService(new Intent(getApplicationContext(), MOCK_GPS_Service.class));
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		app.sensors.startMagACC();

	}

	@Override
	protected void onPause() {
		super.onPause();
		app.sensors.stopMagACC();
	}

	public void ControlOnClick(View v) {
		startActivity(new Intent(getApplicationContext(), ControlActivity.class));
	}

	public void AUXControlOnClick(View v) {
		startActivity(new Intent(getApplicationContext(), AUXControlActivity.class));
	}

	public void FollowMeCheckBoxOnClick(View v) {
		app.FollowMeEnable = CheckBoxFollowMe.isChecked();
	}

	public void FollowHeadingCheckBoxOnClick(View v) {
		app.FollowHeading = CheckBoxFollowHeading.isChecked();
	}

	public void StartMOCKLocationServiceOnClick(View v) {
		Intent service = new Intent(getApplicationContext(), MOCK_GPS_Service.class);
		startService(service);

		Intent startMain = new Intent(Intent.ACTION_MAIN);
		startMain.addCategory(Intent.CATEGORY_HOME);
		startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(startMain);
	}

	// TODO
	/*
	 * if (app.FollowMeBlinkFlag) {
	 * CheckBoxFollowMe.setBackgroundColor(Color.GREEN); } else {
	 * CheckBoxFollowMe.setBackgroundColor(Color.TRANSPARENT); } if
	 * (app.InjectGPSBlinkFlag) {
	 * CheckBoxInjectGPS.setBackgroundColor(Color.GREEN); } else {
	 * CheckBoxInjectGPS.setBackgroundColor(Color.TRANSPARENT); } if
	 * (app.FollowHeadingBlinkFlag) {
	 * CheckBoxFollowHeading.setBackgroundColor(Color.GREEN); } else {
	 * CheckBoxFollowHeading.setBackgroundColor(Color.TRANSPARENT); }
	 */

}
