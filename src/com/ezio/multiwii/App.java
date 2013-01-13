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

package com.ezio.multiwii;

import java.util.Locale;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.ezio.multiwii.frsky.FrskyProtocol;
import com.ezio.multiwii.helpers.Notifications;
import com.ezio.multiwii.helpers.SoundManager;
import com.ezio.multiwii.helpers.TTS;
import com.ezio.multiwii.mw.BT;
import com.ezio.multiwii.mw.MultiWii200;
import com.ezio.multiwii.mw.MultiWii211;
import com.ezio.multiwii.mw.MultirotorData;
import com.google.android.maps.GeoPoint;

public class App extends Application {

	// debug
	public boolean GPSfromNet = false; // false by default
	public boolean UseMapPublicAPI = true; // map API (true by default)
	public String MapAPIKeyDebug = ""; // put
										// your
										// debug
										// key
										// here

	public String MapAPIKeyPublic = "0AxI9Dd4w6Y-ERQuGVB0WKB4x4iZe3uD9HVpWYQ";
	// end debug/////////////////

	public boolean ShowADS = false;
	// public boolean DataSent = false; // to server

	private static String REFRESHRATE = "REFRESHRATE";
	public int RefreshRate = 100; // this means wait 100ms after everything is
									// done

	public BT bt;
	public BT BTFrsky;
	public MultirotorData mw;

	public FrskyProtocol frsky;

	private SharedPreferences prefs;
	private Editor editor;
	public TTS tts;
	public SoundManager soundManager;

	// variables used in FrequentJobs
	private boolean[] oldActiveModes;
	private long timer1 = 0; // Say battery level every xx seconds;
								// PeriodicSpeaking is the frequency in ms
	private long timer2 = 0;
	int timer2Freq = 8000;// bip when low battery
	private long timer3 = 0;
	int timer3Freq = 1000; // timer every 1sek
	private long timer4 = 0;
	int timer4Freq = 5000; // timer every 5sek

	public boolean loggingON = false;
	// ----settings-----
	private static String COPYFRSKYTOMW = "COPYFRSKYTOMW";
	public boolean CopyFrskyToMW;

	private static String RADIOMODE = "RadioMode";
	public int RadioMode;

	private static String PROTOCOL = "PROTOCOL1";
	public int Protocol;

	private static String MAGMODE = "MAGMODE";
	public int MagMode;

	private static String TEXTTOSPEACH = "TEXTTOSPEACH1";
	public boolean TextToSpeach = true;

	private static String MACADDERSS = "MACADDERSS";
	public String MacAddress = "";

	private static String MACADDERSSFRSKY = "MACADDERSSFRSKY";
	public String MacAddressFrsky = "";

	private static String CONNECTONSTART = "CONNECTONSTART";
	public boolean ConnectOnStart = false;

	private static String ALTCORRECTION = "ALTCORRECTION";
	public boolean AltCorrection = false;

	private static String ADVANCEDFINCTIONS = "ADVANCEDFINCTIONS";
	public boolean AdvancedFunctions = false;

	private static String DISABLEBTONEXIT = "DISABLEBTONEXIT";
	public boolean DisableBTonExit = true;

	private static String G1 = "G1";
	private int _1Gtemp; // 1g value, used for g-force display

	private static String FORCELANGUAGE = "FORCELANGUAGE";
	public String ForceLanguage = "";

	private static String PERIODICSPEAKING = "PERIODICSPEAKING";
	public int PeriodicSpeaking = 20000; // in ms

	private static String VOLTAGEALARM = "VOLTAGEALARM";
	public float VoltageAlarm = 0;

	private static String USEOFFLINEMAPS = "USEOFFLINEMAPS";
	public boolean UseOfflineMaps = false;

	private static String APPSTARTCOUNTER = "APPSTARTCOUNTER";
	public int AppStartCounter = 0;

	private static String DONATEBUTTONPRESSED = "DONATEBUTTONPRESSED";
	public int DonateButtonPressed = 0;

	// graphs
	public String ACCROLL = "ACC ROLL";
	public String ACCPITCH = "ACC PITCH";
	public String ACCZ = "ACC Z";

	public String GYROROLL = "GYRO ROLL";
	public String GYROPITCH = "GYRO PITCH";
	public String GYROYAW = "GYRO YAW";

	public String MAGROLL = "MAG ROLL";
	public String MAGPITCH = "MAG PITCH";
	public String MAGYAW = "MAG YAW";

	public String ALT = "ALT";
	public String HEAD = "HEAD";

	private static String GRAPHSTOSHOW = "GRAPHSTOSHOW";
	public String GraphsToShow = ACCROLL + ";" + ACCZ + ";" + ALT + ";" + GYROPITCH;

	// graphs end

	Notifications notifications;

	private int tempLastI2CErrorCount = 0;

