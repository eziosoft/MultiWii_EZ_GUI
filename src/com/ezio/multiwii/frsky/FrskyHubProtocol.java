package com.ezio.multiwii.frsky;

import java.util.LinkedList;
import java.util.List;

import android.util.Log;

public class FrskyHubProtocol {

	public float Altitude = 0;
	public float Heading = 0;
	public int Hour = 0, Minute = 0, Second_ = 0;
	public float Acc_X = 0;
	public float Acc_Y = 0;
	public float Acc_Z = 0;
	public int Temperature_1 = 0;
	public int GPS_Speed = 0;
	public float Voltage = 0;

	private float v1 = 0, v2 = 0;
	private float AltitudeTemp = 0;

	public int GPS_EW = 0;
	public int GPS_LongitudeBefore = 0;
	public int GPS_LongitudeAfter = 0;
	public long GPS_Longtitude = 0;

	public int GPS_NS = 0;
	public int GPS_LatitudeBefore = 0;
	public int GPS_LatitudeAfter = 0;
	public long GPS_Latitude = 0;

	// ////////////////EZ-GUI
	public float angX = 0;
	public float angY = 0;
	// /////////////////

	public int hubErrors = 0;

	public String lastHubFrameslog = "";

	List<LastFrame> whatFrames = new LinkedList<LastFrame>();

	class LastFrame {
		String FrameName = "";
		String FrameValue = "";

		LastFrame(String frameName, String frameValue) {
			FrameName = frameName;
			FrameValue = frameValue;
		}
	}

	static final int GPSAltitudeBefore = 0x01;// GPS altitude m S Before”.”
	static final int GPSAltitudeAfter = 0x09;// U After “.”
	static final int Temperature1 = 0x02;// Temprature1 °C S 1°C / -30~250°C
											// //Number of sattelites
	static final int RPM = 0x03;// RPM RPM U 0~60000
	static final int FuelLevel = 0x04;// Fuel Level % U 0, 25, 50, 75, 100
	static final int Temperature2 = 0x05;// Temprature2 °C S 1°C / -30~250
	static final int Volt = 0x06;// Volt v 0.01v / 0~4.2v
	static final int AltitudeBefore = 0x10;// Altitude m S 0.01m / -500~9000m
											// Before
	// “.”
	static final int AltutudeAfter = 0x21;// U After “.”
	static final int GPSspeedBefore = 0x11;// GPS speed Knots U Before “.”
	static final int GPSspeedAfter = 0x19;// U After “.”
	static final int LongitudeBefore = 0x12;// Longitude dddmm.mmmm Before
											// “.”
	static final int LongitudeAfter = 0x1A;// After “.”
	static final int EW = 0x22;// E/W
	static final int LatitudeBefore = 0x13;// Latitude ddmm.mmmm Before “.”
	static final int LatitudeAfter = 0x1B;// U After “.”
	static final int NS = 0x23;// N/S U
	static final int CourseBefore = 0x14;// Course degree U 0~359.99 Before
											// “.”
	static final int CourseAfter = 0x1C;// After “.”
	static final int Month = 0x15;// Date/Month
	static final int Year = 0x16;// Year
	static final int HourMinute = 0x17;// Hour /Minute
	static final int Second = 0x18;// Second
	static final int AccX = 0x24;// Acc-x S 0.016g / -8g ~ +8g
	static final int AccY = 0x25;// Acc-y S 0.016g / -8g ~ +8g
	static final int ACCZ = 0x26;// Acc-z S 0.016g / -8g ~ +8g
	static final int VoltageBefore = 0x3A;// ﹡Voltage (Ampere Sensor) v U 0.5v
											// /
	// 0~48.0v
	// Before “.”
	static final int VoltageAfter = 0x3B;// After “.”
	static final int Current = 0x28;// Current A U 1A / 0~100A

	// User defined data IDs
	static final int ID_Gyro_X = 0x40;
	static final int ID_Gyro_Y = 0x41;
	static final int ID_Gyro_Z = 0x42;

