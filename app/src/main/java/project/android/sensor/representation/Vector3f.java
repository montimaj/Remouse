package project.android.sensor.representation;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

class Vector3f {

	private float[] mPoints = new float[3];

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

	public Vector3f() {
	}

	public Vector3f(Vector3f vector) {
		this.mPoints[0] = vector.mPoints[0];
		this.mPoints[1] = vector.mPoints[1];
		this.mPoints[2] = vector.mPoints[2];
	}

	public Vector3f(Vector4f vector) {
		if (vector.w() != 0) {
			this.mPoints[0] = vector.x() / vector.w();
			this.mPoints[1] = vector.y() / vector.w();
			this.mPoints[2] = vector.z() / vector.w();
		} else {
			this.mPoints[0] = vector.x();
			this.mPoints[1] = vector.y();
			this.mPoints[2] = vector.z();
		}
	}

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

	public void multiplyByScalar(float scalar) {
		this.mPoints[0] *= scalar;
		this.mPoints[1] *= scalar;
		this.mPoints[2] *= scalar;
	}

	public void normalize() {

		double a = Math.sqrt(mPoints[0] * mPoints[0] + mPoints[1] * mPoints[1] + mPoints[2] * mPoints[2]);
		this.mPoints[0] = (float) (this.mPoints[0] / a);
		this.mPoints[1] = (float) (this.mPoints[1] / a);
		this.mPoints[2] = (float) (this.mPoints[2] / a);

	}

	float getX() {
		return mPoints[0];
	}

	float getY() {
		return mPoints[1];
	}

	float getZ() {
		return mPoints[2];
	}

	void setX(float x) {
		this.mPoints[0] = x;
	}

	void setY(float y) {
		this.mPoints[1] = y;
	}

	void setZ(float z) {
		this.mPoints[2] = z;
	}

	float x() {
		return this.mPoints[0];
	}

	float y() {
		return this.mPoints[1];
	}

	float z() {
		return this.mPoints[2];
	}

	public void x(float x) {
		this.mPoints[0] = x;
	}

	public void y(float y) {
		this.mPoints[1] = y;
	}

	public void z(float z) {
		this.mPoints[2] = z;
	}

	public void setXYZ(float x, float y, float z) {
		this.mPoints[0] = x;
		this.mPoints[1] = y;
		this.mPoints[2] = z;
	}

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