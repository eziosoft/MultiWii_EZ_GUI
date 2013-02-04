package com.ezio.multiwii.helpers;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Sensors implements SensorEventListener {
	/* sensor data */
	SensorManager m_sensorManager;
	float[] m_lastMagFields;
	float[] m_lastAccels;
	private float[] m_rotationMatrix = new float[16];
	private float[] m_remappedR = new float[16];
	private float[] m_orientation = new float[4];

	/* fix random noise by averaging tilt values */
	final static int AVERAGE_BUFFER = 30;
	float[] m_prevPitch = new float[AVERAGE_BUFFER];
	public float GetPitch = 0.f;
	public float GetYaw = 0.f;
	/* current index int m_prevEasts */
	int m_pitchIndex = 0;

	float[] m_prevRoll = new float[AVERAGE_BUFFER];
	public float GetRoll = 0.f;
	/* current index into m_prevTilts */
	int m_rollIndex = 0;

	/* center of the rotation */
	private float m_tiltCentreX = 0.f;
	private float m_tiltCentreY = 0.f;
	private float m_tiltCentreZ = 0.f;

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
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
		m_sensorManager.registerListener(this, m_sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_FASTEST);
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
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
			accel(event);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
			mag(event);
		}
	}

	private void accel(SensorEvent event) {
		if (m_lastAccels == null) {
			m_lastAccels = new float[3];
		}

		System.arraycopy(event.values, 0, m_lastAccels, 0, 3);

		/*
		 * if (m_lastMagFields != null) { computeOrientation(); }
		 */
	}

	private void mag(SensorEvent event) {
		if (m_lastMagFields == null) {
			m_lastMagFields = new float[3];
		}

		System.arraycopy(event.values, 0, m_lastMagFields, 0, 3);

		if (m_lastAccels != null) {
			computeOrientation();
		}
	}

	Filter[] m_filters = { new Filter(), new Filter(), new Filter() };

	private class Filter {
		static final int AVERAGE_BUFFER = 10;
		float[] m_arr = new float[AVERAGE_BUFFER];
		int m_idx = 0;

		public float append(float val) {
			m_arr[m_idx] = val;
			m_idx++;
			if (m_idx == AVERAGE_BUFFER)
				m_idx = 0;
			return avg();
		}

		public float avg() {
			float sum = 0;
			for (float x : m_arr)
				sum += x;
			return sum / AVERAGE_BUFFER;
		}

	}

	private void computeOrientation() {
		if (SensorManager.getRotationMatrix(m_rotationMatrix, null, m_lastMagFields, m_lastAccels)) {
			SensorManager.getOrientation(m_rotationMatrix, m_orientation);

			/* 1 radian = 57.2957795 degrees */
			/*
			 * [0] : yaw, rotation around z axis [1] : pitch, rotation around x
			 * axis [2] : roll, rotation around y axis
			 */
			float yaw = m_orientation[0] * 57.2957795f;
			float pitch = m_orientation[1] * 57.2957795f;
			float roll = m_orientation[2] * 57.2957795f;

			GetYaw = m_filters[0].append(yaw);
			GetPitch = m_filters[1].append(pitch);
			GetRoll = m_filters[2].append(roll);
			// TextView rt = (TextView) findViewById(R.id.roll);
			// TextView pt = (TextView) findViewById(R.id.pitch);
			// TextView yt = (TextView) findViewById(R.id.yaw);
			// yt.setText("azi z: " + m_lastYaw);
			// pt.setText("pitch x: " + m_lastPitch);
			// rt.setText("roll y: " + m_lastRoll);
		}
	}

}