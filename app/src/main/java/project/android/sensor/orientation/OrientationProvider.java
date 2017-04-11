package project.android.sensor.orientation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import project.android.sensor.representation.MatrixF4x4;
import project.android.sensor.representation.Quaternion;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public abstract class OrientationProvider implements SensorEventListener {

	final Object synchronizationToken = new Object();
	final MatrixF4x4 currentOrientationRotationMatrix;
	final Quaternion currentOrientationQuaternion;

	List<Sensor> mSensorList = new ArrayList<>();

	private SensorManager sensorManager;

	OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

	public static boolean checkGyro(Activity activity) {
		SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }

	void sensorStart() {
		for (Sensor sensor : mSensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

	public void sensorStop() {
		for (Sensor sensor : mSensorList) {
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