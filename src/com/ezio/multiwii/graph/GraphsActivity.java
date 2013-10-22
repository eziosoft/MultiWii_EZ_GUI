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
package com.ezio.multiwii.graph;

import java.util.ArrayList;
import java.util.Random;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.graph.GraphView.GraphViewData;
import com.ezio.multiwii.graph.GraphView.LegendAlign;
import com.ezio.multiwii.graph.GraphViewSeries.GraphViewSeriesStyle;

public class GraphsActivity extends SherlockActivity {

	private boolean killme = false;

	App app;
	Handler mHandler = new Handler();

	GraphView graphView;
	ArrayList<GraphViewSeries> series = new ArrayList<GraphViewSeries>();

	Random rnd = new Random();

	int CurentPosition = 0;
	int NextLimit = 5000;

	boolean pause = false;

	private Runnable update = new Runnable() {
		@Override
		public void run() {

			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);
			app.Frequentjobs();

			if (!pause) {
				CurentPosition++;
				if (CurentPosition == NextLimit) {
					for (GraphViewSeries s : series) {
						s.resetData(new GraphViewData[] { new GraphViewData(CurentPosition, 0) });
					}
					NextLimit = CurentPosition + 5000;
				}

				// debug
				// app.mw.ax = rnd.nextFloat();
				// app.mw.ay = rnd.nextFloat();
				// app.mw.az = rnd.nextFloat();
				//
				// app.mw.gx = rnd.nextFloat();
				// app.mw.gy = rnd.nextFloat();
				// app.mw.gz = rnd.nextFloat();
				//
				// app.mw.magx = rnd.nextFloat();
				// app.mw.magy = rnd.nextFloat();
				// app.mw.magz = rnd.nextFloat();
				//
				// app.mw.alt = rnd.nextFloat();
				// app.mw.head = rnd.nextFloat();

				// app.mw.debug1 = rnd.nextFloat();
				// app.mw.debug2 = rnd.nextFloat();
				// app.mw.debug3 = rnd.nextFloat();
				// app.mw.debug4 = rnd.nextFloat();

				// //////

				for (GraphViewSeries s : series) {

					if (s.description.equals(app.ACCROLL))
						s.appendData(new GraphViewData(CurentPosition, app.mw.ax), true);

					if (s.description.equals(app.ACCPITCH))
						s.appendData(new GraphViewData(CurentPosition, app.mw.ay), true);

					if (s.description.equals(app.ACCZ))
						s.appendData(new GraphViewData(CurentPosition, app.mw.az), true);

					// /
					if (s.description.equals(app.GYROROLL))
						s.appendData(new GraphViewData(CurentPosition, app.mw.gx), true);
					if (s.description.equals(app.GYROPITCH))
						s.appendData(new GraphViewData(CurentPosition, app.mw.gy), true);
					if (s.description.equals(app.GYROYAW))
						s.appendData(new GraphViewData(CurentPosition, app.mw.gz), true);
					// /
					if (s.description.equals(app.MAGROLL))
						s.appendData(new GraphViewData(CurentPosition, app.mw.magx), true);
					if (s.description.equals(app.MAGPITCH))
						s.appendData(new GraphViewData(CurentPosition, app.mw.magy), true);
					if (s.description.equals(app.MAGYAW))
						s.appendData(new GraphViewData(CurentPosition, app.mw.magz), true);
					// /
					if (s.description.equals(app.ALT))
						s.appendData(new GraphViewData(CurentPosition, app.mw.alt), true);

					if (s.description.equals(app.HEAD))
						s.appendData(new GraphViewData(CurentPosition, app.mw.head), true);
					// /
					if (s.description.equals(app.DEBUG1))
						s.appendData(new GraphViewData(CurentPosition, app.mw.debug1), true);

					if (s.description.equals(app.DEBUG2))
						s.appendData(new GraphViewData(CurentPosition, app.mw.debug2), true);

					if (s.description.equals(app.DEBUG3))
						s.appendData(new GraphViewData(CurentPosition, app.mw.debug3), true);

					if (s.description.equals(app.DEBUG4))
						s.appendData(new GraphViewData(CurentPosition, app.mw.debug4), true);

				}
			}

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, 100);

