package project.android.sensor.representation;

import java.security.spec.InvalidParameterSpecException;

/**
 * @author Abhisek Maiti
 * @author Sayantan Majumdar
 *
 * The Class MatrixF4x4.
 *
 * Internal the matrix is structured as
 *
 * [ x0 , y0 , z0 , w0 ] [ x1 , y1 , z1 , w1 ] [ x2 , y2 , z2 , w2 ] [ x3 , y3 , z3 , w3 ]
 *
 * it is recommend to use the set{x,#} methods when setting the matrix values individually that you , where 'x' is
 * either x, y, z or w and # is either 0, 1, 2 or 3, <code>setY1</code> for example. These functions will map directly to the
 * specified part of the matrix regardless of whether or not the internal matrix is column major or not. If the matrix
 * is either or length 9 or 16 it will be able to determine if it can set the value or not.
 * Out of bound values will not be set and the set method will return without any error.
 *
 */

public class MatrixF4x4 {

	static final int[] MAT_IND_COL9_3X3 = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
	static final int[] MAT_IND_COL16_3X3 = { 0, 1, 2, 4, 5, 6, 8, 9, 10 };
	static final int[] MAT_IND_ROW9_3X3 = { 0, 3, 6, 1, 4, 7, 3, 5, 8 };
	static final int[] MAT_IND_ROW16_3X3 = { 0, 4, 8, 1, 5, 9, 2, 6, 10 };

	private static final int[] MAT_IND_COL16_4X4 = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	private static final int[] MAT_IND_ROW16_4X4 = { 0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15 };

	private boolean mColMaj = true;

	public float[] matrix;

	/**
	 * Instantiates a new matrixf4x4. The Matrix is assumed to be Column major, however you can change this by using the
	 * setColumnMajor function to false and it will operate like a row major matrix.
	 */
	public MatrixF4x4() {
		this.matrix = new float[16];
		Matrix.setIdentityM(this.matrix, 0);
	}

	/**
	 * Gets the matrix.
	 *
	 * @return the matrix, can be null if the matrix is invalid
	 */
	float[] getMatrix() {
		return this.matrix;
	}

	/**
	 * Gets size of the Matrix.
	 *
	 * @return the length of the matrix
	 */
	int size() {
		return matrix.length;
	}

	void setMatrix(float[] matrix) {
		if (matrix.length == 16 || matrix.length == 9) {
			this.matrix = matrix;
		} else {
			throw new IllegalArgumentException("Matrix set is invalid, size is " + matrix.length + " expected 9 or 16");
		}
	}

	/**
	 * Sets the matrix from a float[16] array. If the matrix isn't 16 long then the matrix will be invalid.
	 *
	 * @param source the source matrixF4x4
	 */
	public void set(MatrixF4x4 source) {
		System.arraycopy(source.matrix, 0, matrix, 0, matrix.length);
	}

	/**
	 * Set whether the internal data is col major by passing true, or false for a row major matrix. The matrix is column
	 * major by default.
	 *
	 * @param mColMajor
	 */
	void setColumnMajor(boolean mColMajor) {
		this.mColMaj = mColMajor;
	}

	/**
	 * Find out if the stored matrix is column major
	 *
	 * @return
	 */
	boolean isColumnMajor() {
		return mColMaj;
	}

	/**
	 * Multiply the given vector by this matrix. This should only be used if the matrix is of size 16 (use the
	 * <code>matrix.size()</code> method).
	 *
	 * @param vector A vector of length 4.
	 */
	public void multiplyVector4fByMatrix(Vector4f vector) throws InvalidParameterSpecException {

		if (matrix.length == 16) {
			float x = 0;
			float y = 0;
			float z = 0;
			float w = 0;

			float[] vectorArray = vector.array();

			if (mColMaj) {
				for (int i = 0; i < 4; i++) {

					int k = i * 4;

					x += this.matrix[k] * vectorArray[i];
					y += this.matrix[k + 1] * vectorArray[i];
					z += this.matrix[k + 2] * vectorArray[i];
					w += this.matrix[k + 3] * vectorArray[i];
				}
			} else {
				for (int i = 0; i < 4; i++) {

					x += this.matrix[i] * vectorArray[i];
					y += this.matrix[4 + i] * vectorArray[i];
					z += this.matrix[8 + i] * vectorArray[i];
					w += this.matrix[12 + i] * vectorArray[i];
				}
			}

			vector.setX(x);
			vector.setY(y);
			vector.setZ(z);
			vector.setW(w);
		} else {
			throw new InvalidParameterSpecException("Expects the matrix to be of size 16");
		}
	}

	/**
	 * Multiply the given vector by this matrix. This should only be used if the matrix is of size 9 (use the
	 * <code>matrix.size()</code> method).
	 *
	 * @param vector A vector of length 3.
	 */
	public void multiplyVector3fByMatrix(Vector3f vector) throws InvalidParameterSpecException {

		if (matrix.length == 9) {
			float x = 0;
			float y = 0;
			float z = 0;

			float[] vectorArray = vector.toArray();

			if (!mColMaj) {
				for (int i = 0; i < 3; i++) {

					int k = i * 3;

					x += this.matrix[k] * vectorArray[i];
					y += this.matrix[k + 1] * vectorArray[i];
					z += this.matrix[k + 2] * vectorArray[i];
				}
			} else {
				for (int i = 0; i < 3; i++) {

					x += this.matrix[i] * vectorArray[i];
					y += this.matrix[3 + i] * vectorArray[i];
					z += this.matrix[6 + i] * vectorArray[i];
				}
			}

			vector.setX(x);
			vector.setY(y);
			vector.setZ(z);
		} else {
			throw new InvalidParameterSpecException("Expects the matrix to be of size 9");
		}
	}

