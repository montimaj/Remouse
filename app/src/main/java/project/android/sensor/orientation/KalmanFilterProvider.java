package project.android.sensor.orientation;

import project.android.sensor.representation.Quaternion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import static project.android.ConnectionFragment.sConnectionAlive;
import static project.android.ConnectionFragment.sSecuredClient;
import static project.android.MouseFragment.sMouseAlive;

/**
 * Utility for providing the filter module.
 *
 * <p>
 *     This class provides methods that deliver the relative orientation from the
 *     gyroscope present in the Android device. This sensor does not deliver an
 *     absolute orientation (with respect to magnetic north and gravity) but only
 *     a relative measurement starting from the point where it started.
 * </p>
 * <p>
 *     It extends the {@link OrientationProvider} class which is responsible for
 *     accessing the sensor hardware.
 * </p>
 *
 * @see project.android.sensor.orientation.OrientationProvider
 */
public class KalmanFilterProvider extends OrientationProvider {

    private final Quaternion deltaQuaternion = new Quaternion();
    private Quaternion mCorrectedQuaternion = new Quaternion();

    private long mTimestamp;
    private boolean mIsInitQuat;

	private static final float NS2S = 1.0f / 1000000000.0f;

	private static final double EPSILON = 0.1f; //[Experimental value] Filter-threshold

    /**
     * Constructor.
     *
     * Initializes this <code>KalmanFilterProvider</code>.
     *
     * @param sensorManager a <code>SensorManager</code> object.
     */
    public KalmanFilterProvider(SensorManager sensorManager) {
		super(sensorManager);
		mSensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mIsInitQuat = true;
        sensorStart();
	}

    /**
     *  Overrides the <code>SensorEventListener.onSensorChanged()</code>
     *  method of the Android API.
     *
     *  <p>
     *      Called when there is a new sensor event. It is also called if there
     *      is a reading available from a sensor with the exact same sensor
     *      values (but a newer timestamp).
     *  </p>
     *
     * @param event the <code>SensorEvent</code> object containing full
     *              information about the event.
     */
	@Override
	public void onSensorChanged(SensorEvent event) {

		// it is a good practice to check that we received the proper event
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

			// This timestamps delta rotation to be multiplied by the current rotation
			if (mTimestamp != 0) {
				final float dT = (event.timestamp - mTimestamp) * NS2S;
				// Axis of the rotation sample, not normalized yet.
				float axisX = event.values[0];
				float axisY = event.values[1];
				float axisZ = event.values[2];

				// Calculate the angular speed of the sample

				double gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

				if (gyroscopeRotationVelocity > EPSILON) {
					axisX /= gyroscopeRotationVelocity;
					axisY /= gyroscopeRotationVelocity;
					axisZ /= gyroscopeRotationVelocity;
				}

				double thetaOverTwo = gyroscopeRotationVelocity * dT / 2.0f;
				double sinThetaOverTwo = Math.sin(thetaOverTwo);
				double cosThetaOverTwo = Math.cos(thetaOverTwo);
				deltaQuaternion.setX((float) (sinThetaOverTwo * axisX));
				deltaQuaternion.setY((float) (sinThetaOverTwo * axisY));
				deltaQuaternion.setZ((float) (sinThetaOverTwo * axisZ));
				deltaQuaternion.setW(-(float) cosThetaOverTwo);

				synchronized (synchronizationToken) {
					deltaQuaternion.multiplyByQuat(currentOrientationQuaternion, currentOrientationQuaternion);
				}

				mCorrectedQuaternion.set(currentOrientationQuaternion);

				mCorrectedQuaternion.setW(-mCorrectedQuaternion.getW());

				synchronized (synchronizationToken) {
					// Set the rotation matrix as well to have both representations
					SensorManager.getRotationMatrixFromVector(currentOrientationRotationMatrix.matrix,
							mCorrectedQuaternion.array());
				}
                if(sConnectionAlive.containsValue(true) && sMouseAlive) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            sSecuredClient.sendData(mCorrectedQuaternion, mIsInitQuat);
                            mIsInitQuat = false;
                        }
                    }).start();
                } else	sensorStop();
			}
			mTimestamp = event.timestamp;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}
}