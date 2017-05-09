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
 * Accesses the sensor hardware to read sensor data.
 *
 * <p>
 *     Classes implementing this interface provide an orientation of the device
 *     either by directly accessing hardware, using Android sensor fusion or
 *     fusing sensors itself. The orientation can be provided as a rotation matrix
 *     ({@link project.android.sensor.representation.MatrixF4x4}) or a quaternion
 *     ({@link project.android.sensor.representation.Quaternion}).
 * </p>
 *
 * @see project.android.sensor.representation.Quaternion
 * @see project.android.sensor.representation.MatrixF4x4
 * @see project.android.sensor.orientation.KalmanFilterProvider
 */
public abstract class OrientationProvider implements SensorEventListener {

	final Object synchronizationToken = new Object();

	final MatrixF4x4 currentOrientationRotationMatrix;

    final Quaternion currentOrientationQuaternion;

	List<Sensor> mSensorList = new ArrayList<>();

	private SensorManager sensorManager;

    /**
     * Constructor.
     *
     * Initializes this <code>OrientationProvider</code>.
     *
     * @param sensorManager a <code>SensorManager</code> object.
     */
	OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

    /**
     * Checks if gyroscope is available in the device.
     *
     * @param activity the current <code>Activity</code>.
     * @return <code>true</code>, if gyroscope is available, <br/>
     *         <code>false</code>, otherwise.
     */
	public static boolean checkGyro(Activity activity) {
		SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }

    /**
     * Starts the sensor fusion (e.g., when resuming the activity).
     */
	void sensorStart() {
		for (Sensor sensor : mSensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_UI);
		}
	}

    /**
     * Stops the sensor fusion (e.g., when pausing/suspending the activity).
     */
	public void sensorStop() {
		for (Sensor sensor : mSensorList) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

    /**
     * Gets the current rotation of the device.
     *
     * @param matrix a {@link project.android.sensor.representation.MatrixF4x4}
     *               object representing the rotation.
     * @see project.android.sensor.representation.MatrixF4x4
     */
	public void getRotationMatrix(MatrixF4x4 matrix) {
		synchronized (synchronizationToken) {
			matrix.set(currentOrientationRotationMatrix);
		}
	}

    /**
     * Gets the current rotation of the device.
     *
     * @param quaternion a {@link project.android.sensor.representation.Quaternion}
     *               object representing the rotation.
     * @see project.android.sensor.representation.Quaternion
     */
	public void getQuaternion(Quaternion quaternion) {
		synchronized (synchronizationToken) {
			quaternion.set(currentOrientationQuaternion);
		}
	}

    /**
     * Gets the current rotation of the device.
     *
     * @param angles a float array representing the rotation.
     */
	public void getEulerAngles(float angles[]) {
		synchronized (synchronizationToken) {
			SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
		}
	}
}