package sxccal.edu.android.remouse.sensor.representation;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

class Vector3f {

	private float[] points = new float[3];

	public Vector3f(float x, float y, float z) {
		this.points[0] = x;
		this.points[1] = y;
		this.points[2] = z;
	}

	public Vector3f(float value) {
		this.points[0] = value;
		this.points[1] = value;
		this.points[2] = value;
	}

	public Vector3f() {
	}

	public Vector3f(Vector3f vector) {
		this.points[0] = vector.points[0];
		this.points[1] = vector.points[1];
		this.points[2] = vector.points[2];
	}

	public Vector3f(Vector4f vector) {
		if (vector.w() != 0) {
			this.points[0] = vector.x() / vector.w();
			this.points[1] = vector.y() / vector.w();
			this.points[2] = vector.z() / vector.w();
		} else {
			this.points[0] = vector.x();
			this.points[1] = vector.y();
			this.points[2] = vector.z();
		}
	}

	float[] toArray() {
		return this.points;
	}

	public void add(Vector3f summand) {
		this.points[0] += summand.points[0];
		this.points[1] += summand.points[1];
		this.points[2] += summand.points[2];
	}

	public void add(float summand) {
		this.points[0] += summand;
		this.points[1] += summand;
		this.points[2] += summand;
	}

	public void subtract(Vector3f subtrahend) {
		this.points[0] -= subtrahend.points[0];
		this.points[1] -= subtrahend.points[1];
		this.points[2] -= subtrahend.points[2];
	}

	public void multiplyByScalar(float scalar) {
		this.points[0] *= scalar;
		this.points[1] *= scalar;
		this.points[2] *= scalar;
	}

	public void normalize() {

		double a = Math.sqrt(points[0] * points[0] + points[1] * points[1] + points[2] * points[2]);
		this.points[0] = (float) (this.points[0] / a);
		this.points[1] = (float) (this.points[1] / a);
		this.points[2] = (float) (this.points[2] / a);

	}

	float getX() {
		return points[0];
	}

	float getY() {
		return points[1];
	}

	float getZ() {
		return points[2];
	}

	void setX(float x) {
		this.points[0] = x;
	}

	void setY(float y) {
		this.points[1] = y;
	}

	void setZ(float z) {
		this.points[2] = z;
	}

	float x() {
		return this.points[0];
	}

	float y() {
		return this.points[1];
	}

	float z() {
		return this.points[2];
	}

	public void x(float x) {
		this.points[0] = x;
	}

	public void y(float y) {
		this.points[1] = y;
	}

	public void z(float z) {
		this.points[2] = z;
	}

	public void setXYZ(float x, float y, float z) {
		this.points[0] = x;
		this.points[1] = y;
		this.points[2] = z;
	}

	public float dotProduct(Vector3f inputVec) {
		return points[0] * inputVec.points[0] + points[1] * inputVec.points[1] + points[2] * inputVec.points[2];

	}

	public void crossProduct(Vector3f inputVec, Vector3f outputVec) {
		outputVec.setX(points[1] * inputVec.points[2] - points[2] * inputVec.points[1]);
		outputVec.setY(points[2] * inputVec.points[0] - points[0] * inputVec.points[2]);
		outputVec.setZ(points[0] * inputVec.points[1] - points[1] * inputVec.points[0]);
	}

	public float getLength() {
		return (float) Math.sqrt(points[0] * points[0] + points[1] * points[1] + points[2] * points[2]);
	}

	@Override
	public String toString() {
		return "X:" + points[0] + " Y:" + points[1] + " Z:" + points[2];
	}

	public void set(Vector3f source) {
		set(source.points);
	}

	private void set(float[] source) {
		System.arraycopy(source, 0, points, 0, 3);
	}
}