	@Override
	public void onCreate() {

		Log.d("aaa", "APP ON CREATE");
		super.onCreate();

		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		editor = prefs.edit();
		ReadSettings();

		ForceLanguage();

		bt = new BT(getApplicationContext());
		BTFrsky = new BT(getApplicationContext());
		tts = new TTS(getApplicationContext());

		SelectProtocol();

		prepareSounds();

		Say(getString(R.string.Started));

		// for testing
		if (GPSfromNet || !UseMapPublicAPI) {
			Toast.makeText(getApplicationContext(), "Debug version", Toast.LENGTH_LONG).show();
			Say("Debug version");
			mw.GPS_longitude = 23654111;
			mw.GPS_latitude = 488547500;
		}

		soundManager.playSound(2);

		notifications = new Notifications(getApplicationContext());

	}

	public void SelectProtocol() {

		if (Protocol == 200) {
			mw = new MultiWii200(bt);
		}

		if (Protocol == 210) {
			mw = new MultiWii211(bt);
		}

		frsky = new FrskyProtocol(BTFrsky);

		oldActiveModes = new boolean[20];// not the best method
		mw._1G = _1Gtemp;

	}

	public void ReadSettings() {
		RadioMode = prefs.getInt(RADIOMODE, 2);
		Protocol = prefs.getInt(PROTOCOL, 210);
		MagMode = prefs.getInt(MAGMODE, 1);
		TextToSpeach = prefs.getBoolean(TEXTTOSPEACH, true);
		MacAddress = prefs.getString(MACADDERSS, "");
		MacAddressFrsky = prefs.getString(MACADDERSSFRSKY, "");
		ConnectOnStart = prefs.getBoolean(CONNECTONSTART, false);
		AltCorrection = prefs.getBoolean(ALTCORRECTION, false);
		AdvancedFunctions = prefs.getBoolean(ADVANCEDFINCTIONS, false);
		DisableBTonExit = prefs.getBoolean(DISABLEBTONEXIT, true);
		_1Gtemp = prefs.getInt(G1, 256);
		ForceLanguage = prefs.getString(FORCELANGUAGE, "");
		PeriodicSpeaking = prefs.getInt(PERIODICSPEAKING, 20000);
		VoltageAlarm = prefs.getFloat(VOLTAGEALARM, 9.9f);
		GraphsToShow = prefs.getString(GRAPHSTOSHOW, GraphsToShow);
		UseOfflineMaps = prefs.getBoolean(USEOFFLINEMAPS, false);
		RefreshRate = prefs.getInt(REFRESHRATE, 100);
		CopyFrskyToMW = prefs.getBoolean(COPYFRSKYTOMW, false);
		AppStartCounter = prefs.getInt(APPSTARTCOUNTER, 0);
		DonateButtonPressed = prefs.getInt(DONATEBUTTONPRESSED, 0);
	}

	public void SaveSettings(boolean quiet) {
		editor.putInt(RADIOMODE, RadioMode);
		editor.putInt(PROTOCOL, Protocol);
		editor.putInt(MAGMODE, MagMode);
		editor.putBoolean(TEXTTOSPEACH, TextToSpeach);
		editor.putString(MACADDERSS, MacAddress);
		editor.putString(MACADDERSSFRSKY, MacAddressFrsky);
		editor.putBoolean(CONNECTONSTART, ConnectOnStart);
		editor.putBoolean(ALTCORRECTION, AltCorrection);
		editor.putBoolean(ADVANCEDFINCTIONS, AdvancedFunctions);
		editor.putBoolean(DISABLEBTONEXIT, DisableBTonExit);
		editor.putInt(G1, mw._1G);
		editor.putString(FORCELANGUAGE, ForceLanguage);
		editor.putInt(PERIODICSPEAKING, PeriodicSpeaking);
		editor.putFloat(VOLTAGEALARM, VoltageAlarm);
		editor.putString(GRAPHSTOSHOW, GraphsToShow);
		editor.putBoolean(USEOFFLINEMAPS, UseOfflineMaps);
		editor.putInt(REFRESHRATE, RefreshRate);
		editor.putBoolean(COPYFRSKYTOMW, CopyFrskyToMW);
		editor.putInt(APPSTARTCOUNTER, AppStartCounter);
		editor.putInt(DONATEBUTTONPRESSED, DonateButtonPressed);
		editor.commit();

		if (!quiet) {
			Toast.makeText(getApplicationContext(), getString(R.string.Settingssaved), Toast.LENGTH_LONG).show();
			Say(getString(R.string.Settingssaved));
		}
	}

	@Override
	public void onTerminate() {
		// Speak("bye");
		mw.CloseLoggingFile();
		super.onTerminate();

	}

	public void Say(String text) {
		if (TextToSpeach)
			tts.Speak(text);
	}

