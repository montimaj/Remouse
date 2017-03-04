package sxccal.edu.android.remouse.sensor.representation;

/**
 * @author Abhisek Maiti
 */

public class Quaternion extends Vector4f {

	/**
	 * For better performance update it only when it is accessed, not on every change
	 */
	private MatrixF4x4 mMatrix;
	private Vector4f mTmpVector = new Vector4f();
	private Quaternion mTmpQuaternion;

    private boolean mDirty = false;

	public Quaternion() {
		super();
		mMatrix = new MatrixF4x4();
		loadIdentityQuat();
	}

	/**
	 * Normalize this Quaternion
	 */
	public void normalize() {
		this.mDirty = true;
		float mag = (float) Math.sqrt(mPoints[3] * mPoints[3] + mPoints[0] * mPoints[0] + mPoints[1] * mPoints[1] + mPoints[2] * mPoints[2]);
		mPoints[3] = mPoints[3] / mag;
		mPoints[0] = mPoints[0] / mag;
		mPoints[1] = mPoints[1] / mag;
		mPoints[2] = mPoints[2] / mag;
	}

	public void set(Quaternion quat) {
		this.mDirty = true;
		copyVec4(quat);
	}

	public void multiplyByQuat(Quaternion input, Quaternion output) {

		if (input != output) {
			output.mPoints[3] = (mPoints[3] * input.mPoints[3] - mPoints[0] * input.mPoints[0] - mPoints[1] * input.mPoints[1] - mPoints[2] * input.mPoints[2]);
			output.mPoints[0] = (mPoints[3] * input.mPoints[0] + mPoints[0] * input.mPoints[3] + mPoints[1] * input.mPoints[2] - mPoints[2]	* input.mPoints[1]);
			output.mPoints[1] = (mPoints[3] * input.mPoints[1] + mPoints[1] * input.mPoints[3] + mPoints[2] * input.mPoints[0] - mPoints[0]	* input.mPoints[2]);
			output.mPoints[2] = (mPoints[3] * input.mPoints[2] + mPoints[2] * input.mPoints[3] + mPoints[0] * input.mPoints[1] - mPoints[1]	* input.mPoints[0]);
		} else {
			mTmpVector.mPoints[0] = input.mPoints[0];
			mTmpVector.mPoints[1] = input.mPoints[1];
			mTmpVector.mPoints[2] = input.mPoints[2];
			mTmpVector.mPoints[3] = input.mPoints[3];

			output.mPoints[3] = (mPoints[3] * mTmpVector.mPoints[3] - mPoints[0] * mTmpVector.mPoints[0] - mPoints[1] * mTmpVector.mPoints[1] - mPoints[2] * mTmpVector.mPoints[2]);
			output.mPoints[0] = (mPoints[3] * mTmpVector.mPoints[0] + mPoints[0] * mTmpVector.mPoints[3] + mPoints[1] * mTmpVector.mPoints[2] - mPoints[2] * mTmpVector.mPoints[1]);
			output.mPoints[1] = (mPoints[3] * mTmpVector.mPoints[1] + mPoints[1] * mTmpVector.mPoints[3] + mPoints[2] * mTmpVector.mPoints[0] - mPoints[0] * mTmpVector.mPoints[2]);
			output.mPoints[2] = (mPoints[3] * mTmpVector.mPoints[2] + mPoints[2] * mTmpVector.mPoints[3] + mPoints[0] * mTmpVector.mPoints[1] - mPoints[1] * mTmpVector.mPoints[0]);
		}
	}

	public void multiplyByQuat(Quaternion input) {
		this.mDirty = true;
		if(mTmpQuaternion == null) mTmpQuaternion = new Quaternion();
		mTmpQuaternion.copyVec4(this);
		multiplyByQuat(input, mTmpQuaternion);
		this.copyVec4(mTmpQuaternion);
	}

	public void multiplyByScalar(float scalar) {
		this.mDirty = true;
		super.multiplyByScalar(scalar);
	}

	public void addQuat(Quaternion input) {
		this.mDirty = true;
		addQuat(input, this);
	}

	private void addQuat(Quaternion input, Quaternion output) {
		output.setX(getX() + input.getX());
		output.setY(getY() + input.getY());
		output.setZ(getZ() + input.getZ());
		output.setW(getW() + input.getW());
	}

	public void subQuat(Quaternion input) {
		this.mDirty = true;
		subQuat(input, this);
	}

	private void subQuat(Quaternion input, Quaternion output) {
		output.setX(getX() - input.getX());
		output.setY(getY() - input.getY());
		output.setZ(getZ() - input.getZ());
		output.setW(getW() - input.getW());
	}

