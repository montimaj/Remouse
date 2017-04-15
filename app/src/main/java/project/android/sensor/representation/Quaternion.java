package project.android.sensor.representation;

/**
 * Class to represent a quaternion.
 *
 * <p>
 *     A quaternion <i>H</i> may be identified with R<sup>4</sup>,
 *     a four-dimensional vector space over the real numbers. The
 *     elements of this basis are customarily denoted as <i>1</i>,
 *     <i>i</i>, <i>j</i>, and <i>k</i>. Every element of <i>H</i>
 *     can be uniquely written as a linear combination of these
 *     basis elements, that is, as <i>a1 + bi + cj + dk</i>, where
 *     <i>a</i>, <i>b</i>, <i>c</i>, and <i>d</i> are real
 *     numbers.
 * </p>
 * <p>
 * 		Quaternions allow for elegant descriptions of 3D rotations, interpolations as well as extrapolations and
 * 		compared to Euler angles, they don't suffer from gimbal lock. Interpolations between two Quaternions are called
 * 		SLERP (Spherical Linear Interpolation).
 * </p>
 *
 * <p>
 *     If <i>a + bi + cj + dk</i> is any quaternion, then <i>a</i>
 *     is called its scalar part and <i>bi + cj + dk</i> is called
 *     its vector part. The scalar part of a quaternion is always
 *     real, and the vector part is always pure imaginary.
 * </p>
 * <p>
 *     Using the basis <i>1, i, j, k</i> of H it is possible to
 *     write <i>H</i> as a set of quadruples:
 * </p>
 * <p>
 *     &nbsp;&nbsp;&nbsp;&nbsp;
 *     <b><i>H = {(a,b,c,d) | a,b,c,d &isin; R}</i></b>.
 * </p>
 * <p>
 *     Then the basis elements are: <br/>
 *     <i>1 = (1,0,0,0)</i>, <br/>
 *     <i>i = (0,1,0,0)</i>, <br/>
 *     <i>j = (0,0,1,0)</i>, <br/>
 *     <i>k = (0,0,0,1)</i>. <br/>
 * </p>
 * <p>
 *     <b>Addition of two quaternions : </b><br/>
 *     <i>
 *     (a<sub>1</sub>,b<sub>1</sub>,c<sub>1</sub>,d<sub>1</sub>) +
 *     (a<sub>2</sub>,b<sub>2</sub>,c<sub>2</sub>,d<sub>2</sub>) =
 *     (a<sub>1</sub>+a<sub>2</sub>, b<sub>1</sub>+b<sub>2</sub>,
 *     c<sub>1</sub>+c<sub>2</sub>, d<sub>1</sub>+d<sub>2</sub>)
 *     </i>.
 * </p>
 * <p>
 *     <b>Multiplications of two quaternions : </b><br/>
 *     <i>
 *     (a<sub>1</sub>,b<sub>1</sub>,c<sub>1</sub>,d<sub>1</sub>)
 *     (a<sub>2</sub>,b<sub>2</sub>,c<sub>2</sub>,d<sub>2</sub>) =
 *     <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     (a<sub>1</sub>a<sub>2</sub> - b<sub>1</sub>b<sub>2</sub> -
 *     c<sub>1</sub>c<sub>2</sub> - d<sub>1</sub>d<sub>2</sub>,
 *     <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     a<sub>1</sub>b<sub>2</sub> + b<sub>1</sub>a<sub>2</sub> +
 *     c<sub>1</sub>d<sub>2</sub> - d<sub>1</sub>c<sub>2</sub>,
 *     <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     a<sub>1</sub>c<sub>2</sub> - b<sub>1</sub>d<sub>2</sub> +
 *     c<sub>1</sub>a<sub>2</sub> + d<sub>1</sub>b<sub>2</sub>,
 *     <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     a<sub>1</sub>d<sub>2</sub> + b<sub>1</sub>c<sub>2</sub> -
 *     c<sub>1</sub>b<sub>2</sub> + d<sub>1</sub>a<sub>2</sub>)
 *     </i>.
 * </p>
 * <p>
 *     <b>Normalization of quaternion</b> <i>(a,b,c,d)</i> <br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;
 *     = <i>(a/mag, b/mag, c/mag, d/mag)</i>, <br/>
 *     where, <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     <i>mag =
 *     &radic;(a<sup>2</sup>+b<sup>2</sup>+c<sup>2</sup>+d<sup>2</sup>)</i>.
 * </p>
 * <p>
 *     The sensor data for 3-D mouse movement is sent from the
 *     mobile device is represented in the form of a
 *     <code>Quaternion</code>.
 * </p>
 *
 * @see project.android.sensor.representation.Vector4f
 */

