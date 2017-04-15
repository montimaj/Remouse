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
 * Classes implementing this interface provide an orientation of the device
 * either by directly accessing hardware, using Android sensor fusion or fusing
 * sensors itself.
 *
 * The orientation can be provided as rotation matrix or quaternion.
 *
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 *
 */
public abstract class OrientationProvider implements SensorEventListener {

    /**
     * Sync-token for syncing read/write to sensor-data from sensor manager and fusion algorithm
     */
	final Object synchronizationToken = new Object();

    /**
     * The matrix that holds the current rotation
     */
	final MatrixF4x4 currentOrientationRotationMatrix;

    /**
     * The quaternion that holds the current rotation
     */
    final Quaternion currentOrientationQuaternion;

    /**
     * The list of sensors used by this provider
     */
	List<Sensor> mSensorList = new ArrayList<>();

    /**
     * The sensor manager for accessing android sensors
     */
	private SensorManager sensorManager;

    /**
     * Initialises a new OrientationProvider
     *
     * @param sensorManager The android sensor manager
     */
	OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

    /**
     *
     * Checks if Gyroscope is available
     *
     * @param activity current activity
     * @return android sensor manager
     */
	public static boolean checkGyro(Activity activity) {
		SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }

    /**
     * Starts the sensor fusion (e.g. when resuming the activity)
     */
	void sensorStart() {
		for (Sensor sensor : mSensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

    /**
     * Stops the sensor fusion (e.g. when pausing/suspending the activity)
     */
	public void sensorStop() {
		for (Sensor sensor : mSensorList) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

    /**
     * Get the current rotation of the device in the rotation matrix format (4x4 matrix)
     */
	public void getRotationMatrix(MatrixF4x4 matrix) {
		synchronized (synchronizationToken) {
			matrix.set(currentOrientationRotationMatrix);
		}
	}

    /**
     * Get the current rotation of the device in the quaternion format (vector4f)
     */
	public void getQuaternion(Quaternion quaternion) {
		synchronized (synchronizationToken) {
			quaternion.set(currentOrientationQuaternion);
		}
	}

    /**
     * Get the current rotation of the device in the Euler angles
     */
	public void getEulerAngles(float angles[]) {
		synchronized (synchronizationToken) {
			SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
		}
	}
}