package com.ezio.multiwii.waypoints;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapWaypointsActivity extends SherlockFragmentActivity {

	MapHelperClass mapHelperClass;
	Menu ActionBarMenu;
	ActionMode mMode;

	float CircleRadius = 0;
	int CirclePointsCount = 10;

	boolean ShowWaypointControls = false;

	boolean MoveMap = true;
	private long centerStep = 0;

	int CurrentWaypointNumber = -1;
	final int distanceWhenWPReached = 5;
	float distanceToNextWaypoint = 9999;

	boolean killme = false;

	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	TextView TVInfoMap;

	NumberFormat format = new DecimalFormat("0.00");

	private Runnable update = new Runnable() {
		@Override
		public void run() {
			app.mw.ProcessSerialData(app.loggingON);
			app.frskyProtocol.ProcessSerialData(false);

			// simulation
			if (app.D) {
				app.mw.GPS_latitude += random.nextInt(50) - 1;
				app.mw.GPS_longitude += random.nextInt(50) - 1;
				app.mw.GPS_fix = 1;
				app.mw.head++;
				if (app.mw.head > 360)
					app.mw.head = 0;
			}

			LatLng copterPositionLatLng = new LatLng(app.mw.GPS_latitude / Math.pow(10, 7), app.mw.GPS_longitude / Math.pow(10, 7));

			// Map centering
			if (MoveMap && centerStep < System.currentTimeMillis()) {
				if (app.mw.GPS_fix == 1) {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(copterPositionLatLng, app.MapZoomLevel, 0, app.mw.head)));
				} else {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(app.sensors.PhoneLatitude, app.sensors.PhoneLongitude), app.MapZoomLevel, 0, 0)));
				}
				centerStep = System.currentTimeMillis() + app.MapCenterPeriod * 1000;
			}

			// State dissplaying ////////////////////
			// String state = "";
			// for (int i = 0; i < app.mw.CHECKBOXITEMS; i++) {
			// if (app.mw.ActiveModes[i]) {
			// state += " " + app.mw.buttonCheckboxLabel[i];
			// }
			// }

			if (distanceToNextWaypoint < 5)
				SetNextWaypoint(false);

			mapHelperClass.SetCopterLocation(copterPositionLatLng, app.mw.head, app.mw.alt);
			mapHelperClass.DrawFlightPath(copterPositionLatLng);
			mapHelperClass.PositionHoldMarker.setPosition(new LatLng(app.mw.Waypoints[16].Lat / Math.pow(10, 7), app.mw.Waypoints[16].Lon / Math.pow(10, 7)));
			mapHelperClass.HomeMarker.setPosition(new LatLng(app.mw.Waypoints[0].Lat / Math.pow(10, 7), app.mw.Waypoints[0].Lon / Math.pow(10, 7)));

			if (CurrentWaypointNumber >= 0) {
				distanceToNextWaypoint = (float) MapHelperClass.gps2m(copterPositionLatLng.latitude, copterPositionLatLng.longitude, mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().latitude, mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().longitude);
			}

			DisplayInfo();
			app.Frequentjobs();

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme)
				mHandler.postDelayed(update, app.RefreshRate);

		}
	};

	void DisplayInfo() {
		TVInfoMap.setText("");
		if (CurrentWaypointNumber >= 0) {
			TVInfoMap.setText("WP#" + String.valueOf(CurrentWaypointNumber + 1) + "\n");
			TVInfoMap.append("To WP#" + String.valueOf(CurrentWaypointNumber + 1) + ":" + format.format(distanceToNextWaypoint) + "m" + "\n");
		}
		TVInfoMap.append("Alt:" + format.format(app.mw.alt) + "m" + "\n");
		TVInfoMap.append("Bat:" + format.format(app.mw.bytevbat / 10.0) + "V" + "\n");
		TVInfoMap.append("Power:" + String.valueOf(app.mw.pMeterSum) + "/" + String.valueOf(app.mw.intPowerTrigger) + "\n");
		TVInfoMap.append("Sat:" + String.valueOf(app.mw.GPS_numSat) + "\n");
		TVInfoMap.append("Head:" + String.valueOf((int) app.mw.head) + "°" + "\n");
		TVInfoMap.append("Speed:" + String.valueOf((int) app.mw.GPS_speed) + "m/s" + "\n");
		TVInfoMap.append("ToHome:" + format.format(app.mw.GPS_distanceToHome) + "m" + "\n");
		TVInfoMap.append("DirToHome:" + String.valueOf((int) app.mw.GPS_directionToHome + "°"));

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext()) != ConnectionResult.SUCCESS) {
			Toast.makeText(this, getString(R.string.GooglePlayServiecesError), Toast.LENGTH_LONG).show();
			finish();
		}

		app = (App) getApplication();
		app.ForceLanguage();
		app.ConnectionBug();

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.waypoints_map_layout);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		getSupportActionBar().setDisplayShowHomeEnabled(false);

		TVInfoMap = (TextView) findViewById(R.id.textViewInfoMap);

		mapHelperClass = new MapHelperClass(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(), distanceWhenWPReached);

		mapHelperClass.map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (app.mw.GPS_fix == 1)
					app.MapZoomLevel = (int) position.zoom;
			}
		});

		mapHelperClass.map.setOnMarkerDragListener(new OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				mapHelperClass.RedrawLines();
			}

			@Override
			public void onMarkerDrag(Marker marker) {
				mapHelperClass.RedrawLines();
			}
		});

		mapHelperClass.map.setOnMapLongClickListener(new OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng point) {

				Toast.makeText(getApplicationContext(), String.valueOf(point.latitude * 1e6), Toast.LENGTH_SHORT).show();

				Intent i = new Intent(getApplicationContext(), WaypointActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("LAT", (long) (point.latitude * 1e6));
				i.putExtra("LON", (long) (point.longitude * 1e6));
				startActivity(i);
			}
		});

		mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(0, 0), mapHelperClass.map.getMinZoomLevel(), 0, 0)));

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ShowWaypointControls = extras.getBoolean("WAYPOINT");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Map));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(null);
		killme = true;

		if (app.mw.GPS_fix == 1) {
			app.MapZoomLevel = mapHelperClass.map.getCameraPosition().zoom;
			app.SaveSettings(true);
		}
	}

	void SetNextWaypoint(boolean previous) {

		if (previous) {
			CurrentWaypointNumber--;
		} else {
			CurrentWaypointNumber++;
		}

		if (CurrentWaypointNumber >= mapHelperClass.markers.size() || CurrentWaypointNumber <= -1) {
			CurrentWaypointNumber = -1;
			mapHelperClass.CurrentWPCircle.setCenter(new LatLng(0, 0));
			return;
		}

		int alt = 0;
		app.mw.SendRequestMSP_SET_WP(new Waypoint(16, (int) (mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().latitude * 1e6 * 10), (int) (mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().longitude * 1e6 * 10), alt, 0, 0, 0));
		app.soundManager.playSound(2);
		mapHelperClass.CurrentWPCircle.setCenter(mapHelperClass.markers.get(CurrentWaypointNumber).getPosition());

		for (Marker m : mapHelperClass.markers) {
			m.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
		}

		mapHelperClass.markers.get(CurrentWaypointNumber).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

		if (app.D) {
			app.mw.Waypoints[16].Lat = (int) (mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().latitude * 1e6 * 10);
			app.mw.Waypoints[16].Lon = (int) (mapHelperClass.markers.get(CurrentWaypointNumber).getPosition().longitude * 1e6 * 10);
		}
	}

	void AddCircleDialogShow() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(getString(R.string.Radius));
		alert.setMessage(getString(R.string.EnterRadius));

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText("10");
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.OK), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String value = input.getText().toString();
				CircleRadius = Float.parseFloat(value.replace(",", "."));
				AddCircle(CircleRadius, CirclePointsCount);
				mMode = startActionMode(new CircleOptionsActionModeMenu());
			}
		});

		alert.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();
	}

	void AddCircle(double diameterInMeters, int howManyPoints) {
		float x = 360 / howManyPoints;
		double azimuth = 0;
		for (int i = 0; i < howManyPoints; i++) {
			mapHelperClass.AddMarker((MapHelperClass.GetPointGivenRadialAndDistance(mapHelperClass.markers.get(0).getPosition(), diameterInMeters, azimuth)));
			azimuth += x;
		}

	}

	// /////menu////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		ActionBarMenu = menu;
		inflater.inflate(R.menu.menu_map_waypoints, menu);

		if (!ShowWaypointControls) {
			menu.findItem(R.id.MenuMapWaypointAddWP).setVisible(false);
			menu.findItem(R.id.MenuMapWaypointClean).setVisible(false);
			menu.findItem(R.id.MenuMapWaypointNextWP).setVisible(false);
			menu.findItem(R.id.MenuMapWaypointPrevWP).setVisible(false);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.MenuMapWaypointAddWP) {
			mapHelperClass.AddMarker();

			if (mapHelperClass.markers.size() == 1) {
				mMode = startActionMode(new AddPatternActionModeMenu());
			} else {
				if (mMode != null) {
					mMode.finish();
				}
			}
			return true;
		}

		if (item.getItemId() == R.id.MenuMapWaypointPrevWP) {
			SetNextWaypoint(true);
			return true;
		}

		if (item.getItemId() == R.id.MenuMapWaypointNextWP) {
			SetNextWaypoint(false);
			return true;
		}

		if (item.getItemId() == R.id.MenuMapWaypointClean) {
			mapHelperClass.CleanMap();
			CurrentWaypointNumber = -1;
			return true;
		}

		if (item.getItemId() == R.id.MenuMapWaypointMoveMap) {
			MoveMap = !MoveMap;
			item.setChecked(MoveMap);
			if (MoveMap)
				Toast.makeText(getApplicationContext(), getString(R.string.MoveMapEnable), Toast.LENGTH_SHORT).show();
			else
				Toast.makeText(getApplicationContext(), getString(R.string.MoveMapDissable), Toast.LENGTH_SHORT).show();
			return true;
		}

		return false;
	}

	// ///menu end//////

	private final class AddPatternActionModeMenu implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.AddCirclePath))
			// .setIcon(isLight ? R.drawable.ic_compose_inverse :
			// R.drawable.ic_compose)
			.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getItemId() == 1) {
				AddCircleDialogShow();
				mode.finish();
			}
			return true;

		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}
	}

	private final class CircleOptionsActionModeMenu implements ActionMode.Callback {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {

			menu.add(Menu.NONE, 1, Menu.NONE, getString(R.string.Bigger)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(Menu.NONE, 2, Menu.NONE, getString(R.string.Smaller)).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
			menu.add(Menu.NONE, 3, Menu.NONE, getString(R.string.AddWP)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
			menu.add(Menu.NONE, 4, Menu.NONE, getString(R.string.RemWP)).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getItemId() == 1) {
				CircleRadius += 1f;
				while (mapHelperClass.markers.size() > 1) {
					mapHelperClass.RemoveMarker(mapHelperClass.markers.size() - 1);
				}

				AddCircle(CircleRadius, CirclePointsCount);
				mapHelperClass.RedrawLines();
			}

			if (item.getItemId() == 2) {
				CircleRadius -= 1f;
				while (mapHelperClass.markers.size() > 1) {
					mapHelperClass.RemoveMarker(mapHelperClass.markers.size() - 1);
				}

				AddCircle(CircleRadius, CirclePointsCount);
				mapHelperClass.RedrawLines();
			}

			if (item.getItemId() == 3) {
				CirclePointsCount += 1;
				while (mapHelperClass.markers.size() > 1) {
					mapHelperClass.RemoveMarker(mapHelperClass.markers.size() - 1);
				}

				AddCircle(CircleRadius, CirclePointsCount);
				mapHelperClass.RedrawLines();
			}

			if (item.getItemId() == 4) {
				CirclePointsCount -= 1;
				while (mapHelperClass.markers.size() > 1) {
					mapHelperClass.RemoveMarker(mapHelperClass.markers.size() - 1);
				}

				AddCircle(CircleRadius, CirclePointsCount);
				mapHelperClass.RedrawLines();
			}

			return true;

		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mapHelperClass.RemoveMarker(0);
			mapHelperClass.RedrawLines();

		}
	}

}
