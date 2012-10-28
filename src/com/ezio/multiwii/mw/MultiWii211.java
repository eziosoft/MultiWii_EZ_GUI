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

import java.util.ArrayList;

import android.util.Log;

import com.ezio.multiwii.notUsed.Waypoint;

public class MultiWii211 extends MultiWii210 {

	public MultiWii211(BT b) {
		super(b);
		EZGUIProtocol = "211 r1177";
	}

	@Override
	public void evaluateCommand(byte cmd, int dataSize) {

		int i;
		int icmd = (int) (cmd & 0xFF);
		switch (icmd) {

		case MSP_STATUS:
			if (version == 211) {
				cycleTime = read16();
				i2cError = read16();
				present = read16();
				mode = read32();
				confSetting = read8();

				if ((present & 1) > 0)
					AccPresent = 1;
				else
					AccPresent = 0;

				if ((present & 2) > 0)
					BaroPresent = 1;
				else
					BaroPresent = 0;

				if ((present & 4) > 0)
					MagPresent = 1;
				else
					MagPresent = 0;

				if ((present & 8) > 0)
					GPSPresent = 1;
				else
					GPSPresent = 0;

				if ((present & 16) > 0)
					SonarPresent = 1;
				else
					SonarPresent = 0;

				for (i = 0; i < CHECKBOXITEMS; i++) {
					if ((mode & (1 << i)) > 0)
						ActiveModes[i] = true;
					else
						ActiveModes[i] = false;
				}
			}
			break;

		case MSP_DEBUGMSG:
			while (dataSize-- > 0) {
				char c = (char) read8();
				if (c != 0) {
					DebugMSG += c;
				}
			}
			break;

		case MSP_RAW_GPS:
			if (version == 211) {
				GPS_fix = read8();
				GPS_numSat = read8();
				GPS_latitude = read32();
				GPS_longitude = read32();
				GPS_altitude = read16();
				GPS_speed = read16();
				GPS_ground_course = read16();
			}
			break;

		case MSP_ALTITUDE:
			if (version == 211) {
				baro = alt = (float) read32() / 100;
				vario = read16();
			}
			break;

		}

		super.evaluateCommand(cmd, dataSize);
	}

	@Override
	public void SendRequestSelectSetting(int setting) {
		{
			payload = new ArrayList<Character>();
			payload.add((char) setting);
			sendRequestMSP(requestMSP(MSP_SELECT_SETTING,
					payload.toArray(new Character[payload.size()])));
		}
		// super.SendRequestSelectSetting(setting);
	}

	@Override
	public void SendRequestSPEK_BIND() {
		sendRequestMSP(requestMSP(MSP_SPEK_BIND));
		// super.SendRequestSPEK_BIND();
	}

	@Override
	public void SendRequestMSP_SET_WP(Waypoint w) {
		// params are:
		// 1 octet: always 0 for the moment, ie WP 0 = HOME POS
		// 4 octets: LAT
		// 4 octets: LON
		// 4 octets: altitude (not used for the moment, no need to set it)
		// 1 octets: nav flag (not used for the moment, no need to set it)
		//

		ArrayList<Character> payload = new ArrayList<Character>();
		payload.add((char) w.Number);
		payload.add((char) (w.Lat & 0xFF));
		payload.add((char) ((w.Lat >> 8) & 0xFF));
		payload.add((char) ((w.Lat >> 16) & 0xFF));
		payload.add((char) ((w.Lat >> 24) & 0xFF));

		payload.add((char) (w.Lon & 0xFF));
		payload.add((char) ((w.Lon >> 8) & 0xFF));
		payload.add((char) ((w.Lon >> 16) & 0xFF));
		payload.add((char) ((w.Lon >> 24) & 0xFF));

		payload.add((char) (w.Alt & 0xFF));
		payload.add((char) ((w.Alt >> 8) & 0xFF));
		payload.add((char) ((w.Alt >> 16) & 0xFF));
		payload.add((char) ((w.Alt >> 24) & 0xFF));

		payload.add((char) w.NavFlag);

		sendRequestMSP(requestMSP(MSP_SET_WP,
				payload.toArray(new Character[payload.size()])));

		Log.d("aaa",
				"MSP_SET_WP " + String.valueOf(w.Number) + "  "
						+ String.valueOf(w.Lat) + "x" + String.valueOf(w.Lon)
						+ " " + String.valueOf(w.Alt) + " "
						+ String.valueOf(w.NavFlag));

		// TODO Auto-generated method stub
		// super.SendRequestMSP_SET_WP(waypoint);
	}

	// TODO add MSP_DEBUGMSG in Request

}
