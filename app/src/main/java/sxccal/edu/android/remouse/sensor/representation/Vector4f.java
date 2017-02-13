package sxccal.edu.android.remouse.sensor.representation;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public class Vector4f {

	float points[] = { 0, 0, 0, 0 };

	public Vector4f(float x, float y, float z, float w) {
		this.points[0] = x;
		this.points[1] = y;
		this.points[2] = z;
		this.points[3] = w;
	}

	Vector4f() {
		this.points[0] = 0;
		this.points[1] = 0;
		this.points[2] = 0;
		this.points[3] = 0;
	}

	public Vector4f(Vector3f vector3f, float w) {
		this.points[0] = vector3f.x();
		this.points[1] = vector3f.y();
		this.points[2] = vector3f.z();
		this.points[3] = w;
	}

	public float[] array() {
		return points;
	}

	void copyVec4(Vector4f vec) {
		this.points[0] = vec.points[0];
		this.points[1] = vec.points[1];
		this.points[2] = vec.points[2];
		this.points[3] = vec.points[3];
	}

	public void add(Vector4f vector) {
		this.points[0] += vector.points[0];
		this.points[1] += vector.points[1];
		this.points[2] += vector.points[2];
		this.points[3] += vector.points[3];
	}

	public void add(Vector3f vector, float w) {
		this.points[0] += vector.x();
		this.points[1] += vector.y();
		this.points[2] += vector.z();
		this.points[3] += w;
	}

	public void subtract(Vector4f vector) {
		this.points[0] -= vector.points[0];
		this.points[1] -= vector.points[1];
		this.points[2] -= vector.points[2];
		this.points[3] -= vector.points[3];
	}

	public void subtract(Vector4f vector, Vector4f output) {
		output.setXYZW(this.points[0] - vector.points[0], this.points[1] - vector.points[1], this.points[2]
				- vector.points[2], this.points[3] - vector.points[3]);
	}

	public void subdivide(Vector4f vector) {
		this.points[0] /= vector.points[0];
		this.points[1] /= vector.points[1];
		this.points[2] /= vector.points[2];
		this.points[3] /= vector.points[3];
	}

	public void multiplyByScalar(float scalar) {
		this.points[0] *= scalar;
		this.points[1] *= scalar;
		this.points[2] *= scalar;
		this.points[3] *= scalar;
	}

	float dotProduct(Vector4f input) {
		return this.points[0] * input.points[0] + this.points[1] * input.points[1] + this.points[2] * input.points[2]
				+ this.points[3] * input.points[3];
	}

	public void lerp(Vector4f input, Vector4f output, float t) {
		output.points[0] = (points[0] * (1.0f * t) + input.points[0] * t);
		output.points[1] = (points[1] * (1.0f * t) + input.points[1] * t);
		output.points[2] = (points[2] * (1.0f * t) + input.points[2] * t);
		output.points[3] = (points[3] * (1.0f * t) + input.points[3] * t);

	}

	public void normalize() {
		if (points[3] == 0)
			return;

		points[0] /= points[3];
		points[1] /= points[3];
		points[2] /= points[3];

		double a = Math.sqrt(this.points[0] * this.points[0] + this.points[1] * this.points[1] + this.points[2]
				* this.points[2]);
		points[0] = (float) (this.points[0] / a);
		points[1] = (float) (this.points[1] / a);
		points[2] = (float) (this.points[2] / a);
	}

	public float getX() {
		return this.points[0];
	}

	public float getY() {
		return this.points[1];
	}

	public float getZ() {
		return this.points[2];
	}

	public float getW() {
		return this.points[3];
	}

	public void setX(float x) {
		this.points[0] = x;
	}

	public void setY(float y) {
		this.points[1] = y;
	}

	public void setZ(float z) {
		this.points[2] = z;
	}

	public void setW(float w) {
		this.points[3] = w;
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

	public float w() {
		return this.points[3];
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

	public void w(float w) {
		this.points[3] = w;
	}

	private void setXYZW(float x, float y, float z, float w) {
		this.points[0] = x;
		this.points[1] = y;
		this.points[2] = z;
		this.points[3] = w;
	}

	public boolean compareTo(Vector4f rhs) {
		boolean ret = false;
		if (this.points[0] == rhs.points[0] && this.points[1] == rhs.points[1] && this.points[2] == rhs.points[2]
				&& this.points[3] == rhs.points[3])
			ret = true;
		return ret;
	}

	void copyFromV3f(Vector3f input, float w) {
		points[0] = (input.x());
		points[1] = (input.y());
		points[2] = (input.z());
		points[3] = (w);
	}

	@Override
	public String toString() {
		return "X:" + points[0] + " Y:" + points[1] + " Z:" + points[2] + " W:" + points[3];
	}

}
