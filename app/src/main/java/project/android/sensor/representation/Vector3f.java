package project.android.sensor.representation;

/**
 * Class to represent a three dimensional vector.
 *
 * <p>
 *     It is defined as a point <i>(x,y,z)</i> in three dimensional
 *     vector space. It is stored as a array of <code>float</code>s.
 *     The three dimensions are known as X,Y and Z.
 * </p>
 * <p>
 *     <b>Dot product of two vectors : </b> <br/>
 *     <i>
 *     (a<sub>1</sub>,b<sub>1</sub>,c<sub>1</sub>) .
 *     (a<sub>2</sub>,b<sub>2</sub>,c<sub>2</sub>) =
 *     a<sub>1</sub>a<sub>2</sub> + b<sub>1</sub>b<sub>2</sub> +
 *     c<sub>1</sub>c<sub>2</sub>
 *     </i>.
 * </p>
 * <p>
 *     <b>Product of a vector with a scalar : </b> <br/>
 *     <i>(a, b, c) * s = (a * s, b * s, c * s)</i>.
 * </p>
 * <p>
 *     <b>Normalization of a vector</b> <i>(a,b,c)</i> <br/>
 *     &nbsp;&nbsp;&nbsp;&nbsp;
 *     = <i>(a/mag, b/mag, c/mag)</i>, <br/>
 *     where, <br/> &nbsp;&nbsp;&nbsp;&nbsp;
 *     <i>mag =
 *     &radic;(a<sup>2</sup>+b<sup>2</sup>+c<sup>2</sup>)</i>.
 * </p>
 *
 * @see project.android.sensor.representation.Vector3f
 * @see project.android.sensor.representation.Quaternion
 */

public class Vector3f {

	private float[] mPoints = new float[3];

	/**
	 * Constructor. <br/>
	 * Creates the <code>Vector3f (x,y,z)</code>.
	 *
	 * @param x the X-dimension.
	 * @param y the Y-dimension.
	 * @param z the Z-dimension.
	 */
	public Vector3f(float x, float y, float z) {
		this.mPoints[0] = x;
		this.mPoints[1] = y;
		this.mPoints[2] = z;
	}

	public Vector3f(float value) {
		this.mPoints[0] = value;
		this.mPoints[1] = value;
		this.mPoints[2] = value;
	}

	/**
	 * Constructor. <br/>
	 * Default constructor.
	 */
	public Vector3f() {
	}

	/**
	 * Constructor. <br/>
	 * Copy constructor. Copies another <code>Vector3f</code> to
	 * this one.
	 *
	 * @param vector the <code>Vector3f</code> to be copied.
	 */
	public Vector3f(Vector3f vector) {
		this.mPoints[0] = vector.mPoints[0];
		this.mPoints[1] = vector.mPoints[1];
		this.mPoints[2] = vector.mPoints[2];
	}

	public Vector3f(Vector4f vector) {
		if (vector.getW() != 0) {
			this.mPoints[0] = vector.getX() / vector.getW();
			this.mPoints[1] = vector.getY() / vector.getW();
			this.mPoints[2] = vector.getZ() / vector.getW();
		} else {
			this.mPoints[0] = vector.getX();
			this.mPoints[1] = vector.getY();
			this.mPoints[2] = vector.getZ();
		}
	}

	/**
	 * Returns the array representation of this <code>Vector3f</code>.
	 *
	 * @return the array <code>[ X, Y, Z ]</code>.
	 */
	float[] toArray() {
		return this.mPoints;
	}

	public void add(Vector3f summand) {
		this.mPoints[0] += summand.mPoints[0];
		this.mPoints[1] += summand.mPoints[1];
		this.mPoints[2] += summand.mPoints[2];
	}

	public void add(float summand) {
		this.mPoints[0] += summand;
		this.mPoints[1] += summand;
		this.mPoints[2] += summand;
	}

	public void subtract(Vector3f subtrahend) {
		this.mPoints[0] -= subtrahend.mPoints[0];
		this.mPoints[1] -= subtrahend.mPoints[1];
		this.mPoints[2] -= subtrahend.mPoints[2];
	}