			if (app.D)
				Log.d(app.TAG, "loop " + this.getClass().getName());

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (App) getApplication();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	void graphInit() {

		CurentPosition = 0;
		NextLimit = 5000;

		series = new ArrayList<GraphViewSeries>();
		graphView = new LineGraphView(getApplicationContext(), getString(R.string.Graphs));
		graphView.setViewPort(1, 100);
		graphView.setScalable(true);
		graphView.setShowLegend(true);
		graphView.setLegendAlign(LegendAlign.BOTTOM);

		String gr = app.GraphsToShow;

		if (gr.contains(app.ACCROLL))
			series.add(new GraphViewSeries(app.ACCROLL, new GraphViewSeriesStyle(Color.RED, 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.ACCPITCH))
			series.add(new GraphViewSeries(app.ACCPITCH, new GraphViewSeriesStyle(Color.GREEN, 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.ACCZ))
			series.add(new GraphViewSeries(app.ACCZ, new GraphViewSeriesStyle(Color.BLUE, 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.GYROROLL))
			series.add(new GraphViewSeries(app.GYROROLL, new GraphViewSeriesStyle(Color.rgb(196, 201, 0), 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.GYROPITCH))
			series.add(new GraphViewSeries(app.GYROPITCH, new GraphViewSeriesStyle(Color.rgb(0, 255, 255), 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.GYROYAW))
			series.add(new GraphViewSeries(app.GYROYAW, new GraphViewSeriesStyle(Color.rgb(255, 0, 255), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.MAGROLL))
			series.add(new GraphViewSeries(app.MAGROLL, new GraphViewSeriesStyle(Color.rgb(52, 101, 144), 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.MAGPITCH))
			series.add(new GraphViewSeries(app.MAGPITCH, new GraphViewSeriesStyle(Color.rgb(98, 51, 149), 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.MAGYAW))
			series.add(new GraphViewSeries(app.MAGYAW, new GraphViewSeriesStyle(Color.rgb(150, 100, 49), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.ALT))
			series.add(new GraphViewSeries(app.ALT, new GraphViewSeriesStyle(Color.rgb(130, 122, 125), 3), new GraphViewData[] { new GraphViewData(0, 0) }));
		if (gr.contains(app.HEAD))
			series.add(new GraphViewSeries(app.HEAD, new GraphViewSeriesStyle(Color.rgb(255, 226, 124), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.DEBUG1))
			series.add(new GraphViewSeries(app.DEBUG1, new GraphViewSeriesStyle(Color.rgb(200, 50, 0), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.DEBUG2))
			series.add(new GraphViewSeries(app.DEBUG2, new GraphViewSeriesStyle(Color.rgb(0, 200, 50), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.DEBUG3))
			series.add(new GraphViewSeries(app.DEBUG3, new GraphViewSeriesStyle(Color.rgb(50, 0, 200), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		if (gr.contains(app.DEBUG4))
			series.add(new GraphViewSeries(app.DEBUG4, new GraphViewSeriesStyle(Color.rgb(150, 100, 50), 3), new GraphViewData[] { new GraphViewData(0, 0) }));

		// /
		for (GraphViewSeries s : series) {
			graphView.addSeries(s);
		}

		setContentView(graphView);

	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Graphs));
		graphInit();
		killme = false;
		mHandler.postDelayed(update, 100);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;
	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.menu_graphs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuGraphsShow) {
			startActivity(new Intent(getApplicationContext(), SelectToShowActivity.class));
			return true;
		}

		if (item.getItemId() == R.id.MenuGraphsPause) {
			pause = !pause;
		}
		return false;
	}

	// ///menu end//////
}
