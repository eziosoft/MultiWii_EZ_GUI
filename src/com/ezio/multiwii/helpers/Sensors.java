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
package com.ezio.multiwii.helpers;

import java.util.Iterator;

import com.google.android.maps.GeoPoint;

import android.content.Context;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class Sensors implements SensorEventListener, LocationListener {

	private Listener mListener = null;

	public interface Listener {
		public void onSensorsStateChangeMagAcc();

		public void onSensorsStateGPSLocationChange();

		public void onSensorsStateGPSStatusChange();
	}

	public void registerListener(Listener listener) {
		mListener = listener;
	}

	private LocationManager locationManager;
	private String provider;
	GeomagneticField geoField;

	public int PhoneNumSat = 0;
	public double PhoneLatitude = 0;
	public double PhoneLongitude = 0;
	public double PhoneAltitude = 0;
	public double PhoneSpeed = 0;
	public int PhoneFix = 0;
	public float PhoneAccuracy = 0;
	public float Declination = 0;

	public org.osmdroid.util.GeoPoint geopointOfflineMapCurrentPosition = new org.osmdroid.util.GeoPoint(0, 0);
	public com.google.android.maps.GeoPoint geopointOnlineMapCurrentPosition = new com.google.android.maps.GeoPoint(0, 0);

	SensorManager m_sensorManager;
	float[] m_lastMagFields = new float[3];;
	float[] m_lastAccels = new float[3];;
	private float[] m_rotationMatrix = new float[16];
	private float[] m_orientation = new float[4];

	public float GetPitch = 0.f;
	public float GetHeading = 0.f;
	public float GetRoll = 0.f;

	private Context context;

	public Sensors(Context context) {
		this.context = context;

		m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		// if (!app.D)
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
			Declination = geoField.getDeclination();
			geopointOfflineMapCurrentPosition = new org.osmdroid.util.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
			geopointOnlineMapCurrentPosition = new com.google.android.maps.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
		}

		locationManager.addGpsStatusListener(new GpsStatus.Listener() {

			@Override
			public void onGpsStatusChanged(int event) {
				if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
					GpsStatus status = locationManager.getGpsStatus(null);
					Iterable<GpsSatellite> sats = status.getSatellites();
					Iterator<GpsSatellite> it = sats.iterator();

					PhoneNumSat = 0;
					while (it.hasNext()) {

						GpsSatellite oSat = (GpsSatellite) it.next();
						if (oSat.usedInFix())
							PhoneNumSat++;
					}

				}
				if (event == GpsStatus.GPS_EVENT_FIRST_FIX)
					PhoneFix = 1;

				if (mListener != null)
					mListener.onSensorsStateGPSStatusChange();
			}
		});

	}

	public void start() {

		registerListeners();

	}

	private void registerListeners() {
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
		locationManager.requestLocationUpdates(provider, 0, 0, this);
	}

	private void unregisterListeners() {
		m_sensorManager.unregisterListener(this);
		locationManager.removeUpdates(this);
	}

	public void stop() {
		unregisterListeners();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		switch (event.sensor.getType()) {
		case Sensor.TYPE_ACCELEROMETER:
			System.arraycopy(event.values, 0, m_lastAccels, 0, 3);
			break;
		case Sensor.TYPE_MAGNETIC_FIELD:
			System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);
			break;
		default:
			return;
		}

		computeOrientation();
	}

	LowPassFilter filterYaw = new LowPassFilter(0.05f);
	LowPassFilter filterPitch = new LowPassFilter(0.05f);
	LowPassFilter filterRoll = new LowPassFilter(0.05f);

	private void computeOrientation() {
		if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastAccels, m_lastMagFields)) {
			SensorManager.getOrientation(m_rotationMatrix, m_orientation);

			float yaw = (float) (Math.toDegrees(m_orientation[0]) + Declination);
			float pitch = (float) Math.toDegrees(m_orientation[1]);
			float roll = (float) Math.toDegrees(m_orientation[2]);

			GetHeading = filterYaw.lowPass(yaw);
			GetPitch = filterPitch.lowPass(pitch);
			GetRoll = filterRoll.lowPass(roll);

			if (mListener != null)
				mListener.onSensorsStateChangeMagAcc();

		}
	}

	public class LowPassFilter {
		/*
		 * time smoothing constant for low-pass filter 0 ≤ alpha ≤ 1 ; a smaller
		 * value basically means more smoothing See:
		 * http://en.wikipedia.org/wiki
		 * /Low-pass_filter#Discrete-time_realization
		 */
		float ALPHA = 0f;
		float lastOutput = 0;

		public LowPassFilter(float ALPHA) {
			this.ALPHA = ALPHA;
		}

		protected float lowPass(float input) {
			lastOutput = lastOutput + ALPHA * (input - lastOutput);
			return lastOutput;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		PhoneLatitude = location.getLatitude();
		PhoneLongitude = location.getLongitude();
		PhoneAltitude = location.getAltitude();
		PhoneSpeed = location.getSpeed() * 100f;
		PhoneAccuracy = location.getAccuracy() * 100f;

		geopointOfflineMapCurrentPosition = new org.osmdroid.util.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
		geopointOnlineMapCurrentPosition = new com.google.android.maps.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));

		geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
		Declination = geoField.getDeclination();

		if (mListener != null)
			mListener.onSensorsStateGPSLocationChange();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

}
