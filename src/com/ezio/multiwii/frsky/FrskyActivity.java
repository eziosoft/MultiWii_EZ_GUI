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
package com.ezio.multiwii.frsky;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;

public class FrskyActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	TextView TV;
	TextView TVSmall;

	ProgressBar pbTx;
	ProgressBar pbRx;
	TextView TxdBTV;
	TextView RxdBTV;

	
	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);

			app.Frequentjobs();

			app.mw.SendRequest();

			app.frsky.ProcessSerialData(false);

			pbRx.setProgress(app.frsky.RxRSSI);
			pbTx.setProgress(app.frsky.TxRSSI);

			RxdBTV.setText(String.valueOf(app.frsky.RxRSSI) + "dBc");
			TxdBTV.setText(String.valueOf(app.frsky.TxRSSI) + "dBc");

			TV.setText("A1=" + String.valueOf(app.frsky.Analog1) + " A2="
					+ String.valueOf(app.frsky.Analog2) + " RxRSSI="
					+ String.valueOf(app.frsky.RxRSSI) + " TxRSSI="
					+ String.valueOf(app.frsky.TxRSSI));

			TV.setText("");
			log("A1=" + String.valueOf((float) app.frsky.Analog1 * 0.052f)
					+ " A2=" + String.valueOf(app.frsky.Analog2) + " "
					+ "HubErr="
					+ String.valueOf(app.frsky.frskyHubProtocol.hubErrors));
//			log("Altitude="
//					+ String.valueOf(app.frsky.frskyHubProtocol.Altitude));
//			log("Heading=" + String.valueOf(app.frsky.frskyHubProtocol.Heading));
//			log("AccX=" + String.valueOf(app.frsky.frskyHubProtocol.Acc_X / 10));
//			log("AccY=" + String.valueOf(app.frsky.frskyHubProtocol.Acc_Y / 10));
//			log("AccZ=" + String.valueOf(app.frsky.frskyHubProtocol.Acc_Z / 10));
			// log("Time=" + String.valueOf(app.frsky.FHour) + ":"
			// + String.valueOf(app.frsky.FMinute) + ":"
			// + String.valueOf(app.frsky.FSecond));

			TVSmall.setText(app.frsky.frskyHubProtocol.whatFramesToString());

			// log(app.frsky.lastHubFrameslog);

			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);
			
			Log.d(app.TAG, "loop "+this.getClass().getName());

		}
	};

	void log(String t) {
		TV.append(t + "\n");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();
		setContentView(R.layout.frsky_layout);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		// bt = new BT(getApplicationContext());
		// frsky = new FrskyProtocol(bt);

		TV = (TextView) findViewById(R.id.textViewFrsky);
		TVSmall = (TextView) findViewById(R.id.textViewFrskySmall);

		pbRx = (ProgressBar) findViewById(R.id.progressBarFrskyRxRSSI);
		pbTx = (ProgressBar) findViewById(R.id.progressBarFrskyTxRSSI);

		RxdBTV = (TextView) findViewById(R.id.textViewRxdB);
		TxdBTV = (TextView) findViewById(R.id.textViewTxdB);

		pbRx.setMax(110);
		pbTx.setMax(110);

	}

	@Override
	protected void onResume() {
		super.onResume();

		// app.BTFrsky.Connect(app.MacAddressFrsky);

		app.ForceLanguage();
		app.Say(getString(R.string.Frsky));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;

		// bt.CloseSocket();
	}

}
