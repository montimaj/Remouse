package project.android.sensor.representation;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public class Vector4f {

	float mPoints[] = { 0, 0, 0, 0 };

	public Vector4f(float x, float y, float z, float w) {
		this.mPoints[0] = x;
		this.mPoints[1] = y;
		this.mPoints[2] = z;
		this.mPoints[3] = w;
	}

	Vector4f() {
		this.mPoints[0] = 0;
		this.mPoints[1] = 0;
		this.mPoints[2] = 0;
		this.mPoints[3] = 0;
	}

	public Vector4f(Vector3f vector3f, float w) {
		this.mPoints[0] = vector3f.x();
		this.mPoints[1] = vector3f.y();
		this.mPoints[2] = vector3f.z();
		this.mPoints[3] = w;
	}

	public float[] array() {
		return mPoints;
	}

	void copyVec4(Vector4f vec) {
		this.mPoints[0] = vec.mPoints[0];
		this.mPoints[1] = vec.mPoints[1];
		this.mPoints[2] = vec.mPoints[2];
		this.mPoints[3] = vec.mPoints[3];
	}

	public void add(Vector4f vector) {
		this.mPoints[0] += vector.mPoints[0];
		this.mPoints[1] += vector.mPoints[1];
		this.mPoints[2] += vector.mPoints[2];
		this.mPoints[3] += vector.mPoints[3];
	}

	public void add(Vector3f vector, float w) {
		this.mPoints[0] += vector.x();
		this.mPoints[1] += vector.y();
		this.mPoints[2] += vector.z();
		this.mPoints[3] += w;
	}

	public void subtract(Vector4f vector) {
		this.mPoints[0] -= vector.mPoints[0];
		this.mPoints[1] -= vector.mPoints[1];
		this.mPoints[2] -= vector.mPoints[2];
		this.mPoints[3] -= vector.mPoints[3];
	}

	public void subtract(Vector4f vector, Vector4f output) {
		output.setXYZW(this.mPoints[0] - vector.mPoints[0], this.mPoints[1] - vector.mPoints[1], this.mPoints[2]
				- vector.mPoints[2], this.mPoints[3] - vector.mPoints[3]);
	}

	public void subdivide(Vector4f vector) {
		this.mPoints[0] /= vector.mPoints[0];
		this.mPoints[1] /= vector.mPoints[1];
		this.mPoints[2] /= vector.mPoints[2];
		this.mPoints[3] /= vector.mPoints[3];
	}

	public void multiplyByScalar(float scalar) {
		this.mPoints[0] *= scalar;
		this.mPoints[1] *= scalar;
		this.mPoints[2] *= scalar;
		this.mPoints[3] *= scalar;
	}

	float dotProduct(Vector4f input) {
		return this.mPoints[0] * input.mPoints[0] + this.mPoints[1] * input.mPoints[1] + this.mPoints[2] * input.mPoints[2]
				+ this.mPoints[3] * input.mPoints[3];
	}

	public void lerp(Vector4f input, Vector4f output, float t) {
		output.mPoints[0] = (mPoints[0] * (1.0f * t) + input.mPoints[0] * t);
		output.mPoints[1] = (mPoints[1] * (1.0f * t) + input.mPoints[1] * t);
		output.mPoints[2] = (mPoints[2] * (1.0f * t) + input.mPoints[2] * t);
		output.mPoints[3] = (mPoints[3] * (1.0f * t) + input.mPoints[3] * t);

	}

	public void normalize() {
		if (mPoints[3] == 0)
			return;

		mPoints[0] /= mPoints[3];
		mPoints[1] /= mPoints[3];
		mPoints[2] /= mPoints[3];

		double a = Math.sqrt(this.mPoints[0] * this.mPoints[0] + this.mPoints[1] * this.mPoints[1] + this.mPoints[2]
				* this.mPoints[2]);
		mPoints[0] = (float) (this.mPoints[0] / a);
		mPoints[1] = (float) (this.mPoints[1] / a);
		mPoints[2] = (float) (this.mPoints[2] / a);
	}

	public float getX() {
		return this.mPoints[0];
	}

	public float getY() {
		return this.mPoints[1];
	}

	public float getZ() {
		return this.mPoints[2];
	}

	public float getW() {
		return this.mPoints[3];
	}

	public void setX(float x) {
		this.mPoints[0] = x;
	}

	public void setY(float y) {
		this.mPoints[1] = y;
	}

	public void setZ(float z) {
		this.mPoints[2] = z;
	}

	public void setW(float w) {
		this.mPoints[3] = w;
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

	public float w() {
		return this.mPoints[3];
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

	public void w(float w) {
		this.mPoints[3] = w;
	}

	private void setXYZW(float x, float y, float z, float w) {
		this.mPoints[0] = x;
		this.mPoints[1] = y;
		this.mPoints[2] = z;
		this.mPoints[3] = w;
	}

	public boolean compareTo(Vector4f rhs) {
		boolean ret = false;
		if (this.mPoints[0] == rhs.mPoints[0] && this.mPoints[1] == rhs.mPoints[1] && this.mPoints[2] == rhs.mPoints[2]
				&& this.mPoints[3] == rhs.mPoints[3])
			ret = true;
		return ret;
	}

	void copyFromV3f(Vector3f input, float w) {
		mPoints[0] = (input.x());
		mPoints[1] = (input.y());
		mPoints[2] = (input.z());
		mPoints[3] = (w);
	}

	@Override
	public String toString() {
		return "X:" + mPoints[0] + " Y:" + mPoints[1] + " Z:" + mPoints[2] + " W:" + mPoints[3];
	}
}