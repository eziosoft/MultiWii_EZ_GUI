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

package nav;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TableLayout;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class WPEditorActivity extends Activity {

	App app;
	String MarkerId;

	TextView TVWPTitle;
	Spinner SpinnerAction;
	EditText ETAltitude;
	EditText ETParameter1;
	EditText ETParameter2;
	EditText ETParameter3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		app.ForceLanguage();

		setContentView(R.layout.nav_wp_editor);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		getWindow().setLayout(LayoutParams.MATCH_PARENT /* width */, LayoutParams.WRAP_CONTENT /* height */);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			MarkerId = extras.getString("MARKERID");
		}

		TVWPTitle = (TextView) findViewById(R.id.textViewWPTitle);
		ETAltitude = (EditText) findViewById(R.id.editTextAltitude);
		ETParameter1 = (EditText) findViewById(R.id.editTextParameter1);
		ETParameter2 = (EditText) findViewById(R.id.editTextParameter2);
		ETParameter3 = (EditText) findViewById(R.id.editTextParameter3);
		SpinnerAction = (Spinner) findViewById(R.id.spinnerAction);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.wp_actions, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		SpinnerAction.setAdapter(adapter);

		((CheckBox) findViewById(R.id.checkBoxCircle)).setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					((TableLayout) findViewById(R.id.TLCircle)).setVisibility(View.VISIBLE);
					((TableLayout) findViewById(R.id.TLWP)).setVisibility(View.GONE);
				} else {
					((TableLayout) findViewById(R.id.TLCircle)).setVisibility(View.GONE);
					((TableLayout) findViewById(R.id.TLWP)).setVisibility(View.VISIBLE);
				}

			}
		});

		loadWPData();

	}

	void loadWPData() {
		for (WaypointNav WP : app.mw.WaypointsList) {
			if (WP.MarkerId.equals(MarkerId)) {
				TVWPTitle.setText(WP.getMarkerTitle());
				ETAltitude.setText(String.valueOf(WP.Altitude));
				ETParameter1.setText(String.valueOf(WP.Parameter1));
				ETParameter2.setText(String.valueOf(WP.Parameter2));
				ETParameter3.setText(String.valueOf(WP.Parameter3));
				SpinnerAction.setSelection(WP.Action - 1);
				return;
			}
		}
	}

	void updateWPData() {
		for (WaypointNav WP : app.mw.WaypointsList) {
			if (WP.MarkerId.equals(MarkerId)) {
				WP.Altitude = Integer.parseInt(ETAltitude.getText().toString());
				WP.Parameter1 = Integer.parseInt(ETParameter1.getText().toString());
				WP.Parameter2 = Integer.parseInt(ETParameter2.getText().toString());
				WP.Parameter3 = Integer.parseInt(ETParameter3.getText().toString());
				WP.Action = SpinnerAction.getSelectedItemPosition() + 1;
				return;
			}
		}
	}

	void removeWP() {
		for (WaypointNav WP : app.mw.WaypointsList) {
			if (WP.MarkerId.equals(MarkerId)) {
				app.mw.WaypointsList.remove(WP);
				finish();
				return;
			}
		}
	}

	public void RemoveWPOnClick(View v) {
		removeWP();
	}

	public void OKOnClick(View v) {
		// updateWPData();
		finish();
	}

	public void CircleOnClick(View v) {

		Intent returnIntent = new Intent();
		returnIntent.putExtra("MarkerId", MarkerId);

		returnIntent.putExtra("RADIUS", ((EditText) findViewById(R.id.editTextRadius)).getText().toString());
		returnIntent.putExtra("NRPOINTS", ((EditText) findViewById(R.id.editTextNrOfPoints)).getText().toString());
		returnIntent.putExtra("DIRECTION", ((EditText) findViewById(R.id.editTextDirection)).getText().toString());

		setResult(RESULT_OK, returnIntent);
		finish();
	}

	public WaypointNav getWPfromMarkerId(String markerId) {
		for (WaypointNav WP : app.mw.WaypointsList) {
			if (WP.MarkerId.equals(markerId)) {
				return WP;
			}
		}
		return null;
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();

	}

	@Override
	protected void onPause() {
		updateWPData();
		super.onPause();
	}
	// ///////////////////

}