	private void convertQuatToMatrix() {
		float x = mPoints[0];
		float y = mPoints[1];
		float z = mPoints[2];
		float w = mPoints[3];

		mMatrix.setX0(1 - 2 * (y * y) - 2 * (z * z));
		mMatrix.setX1(2 * (x * y) + 2 * (w * z));
		mMatrix.setX2(2 * (x * z) - 2 * (w * y));
		mMatrix.setX3(0);
		mMatrix.setY0(2 * (x * y) - 2 * (w * z));
		mMatrix.setY1(1 - 2 * (x * x) - 2 * (z * z));
		mMatrix.setY2(2 * (y * z) + 2 * (w * x));
		mMatrix.setY3(0);
		mMatrix.setZ0(2 * (x * z) + 2 * (w * y));
		mMatrix.setZ1(2 * (y * z) - 2 * (w * x));
		mMatrix.setZ2(1 - 2 * (x * x) - 2 * (y * y));
		mMatrix.setZ3(0);
		mMatrix.setW0(0);
		mMatrix.setW1(0);
		mMatrix.setW2(0);
		mMatrix.setW3(1);
	}

	public void toAxisAngle(Vector4f output) {
		if (getW() > 1) {
			normalize();
		}
		float angle = 2 * (float) Math.toDegrees(Math.acos(getW()));
		float x;
		float y;
		float z;

		float s = (float) Math.sqrt(1 - getW() * getW());
		if (s < 0.001) {
			x = mPoints[0]; // To get normalised axis replace with x=1 y=z=0
			y = mPoints[1];
			z = mPoints[2];
		} else {
			// normalise axis
			x = mPoints[0] / s;
			y = mPoints[1] / s;
			z = mPoints[2] / s;
		}

		output.mPoints[0] = x;
		output.mPoints[1] = y;
		output.mPoints[2] = z;
		output.mPoints[3] = angle;
	}

	public double[] toEulerAngles() {
		double[] ret = new double[3];

		ret[0] = Math.atan2(2 * mPoints[1] * getW() - 2 * mPoints[0] * mPoints[2], 1 - 2 * (mPoints[1] * mPoints[1]) - 2 * (mPoints[2] * mPoints[2]));
		ret[1] = Math.asin(2 * mPoints[0] * mPoints[1] + 2 * mPoints[2] * getW());
		ret[2] = Math.atan2(2 * mPoints[0] * getW() - 2 * mPoints[1] * mPoints[2], 1 - 2 * (mPoints[0] * mPoints[0]) - 2 * (mPoints[2] * mPoints[2]));
		return ret;
	}

	private void loadIdentityQuat() {
		this.mDirty = true;
		setX(0);
		setY(0);
		setZ(0);
		setW(1);
	}

	@Override
	public String toString() {
		return "{X: " + getX() + ", Y:" + getY() + ", Z:" + getZ() + ", W:" + getW() + "}";
	}

	private void generateQuaternionFromMatrix() {

		float qx;
		float qy;
		float qz;
		float qw;

		float[] mat = mMatrix.getMatrix();
		int[] indices;

		if (this.mMatrix.size() == 16) {
			if (this.mMatrix.isColumnMajor()) {
				indices = MatrixF4x4.MAT_IND_COL16_3X3;
			} else {
				indices = MatrixF4x4.MAT_IND_ROW16_3X3;
			}
		} else {
			if (this.mMatrix.isColumnMajor()) {
				indices = MatrixF4x4.MAT_IND_COL9_3X3;
			} else {
				indices = MatrixF4x4.MAT_IND_ROW9_3X3;
			}
		}

		int m00 = indices[0];
		int m01 = indices[1];
		int m02 = indices[2];

		int m10 = indices[3];
		int m11 = indices[4];
		int m12 = indices[5];

		int m20 = indices[6];
		int m21 = indices[7];
		int m22 = indices[8];

		float tr = mat[m00] + mat[m11] + mat[m22];
		if (tr > 0) {
			float s = (float) Math.sqrt(tr + 1.0) * 2;
			qw = 0.25f * s;
			qx = (mat[m21] - mat[m12]) / s;
			qy = (mat[m02] - mat[m20]) / s;
			qz = (mat[m10] - mat[m01]) / s;
		} else if ((mat[m00] > mat[m11]) & (mat[m00] > mat[m22])) {
			float s = (float) Math.sqrt(1.0 + mat[m00] - mat[m11] - mat[m22]) * 2;
			qw = (mat[m21] - mat[m12]) / s;
			qx = 0.25f * s;
			qy = (mat[m01] + mat[m10]) / s;
			qz = (mat[m02] + mat[m20]) / s;
		} else if (mat[m11] > mat[m22]) {
			float s = (float) Math.sqrt(1.0 + mat[m11] - mat[m00] - mat[m22]) * 2;
			qw = (mat[m02] - mat[m20]) / s;
			qx = (mat[m01] + mat[m10]) / s;
			qy = 0.25f * s;
			qz = (mat[m12] + mat[m21]) / s;
		} else {
			float s = (float) Math.sqrt(1.0 + mat[m22] - mat[m00] - mat[m11]) * 2;
			qw = (mat[m10] - mat[m01]) / s;
			qx = (mat[m02] + mat[m20]) / s;
			qy = (mat[m12] + mat[m21]) / s;
			qz = 0.25f * s;
		}

		setX(qx);
		setY(qy);
		setZ(qz);
		setW(qw);
	}

