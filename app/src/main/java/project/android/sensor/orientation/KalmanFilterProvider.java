package project.android.sensor.orientation;

import project.android.sensor.representation.Quaternion;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import static project.android.ConnectionFragment.sConnectionAlive;
import static project.android.ConnectionFragment.sSecuredClient;
import static project.android.MouseFragment.sMouseAlive;

/**
 * KalmanFilterProvider that delivers the relative orientation from the {@link Sensor#TYPE_GYROSCOPE
 * Gyroscope}. This sensor does not deliver an absolute orientation (with respect to magnetic north
 * and gravity) but only a relative measurement starting from the point where it started.
 *
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 *
 */
public class KalmanFilterProvider extends OrientationProvider {

    /**
     * The quaternion that stores the difference that is obtained by the gyroscope.
     * Basically it contains a rotational difference encoded into a quaternion.
     *
     * To obtain the absolute orientation one must add this into an initial position by
     * multiplying it with another quaternion
     */
    private final Quaternion deltaQuaternion = new Quaternion();
    private Quaternion mCorrectedQuaternion = new Quaternion();

    private long mTimestamp;
    private boolean mIsInitQuat;

    /**
     * Constant specifying the factor between a Nano-second and a second
     */
	private static final float NS2S = 1.0f / 1000000000.0f;

    /**
     * This is a filter-threshold for discarding Gyroscope measurements that are below a certain
     * level and potentially are only noise and not real motion. Values from the gyroscope are
     * usually between 0 (stop) and 10 (rapid rotation), so 0.1 seems to be a reasonable threshold
     * to filter noise (usually smaller than 0.1) and real motion (usually > 0.1). Note that there
     * is a chance of missing real motion, if the use is turning the device really slowly, so this
     * value has to find a balance between accepting noise (threshold = 0) and missing slow
     * user-action (threshold > 0.5). 0.1 seems to work fine for this application.
     *
     */
	private static final double EPSILON = 0.1f; //[Experimental value] Filter-threshold

    /**
     * Initialises a new CalibratedGyroscopeProvider
     *
     * @param sensorManager The android sensor manager
     */
    public KalmanFilterProvider(SensorManager sensorManager) {
		super(sensorManager);
		mSensorList.add(sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        mIsInitQuat = true;
        sensorStart();
	}

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
                /**
                 * Value giving the total velocity of the gyroscope (will be high, when the device
                 * is moving fast and low when the device is standing still). This is usually a
                 * value between 0 and 10 for normal motion. Heavy shaking can increase it to about
                 * 25. Note that, these values are time-depended, so changing the sampling rate of
                 * the sensor will affect this value!
                 */
				double gyroscopeRotationVelocity = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);

                /**
				 * The rotation vector has to be Normalised if it's big enough to get the axis
                 */
				if (gyroscopeRotationVelocity > EPSILON) {
					axisX /= gyroscopeRotationVelocity;
					axisY /= gyroscopeRotationVelocity;
					axisZ /= gyroscopeRotationVelocity;
				}

				/**
				 * To be integrated around this axis with the angular speed by the timestep.
				 * in order to get a delta rotation from this sample over the timestep.
				 */
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

                /**
                 * <code>w</code> is inverted in the deltaQuaternion, because
                 * <code>currentOrientationQuaternion</code> required it. Before converting it back
                 * to matrix representation, it should be reverted
                 */
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