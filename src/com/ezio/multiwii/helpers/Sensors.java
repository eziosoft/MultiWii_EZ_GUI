package com.ezio.multiwii.helpers;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Sensors implements SensorEventListener {
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

			/*
			 * [0] : yaw, rotation around z axis [1] : pitch, rotation around x
			 * axis [2] : roll, rotation around y axis
			 */
			float yaw = (float) Math.toDegrees(m_orientation[0]);
			float pitch = (float) Math.toDegrees(m_orientation[1]);
			float roll = (float) Math.toDegrees(m_orientation[2]);

			GetYaw = yaw;
			GetPitch = pitch;
			GetRoll = roll;
			// TextView rt = (TextView) findViewById(R.id.roll);
			// TextView pt = (TextView) findViewById(R.id.pitch);
			// TextView yt = (TextView) findViewById(R.id.yaw);
			// yt.setText("azi z: " + m_lastYaw);
			// pt.setText("pitch x: " + m_lastPitch);
			// rt.setText("roll y: " + m_lastRoll);
		}
	}

}