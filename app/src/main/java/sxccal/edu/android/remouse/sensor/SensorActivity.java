package sxccal.edu.android.remouse.sensor;

import android.app.Activity;
import android.hardware.SensorManager;

import sxccal.edu.android.remouse.sensor.orientation.KalmanFilterProvider;
import sxccal.edu.android.remouse.sensor.orientation.OrientationProvider;
import sxccal.edu.android.remouse.sensor.representation.Quaternion;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public class SensorActivity {
	private Quaternion mQuaternion;
    private OrientationProvider mCurrentOrientationProvider;

    public SensorActivity(Activity activity) {
        mCurrentOrientationProvider = new KalmanFilterProvider((SensorManager) activity.getSystemService(Activity.SENSOR_SERVICE));
        mQuaternion = new Quaternion();
        // Get the rotation from the currOrientationObjectent orientationProvider as quaternion
        mCurrentOrientationProvider.getQuaternion(mQuaternion);
	}
	public Quaternion getQuaternion() { return mQuaternion; }
    public void stopSensor() { mCurrentOrientationProvider.sensorStop(); }
}