	// Multiwii EZ-GUI
	static final int ID_Ang_X = 0x50;
	static final int ID_Ang_Y = 0x51;

	public void ProcessFrame(int[] frame) throws Exception {

		switch (frame[1]) {
		case GPSAltitudeBefore:
			log("GPSAltitudeBefore", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x01;// GPS altitude m S Before”.”
		case GPSAltitudeAfter:
			log("GPSAltitudeAfter", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x01 + 8;// U After “.”
		case Temperature1:
			Temperature_1 = getIntFromFrame(frame);
			log("+Temperature1 (Number of sat)", String.valueOf(getIntFromFrame(frame)));
			break; // 0x02;// Temprature1 °C S 1°C / -30~250°C
		case RPM:
			log("+RPM", String.valueOf(getIntFromFrame(frame)));// getHex(new
																// int[] {
																// frame[2],
																// frame[3] }));
			break; // 0x03;// RPM RPM U 0~60000
		case FuelLevel:
			log("FuelLevel", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x04;// Fuel Level % U 0, 25, 50, 75, 100
		case Temperature2:
			log("+Temperature2 (Distance to home)", String.valueOf(getIntFromFrame(frame)));
			break;// 0x05;// Temprature2 °C S 1°C / -30~250
		case Volt:
			log("Volt", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x06;// Volt v 0.01v / 0~4.2v
		case AltitudeBefore:
			AltitudeTemp = (float) getIntFromFrame(frame);
			log("+AltitudeBefore", String.valueOf(Altitude));
			break;// 0x10;// Altitude m S 0.01m / -500~9000m Before “.”
		case AltutudeAfter:
			Altitude = AltitudeTemp + ((float) getIntFromFrame(frame)) / 100f;
			log("+AltutudeAfter", String.valueOf(Altitude));
			break;// 0x21;// U After “.”
		case GPSspeedBefore:
			GPS_Speed = getIntFromFrame(frame);
			log("+GPSspeedBefore", String.valueOf(getIntFromFrame(frame)));
			break;// 0x11;// GPS speed Knots U Before “.”
		case GPSspeedAfter:
			log("GPSspeedAfter", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x11 + 8;// U After “.”
		case LongitudeBefore:
			GPS_LongitudeBefore = getIntFromFrame(frame);
			log("+LongitudeBefore", String.valueOf(getIntFromFrame(frame)));
			break; // 0x12;// Longitude dddmm.mmmm Before “.”
		case LongitudeAfter:
			GPS_LongitudeAfter = getIntFromFrame(frame);
			log("+LongitudeAfter", String.valueOf(getIntFromFrame(frame)));
			break;// 0x12 + 8;// After “.”
		case EW:
			GPS_EW = getIntFromFrame(frame) == 87 ? -1 : 1;
			log("+EW", String.valueOf(getIntFromFrame(frame)));
			break; // 0x1A + 8;// E/W
		case LatitudeBefore:
			GPS_LatitudeBefore = getIntFromFrame(frame);
			log("+LatitudeBefore", String.valueOf(getIntFromFrame(frame)));
			break; // 0x13;// Latitude ddmm.mmmm Before “.”
		case LatitudeAfter:
			GPS_LatitudeAfter = getIntFromFrame(frame);
			log("+LatitudeAfter", String.valueOf(getIntFromFrame(frame)));
			break;// 0x13 + 8;// U After “.”
		case NS:
			GPS_NS = getIntFromFrame(frame) == 78 ? 1 : -1;
			log("+NS", String.valueOf(getIntFromFrame(frame)));
			break;// 0x1B + 8;// N/S U
		case CourseBefore:
			Heading = getIntFromFrame(frame);
			log("+CourseBefore", String.valueOf(getIntFromFrame(frame)));
			break; // 0x14;// Course degree U 0~359.99 Before “.”
		case CourseAfter:
			// not needed
			log("CourseAfter", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x14 + 8;// After “.”
		case Month:
			log("Month", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x15;// Date/Month
		case Year:
			log("Year", getHex(new int[] { frame[2], frame[3] }));
			break; // 0x16;// Year
		case HourMinute:
			Hour = frame[2];
			Minute = frame[3];
			log("+HourMinute", String.valueOf(frame[2]) + ":" + String.valueOf(frame[3]));
			break; // 0x17;// Hour /Minute
		case Second:
			Second_ = getIntFromFrame(frame);
			log("+Second", String.valueOf(getIntFromFrame(frame)));
			break; // 0x18;// Second
		case AccX:
			Acc_X = (float) (getIntFromFrame(frame));
			log("+AccX", String.valueOf(getIntFromFrame(frame)));
			break; // 0x24;// Acc-x S 0.016g / -8g ~ +8g
		case AccY:
			Acc_Y = (float) (getIntFromFrame(frame));
			log("+AccY", String.valueOf(getIntFromFrame(frame)));
			break; // 0x25;// Acc-y S 0.016g / -8g ~ +8g
		case ACCZ:
			Acc_Z = (float) (getIntFromFrame(frame));
			log("+AccZ", String.valueOf(getIntFromFrame(frame)));
			break;// 0x26;// Acc-z S 0.016g / -8g ~ +8g
		case VoltageBefore:
			// TODO
			v1 = getIntFromFrame(frame) * 100;
			log("+VoltageBefore", String.valueOf(Voltage));
			break;// 0x3A;// ﹡Voltage (Ampere Sensor) v U 0.5v / 0~48.0v
		// Before “.”
		case VoltageAfter:
			// TODO
			v2 = getIntFromFrame(frame) * 10 - 5;
			Voltage = (v1 + v2) / 110f * 21f;

			log("+VoltageAfter", String.valueOf(Voltage));
			break; // 0x3B;// After “.”
		case Current:
			log("Current", getHex(new int[] { frame[2], frame[3] }));
			break;// 0x28;// Current A U 1A / 0~100A

		case ID_Gyro_X:
			log("ID_Gyro_X", getHex(new int[] { frame[2], frame[3] }));
			break;
		case ID_Gyro_Y:
			log("ID_Gyro_Y", getHex(new int[] { frame[2], frame[3] }));
			break;
		case ID_Gyro_Z:
			log("ID_Gyro_Z", getHex(new int[] { frame[2], frame[3] }));
			break;

		// ////////my protocol
		case ID_Ang_X:
			angX = getIntFromFrame(frame) / 10;
			log("+ID_Ang_X", String.valueOf(getIntFromFrame(frame)));
			break;
		case ID_Ang_Y:
			angY = getIntFromFrame(frame) / 10;
			log("+ID_Ang_Y", String.valueOf(getIntFromFrame(frame)));
			break;
		// ////////////////////////
		default:
			log("error ID", getHex(frame));
			hubErrors++;
			break;
		}
	}

	public int getIntFromFrame(int[] frame) {
		return ((byte) frame[2] & 0xff) + ((byte) frame[2 + 1] << 8);

	}

	static final String HEXES = "0123456789ABCDEF";

	public static String getHex(int[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final int b : raw) {
			hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F)));
		}
		return hex.toString();
	}

	public static String getHex(Object[] raw) {
		if (raw == null) {
			return null;
		}
		final StringBuilder hex = new StringBuilder(2 * raw.length);
		for (final Object b : raw) {
			hex.append(HEXES.charAt(((Integer) b & 0xF0) >> 4)).append(HEXES.charAt(((Integer) b & 0x0F)));
		}
		return hex.toString();
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

	void log(String frameName, String value) {
		lastHubFrameslog = frameName + "\n" + lastHubFrameslog;

		if (lastHubFrameslog.length() > 1000)
			lastHubFrameslog = "";

		Log.d("frsky", frameName);
		whatFramesAdd(frameName, value);
	}

	public String whatFramesToString() {
		String a = "";
		for (LastFrame n : whatFrames) {
			a = a + n.FrameName + "=" + n.FrameValue + "\n";
		}
		return a;

	}
}
