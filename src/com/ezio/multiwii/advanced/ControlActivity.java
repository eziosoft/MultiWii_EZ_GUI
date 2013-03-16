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

import com.ezio.multiwii.R;
import com.ezio.multiwii.R.id;
import com.ezio.multiwii.R.layout;
import com.ezio.multiwii.R.string;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.radio.StickView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.SeekBar;

public class ControlActivity extends Activity {

	private boolean		killme		= false;

	App					app;
	Handler				mHandler	= new Handler();

	StickView			s1;
	StickView			s2;

	SeekBar				SeekBar1;
	SeekBar				SeekBar2;
	SeekBar				SeekBar3;
	SeekBar				SeekBar4;

	int[]				CH8			= { 1500, 1500, 1500, 1500, 1500, 1500, 1500, 1500 };

	private Runnable	update		= new Runnable() {
										@Override
										public void run() {

											app.mw.ProcessSerialData(app.loggingON);

											Log.d("aaa","Throttle="+String.valueOf(app.mw.rcThrottle));
											Log.d("aaa","Yaw="+String.valueOf(app.mw.rcYaw));;
											Log.d("aaa","Pitch="+String.valueOf(app.mw.rcPitch));
											Log.d("aaa","Roll="+String.valueOf(app.mw.rcRoll));
											
											
											if (app.RadioMode == 2) {
												s1.SetPosition(app.mw.rcYaw, app.mw.rcThrottle);
												s2.SetPosition(app.mw.rcRoll, app.mw.rcPitch);
											}

											if (app.RadioMode == 1) {
												s1.SetPosition(app.mw.rcYaw, app.mw.rcPitch);
												s2.SetPosition(app.mw.rcRoll, app.mw.rcThrottle);
											}

											// * 0rcRoll 1rcPitch 2rcYaw
											// 3rcThrottle 4rcAUX1 5rcAUX2
											// 6rcAUX3 7rcAUX4
//
											CH8[4] = SeekBar1.getProgress() + 1000;
											CH8[5] = SeekBar2.getProgress() + 1000;
											CH8[6] = SeekBar3.getProgress() + 1000;
											CH8[7] = SeekBar4.getProgress() + 1000;

											//app.mw.SendRequestSetRawRC(CH8);
											app.Frequentjobs();

											//app.mw.SendRequest();
											app.mw.SendRequestSetRawRC(CH8);
											if (!killme)
												mHandler.postDelayed(update, 70);

										}
									};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.control_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		s1 = (StickView) findViewById(R.id.stickView1);
		s2 = (StickView) findViewById(R.id.stickView2);

		SeekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		SeekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		SeekBar3 = (SeekBar) findViewById(R.id.seekBar3);
		SeekBar4 = (SeekBar) findViewById(R.id.seekBar4);

		s1.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						break;
					case MotionEvent.ACTION_MOVE:
						//s1.SetPosition((s1.InputX(event.getX())), s1.InputY(event.getY())); // TODO
																							// REMOVE
						CH8[3] = (int) s1.InputY(event.getY()); // throttle
						CH8[2] = (int) s1.InputX(event.getX()); // yaw
						break;
					case MotionEvent.ACTION_UP:
						//s1.SetPosition(1500, s1.InputY(event.getY()));// TODO
						// REMOVE
						CH8[3] = (int) s1.InputY(event.getY()); // throttle
						CH8[2] = 1500; // yaw
						break;
				}
				return true;
			}
		});

		s2.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:

						break;
					case MotionEvent.ACTION_MOVE:
						// s2.SetPosition((s2.InputX(event.getX())),
						// s2.InputY(event.getY()));
						CH8[1] = (int) s1.InputY(event.getY()); // pitch
						CH8[0] = (int) s1.InputX(event.getX());// roll
						break;
					case MotionEvent.ACTION_UP:
						// s2.SetPosition(1500, 1500);
						CH8[1] = 1500;
						CH8[0] = 1500;
						break;
				}
				return true;
			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Control));
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