public class Quaternion extends Vector4f {

	//For better performance update it only when it is accessed, not on every change

	private MatrixF4x4 mMatrix;
	private boolean mDirty = false;
	private Vector4f mTmpVector = new Vector4f();
	private Quaternion mTmpQuaternion;

	/**
	 * Constructor. <br/>
	 * Initializes this <code>Quaternion</code>.
	 */
	public Quaternion() {
		super();
		mMatrix = new MatrixF4x4();
		loadIdentityQuat();
	}

	/**
	 * Normalizes this <code>Quaternion</code>.
	 */
	public void normalize() {
		this.mDirty = true;
		float mag = (float) Math.sqrt(mPoints[3] * mPoints[3] + mPoints[0] * mPoints[0] + mPoints[1] * mPoints[1] +
				mPoints[2] * mPoints[2]);
		mPoints[3] = mPoints[3] / mag;
		mPoints[0] = mPoints[0] / mag;
		mPoints[1] = mPoints[1] / mag;
		mPoints[2] = mPoints[2] / mag;
	}

	/**
	 * Copies the values from the given quaternion to this one
	 *
	 * @param quat The quaternion to copy from
	 */
	public void set(Quaternion quat) {
		this.mDirty = true;
		copyVec4(quat);
	}

	/**
	 * Multiplies this <code>Quaternion</code> with another.
	 *
	 * @param input <code>Quaternion</code> with which
	 *              <code>this</code> is multiplied.
	 * @param output result of <code>(this * input)</code>.
	 */
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

	/**
	 * Multiply this quaternion by the input quaternion
	 *
	 * @param input Quaternion to be multiplied with
	 */
	public void multiplyByQuat(Quaternion input) {
		this.mDirty = true;
		if(mTmpQuaternion == null) mTmpQuaternion = new Quaternion();
		mTmpQuaternion.copyVec4(this);
		multiplyByQuat(input, mTmpQuaternion);
		this.copyVec4(mTmpQuaternion);
	}

	/**
	 * Multiples this <code>Quaternion</code> with a scalar.
	 * This overrides the {@link Vector4f#multiplyByScalar(float)}
	 * method.
	 *
	 * @param scalar the scalar.
	 * @see project.android.sensor.representation.Vector4f#multiplyByScalar(float)
	 */
	public void multiplyByScalar(float scalar) {
		this.mDirty = true;
		super.multiplyByScalar(scalar);
	}

	/**
	 * Add a quaternion to this quaternion
	 *
	 * @param input The quaternion that you want to add to this one
	 */
	public void addQuat(Quaternion input) {
		this.mDirty = true;
		addQuat(input, this);
	}

	/**
	 * Add a quaternion to this quaternion and stores it into output.
	 *
	 * @param input The quaternion that you want to add to this one
	 */
	private void addQuat(Quaternion input, Quaternion output) {
		output.setX(getX() + input.getX());
		output.setY(getY() + input.getY());
		output.setZ(getZ() + input.getZ());
		output.setW(getW() + input.getW());
	}

	/**
	 * Subtract a quaternion to this quaternion
	 *
	 * @param input The quaternion to be subtracted from this one
	 */
	public void subQuat(Quaternion input) {
		this.mDirty = true;
		subQuat(input, this);
	}

	/**
	 * Subtract another quaternion from this quaternion and store the result in the output quaternion
	 *
	 * @param input The quaternion to be subtracted from this quaternion
	 * @param output The quaternion where the output will be stored.
	 */
	private void subQuat(Quaternion input, Quaternion output) {
		output.setX(getX() - input.getX());
		output.setY(getY() - input.getY());
		output.setZ(getZ() - input.getZ());
		output.setW(getW() - input.getW());
	}

	/**
	 * Converts this Quaternion into the Rotation-Matrix representation which can be accessed by
	 * {@link Quaternion#getMatrix4x4 getMatrix4x4}
	 */
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

