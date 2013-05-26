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

import java.lang.reflect.Method;
import java.util.Iterator;

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
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

public class Sensors implements SensorEventListener, LocationListener {

	private Listener mListener = null;

	Location location, oldLocation;

	LowPassFilter filterYaw = new LowPassFilter(0.03f);
	LowPassFilter filterPitch = new LowPassFilter(0.03f);
	LowPassFilter filterRoll = new LowPassFilter(0.03f);

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

	public float Pitch = 0.f;
	public float Heading = 0.f;
	public float Roll = 0.f;

	private Context context;

	String mocLocationProvider;
	public boolean MockLocationWorking = false;

	public interface Listener {
		public void onSensorsStateChangeMagAcc();

		public void onSensorsStateGPSLocationChange();

		public void onSensorsStateGPSStatusChange();
	}

	public void registerListener(Listener listener) {
		mListener = listener;
	}

	public void initMOCKLocation() {
		mocLocationProvider = LocationManager.GPS_PROVIDER;
		// mocLocationProvider = LocationManager.NETWORK_PROVIDER;
		locationManager.addTestProvider(mocLocationProvider, false, false, false, false, true, true, true, 0, 5);
		locationManager.setTestProviderEnabled(mocLocationProvider, true);
		MockLocationWorking = true;
	}

	public boolean isMockEnabled() {
		try {
			int mock_location = Settings.Secure.getInt(context.getContentResolver(), "mock_location");
			if (mock_location == 0) {
				try {
					Settings.Secure.putInt(context.getContentResolver(), "mock_location", 1);
				} catch (Exception ex) {
				}
				mock_location = Settings.Secure.getInt(context.getContentResolver(), "mock_location");
			}

			if (mock_location == 0) {
				Toast.makeText(context, "Turn on the mock locations in your Android settings", Toast.LENGTH_LONG).show();
				return false;
			} else {
				return true;
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return false;
	}

	public void setMOCKLocation(double Latitude, double Longitude, float Altitude, float Heading, float speed) {

		Location mockLocation = new Location(mocLocationProvider); // a string
		mockLocation.setLatitude(Latitude); // double
		mockLocation.setLongitude(Longitude);
		mockLocation.setAltitude(Altitude);
		mockLocation.setTime(System.currentTimeMillis());
		mockLocation.setAccuracy(1);
		mockLocation.setBearing(Heading);
		mockLocation.setSpeed(speed * 0.01f);

		try {
			Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
			if (locationJellyBeanFixMethod != null) {
				locationJellyBeanFixMethod.invoke(mockLocation);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}

		locationManager.setTestProviderLocation(mocLocationProvider, mockLocation);

	}

	public void ClearMOCKLocation() {
		if (mocLocationProvider != null) {
			Log.d("aaa", "ClearMOCKLocation");

			locationManager.clearTestProviderEnabled(mocLocationProvider);
			locationManager.clearTestProviderLocation(mocLocationProvider);
			locationManager.clearTestProviderStatus(mocLocationProvider);
			locationManager.removeTestProvider(mocLocationProvider);
			start();
			MockLocationWorking = false;

		}
	}

	public Sensors(Context context) {
		this.context = context;

		m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		// if (!app.D)
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, false);
		location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
			Declination = geoField.getDeclination();
			geopointOfflineMapCurrentPosition = new org.osmdroid.util.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
			geopointOnlineMapCurrentPosition = new com.google.android.maps.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));

			oldLocation = location;

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
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_NORMAL);
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
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

	private void computeOrientation() {
		if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastAccels, m_lastMagFields)) {
			SensorManager.getOrientation(m_rotationMatrix, m_orientation);

			float yaw = (float) (Math.toDegrees(m_orientation[0]) + Declination);
			float pitch = (float) Math.toDegrees(m_orientation[1]);
			float roll = (float) Math.toDegrees(m_orientation[2]);

			Heading = filterYaw.lowPass(yaw);
			Pitch = filterPitch.lowPass(pitch);
			Roll = filterRoll.lowPass(roll);

			if (mListener != null)
				mListener.onSensorsStateChangeMagAcc();

		}
	}

	@Override
	public void onLocationChanged(Location location) {

		oldLocation = this.location;
		this.location = location;

		PhoneLatitude = location.getLatitude();
		PhoneLongitude = location.getLongitude();
		PhoneAltitude = location.getAltitude();
		PhoneSpeed = location.getSpeed() * 100f;
		PhoneAccuracy = location.getAccuracy() * 100f;

		geopointOfflineMapCurrentPosition = new org.osmdroid.util.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));
		geopointOnlineMapCurrentPosition = new com.google.android.maps.GeoPoint((int) (location.getLatitude() * 1e6), (int) (location.getLongitude() * 1e6));

		geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
		Declination = geoField.getDeclination();

		// this is proper way but it doesn't work on some phones
		// if(location.getExtras()!=null)
		// PhoneNumSat = (Integer) location.getExtras().get("satellites");

		if (mListener != null)
			mListener.onSensorsStateGPSLocationChange();
	}

	@Override
	public void onProviderDisabled(String provider) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	public org.osmdroid.util.GeoPoint getNextPredictedLocationOfflineMap() {
		if (location != null && oldLocation != null) {
			// double lat = (location.getLatitude() + (location.getLatitude() -
			// oldLocation.getLatitude()));
			// double lon = (location.getLongitude() + (location.getLongitude()
			// - oldLocation.getLongitude()));

			return new org.osmdroid.util.GeoPoint(location.getLatitude() + (location.getLatitude() - oldLocation.getLatitude()), location.getLongitude() + (location.getLongitude() - oldLocation.getLongitude()));
		} else
			return new org.osmdroid.util.GeoPoint(0, 0);
	}

	public com.google.android.maps.GeoPoint getNextPredictedLocationOnlineMap() {
		if (location != null && oldLocation != null) {
			int lat = (int) ((location.getLatitude() + (location.getLatitude() - oldLocation.getLatitude())) * 1e6);
			int lon = (int) ((location.getLongitude() + (location.getLongitude() - oldLocation.getLongitude())) * 1e6);
			return new com.google.android.maps.GeoPoint(lat, lon);
		} else
			return new com.google.android.maps.GeoPoint(0, 0);

	}
}
