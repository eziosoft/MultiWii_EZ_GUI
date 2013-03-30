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
package com.ezio.multiwii.mw;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;
import android.util.Log;

import com.ezio.multiwii.helpers.FileAccess;
import com.ezio.multiwii.waypoints.Waypoint;
import communication.Communication;

public abstract class MultirotorData {

	public String EZGUIProtocol = "";

	// ////////Protocol 2.0 ////////
	public String[] MultiTypeName = { "", "TRI", "QUADP", "QUADX", "BI", "GIMBAL", "Y6", "HEX6", "FLYING_WING", "Y4", "HEX6X", "OCTOX8", "OCTOFLATP", "OCTOFLATX", "AIRPLANE", "HELI_120_CCPM", "HELI_90_DEG", "VTAIL4", "HEX6_H" };

	public int PIDITEMS = 8; // 8 in 2.0, extended to 10 in 2.1
	public int CHECKBOXITEMS = 11; // in 2.1
	public int multiType; // 1

	public int byteRC_RATE, byteRC_EXPO, byteRollPitchRate, byteYawRate, byteDynThrPID;
	public int byteP[] = new int[PIDITEMS], byteI[] = new int[PIDITEMS], byteD[] = new int[PIDITEMS];

	public int version, versionMisMatch ;
	public float gx, gy, gz, ax, ay, az, magx, magy, magz, baro, head, angx, angy, debug1, debug2, debug3, debug4;
	public int GPS_distanceToHome, GPS_directionToHome;
	public int GPS_numSat, GPS_fix, GPS_update;
	public int init_com, graph_on, pMeterSum = 0, intPowerTrigger = 0, bytevbat = 0;
	public int rssi;

	public float mot[] = new float[8];
	public float servo[] = new float[8];
	public float rcThrottle = 1500, rcRoll = 1500, rcPitch = 1500, rcYaw = 1500, rcAUX1 = 1500, rcAUX2 = 1500, rcAUX3 = 1500, rcAUX4 = 1500;
	public int nunchukPresent, AccPresent, BaroPresent, MagPresent, GPSPresent, levelMode;

	public float timer1, timer2;
	public int cycleTime, i2cError;
	public int activation1[] = new int[CHECKBOXITEMS];
	public int activation2[] = new int[CHECKBOXITEMS];
	public int frame_size_read = 108 + 3 * PIDITEMS + 2 * CHECKBOXITEMS;

	public boolean I2cAccActive;
	public boolean I2cBaroActive;
	public boolean I2cMagnetoActive;
	public boolean GPSActive;

	public Communication bt;
	public String buttonCheckboxLabel[] = { "LEVEL", "BARO", "MAG", "CAMSTAB", "CAMTRIG", "ARM", "GPS HOME", "GPS HOLD", "PASSTHRU", "HEADFREE", "BEEPER" }; // compatibility

	public boolean[] ActiveModes = new boolean[CHECKBOXITEMS];

	// //////end 2.0/////////////

	// ///////Protocol 2.10 additions/////////////
	/******************************* Multiwii Serial Protocol **********************/
	final String MSP_HEADER = "$M<";

	public static final int MSP_IDENT = 100;
	public static final int MSP_STATUS = 101;
	public static final int MSP_RAW_IMU = 102;
	public static final int MSP_SERVO = 103;
	public static final int MSP_MOTOR = 104;
	public static final int MSP_RC = 105;
	public static final int MSP_RAW_GPS = 106;
	public static final int MSP_COMP_GPS = 107;
	public static final int MSP_ATTITUDE = 108;
	public static final int MSP_ALTITUDE = 109;
	public static final int MSP_ANALOG = 110;
	public static final int MSP_RC_TUNING = 111;
	public static final int MSP_PID = 112;
	public static final int MSP_BOX = 113;
	public static final int MSP_MISC = 114;
	public static final int MSP_MOTOR_PINS = 115;
	public static final int MSP_BOXNAMES = 116;
	public static final int MSP_PIDNAMES = 117;
	public static final int MSP_WP = 118;
	public static final int MSP_BOXIDS = 119;

	public static final int MSP_SET_RAW_RC = 200;
	public static final int MSP_SET_RAW_GPS = 201;
	public static final int MSP_SET_PID = 202;
	public static final int MSP_SET_BOX = 203;
	public static final int MSP_SET_RC_TUNING = 204;
	public static final int MSP_ACC_CALIBRATION = 205;
	public static final int MSP_MAG_CALIBRATION = 206;
	public static final int MSP_SET_MISC = 207;
	public static final int MSP_RESET_CONF = 208;
	public static final int MSP_SET_WP = 209;
	public static final int MSP_SELECT_SETTING = 210;
	public static final int MSP_SET_HEAD = 211; // rate

	public static final int MSP_BIND = 240;

	public static final int MSP_EEPROM_WRITE = 250;

	public static final int MSP_DEBUGMSG = 253;
	public static final int MSP_DEBUG = 254;

	public static final int MSP_SET_SERIAL_BAUDRATE = 199;
	public static final int MSP_ENABLE_FRSKY = 198;

	public static final int IDLE = 0, HEADER_START = 1, HEADER_M = 2, HEADER_ARROW = 3, HEADER_SIZE = 4, HEADER_CMD = 5, HEADER_ERR = 6;

	public float alt;
	public int vario;
	public int GPS_altitude, GPS_speed, GPS_latitude, GPS_longitude, GPS_ground_course;
	public int MSPversion;

	public int present = 0;

	public int mode = 0;

	public int SonarPresent;
	public int byteThrottle_EXPO;

	public int byteThrottle_MID;
	int activation[];
	public Boolean[][] Checkbox = new Boolean[CHECKBOXITEMS][12]; // state of
																	// chexboxes

