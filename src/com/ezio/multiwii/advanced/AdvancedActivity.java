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
import android.os.Bundle;
import android.view.View;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class AdvancedActivity extends Activity {

	App app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.advanced_layout);
		app = (App) getApplication();
		// app.Say(getString(R.string.AdvancedWarning).toLowerCase());
	}

	public void ControlOnClick(View v) {

		startActivity(new Intent(getApplicationContext(), ControlActivity.class));

	}

	public void AUXControlOnClick(View v) {
		// killme = true;
		// mHandler.removeCallbacksAndMessages(null);
		startActivity(new Intent(getApplicationContext(), AUXControlActivity.class));
	}

}
