package sxccal.edu.android.remouse.sensor.representation;

import java.security.spec.InvalidParameterSpecException;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 */

public class MatrixF4x4 {

	static final int[] MAT_IND_COL9_3X3 = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	static final int[] MAT_IND_COL16_3X3 = { 0, 1, 2, 4, 5, 6, 8, 9, 10 };
	static final int[] MAT_IND_ROW9_3X3 = { 0, 3, 6, 1, 4, 7, 3, 5, 8 };
	static final int[] MAT_IND_ROW16_3X3 = { 0, 4, 8, 1, 5, 9, 2, 6, 10 };

	private static final int[] MAT_IND_COL16_4X4 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	private static final int[] MAT_IND_ROW16_4X4 = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

	private boolean mColMaj = true;

	public float[] mMatrix;

	public MatrixF4x4() {
		this.mMatrix = new float[16];
		Matrix.setIdentityM(this.mMatrix, 0);
	}

	float[] getMatrix() {
		return this.mMatrix;
	}

	int size() {
		return mMatrix.length;
	}

	void setMatrix(float[] mMatrix) {
		if (mMatrix.length == 16 || mMatrix.length == 9)
			this.mMatrix = mMatrix;
		else {
			throw new IllegalArgumentException("Matrix set is invalid, size is " + mMatrix.length + " expected 9 or 16");
		}
	}

	public void set(MatrixF4x4 source) {
		System.arraycopy(source.mMatrix, 0, mMatrix, 0, mMatrix.length);
	}

	void setColumnMajor(boolean mColMajor) {
		this.mColMaj = mColMajor;
	}

	boolean isColumnMajor() {
		return mColMaj;
	}

	public void multiplyVector4fByMatrix(Vector4f vector) throws InvalidParameterSpecException {

		if (mMatrix.length == 16) {
			float x = 0;
			float y = 0;
			float z = 0;
			float w = 0;

			float[] vectorArray = vector.array();

			if (mColMaj) {
				for (int i = 0; i < 4; i++) {

					int k = i * 4;

					x += this.mMatrix[k] * vectorArray[i];
					y += this.mMatrix[k + 1] * vectorArray[i];
					z += this.mMatrix[k + 2] * vectorArray[i];
					w += this.mMatrix[k + 3] * vectorArray[i];
				}
			} else {
				for (int i = 0; i < 4; i++) {

					x += this.mMatrix[i] * vectorArray[i];
					y += this.mMatrix[4 + i] * vectorArray[i];
					z += this.mMatrix[8 + i] * vectorArray[i];
					w += this.mMatrix[12 + i] * vectorArray[i];
				}
			}

			vector.setX(x);
			vector.setY(y);
			vector.setZ(z);
			vector.setW(w);
		}
		else{
			throw new InvalidParameterSpecException("Expects the mMatrix to be of size 16");
		}
	}

	public void multiplyVector3fByMatrix(Vector3f vector) throws InvalidParameterSpecException {

		if (mMatrix.length == 9) {
			float x = 0;
			float y = 0;
			float z = 0;

			float[] vectorArray = vector.toArray();

			if (!mColMaj) {
				for (int i = 0; i < 3; i++) {

					int k = i * 3;

					x += this.mMatrix[k] * vectorArray[i];
					y += this.mMatrix[k + 1] * vectorArray[i];
					z += this.mMatrix[k + 2] * vectorArray[i];
				}
			} else {
				for (int i = 0; i < 3; i++) {

					x += this.mMatrix[i] * vectorArray[i];
					y += this.mMatrix[3 + i] * vectorArray[i];
					z += this.mMatrix[6 + i] * vectorArray[i];
				}
			}

			vector.setX(x);
			vector.setY(y);
			vector.setZ(z);
		}
		else{
			throw new InvalidParameterSpecException("Expects the mMatrix to be of size 9");
		}
	}

	public void multiplyMatrix4x4ByMatrix(MatrixF4x4 mMatrixf) {

		float[] bufferMatrix = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] mMatrix = mMatrixf.getMatrix();
		multiplyMatrix(mMatrix, 0, bufferMatrix, 0);
		mMatrixf.setMatrix(bufferMatrix);
	}

	private void multiplyMatrix(float[] input, int inputOffset, float[] output, int outputOffset) {

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int k = i * 4;
				output[outputOffset + j] += this.mMatrix[k + j] * input[inputOffset + i];
				output[outputOffset + 4 + j] += this.mMatrix[k + j] * input[inputOffset + 4 + i];
				output[outputOffset + 2 * 4 + j] += this.mMatrix[k + j] * input[inputOffset + 2 * 4 + i];
				output[outputOffset + 3 * 4 + j] += this.mMatrix[k + j] * input[inputOffset + 3 * 4 + i];
			}
		}
	}

	public void transpose() {
		if (this.mMatrix.length == 16) {
			float[] newMatrix = new float[16];
			for (int i = 0; i < 4; i++) {
				int k = i * 4;
				newMatrix[k] = mMatrix[i];
				newMatrix[k + 1] = mMatrix[4 + i];
				newMatrix[k + 2] = mMatrix[8 + i];
				newMatrix[k + 3] = mMatrix[12 + i];
			}
			mMatrix = newMatrix;

		} else {
			float[] newMatrix = new float[9];
			for (int i = 0; i < 3; i++) {
				int k = i * 3;
				newMatrix[k] = mMatrix[i];
				newMatrix[k + 1] = mMatrix[3 + i];
				newMatrix[k + 2] = mMatrix[6 + i];
			}
			mMatrix = newMatrix;
		}

	}

	void setX0(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[0]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[0]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[0]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[0]] = value;
		}
	}

	void setX1(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[1]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[1]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[1]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[1]] = value;
		}
	}

	void setX2(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[2]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[2]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[2]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[2]] = value;
		}
	}

	void setY0(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[3]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[3]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[3]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[3]] = value;
		}
	}

	void setY1(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[4]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[4]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[4]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[4]] = value;
		}
	}

	void setY2(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[5]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[5]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[5]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[5]] = value;
		}
	}

	void setZ0(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[6]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[6]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[6]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[6]] = value;
		}
	}

	void setZ1(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[7]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[7]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[7]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[7]] = value;
		}
	}

	void setZ2(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_3X3[8]] = value;
			else
				mMatrix[MAT_IND_ROW16_3X3[8]] = value;
		} else {
			if (mColMaj)
				mMatrix[MAT_IND_COL9_3X3[8]] = value;
			else
				mMatrix[MAT_IND_ROW9_3X3[8]] = value;
		}
	}

	void setX3(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[3]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[3]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setY3(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[7]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[7]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setZ3(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[11]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[11]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setW0(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[12]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[12]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setW1(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[13]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[13]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setW2(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[14]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[14]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

	void setW3(float value) {

		if (mMatrix.length == 16) {
			if (mColMaj)
				mMatrix[MAT_IND_COL16_4X4[15]] = value;
			else
				mMatrix[MAT_IND_ROW16_4X4[15]] = value;
		}else
			throw new IllegalStateException("length of mMatrix should be 16");
	}

}

