package com.ezio.multiwii.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

public class Sensors implements SensorEventListener, LocationListener {
	/* sensor data */
	SensorManager m_sensorManager;
	float[] m_lastMagFields = new float[3];;
	float[] m_lastAccels = new float[3];;
	private float[] m_rotationMatrix = new float[16];
	private float[] m_orientation = new float[4];

	public float GetPitch = 0.f;
	public float GetYaw = 0.f;
	public float GetRoll = 0.f;

	private Context context;

	public Sensors(Context context) {
		this.context = context;
		start();
	}

	public void start() {
		m_sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		registerListeners();
	}

	private void registerListeners() {
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_GAME);
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_GAME);
	}

	private void unregisterListeners() {
		m_sensorManager.unregisterListener(this);
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

			float yaw = (float) Math.toDegrees(m_orientation[0]);
			float pitch = (float) Math.toDegrees(m_orientation[1]);
			float roll = (float) Math.toDegrees(m_orientation[2]);

			GetYaw = filterYaw.lowPass(yaw);
			GetPitch = filterPitch.lowPass(pitch);
			GetRoll = filterRoll.lowPass(roll);

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
		// TODO Auto-generated method stub

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