	/**
	 * Multiples this <code>Vector3f</code> with a scalar.
	 * .
	 * @param scalar the scalar.
	 */
	public void multiplyByScalar(float scalar) {
		this.mPoints[0] *= scalar;
		this.mPoints[1] *= scalar;
		this.mPoints[2] *= scalar;
	}

	/**
	 * Normalizes this <code>Vector3f</code>.
	 */
	public void normalize() {

		double a = Math.sqrt(mPoints[0] * mPoints[0] + mPoints[1] * mPoints[1] + mPoints[2] * mPoints[2]);
		this.mPoints[0] = (float) (this.mPoints[0] / a);
		this.mPoints[1] = (float) (this.mPoints[1] / a);
		this.mPoints[2] = (float) (this.mPoints[2] / a);

	}

	/**
	 * Returns the X-dimension of this <code>Vector3f</code>.
	 *
	 * @return the X-dimension.
	 */
	public float getX() {
		return mPoints[0];
	}

	/**
	 * Returns the Y-dimension of this <code>Vector3f</code>.
	 *
	 * @return the Y-dimension.
	 */
	public float getY() {
		return mPoints[1];
	}

	/**
	 * Returns the Z-dimension of this <code>Vector3f</code>.
	 *
	 * @return the Z-dimension.
	 */
	public float getZ() {
		return mPoints[2];
	}

	/**
	 * Sets the X-dimension of this <code>Vector3f</code>.
	 *
	 * @param x the X-dimension
	 */
	void setX(float x) {
		this.mPoints[0] = x;
	}

	/**
	 * Sets the Y-dimension of this <code>Vector3f</code>.
	 *
	 * @param y the Y-dimension
	 */
	void setY(float y) {
		this.mPoints[1] = y;
	}

	/**
	 * Sets the Z-dimension of this <code>Vector3f</code>.
	 *
	 * @param z the Z-dimension
	 */
	void setZ(float z) {
		this.mPoints[2] = z;
	}

	/**
	 * Sets this <code>Vector3f</code> as <code>(x,y,z)</code>.
	 *
	 * @param x the X-dimension.
	 * @param y the Y-dimension.
	 * @param z the Z-dimension.
	 */
	public void setXYZ(float x, float y, float z) {
		this.mPoints[0] = x;
		this.mPoints[1] = y;
		this.mPoints[2] = z;
	}

	/**
	 * Performs the dot product of two vectors.
	 *
	 * @param inputVec the <code>Vector3f</code> to be multiplied.
	 * @return result of <code>(this . inputVec)</code>.
	 */
	public float dotProduct(Vector3f inputVec) {
		return mPoints[0] * inputVec.mPoints[0] + mPoints[1] * inputVec.mPoints[1] + mPoints[2] * inputVec.mPoints[2];

	}

	public void crossProduct(Vector3f inputVec, Vector3f outputVec) {
		outputVec.setX(mPoints[1] * inputVec.mPoints[2] - mPoints[2] * inputVec.mPoints[1]);
		outputVec.setY(mPoints[2] * inputVec.mPoints[0] - mPoints[0] * inputVec.mPoints[2]);
		outputVec.setZ(mPoints[0] * inputVec.mPoints[1] - mPoints[1] * inputVec.mPoints[0]);
	}

	public float getLength() {
		return (float) Math.sqrt(mPoints[0] * mPoints[0] + mPoints[1] * mPoints[1] + mPoints[2] * mPoints[2]);
	}

	/**
	 * Returns the <code>String</code> representation of this <code>
	 * Vector3f</code>. <br/>
	 * It overrides the {@link java.lang.Object#toString()} method.
	 *
	 * @return <code>String</code> representing this
	 *         <code>Vector3f</code>.
	 */
	@Override
	public String toString() {
		return "X:" + mPoints[0] + " Y:" + mPoints[1] + " Z:" + mPoints[2];
	}

	public void set(Vector3f source) {
		set(source.mPoints);
	}

	private void set(float[] source) {
		System.arraycopy(source, 0, mPoints, 0, 3);
	}
}