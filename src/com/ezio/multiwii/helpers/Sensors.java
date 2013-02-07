package com.ezio.multiwii.helpers;

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

public class Sensors implements SensorEventListener, LocationListener {

	private Listener mListener = null;

	public interface Listener {
		public void onSensorsStateChange();
	}

	public void registerListener(Listener listener) {
		mListener = listener;
	}

	private LocationManager locationManager;
	private String provider;
	GeomagneticField geoField;

	int PhoneNumSat = 0;
	double PhoneLatitude = 0;
	double PhoneLongitude = 0;
	double PhoneAltitude = 0;
	double PhoneSpeed = 0;
	int PhoneFix = 0;
	float PhoneAccuracy = 0;
	float Declination = 0;

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
		start();
	}

	public void start() {
		m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		// if (!app.D)
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		provider = locationManager.getBestProvider(criteria, false);
		Location location = locationManager.getLastKnownLocation(provider);
		if (location != null) {
			geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
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
			}
		});

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
				mListener.onSensorsStateChange();

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

		geoField = new GeomagneticField(Double.valueOf(location.getLatitude()).floatValue(), Double.valueOf(location.getLongitude()).floatValue(), Double.valueOf(location.getAltitude()).floatValue(), System.currentTimeMillis());
		Declination = geoField.getDeclination();
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
