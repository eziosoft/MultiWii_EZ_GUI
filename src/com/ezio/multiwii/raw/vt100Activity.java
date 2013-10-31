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

package com.ezio.multiwii.raw;

import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class vt100Activity extends SherlockActivity {

	private boolean killme = false;

	EditText tv;
	EditText tvSend;

	App app;
	Handler mHandler = new Handler();

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			// app.mw.ProcessSerialData(app.loggingON);

			// app.frskyProtocol.ProcessSerialData(false);
			// app.Frequentjobs();

			// app.mw.SendRequest(app.MainRequestMethod);

			ProcessDisplay();
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
		setContentView(R.layout.vt100);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		tv = (EditText) findViewById(R.id.editTextvt100);
		tvSend = (EditText) findViewById(R.id.editTextSend);
		tv.setText("");

	}

	void ProcessDisplay() {
		String d[] = new String[80];

		// if (tv.getText().length() > 10000)
		// tv.setText("");

		String s = ReadString();

		s = s.replace((char) 0x1b + "[2K", "\n");

		tv.append(s);

		int x = tv.getText().toString().indexOf("[01;1H");

		if (x > 0) {
			tv.setText(tv.getText().toString().substring(x+6));
			
		}

	}

	public void Send2OnClick(View v) {
		WriteString(tvSend.getText().toString());
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say("Serial monitor");
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	public String ReadString() {
		String b = "";
		while (app.mw.communication.dataAvailable()) {
			b += (char) app.mw.communication.Read();
		}
		return b;
	}

	public void WriteString(String string) {
		app.mw.communication.Write(string.getBytes());
	}

}
