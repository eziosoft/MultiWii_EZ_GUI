package nav;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlSerializer;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.ezio.multiwii.R;
import com.ezio.multiwii.app.App;
import com.ezio.multiwii.helpers.FilePickerActivity;
import com.ezio.sec.Sec;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;

public class NavActivity extends SherlockFragmentActivity {

	String supportedWinGuiMissionFiles = "2.3 pre7";

	MapHelperClass mapHelperClass;
	Menu ActionBarMenu;
	ActionMode mMode;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ListView mDrawerList;

	int defaultAltitude = 25;
	int defaultAction = 1;

	// float CircleRadius = 0;
	// int CirclePointsCount = 10;

	boolean ShowWaypointControls = true;

	boolean CenterMap = false;
	private long centerPeriod = 0;

	// int CurrentWaypointNumber = -1;
	final int CircleAroundWPinMeters = 5;

	boolean killme = false;
	boolean killed = false;

	Random random = new Random(); // for test

	App app;
	Handler mHandler = new Handler();

	TextView TVWPInfo;

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
			if (CenterMap && centerPeriod < System.currentTimeMillis()) {
				if (app.mw.GPS_fix == 1) {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(copterPositionLatLng, app.MapZoomLevel, 0, app.mw.head)));
				} else {
					mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(app.sensors.PhoneLatitude, app.sensors.PhoneLongitude), app.MapZoomLevel, 0, 0)));
				}
				centerPeriod = System.currentTimeMillis() + app.MapCenterPeriod * 1000;
			}

			mapHelperClass.SetCopterLocation(copterPositionLatLng, app.mw.head, app.mw.alt);
			// mapHelperClass.SetCopterLocation(app.sensors.MapCurrentPosition,
			// app.sensors.Heading, 0);
			mapHelperClass.DrawFlightPath(copterPositionLatLng);
			mapHelperClass.PositionHoldMarker.setPosition(app.mw.Waypoints[16].Lat_Lng());
			mapHelperClass.HomeMarker.setPosition(app.mw.Waypoints[0].Lat_Lng());

			// DisplayInfo();
			app.Frequentjobs();

			app.mw.SendRequest(app.MainRequestMethod);
			if (!killme) {
				mHandler.postDelayed(update, app.RefreshRate);
			} else {
				mHandler.removeCallbacksAndMessages(null);
				killed = true;
				Log.d("nav", "Killed");
			}

		}
	};

	void DisplayInfo() {
		TVWPInfo.setText("");
		TVWPInfo.setText(getString(R.string.MaxNumberOfWP) + String.valueOf(app.mw.NAVmaxWpNumber));

		mDrawerList.setAdapter(new ArrayAdapter<WaypointNav>(this, R.layout.nav_drawer_list_item, app.mw.WaypointsList) {
			//
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				// 1. Create inflater
				LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

				// 2. Get rowView from inflater

				View rowView = null;

				// if(!modelsArrayList.get(position).isGroupHeader()){
				rowView = inflater.inflate(R.layout.nav_drawer_list_item, parent, false);

				// 3. Get icon,title & counter views from the rowView
				ImageView imgView = (ImageView) rowView.findViewById(R.id.item_icon);
				TextView titleView = (TextView) rowView.findViewById(R.id.item_title);

				// 4. Set the text for textView
				switch (app.mw.WaypointsList.get(position).Action) {

				case WaypointNav.WP_ACTION_SET_HEAD:
					imgView.setImageResource(R.drawable.set_heading);
					break;

				case WaypointNav.WP_ACTION_RTH:
					imgView.setImageResource(R.drawable.rth);
					break;

				case WaypointNav.WP_ACTION_POSHOLD_UNLIM:
					imgView.setImageResource(R.drawable.poshold_unlim);
					break;

				case WaypointNav.WP_ACTION_POSHOLD_TIME:
					imgView.setImageResource(R.drawable.poshold_time);
					break;

				case WaypointNav.WP_ACTION_JUMP:
					imgView.setImageResource(R.drawable.jump);
					break;

				case WaypointNav.WP_ACTION_WAYPOINT:
					imgView.setImageResource(R.drawable.waypoint);
					break;

				case WaypointNav.WP_ACTION_SET_POI:
					imgView.setImageResource(R.drawable.poi);
					break;

				default:
					imgView.setImageResource(R.drawable.green_light);
					break;
				}

				titleView.setText(app.mw.WaypointsList.get(position).toString());

				return rowView;
			}

		});
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

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.nav_layout);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setTitle(getString(R.string.Navigation));

		TVWPInfo = (TextView) findViewById(R.id.TextViewWPinfo);

		mDrawerList = (ListView) findViewById(R.id.ListViewWPList);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				getSupportActionBar().setTitle(getString(R.string.Navigation));
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(getString(R.string.Options));
				DisplayInfo();
			}

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}
		};

		mDrawerToggle.syncState();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mDrawerList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				openMWEditor(app.mw.WaypointsList.get(position).MarkerId);

			}
		});

		// mapHelperClass////////////////////////////////////////////////
		mapHelperClass = new MapHelperClass(getApplicationContext(), ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap(), CircleAroundWPinMeters, app.mw.multiType);

		mapHelperClass.map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(CameraPosition position) {
				if (app.mw.GPS_fix == 1)
					app.MapZoomLevel = (int) position.zoom;
			}
		});

		mapHelperClass.map.setOnMarkerClickListener(new OnMarkerClickListener() {

			@Override
			public boolean onMarkerClick(Marker marker) {

				return false;
			}
		});

		mapHelperClass.map.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDragStart(Marker marker) {
				// Toast.makeText(getApplicationContext(), marker.getId(),
				// Toast.LENGTH_LONG).show();
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {

				for (WaypointNav WP : app.mw.WaypointsList) {
					if (WP.MarkerId.equals(marker.getId())) {
						WP.setLatLng(marker.getPosition());
					}
				}
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

				if (app.mw.NAVmaxWpNumber == 0 || app.mw.WaypointsList.size() <= app.mw.NAVmaxWpNumber) {
					AddNewWP(point);
					Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
					if (vibrator != null) {
						vibrator.vibrate(50);
					}
				} else {

					DisplayInfoDialog(getString(R.string.Info), getString(R.string.MaxNumberWPreached), getString(R.string.OK));

				}
			}
		});

		mapHelperClass.map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {

			@Override
			public void onInfoWindowClick(Marker marker) {
				openMWEditor(marker.getId());
			}
		});

		mapHelperClass.map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(0, 0), mapHelperClass.map.getMinZoomLevel(), 0, 0)));
		// mapHelperClass///END/////////////////////////////////////////////

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {

			if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
				mDrawerLayout.closeDrawer(GravityCompat.START);
			} else {
				mDrawerLayout.openDrawer(GravityCompat.START);
			}
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		app.ForceLanguage();
		app.Say(getString(R.string.Navigation));
		killme = false;
		mHandler.postDelayed(update, app.RefreshRate);
		setVolumeControlStream(AudioManager.STREAM_MUSIC);

		LoadMarkersFromWPlist();
		DisplayInfo();

		if (Sec.VerifyDeveloperID(Sec.GetDeviceID(getApplicationContext()), Sec.TestersIDs) || Sec.Verify(getApplicationContext(), "D..3")) {
			mHandler.postDelayed(update, app.RefreshRate);
		} else {
			AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);

			dlgAlert.setTitle(getString(R.string.Locked));
			dlgAlert.setMessage(getString(R.string.DoYouWantToUnlock));

			// dlgAlert.setPositiveButton(getString(R.string.Yes), null);
			dlgAlert.setCancelable(false);
			dlgAlert.setPositiveButton(getString(R.string.Yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					try {
						Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.ezio.ez_gui_unlocker");
						startActivity(LaunchIntent);
					} catch (Exception e) {
						Intent goToMarket = null;
						goToMarket = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ezio.ez_gui_unlocker"));
						startActivity(goToMarket);
					}
					finish();
				}
			});
			dlgAlert.setNegativeButton(getString(R.string.No), new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					finish();
				}
			});

			dlgAlert.create().show();
		}

		app.sensors.startMagACC();

		if (app.Protocol != App.PROTOCOL_NAV) {
			DisplayInfoDialog("Protocol", "This requires selected NAV Protocol in Settings", getString(R.string.OK));
		}
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

		app.sensors.stopMagACC();
	}

	private WaypointNav getWPfromMarkerId(String markerId) {
		for (WaypointNav WP : app.mw.WaypointsList) {
			if (WP.MarkerId.equals(markerId)) {
				return WP;
			}
		}
		return null;
	}

	void AddNewWP(LatLng point) {
		app.mw.WaypointsList.add(new WaypointNav(app.mw.WaypointsList.size() + 1, point, defaultAction, 0, 0, 0, defaultAltitude, 0));
		LoadMarkersFromWPlist();
	}

	void ClearMap() {
		mapHelperClass.CleanMap();
		app.mw.WaypointsList.clear();
	}

	void ZoomToShawAllMarkers() {
		if (mapHelperClass.markers.size() > 0) {
			LatLngBounds.Builder builder = new LatLngBounds.Builder();
			for (Marker marker : mapHelperClass.markers) {
				builder.include(marker.getPosition());
			}
			LatLngBounds bounds = builder.build();
			int padding = 50; // offset from edges of the map in pixels
			CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
			mapHelperClass.map.animateCamera(cu);
		}
	}

	void SortAndPrepareWPs() {
		if (app.mw.WaypointsList.size() > 0) {
			Collections.sort(app.mw.WaypointsList);
			int i = 0;
			for (i = 0; i < app.mw.WaypointsList.size(); i++) {
				app.mw.WaypointsList.get(i).Number = i + 1;
				app.mw.WaypointsList.get(i).Flag = 0;
			}
			app.mw.WaypointsList.get(i - 1).Flag = WaypointNav.MISSION_FLAG_END;
		}
	}

	boolean IsWPLastToDownload(int WPNumber) {
		for (WaypointNav wp : app.mw.WaypointsList) {
			if (wp.Number == WPNumber && wp.Flag == WaypointNav.MISSION_FLAG_END)
				return true;
		}
		return false;
	}

	int isWPhasBeenDownloaded(int WPNumber) {
		for (int i = 0; i < app.mw.WaypointsList.size(); i++) {
			if (app.mw.WaypointsList.get(i).Number == WPNumber)
				return i;
		}
		return -1;
	}

	void CheckWPErrors() {
		for (int i = 0; i < app.mw.WaypointsList.size(); i++) {
			if (app.mw.WaypointsList.get(i).Flag == WaypointNav.ERROR_ERROR) {
				app.mw.WaypointsList.get(i).Error = WaypointNav.ERROR_ERROR;

				DisplayInfoDialog(getString(R.string.WPError), "WP" + String.valueOf(app.mw.WaypointsList.get(i).Number) + " " + getString(R.string.isNotCorrect), getString(R.string.OK));

			}
			if (app.mw.WaypointsList.get(i).Flag == WaypointNav.ERROR_CRC) {
				app.mw.WaypointsList.get(i).Error = WaypointNav.ERROR_CRC;
				DisplayInfoDialog(getString(R.string.WPError), "WP" + String.valueOf(app.mw.WaypointsList.get(i).Number) + " " + getString(R.string.CRCisNotCorrect), getString(R.string.OK));

			}
		}
	}

	void DownloadMission() {
		if (app.commMW.Connected) {
			Log.d("nav", "download mission");
			ClearMap();

			final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.Downloading), getString(R.string.PleaseWait), true);

			new Thread(new Runnable() {
				@Override
				public void run() {
					boolean finished = false;
					int i = 1;
					boolean error = false;

					killme = true;
					killed = false;
					mHandler.removeCallbacks(null);
					while (!killed) {
						try {
							Thread.sleep(app.RefreshRate);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}

					while (!finished) {
						app.mw.SendRequestMSP_WP(i);
						Log.d("nav", "send request " + String.valueOf(i));

						int t = 0;
						int t1 = 0;

						while (isWPhasBeenDownloaded(i) < 0) {
							Log.d("nav", "waiting..." + String.valueOf(i));
							try {
								Thread.sleep(app.RefreshRate);
							} catch (InterruptedException e) {
								//
								e.printStackTrace();
							}

							app.mw.ProcessSerialData(false);

							t++;
							if (t > 20) {
								app.mw.SendRequestMSP_WP(i);
								Log.d("nav", "send request " + String.valueOf(i));
								t = 0;
								t1++;

								if (t1 > 5) {
									finished = true;
									error = true;
								}
							}

						}

						Log.d("nav", "2");
						if (IsWPLastToDownload(i))
							finished = true;

						i++;
						if (i > 255)
							finished = true;
					}

					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							CheckWPErrors();
							LoadMarkersFromWPlist();
							progress.dismiss();
							ZoomToShawAllMarkers();
							DisplayInfo();
							killme = false;
							killed = false;
							mHandler.postDelayed(update, app.RefreshRate);

						}
					});
				}
			}).start();
		}

	}

	void UploadMission() {
		Log.d("nav", "upload mission");

		final ProgressDialog progress = ProgressDialog.show(this, getString(R.string.Uploading), getString(R.string.PleaseWait), true);

		new Thread(new Runnable() {
			@Override
			public void run() {

				SortAndPrepareWPs();

				killme = true;
				killed = false;
				mHandler.removeCallbacks(null);

				while (!killed) {
					try {
						Thread.sleep(app.RefreshRate);
					} catch (InterruptedException e) {
						//
						e.printStackTrace();
					}
				}

				if (app.mw.WaypointsList.size() == 0) {
					app.mw.SendRequestMSP_SET_WP_NAV(new WaypointNav(1, 0, 0, WaypointNav.WP_ACTION_RTH, 0, 0, 0, defaultAltitude, WaypointNav.MISSION_FLAG_END));
					Log.d("nav", "RTH upladed");
					killme = false;
					mHandler.postDelayed(update, app.RefreshRate);
					progress.dismiss();

				} else {

					for (WaypointNav wp : app.mw.WaypointsList) {
						app.mw.SendRequestMSP_SET_WP_NAV(wp);
						Log.d("nav", "send request " + String.valueOf(wp.Number));
						try {
							Thread.sleep(app.RefreshRate);
						} catch (InterruptedException e) {
							//
							e.printStackTrace();
						}
					}
				}

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						progress.dismiss();
						killme = false;
						killed = false;
						mHandler.postDelayed(update, app.RefreshRate);
					}
				});
			}
		}).start();
	}

	void LoadMarkersFromWPlist() {
		mapHelperClass.CleanMap();
		SortAndPrepareWPs();

		if (app.mw.WaypointsList.size() > 0) {
			for (WaypointNav WP : app.mw.WaypointsList) {

				if (WP.ShowMarkerForThisWP())
					WP.MarkerId = mapHelperClass.AddMarker(WP.getLatLng(), WP.getMarkerTitle(), WP.getMarkerSnippet(), WP.Action);

				if (WP.Action == WaypointNav.WP_ACTION_RTH || WP.Action == WaypointNav.WP_ACTION_POSHOLD_UNLIM)
					return;
			}
		}
		DisplayInfo();

	}

	public void ClearMapOnClick(View v) {
		ClearMap();
		DisplayInfo();
	}

	public void DownloadMissionOnClick(View v) {
		DownloadMission();
	}

	public void UploadMissionOnClick(View v) {
		UploadMission();
	}

	public void ZoomInOnClick(View v) {
		ZoomToShawAllMarkers();
		mDrawerLayout.closeDrawers();
	}

	public void LoadMissionOnClick(View v) {
		Intent i = new Intent(this, FilePickerActivity.class);
		startActivityForResult(i, 1);
	}

	void openMWEditor(String markerId) {
		Intent i = new Intent(getApplicationContext(), WPEditorActivity.class);
		// i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.putExtra("MARKERID", markerId);
		startActivityForResult(i, 2);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		Log.d("nav", "onActivityResult");
		if (requestCode == 1) {

			if (resultCode == RESULT_OK) {
				String result = data.getStringExtra("fileName");
				try {

					LoadMission(result);
				} catch (XmlPullParserException e) {
					//
					e.printStackTrace();
				} catch (IOException e) {
					//
					e.printStackTrace();
				}
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		}

		if (requestCode == 2) {

			if (resultCode == RESULT_OK) {
				createCircle(data.getStringExtra("MarkerId"), data.getStringExtra("RADIUS"), data.getStringExtra("NRPOINTS"), data.getStringExtra("DIRECTION"));
			}
			if (resultCode == RESULT_CANCELED) {
				// Write your code if there's no result
			}
		}
	}

	public void LoadMission(String filePath) throws XmlPullParserException, IOException {

		ClearMap();

		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();

		File file = new File(filePath);
		xpp.setInput(new FileReader(file));

		int eventType = xpp.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			if (eventType == XmlPullParser.START_DOCUMENT) {
				Log.d("xml", "Start document");
			} else if (eventType == XmlPullParser.END_DOCUMENT) {
				Log.d("xml", "End document");
			} else if (eventType == XmlPullParser.START_TAG) {

				if (xpp.getName().equals("MISSIONITEM")) {
					Log.d("xml", "Start tag " + xpp.getName());
					try {
						Map<String, String> attributes = getAttributes(xpp);

						int no = Integer.parseInt(attributes.get("no"));
						String action = attributes.get("action");
						int parameter1 = Integer.parseInt(attributes.get("parameter1"));
						int parameter2 = Integer.parseInt(attributes.get("parameter2"));
						int parameter3 = Integer.parseInt(attributes.get("parameter3"));
						double lat = Double.parseDouble(attributes.get("lat").replace(",", "."));
						double lon = Double.parseDouble(attributes.get("lon").replace(",", "."));
						int alt = Integer.parseInt(attributes.get("alt"));

						app.mw.WaypointsList.add(new WaypointNav(no, new LatLng(lat, lon), WaypointNav.getActionNumberFromString(action), parameter1, parameter2, parameter3, alt, 0));

					} catch (Exception e) {
						//
						e.printStackTrace();
					}
				}

				if (xpp.getName().equals("VERSION")) {
					Log.d("xml", "Start tag " + xpp.getName());
					try {
						Map<String, String> attributes = getAttributes(xpp);
						String version = attributes.get("value");
						Log.d("xml", "version= " + version);

						if (!version.equals(supportedWinGuiMissionFiles)) {
							DisplayInfoDialog(getString(R.string.Info), getString(R.string.FileVersionMismach), getString(R.string.OK));
							return;
						}

					} catch (Exception e) {
						//
						e.printStackTrace();
					}
				}

			} else if (eventType == XmlPullParser.END_TAG) {
				Log.d("xml", "End tag " + xpp.getName());
			} else if (eventType == XmlPullParser.TEXT) {
				Log.d("xml", "Text " + xpp.getText());
			}
			eventType = xpp.next();
		}

		LoadMarkersFromWPlist();
		ZoomToShawAllMarkers();
		mDrawerLayout.closeDrawers();
	}

	private Map<String, String> getAttributes(XmlPullParser parser) throws Exception {
		Map<String, String> attrs = null;
		int acount = parser.getAttributeCount();
		if (acount != -1) {
			Log.d("xml", "Attributes for [" + parser.getName() + "]");
			attrs = new HashMap<String, String>(acount);
			for (int x = 0; x < acount; x++) {
				Log.d("xml", "\t[" + parser.getAttributeName(x) + "]=" + "[" + parser.getAttributeValue(x) + "]");
				attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
			}
		} else {
			throw new Exception("Required entity attributes missing");
		}
		return attrs;
	}

	void saveMission(String filePath) throws IllegalArgumentException, IllegalStateException, IOException {

		XmlSerializer s = Xml.newSerializer();
		FileWriter writer = new FileWriter(filePath, false);

		s.setOutput(writer);

		s.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
		s.startDocument("UTF-8", true);

		String app_ver = "";
		int app_ver_code = 0;
		try {
			app_ver = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
			app_ver_code = getPackageManager().getPackageInfo(this.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e1) {
			e1.printStackTrace();
		}

		s.comment(getString(R.string.app_name) + ", Version " + app_ver + "." + String.valueOf(app_ver_code));
		s.comment("MultiWii mission");

		s.startTag("", "MISSION");

		s.startTag("", "VERSION");
		s.attribute("", "value", supportedWinGuiMissionFiles);
		s.endTag("", "VERSION");

		for (WaypointNav wp : app.mw.WaypointsList) {
			s.startTag("", "MISSIONITEM");
			s.attribute("", "no", String.valueOf(wp.Number));
			s.attribute("", "action", WaypointNav.WP_ACTION_NAMES[wp.Action]);
			s.attribute("", "parameter1", String.valueOf(wp.Parameter1));
			s.attribute("", "parameter2", String.valueOf(wp.Parameter2));
			s.attribute("", "parameter3", String.valueOf(wp.Parameter3));
			s.attribute("", "lat", String.valueOf(wp.getLatLng().latitude));
			s.attribute("", "lon", String.valueOf(wp.getLatLng().longitude));
			s.attribute("", "alt", String.valueOf(wp.Altitude));
			s.endTag("", "MISSIONITEM");
		}

		s.endTag("", "MISSION");
		s.endDocument();

		writer.close();
		// Log.d("xml", writer.toString());

	}



	public void SaveMissionOnClick(View v) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getString(R.string.EnterFileName));

		final EditText input = new EditText(this);

		Calendar c = Calendar.getInstance();
		final String s = String.valueOf(c.get(Calendar.DAY_OF_MONTH)) + String.valueOf(c.get(Calendar.MONTH) + 1) + String.valueOf(c.get(Calendar.YEAR)) + "-" + String.valueOf(c.get(Calendar.HOUR)) + String.valueOf(c.get(Calendar.MINUTE));

		input.setText(s);

		alert.setView(input);

		alert.setPositiveButton(getString(R.string.Save), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				try {
					saveMission(Environment.getExternalStorageDirectory() + "/MultiWiiLogs/" + input.getText().toString() + ".mission");
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});

		alert.setNegativeButton(getString(R.string.Cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Canceled.
			}
		});

		alert.show();

	}

	private final void DisplayInfoDialog(String title, String text, String buttonText) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title).setMessage(text).setCancelable(false).setNegativeButton(buttonText, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void createCircle(String MarkerId, String RadiusIn, String Pointsin, String Directionin) {

		Log.d("nav", "create circle");
		LatLng center = getWPfromMarkerId(MarkerId).getLatLng();

		int Points = 0;
		int Radius = 0;
		int Direction = 1;

		Radius = Integer.parseInt(RadiusIn);

		if (Radius < 20) {
			DisplayInfoDialog(getString(R.string.Info), "Invalid Radius", getString(R.string.OK));
			return;
		}

		Points = Integer.parseInt(Pointsin);

		if (Points < 5 || Points > 30) {
			DisplayInfoDialog(getString(R.string.Info), "Invalid Number of points", getString(R.string.OK));
			return;
		}

		Direction = Integer.parseInt(Directionin);

		if (Direction != -1 && Direction != 1) {
			DisplayInfoDialog(getString(R.string.Info), "Invalid Direction value", getString(R.string.OK));
			return;
		}

		double a = 0;
		double step = 360.0f / Points;
		if (Direction == -1) {
			a = 360;
			step *= -1;
		}
		for (; a <= 360 && a >= 0; a += step) {

			float d = Radius;
			float R = 6371000;

			final float rad2deg = (float) (180 / Math.PI);
			final float deg2rad = (float) (1.0 / rad2deg);

			double lat2 = Math.asin(Math.sin(center.latitude * deg2rad) * Math.cos(d / R) + Math.cos(center.latitude * deg2rad) * Math.sin(d / R) * Math.cos(a * deg2rad));
			double lon2 = center.longitude * deg2rad + Math.atan2(Math.sin(a * deg2rad) * Math.sin(d / R) * Math.cos(center.latitude * deg2rad), Math.cos(d / R) - Math.sin(center.latitude * deg2rad) * Math.sin(lat2));

			LatLng pll = new LatLng(lat2 * rad2deg, lon2 * rad2deg);
			app.mw.WaypointsList.add(new WaypointNav(app.mw.WaypointsList.size() + 1, pll, defaultAction, 0, 0, 0, defaultAltitude, 0));

			// AddNewWP(pll);
		}

		LoadMarkersFromWPlist();
		ZoomToShawAllMarkers();
	}

}
