package sxccal.edu.android.remouse.sensor.orientation;

import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import sxccal.edu.android.remouse.sensor.representation.MatrixF4x4;
import sxccal.edu.android.remouse.sensor.representation.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * Warning: This file has not checked thoroughly
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public abstract class OrientationProvider implements SensorEventListener {

	final Object synchronizationToken = new Object();

	List<Sensor> sensorList = new ArrayList<>();

	final MatrixF4x4 currentOrientationRotationMatrix;

	final Quaternion currentOrientationQuaternion;

	private SensorManager sensorManager;

	OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

	void sensorStart() {
		for (Sensor sensor : sensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void sensorStop() {
		for (Sensor sensor : sensorList) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

	public void getRotationMatrix(MatrixF4x4 matrix) {
		synchronized (synchronizationToken) {
			matrix.set(currentOrientationRotationMatrix);
		}
	}

	public void getQuaternion(Quaternion quaternion) {
		synchronized (synchronizationToken) {
			quaternion.set(currentOrientationQuaternion);
		}
	}

	public void getEulerAngles(float angles[]) {
		synchronized (synchronizationToken) {
			SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
		}
	}
}
