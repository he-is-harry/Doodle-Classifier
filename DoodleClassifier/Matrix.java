package DoodleClassifier;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Matrix {
	int rows;
	int cols;
	double [][] data;
	
	public Matrix(int rows, int cols) {
		this.rows = rows;
		this.cols = cols;
		data = new double[rows][cols];
	}
	
	public Matrix(double [][] matrix) {
		rows = matrix.length;
		cols = matrix[0].length;
		this.data = new double[rows][cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				this.data[i][j] = matrix[i][j];
			}
		}
	}
	
	void multiply(double n) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] *= n;
			}
		}
	} 
	
	void multiply(Matrix m) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] *= m.data[i][j];
			}
		}
	}
	
	static Matrix multiply(Matrix m1, Matrix m2) throws Exception {
		if(m1.cols != m2.rows) {
			System.out.println("Error: Cols of m1 must match rows of m2");
			throw new Exception("Matrix Mutiplication Error");
		}
		Matrix result = new Matrix(m1.rows, m2.cols);
		for(int i = 0; i < result.rows; i++) {
			for(int j = 0; j < result.cols; j++) {
				// Dot product of values in col
				double sum = 0;
				for(int k = 0; k < m1.cols; k++) {
					sum += m1.data[i][k] * m2.data[k][j];
				}
				result.data[i][j] = sum;
			}
		}
		return result;
	}
	
	static Matrix fromArray(double [] arr) {
		Matrix m = new Matrix(arr.length, 1);
		for(int i = 0; i < arr.length; i++) {
			m.data[i][0] = arr[i];
		}
		return m;
	}
	
	double[] toArray() {
		double [] arr = new double[rows * cols];
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				arr[i * cols + j] = data[i][j];
			}
		}
		return arr;
	}
	
	static Matrix transpose(Matrix m) {
		Matrix result = new Matrix(m.cols, m.rows);
		for(int i = 0; i < m.rows; i++) {
			for(int j = 0; j < m.cols; j++) {
				result.data[j][i] = m.data[i][j];
			}
		}
		return result;
	}
	
	void randomize() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] = Math.random() * 2 - 1;
			}
		}
	}
	
	void add(double n) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] += n;
			}
		}
	}
	
	void add(Matrix m) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] += m.data[i][j];
			}
		}
	}
	
	static Matrix subtract(Matrix a, Matrix b) {
		Matrix result = new Matrix(a.rows, a.cols);
		for(int i = 0; i < result.rows; i++) {
			for(int j = 0; j < result.cols; j++) {
				result.data[i][j] = a.data[i][j] - b.data[i][j];
			}
		}
		return result;
	}
	
	void increase(int row, int col, double x) {
		data[row][col] += x;
	}
	
	void map(Function e) {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				data[i][j] = e.f(data[i][j], i, j);
			}
		}
	}
	
	static Matrix map(Matrix m, Function e) {
		Matrix result = new Matrix(m.rows, m.cols);
		for(int i = 0; i < m.rows; i++) {
			for(int j = 0; j < m.cols; j++) {
				result.data[i][j] = e.f(m.data[i][j], i, j);
			}
		}
		return result;
	}
	
	void print() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				System.out.print(data[i][j] + " ");
			}
			System.out.println();
		}
		System.out.println();
	}
	void serialize(PrintWriter pw) {
		pw.println(rows + " " + cols);
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) {
				if(j == cols - 1) {
					pw.println(data[i][j]);
				} else {
					pw.print(data[i][j] + " ");
				}
			}
		}
	}
	void deserialize(BufferedReader br, StringTokenizer st) throws IOException {
		st = new StringTokenizer(br.readLine().trim());
		rows = Integer.parseInt(st.nextToken());
		cols = Integer.parseInt(st.nextToken());
		data = new double[rows][cols];
		for(int i = 0; i < rows; i++) {
			st = new StringTokenizer(br.readLine().trim());
			for(int j = 0; j < cols; j++) {
				data[i][j] = Double.parseDouble(st.nextToken());
			}
		}
	}
}
