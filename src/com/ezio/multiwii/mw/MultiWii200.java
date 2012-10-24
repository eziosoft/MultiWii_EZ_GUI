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

import android.util.Log;

public class MultiWii200 extends MultirotorData {

	public MultiWii200(BT b) {
		bt = b;
		EZGUIProtocol = "200";
	}

	private void log(String co, int wartosc) {
		Log.d(BT.TAG, co + "=" + String.valueOf(wartosc));
	}

	private void processData() {
		int present = 0, mode = 0;
		if (bt.available() > 0) {
			if ((bt.Read() == 'M')) {
				version = bt.Read8(); // version is read even if buffer length
										// doesn't check //1
				versionMisMatch = 0;
				// if (inBuf[frame_size_read-1] == 'M') { // Multiwii @ arduino
				// send
				// all data to GUI
				ax = bt.Read16();
				ay = bt.Read16();
				az = bt.Read16();
				gx = bt.Read16() / 8;
				gy = bt.Read16() / 8;
				gz = bt.Read16() / 8; // 13
				magx = bt.Read16() / 3;
				magy = bt.Read16() / 3;
				magz = bt.Read16() / 3; // 19
				baro = alt = bt.Read16();

				head = bt.Read16(); // 23
				for (int i = 0; i < 8; i++)
					servo[i] = bt.Read16();
				for (int i = 0; i < 8; i++)
					mot[i] = bt.Read16();
				rcRoll = bt.Read16();
				rcPitch = bt.Read16();
				rcYaw = bt.Read16();
				rcThrottle = bt.Read16();
				rcAUX1 = bt.Read16();
				rcAUX2 = bt.Read16();
				rcAUX3 = bt.Read16();
				rcAUX4 = bt.Read16();
				present = bt.Read8();
				mode = bt.Read8();
				cycleTime = bt.Read16();
				i2cError = bt.Read16();
				angx = bt.Read16() / 10;
				angy = bt.Read16() / 10;
				multiType = bt.Read8();

				for (int i = 0; i < PIDITEMS; i++) {
					byteP[i] = bt.Read8();
					byteI[i] = bt.Read8();
					byteD[i] = bt.Read8();
				}
				byteRC_RATE = bt.Read8();
				byteRC_EXPO = bt.Read8();
				byteRollPitchRate = bt.Read8();
				byteYawRate = bt.Read8();
				byteDynThrPID = bt.Read8();
				for (int i = 0; i < CHECKBOXITEMS; i++) {
					activation1[i] = bt.Read8();
					activation2[i] = bt.Read8();
				}
				GPS_distanceToHome = bt.Read16();
				GPS_directionToHome = bt.Read16();
				GPS_numSat = bt.Read8();
				GPS_fix = bt.Read8();
				GPS_update = bt.Read8();
				pMeterSum = bt.Read16();
				intPowerTrigger = bt.Read16();
				bytevbat = bt.Read8();
				debug1 = bt.Read16();
				debug2 = bt.Read16();
				debug3 = bt.Read16();
				debug4 = bt.Read16();

				if ((present & 1) > 0)
					nunchukPresent = 1;
				else
					nunchukPresent = 0;
				if ((present & 2) > 0)
					AccPresent = 1;
				else
					AccPresent = 0;
				if ((present & 4) > 0)
					BaroPresent = 1;
				else
					BaroPresent = 0;
				if ((present & 8) > 0)
					MagPresent = 1;
				else
					MagPresent = 0;
				if ((present & 16) > 0)
					GPSPresent = 1;
				else
					GPSPresent = 0;

				I2cAccActive = ((mode & 1) > 0);
				I2cBaroActive = ((mode & 2) > 0);
				I2cMagnetoActive = ((mode & 4) > 0);
				GPSActive = (((mode & 8) > 0) || ((mode & 16) > 0));

				for (int i = 0; i < CHECKBOXITEMS; i++) { // highest bit
															// contains
															// mwc state for
															// this
															// item xxx
					if (((activation2[i]) & (1 << 7)) > 0) {
						ActiveModes[i] = true;
					} else {
						ActiveModes[i] = false;
					}
				}

				// ///////////////
				if (bt.Read() != 'M') {
					bt.Read();
					versionMisMatch = 1;
					log("versionMisMatch", versionMisMatch);
				}
			}
		}

	}

	@Override
	public void SendRequest() {
		if (bt.Connected) {
			bt.Send("M");
		}
	}

	@Override
	public void ProcessSerialData(boolean appLogging) {
		if (bt.Connected) {
			processData();

			baro = alt = baro - AltCorrection;

			if (appLogging)
				Logging();
		}
	}

	@Override
	public void SendRequestGetPID() {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestAccCalibration() {
		if (bt.Connected) {
			bt.Send("S");
		}
	}

	@Override
	public void SendRequestMagCalibration() {
		if (bt.Connected) {
			bt.Send("E");
		}
	}

	@Override
	public void SendRequestResetSettings() {

	}

	@Override
	public void SendRequestSetPID(float confRC_RATE, float confRC_EXPO,
			float rollPitchRate, float yawRate, float dynamic_THR_PID,
			float throttle_MID, float throttle_EXPO, float[] confP,
			float[] confI, float[] confD) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestGPSinject21(byte GPS_FIX, byte numSat, int coordLAT,
			int coordLON, int altitude, int speed) {
		// not supported

	}

	@Override
	public void SendRequestGetMisc() {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestSetandSaveMISC(int confPowerTrigger) {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestGetWayPoints() {
		// not supported
	}

	@Override
	public void SendRequestSetRawRC(int[] channels8) {
		// not supported
	}

	@Override
	public void SendRequestGetCheckboxes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestSetCheckboxes() {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestWriteToEEprom() {
		// TODO Auto-generated method stub

	}

	@Override
	public void SendRequestSelectSetting(int setting) {
		// not supported
	}

	@Override
	public void SendRequestSPEK_BIND() {
		// not supported
	}
}
