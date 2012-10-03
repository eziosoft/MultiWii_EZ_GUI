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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.util.Log;

import com.ezio.multiwii.mw.BT;

public class FrskyProtocol {

	static final int GPSAltitudeBefore = 0x01;// GPS altitude m S Before”.”
	static final int GPSAltitudeAfter = 0x01 + 8;// U After “.”
	static final int Temperature1 = 0x02;// Temprature1 °C S 1°C / -30~250°C
	static final int RPM = 0x03;// RPM RPM U 0~60000
	static final int FuelLevel = 0x04;// Fuel Level % U 0, 25, 50, 75, 100
	static final int Temperature2 = 0x05;// Temprature2 °C S 1°C / -30~250
	static final int Volt = 0x06;// Volt v 0.01v / 0~4.2v
	static final int AltitudeBefore = 0x10;// Altitude m S 0.01m / -500~9000m
											// Before
	// “.”
	static final int AltutudeAfter = 0x21;// U After “.”
	static final int GPSspeedBefore = 0x11;// GPS speed Knots U Before “.”
	static final int GPSspeedAfter = 0x11 + 8;// U After “.”
	static final int LongitudeBefore = 0x12;// Longitude dddmm.mmmm Before “.”
	static final int LongitudeAfter = 0x12 + 8;// After “.”
	static final int EW = 0x1A + 8;// E/W
	static final int LatitudeBefore = 0x13;// Latitude ddmm.mmmm Before “.”
	static final int LatitudeAfter = 0x13 + 8;// U After “.”
	static final int NS = 0x1B + 8;// N/S U
	static final int CourseBefore = 0x14;// Course degree U 0~359.99 Before “.”
	static final int CourseAfter = 0x14 + 8;// After “.”
	static final int Month = 0x15;// Date/Month
	static final int Year = 0x16;// Year
	static final int HourMinute = 0x17;// Hour /Minute
	static final int Second = 0x18;// Second
	static final int AccX = 0x24;// Acc-x S 0.016g / -8g ~ +8g
	static final int AccY = 0x25;// Acc-y S 0.016g / -8g ~ +8g
	static final int ACCZ = 0x26;// Acc-z S 0.016g / -8g ~ +8g
	static final int VoltageBefore = 0x3A;// ﹡Voltage (Ampere Sensor) v U 0.5v /
	// 0~48.0v
	// Before “.”
	static final int VoltageAfter = 0x3B;// After “.”
	static final int Current = 0x28;// Current A U 1A / 0~100A

	public int FAccX = 0;
	public int FAccY = 0;
	public int FAccZ = 0;
	public int FHour = 0, FMinute = 0, FSecond = 0;
	public float FAltitude = 0;

	public int Analog1 = 0;
	public int Analog2 = 0;
	public int RxRSSI = 0;
	public int TxRSSI = 0;

	int f = 0;
	int frame[] = new int[11];
	public int hubErrors = 0;

	BT bt;

	public String lastHubFrameslog = "";

	void log(String frameName, String value) {
		lastHubFrameslog = frameName + "\n" + lastHubFrameslog;

		if (lastHubFrameslog.length() > 1000)
			lastHubFrameslog = "";

		Log.d("frsky", frameName);
		whatFramesAdd(frameName, value);
	}

	List<LastFrame> whatFrames = new LinkedList<LastFrame>();

	class LastFrame {
		String FrameName = "";
		String FrameValue = "";

		LastFrame(String frameName, String frameValue) {
			FrameName = frameName;
			FrameValue = frameValue;
		}

	}

	void whatFramesAdd(String frameName, String frameValue) {
		boolean exists = false;

		for (LastFrame n : whatFrames) {
			if (n.FrameName.equals(frameName)) {
				exists = true;
				n.FrameValue = frameValue;
				break;
			}
		}

		if (exists == false)
			whatFrames.add(new LastFrame(frameName, frameValue));
	}

	public String whatFramesToString() {
		String a = "";
		for (LastFrame n : whatFrames) {
			a = a + n.FrameName + "=" + n.FrameValue + "\n";
		}
		return a;

	}