	/**
	 * Get an axis angle representation of this quaternion.
	 *
	 * @param output Vector4f axis angle.
	 */
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

	/**
	 * Returns the heading, attitude and bank of this quaternion as euler angles in the double array respectively
	 *
	 * @return An array of size 3 containing the euler angles for this quaternion
	 */
	public double[] toEulerAngles() {
		double[] ret = new double[3];

		ret[0] = Math.atan2(2 * mPoints[1] * getW() - 2 * mPoints[0] * mPoints[2], 1 - 2 * (mPoints[1] * mPoints[1]) - 2 * (mPoints[2] * mPoints[2]));
		ret[1] = Math.asin(2 * mPoints[0] * mPoints[1] + 2 * mPoints[2] * getW());
		ret[2] = Math.atan2(2 * mPoints[0] * getW() - 2 * mPoints[1] * mPoints[2], 1 - 2 * (mPoints[0] * mPoints[0]) - 2 * (mPoints[2] * mPoints[2]));
		return ret;
	}

	/**
	 * Sets the quaternion to an identity quaternion of 0,0,0,1.
	 */
	private void loadIdentityQuat() {
		this.mDirty = true;
		setX(0);
		setY(0);
		setZ(0);
		setW(1);
	}

	/**
	 * Returns a <code>String</code> representing this
	 * <code>Quaternion</code>. This overrides the
	 * {@link Vector4f#toString()} method.
	 *
	 * @return <code>String</code> representing this
	 *         <code>Quaternion</code>.
	 */
	@Override
	public String toString() {
		return "{X: " + getX() + ", Y:" + getY() + ", Z:" + getZ() + ", W:" + getW() + "}";
	}

	/**
	 * This is an internal method used to build a quaternion from a rotation matrix and then sets the current quaternion
	 * from that matrix.
	 *
	 */
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


	/**
	 * The values of this quaternion can be set based on a rotation matrix. If the supplied matrix is not a rotation
	 * matrix this will fail. A 4x4 matrix must be provided.
	 *
	 * @param mMatrix A column major rotation matrix
	 */
	public void setColumnMajor(float[] mMatrix) {

		this.mMatrix.setMatrix(mMatrix);
		this.mMatrix.setColumnMajor(true);

		generateQuaternionFromMatrix();
	}

	/**
	 * The values for this quaternion can be set based on a rotation matrix. If the matrix you supply is not a
	 * rotation matrix this will fail.
	 *
	 * @param mMatrix A column major rotation matrix
	 */
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

	/**
	 * Rotation is in degrees. Set this quaternion from the supplied axis angle.
	 *
	 * @param vec The vector of rotation
	 * @param rot The angle of rotation around that vector in degrees.
	 */
	public void setAxisAngle(Vector3f vec, float rot) {
		double s = Math.sin(Math.toRadians(rot / 2));
		setX(vec.getX() * (float) s);
		setY(vec.getY() * (float) s);
		setZ(vec.getZ() * (float) s);
		setW((float) Math.cos(Math.toRadians(rot / 2)));

		mDirty = true;
	}

	/**
	 * Same as <code>setAxisAngle</code>
	 * Takes the angles in Radian.
	 *
	 * @param vec The vector of rotation.
	 * @param rot The angle of rotation around that vector in degrees.
	 */
	public void setAxisAngleRad(Vector3f vec, double rot) {
		double s = rot / 2;
		setX(vec.getX() * (float) s);
		setY(vec.getY() * (float) s);
		setZ(vec.getZ() * (float) s);
		setW((float) rot / 2);

		mDirty = true;
	}

	/**
	 * @return Returns this Quaternion in the Rotation Matrix representation
	 */
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

	/**
	 * Get a linear interpolation between this quaternion and the input quaternion, storing the
     * result in the output quaternion.
	 *
	 * @param input The quaternion to be slerped with this quaternion.
	 * @param output The quaternion to store the result in.
	 * @param t The ratio between the two quaternions where 0 <= t <= 1.0 . Increase value of t will
     * bring rotation closer to the input quaternion.
	 */
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

	//

	/**
	 *Rotate a 3-D vector by this <code>Quaternion</code>.
	 *
	 * @param v the {@link Vector3f} to be rotated.
	 * @return the rotated {@link Vector3f}.
	 */
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