	/**
	 * Multiply matrix4x4 by matrix.
	 *
	 * @param matrixf the matrixf
	 */
	public void multiplyMatrix4x4ByMatrix(MatrixF4x4 matrixf) {

		float[] bufferMatrix = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
		float[] matrix = matrixf.getMatrix();
		multiplyMatrix(matrix, 0, bufferMatrix, 0);
		matrixf.setMatrix(bufferMatrix);
	}

	private void multiplyMatrix(float[] input, int inputOffset, float[] output, int outputOffset) {

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				int k = i * 4;
				output[outputOffset + j] += this.matrix[k + j] * input[inputOffset + i];
				output[outputOffset + 4 + j] += this.matrix[k + j] * input[inputOffset + 4 + i];
				output[outputOffset + 2 * 4 + j] += this.matrix[k + j] * input[inputOffset + 2 * 4 + i];
				output[outputOffset + 3 * 4 + j] += this.matrix[k + j] * input[inputOffset + 3 * 4 + i];
			}
		}
	}

	/**
	 * This will rearrange the internal structure of the matrix. Note: this is an expensive operation.
	 */
	public void transpose() {
		if (this.matrix.length == 16) {
			float[] newMatrix = new float[16];
			for (int i = 0; i < 4; i++) {
				int k = i * 4;
				newMatrix[k] = matrix[i];
				newMatrix[k + 1] = matrix[4 + i];
				newMatrix[k + 2] = matrix[8 + i];
				newMatrix[k + 3] = matrix[12 + i];
			}
			matrix = newMatrix;

		} else {
			float[] newMatrix = new float[9];
			for (int i = 0; i < 3; i++) {
				int k = i * 3;
				newMatrix[k] = matrix[i];
				newMatrix[k + 1] = matrix[3 + i];
				newMatrix[k + 2] = matrix[6 + i];
			}
			matrix = newMatrix;
		}

	}

	/**
	 * Sets the value of X0
	 * @param value value to set
	 */
	void setX0(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[0]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[0]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[0]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[0]] = value;
			}
		}
	}

	/**
	 * Sets the value of X1
	 * @param value value to set
	 */
	void setX1(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[1]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[1]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[1]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[1]] = value;
			}
		}
	}

	/**
	 * Sets the value of X2
	 * @param value value to set
	 */
	void setX2(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[2]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[2]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[2]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[2]] = value;
			}
		}
	}

	/**
	 * Sets the value of Y0
	 * @param value value to set
	 */
	void setY0(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[3]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[3]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[3]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[3]] = value;
			}
		}
	}

	/**
	 * Sets the value of Y1
	 * @param value  value to set
	 */
	void setY1(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[4]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[4]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[4]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[4]] = value;
			}
		}
	}

	/**
	 * Sets the value of Y2
	 * @param value  value to set
	 */
	void setY2(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[5]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[5]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[5]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[5]] = value;
			}
		}
	}

	/**
	 * Sets the value of Z0
	 * @param value value to set
	 */
	void setZ0(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[6]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[6]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[6]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[6]] = value;
			}
		}
	}

	/**
	 * Sets the value of Z1
	 * @param value value to set
	 */
	void setZ1(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[7]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[7]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[7]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[7]] = value;
			}
		}
	}

	/**
	 * Sets the value of Z2
	 * @param value value to set
	 */
	void setZ2(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_3X3[8]] = value;
			} else {
				matrix[MAT_IND_ROW16_3X3[8]] = value;
			}
		} else {
			if (mColMaj) {
				matrix[MAT_IND_COL9_3X3[8]] = value;
			} else {
				matrix[MAT_IND_ROW9_3X3[8]] = value;
			}
		}
	}

	/**
	 * Sets the value of X3
	 * @param value value to set
	 */
	void setX3(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[3]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[3]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of Y3
	 * @param value  value to set
	 */
	void setY3(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[7]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[7]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of Z3
	 * @param value value to set
	 */
	void setZ3(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[11]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[11]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of W0
	 * @param value value to set
	 */
	void setW0(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[12]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[12]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of W1
	 * @param value value to set
	 */
	void setW1(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[13]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[13]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of W2
	 * @param value value to set
	 */
	void setW2(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[14]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[14]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}

	/**
	 * Sets the value of W3
	 * @param value value to set
	 */
	void setW3(float value) {

		if (matrix.length == 16) {
			if (mColMaj) {
				matrix[MAT_IND_COL16_4X4[15]] = value;
			} else {
				matrix[MAT_IND_ROW16_4X4[15]] = value;
			}
		} else {
			throw new IllegalStateException("length of matrix should be 16");
		}
	}
}