	public void setColumnMajor(float[] mMatrix) {

		this.mMatrix.setMatrix(mMatrix);
		this.mMatrix.setColumnMajor(true);

		generateQuaternionFromMatrix();
	}

	public void setRowMajor(float[] mMatrix) {

		this.mMatrix.setMatrix(mMatrix);
		this.mMatrix.setColumnMajor(false);

		generateQuaternionFromMatrix();
	}

	public void setEulerAngle(float azimuth, float pitch, float roll) {

		double heading = Math.toRadians(roll);
		double attitude = Math.toRadians(pitch);
		double bank = Math.toRadians(azimuth);

		double c1 = Math.cos(heading / 2);
		double s1 = Math.sin(heading / 2);
		double c2 = Math.cos(attitude / 2);
		double s2 = Math.sin(attitude / 2);
		double c3 = Math.cos(bank / 2);
		double s3 = Math.sin(bank / 2);
		double c1c2 = c1 * c2;
		double s1s2 = s1 * s2;
		setW((float) (c1c2 * c3 - s1s2 * s3));
		setX((float) (c1c2 * s3 + s1s2 * c3));
		setY((float) (s1 * c2 * c3 + c1 * s2 * s3));
		setZ((float) (c1 * s2 * c3 - s1 * c2 * s3));

		mDirty = true;
	}

	public void setAxisAngle(Vector3f vec, float rot) {
		double s = Math.sin(Math.toRadians(rot / 2));
		setX(vec.getX() * (float) s);
		setY(vec.getY() * (float) s);
		setZ(vec.getZ() * (float) s);
		setW((float) Math.cos(Math.toRadians(rot / 2)));

		mDirty = true;
	}

	public void setAxisAngleRad(Vector3f vec, double rot) {
		double s = rot / 2;
		setX(vec.getX() * (float) s);
		setY(vec.getY() * (float) s);
		setZ(vec.getZ() * (float) s);
		setW((float) rot / 2);

		mDirty = true;
	}

	public MatrixF4x4 getMatrix4x4() {
		if (mDirty) {
			convertQuatToMatrix();
			mDirty = false;
		}
		return this.mMatrix;
	}

	public void copyFromVec3(Vector3f vec, float w) {
		copyFromV3f(vec, w);
	}

	public void slerp(Quaternion input, Quaternion output, float t) {
		// Calculate angle between them
		Quaternion bufferQuat;
		float cosHalftheta = this.dotProduct(input);

		if (cosHalftheta < 0) {
			if(mTmpQuaternion == null) mTmpQuaternion = new Quaternion();
			bufferQuat = mTmpQuaternion;
			cosHalftheta = -cosHalftheta;
			bufferQuat.mPoints[0] = (-input.mPoints[0]);
			bufferQuat.mPoints[1] = (-input.mPoints[1]);
			bufferQuat.mPoints[2] = (-input.mPoints[2]);
			bufferQuat.mPoints[3] = (-input.mPoints[3]);
		} else {
			bufferQuat = input;
		}

		if (Math.abs(cosHalftheta) >= 1.0) {
			output.mPoints[0] = (this.mPoints[0]);
			output.mPoints[1] = (this.mPoints[1]);
			output.mPoints[2] = (this.mPoints[2]);
			output.mPoints[3] = (this.mPoints[3]);
		} else {
			double sinHalfTheta = Math.sqrt(1.0 - cosHalftheta * cosHalftheta);
			double halfTheta = Math.acos(cosHalftheta);

			double ratioA = Math.sin((1 - t) * halfTheta) / sinHalfTheta;
			double ratioB = Math.sin(t * halfTheta) / sinHalfTheta;

			//Calculate Quaternion
			output.mPoints[3] = ((float) (mPoints[3] * ratioA + bufferQuat.mPoints[3] * ratioB));
			output.mPoints[0] = ((float) (this.mPoints[0] * ratioA + bufferQuat.mPoints[0] * ratioB));
			output.mPoints[1] = ((float) (this.mPoints[1] * ratioA + bufferQuat.mPoints[1] * ratioB));
			output.mPoints[2] = ((float) (this.mPoints[2] * ratioA + bufferQuat.mPoints[2] * ratioB));
		}
	}

	//Rotate a Vector by Quaternion
	public Vector3f rotateVector(Vector3f v) {
		float q0 = this.mPoints[3];
		float q1 = this.mPoints[0];
		float q2 = this.mPoints[1];
		float q3 = this.mPoints[2];
		float v1 = v.getX();
		float v2 = v.getY();
		float v3 = v.getZ();
		float x = (1-2*q2*q2-2*q3*q3)*v1 + 2*(q1*q2+q0*q3)*v2 + 2*(q1*q3-q0*q2)*v3;
		float y = 2*(q1*q2-q0*q3)*v1 + (1-2*q1*q1-2*q3*q3)*v2 + 2*(q2*q3+q0*q1)*v3;
		float z = 2*(q1*q3-q0*q2) + (q2*q3-q0*q1)*v2 + (1-2*q1*q1-2*q2*q2)*v3;
		return new Vector3f(x,y,z);
	}
}