	private void HubProtocol(int[] frame) {
		switch (frame[1]) {
		case GPSAltitudeBefore:

			log("GPSAltitudeBefore", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x01;// GPS altitude m S Before”.”
		case GPSAltitudeAfter:
			log("GPSAltitudeAfter", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x01 + 8;// U After “.”
		case Temperature1:
			log("Temperature1", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x02;// Temprature1 °C S 1°C / -30~250°C
		case RPM:
			log("RPM", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x03;// RPM RPM U 0~60000
		case FuelLevel:
			log("FuelLevel", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x04;// Fuel Level % U 0, 25, 50, 75, 100
		case Temperature2:
			log("Temperature2", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x05;// Temprature2 °C S 1°C / -30~250
		case Volt:
			log("Volt", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x06;// Volt v 0.01v / 0~4.2v
		case AltitudeBefore:
			FAltitude = (float) getInt(frame, 2);
			log("AltitudeBefore", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x10;// Altitude m S 0.01m / -500~9000m Before “.”
		case AltutudeAfter:
			FAltitude += ((float) getInt(frame, 2)) / 100f;
			log("AltutudeAfter", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x21;// U After “.”
		case GPSspeedBefore:
			log("GPSspeedBefore", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x11;// GPS speed Knots U Before “.”
		case GPSspeedAfter:
			log("GPSspeedAfter", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x11 + 8;// U After “.”
		case LongitudeBefore:
			log("LongitudeBefore", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x12;// Longitude dddmm.mmmm Before “.”
		case LongitudeAfter:
			log("LongitudeAfter", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x12 + 8;// After “.”
		case EW:
			log("EW", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x1A + 8;// E/W
		case LatitudeBefore:
			log("LatitudeBefore", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x13;// Latitude ddmm.mmmm Before “.”
		case LatitudeAfter:
			log("LatitudeAfter", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x13 + 8;// U After “.”
		case NS:
			log("NS", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x1B + 8;// N/S U
		case CourseBefore:
			log("CourseBefore", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x14;// Course degree U 0~359.99 Before “.”
		case CourseAfter:
			log("CourseAfter", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x14 + 8;// After “.”
		case Month:
			log("Month", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x15;// Date/Month
		case Year:
			log("Year", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x16;// Year
		case HourMinute:
			FHour = frame[2];
			FMinute = frame[3];
			log("HourMinute", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x17;// Hour /Minute
		case Second:
			FSecond = getInt(frame, 2);
			log("Second", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x18;// Second
		case AccX:
			FAccX = getInt(frame, 2);
			log("AccX", getHex(new int[] { frame[2], frame[3] }));

			break; // 0x24;// Acc-x S 0.016g / -8g ~ +8g
		case AccY:
			FAccY = getInt(frame, 2);
			log("AccY", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x25;// Acc-y S 0.016g / -8g ~ +8g
		case ACCZ:
			FAccZ = getInt(frame, 2);
			log("AccZ", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x26;// Acc-z S 0.016g / -8g ~ +8g
		case VoltageBefore:
			log("VoltageBefore", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x3A;// ﹡Voltage (Ampere Sensor) v U 0.5v / 0~48.0v
		// Before “.”
		case VoltageAfter:
			log("VoltageAfter", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x3B;// After “.”
		case Current:
			log("Current", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x28;// Current A U 1A / 0~100A

		default:
			log("error ID", getHex(frame));
			hubErrors++;
			break;
		}
	}

	public FrskyProtocol(BT b) {
		bt = b;
	}

	public void ProcessSerialData(boolean appLogging) {
		while (bt.available() > 0) {
			int b = bt.Read8();

			if (b == 0x7e) {
				frame[f] = b;
				if (frame[0] == 0x7e && frame[10] == 0x7e) {

					if (frame[1] == 0xFE) {
						evaluateCommandFE(frame);
					}

					if (frame[1] == 0xFD) {
						evaluateCommandFD(frame);
					}

					frame = new int[11];
				}
				f = 0;
			}

			if (b == 0x7d)
				b = (bt.Read8() ^ 0x20);

			frame[f] = b;
			f++;
			if (f > 10)
				f = 0;

		}

	}

	public int getInt(int[] arr, int off) {
		return (arr[off] & 0xff) + (arr[off + 1] << 8);
	} // end of

	void evaluateCommandFE(int[] frame) {
		Analog1 = frame[2];
		Analog2 = frame[3];
		TxRSSI = frame[4];
		RxRSSI = frame[5] / 2;

		// Log.d("frsky",
		// getHex(frame) + "   ->  v1=" + String.valueOf(Analog1) + " 21="
		// + String.valueOf(Analog2) + " lRx="
		// + String.valueOf(RxRSSI) + " lTx="
		// + String.valueOf(TxRSSI));
	}

	ArrayList<Integer> buffor = new ArrayList<Integer>();

	void evaluateCommandFD(int[] frame) {

		int validBytes = frame[2];

		int[] dataInFrame = new int[validBytes];

		for (int i = 4; i < 4 + validBytes; i++) {
			dataInFrame[i - 4] = frame[i];
		}

		int d5 = 0;
		for (int d : dataInFrame) {
			if (d5 == 1) {
				d = d ^ 0x60;
				d5 = 0;
			}

			if (d == 0x5d) {
				d5 = 1;

			}

			if (d5 == 0)
				buffor.add(d);
		}

		while (buffor.size() > 4) {
			if (buffor.get(0) == 0x5e && buffor.get(1) != 0x5e) {
				int[] hubFrame = new int[4];
				hubFrame[0] = buffor.get(0);
				hubFrame[1] = buffor.get(1);
				hubFrame[2] = buffor.get(2);
				hubFrame[3] = buffor.get(3);

				buffor.remove(0);
				buffor.remove(0);
				buffor.remove(0);
				buffor.remove(0);

				HubProtocol(hubFrame);

				Log.d("frsky",
						getHex(frame) + "->bytes " + String.valueOf(validBytes)
								+ "   b=" + getHex(buffor.toArray()) + "    f="
								+ getHex(dataInFrame) + "    hubFrame="
								+ getHex(hubFrame));

			} else {

				buffor.remove(0);
			}

			// Log.d("frsky",
			// getHex(frame) + "->bytes " + String.valueOf(validBytes)
			// + "   b=" + getHex(buffor.toArray()) + "    f="
			// + getHex(dataInFrame));
		}

	}

	static final String HEXES = "0123456789ABCDEF";

	public static String getHex(int[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final int b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(
					HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static String getHex(Object[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final Object b : raw) {
			hex.append(HEXES.charAt(((Integer) b & 0xF0) >> 4)).append(
					HEXES.charAt(((Integer) b & 0x0F)));
		}
		return hex.toString();
	}

}