	public void Frequentjobs() {

		// Copy data from FrSky
		if (CopyFrskyToMW && BTFrsky.Connected && !bt.Connected)
			FrskyToMW();

		// Say battery level every xx seconds
		if (PeriodicSpeaking > 0 && bt.Connected && timer1 < System.currentTimeMillis()) {
			timer1 = System.currentTimeMillis() + PeriodicSpeaking;
			if (mw.bytevbat > 10) {
				Say(getString(R.string.BatteryLevelIs) + " " + String.valueOf((float) (mw.bytevbat / 10f)));
			}
		}

		// bip when low battery
		if (mw.bytevbat > 10 && VoltageAlarm > 0 && bt.Connected && timer2 < System.currentTimeMillis() && (float) (mw.bytevbat / 10f) < VoltageAlarm) {
			timer2 = System.currentTimeMillis() + timer2Freq;
			soundManager.playSound(0);
		}

		// ===================timer every 1sek===============================
		if (timer3 < System.currentTimeMillis()) {
			timer3 = System.currentTimeMillis() + timer3Freq;

			// Notifications
			if (mw.i2cError != tempLastI2CErrorCount) {
				displayNotification(getString(R.string.Warning), "I2C Error=" + String.valueOf(mw.i2cError), 1);
				tempLastI2CErrorCount = mw.i2cError;
			}

			// Checkboxes speaking; ON OFF
			for (int i = 0; i < mw.CHECKBOXITEMS; i++) {
				if (mw.ActiveModes[i] != oldActiveModes[i]) {
					String s = "";
					if (mw.ActiveModes[i]) {
						s = getString(R.string.isON);
						soundManager.playSound(2);
					} else {
						s = getString(R.string.isOFF);
					}

					Say((mw.buttonCheckboxLabel[i] + s).toLowerCase());

					if (mw.buttonCheckboxLabel[i].equals("ARM") && AltCorrection) {
						mw.AltCorrection = mw.alt;
						soundManager.playSound(1);
						mw._1G = (int) Math.sqrt(mw.ax * mw.ax + mw.ay * mw.ay + mw.az * mw.az);
					}

					if (!AltCorrection)
						mw.AltCorrection = 0;
				}
				oldActiveModes[i] = mw.ActiveModes[i];
			}

		}
		// --------------------END timer every 1sek---------------------------

		// ===================timer every 5sek===============================
		if (timer4 < System.currentTimeMillis()) {
			timer4 = System.currentTimeMillis() + timer4Freq;

			// Reconecting
			if (bt.ConnectionLost) {
				if (bt.ReconnectTry < 1) {
					tts.Speak(getString(R.string.Reconnecting));
					bt.Connect(MacAddress);
					bt.ReconnectTry++;
				}
			}

			// update Home position
			mw.SendRequestGetWayPoint(0);

		}
		// --------------------END timer every 5sek---------------------------
	}

	private void playSound() {
		try {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
			r.play();
		} catch (Exception e) {
		}
	}

	public void displayNotification(String title, String text, int Id) {
		notifications.displayNotification(title, text, Id);
	}

	private void prepareSounds() {
		soundManager = new SoundManager(getApplicationContext());
		soundManager.addSound(0, R.raw.alarma);
		soundManager.addSound(1, R.raw.alert1);
		soundManager.addSound(2, R.raw.blip);
	}

	public void ForceLanguage() {
		if (!ForceLanguage.equals("")) {
			String languageToLoad = ForceLanguage;
			Locale locale = new Locale(languageToLoad);
			Locale.setDefault(locale);
			Configuration config = new Configuration();
			config.locale = locale;
			getBaseContext().getResources().updateConfiguration(config, null);
		}
	}

	public void ConnectionBug() { // autoconnect again when new activity is
									// started
		if (ConnectOnStart && !bt.Connected) {
			bt.Connect(MacAddress);
			Say(getString(R.string.menu_connect));
		}
	}

	private void FrskyToMW() {
		mw.angx = frsky.frskyHubProtocol.angX;
		mw.angy = frsky.frskyHubProtocol.angY;

		mw.ax = frsky.frskyHubProtocol.Acc_X;
		mw.ay = frsky.frskyHubProtocol.Acc_Y;
		mw.az = frsky.frskyHubProtocol.Acc_Z;

		mw.head = frsky.frskyHubProtocol.Heading;
		mw.GPS_numSat = frsky.frskyHubProtocol.Temperature_1;
		mw.GPS_speed = frsky.frskyHubProtocol.GPS_Speed;

		mw.GPS_latitude = (int) frsky.frskyHubProtocol.GPS_Latitude;
		mw.GPS_longitude = (int) frsky.frskyHubProtocol.GPS_Longtitude;

		// mw.GPS_latitude = frsky.frskyHubProtocol.GPS_NS * 10 *
		// Integer.parseInt(String.valueOf(frsky.frskyHubProtocol.GPS_LatitudeBefore)
		// + String.valueOf(frsky.frskyHubProtocol.GPS_LatitudeAfter));
		// mw.GPS_longitude = frsky.frskyHubProtocol.GPS_EW * 10 *
		// Integer.parseInt(String.valueOf(frsky.frskyHubProtocol.GPS_LongitudeBefore)
		// + String.valueOf(frsky.frskyHubProtocol.GPS_LongitudeAfter));
		mw.alt = frsky.frskyHubProtocol.Altitude;

		mw.bytevbat = (byte) frsky.frskyHubProtocol.Voltage;
	}

}