	int byteMP[] = new int[8]; // Motor // Pins. // Varies // by // multiType //
								// and // Arduino // model // (pro // Mini, //
								// // Mega, // etc).

	public Waypoint[] Waypoints = { new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint(), new Waypoint() };

	// ///////////////////////////////////////////////////////////////////////////////////////////////
	// //////////end 2.10///////////

	// //Protocol 2.11///////////////////////////////////////
	public String DebugMSG;
	public int multiCapability = 0; // Bitflags stating what capabilities
									// are/are not
	// present in the compiled code.
	public int confSetting = 0;
	// end 2.11//////////////////////////

	public FileAccess FA;
	public float AltCorrection = 0;

	public int _1G = 256;

	public abstract void ProcessSerialData(boolean appLogging);

	public abstract void SendRequest();

	public abstract void SendRequestGetPID();

	public abstract void SendRequestGetMisc();

	public abstract void SendRequestAccCalibration();

	public abstract void SendRequestMagCalibration();

	public abstract void SendRequestResetSettings();

	public abstract void SendRequestSetandSaveMISC(int confPowerTrigger);

	public abstract void SendRequestSetPID(float confRC_RATE, float confRC_EXPO, float rollPitchRate, float yawRate, float dynamic_THR_PID, float throttle_MID, float throttle_EXPO, float[] confP, float[] confI, float[] confD);

	public abstract void SendRequestGetCheckboxes();

	public abstract void SendRequestSetCheckboxes();

	public abstract void SendRequestGPSinject21(byte GPS_FIX, byte numSat, int coordLAT, int coordLON, int altitude, int speed);

	public abstract void SendRequestGetWayPoint(int Number);

	public abstract void SendRequestSetRawRC(int[] channels8);

	public abstract void SendRequestWriteToEEprom();

	public abstract void SendRequestSelectSetting(int setting);

	public abstract void SendRequestBIND();

	public abstract void SendRequestMSP_SET_WP(Waypoint waypoint);

	public abstract void SendRequestMSP_SET_SERIAL_BAUDRATE(int baudRate);

	public abstract void SendRequestMSP_ENABLE_FRSKY();
	
	public abstract void SendRequestMSP_SET_HEAD(int heading);

	// ///////////////////////////////////////////////

	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
	private Date now;

	public void CreateNewLogFile() {
		File folder = new File(Environment.getExternalStorageDirectory() + "/MultiWiiLogs");
		boolean success = false;
		if (!folder.exists()) {
			success = folder.mkdir();
		}
		if (!success) {
			// Do something on success
		} else {
			// Do something else on failure
		}

		now = new Date();
		String fileName = "/MultiWiiLogs/MultiWiiLog_" + formatter.format(now) + ".csv";
		FA = new FileAccess(fileName);
		writeFirstLine();
	}

	private void writeFirstLine() {

		String s = "";
		s += "Time" + ";"; // 1
		s += "Time in ms" + ";";

		s += "gx" + ";";
		s += "gy" + ";";
		s += "gz" + ";";

		s += "ax" + ";";
		s += "ay" + ";";
		s += "az" + ";";

		s += "magx" + ";";
		s += "magy" + ";";
		s += "magz" + ";";

		s += "baro" + ";";
		s += "head" + ";";

		s += "angx" + ";";
		s += "angy" + ";";

		s += "debug1" + ";";
		s += "debug2" + ";";
		s += "debug3" + ";";
		s += "debug4" + ";";

		s += "GPS_distanceToHome" + ";";
		s += "GPS_directionToHome" + ";";
		s += "GPS_altitude" + ";";
		s += "GPS_fix" + ";";
		s += "GPS_numSat" + ";";
		s += "GPS_speed" + ";";
		s += "GPS_update" + ";";
		s += "GPS_latitude" + ";";
		s += "GPS_longitude" + ";";
		s += "cycleTime" + ";";
		s += "i2cError" + ";";
		FA.Write(s);
	}

	public void Logging() {
		now = new Date();
		String s = "";
		s += formatter.format(now) + ";";
		s += String.valueOf(System.currentTimeMillis()) + ";";

		s += String.valueOf(gx) + ";";
		s += String.valueOf(gy) + ";";
		s += String.valueOf(gz) + ";";

		s += String.valueOf(ax) + ";";
		s += String.valueOf(ay) + ";";
		s += String.valueOf(az) + ";";

		s += String.valueOf(magx) + ";";
		s += String.valueOf(magy) + ";";
		s += String.valueOf(magz) + ";";

		s += String.valueOf(baro) + ";";
		s += String.valueOf(head) + ";";

		s += String.valueOf(angx) + ";";
		s += String.valueOf(angy) + ";";

		s += String.valueOf(debug1) + ";";
		s += String.valueOf(debug2) + ";";
		s += String.valueOf(debug3) + ";";
		s += String.valueOf(debug4) + ";";

		s += String.valueOf(GPS_distanceToHome) + ";";
		s += String.valueOf(GPS_directionToHome) + ";";
		s += String.valueOf(GPS_altitude) + ";";
		s += String.valueOf(GPS_fix) + ";";
		s += String.valueOf(GPS_numSat) + ";";
		s += String.valueOf(GPS_speed) + ";";
		s += String.valueOf(GPS_update) + ";";
		s += String.valueOf(GPS_latitude) + ";";
		s += String.valueOf(GPS_longitude) + ";";

		s += String.valueOf(cycleTime) + ";";
		s += String.valueOf(i2cError) + ";";

		FA.Write(s);
		Log.d("plik", s);
	}

	public void CloseLoggingFile() {
		if (FA != null)
			FA.closeFile();
	}

}
