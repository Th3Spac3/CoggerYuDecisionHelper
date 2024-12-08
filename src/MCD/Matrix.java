package MCD;

import java.lang.reflect.Array;
import java.util.Arrays;

public class Matrix {
    Double[][] values;
    Matrix(Integer height, Integer width){
        values = new Double[height][width];
        for(int i = 0; i < values.length; i++) Arrays.fill(values[i], 0d);
    }
    Matrix(Double[][] data){
        values = data;
    }

    Matrix(double[][] data){
        values = new Double[data.length][data[0].length];
        for(int i = 0; i < data.length; i++) for(int j = 0; j < data[0].length; j++) values[i][j] = data[i][j];
    }

    public static Matrix Multiply(Matrix a, Matrix b) throws Exception{
        int row1 = a.values.length, col1 = a.values[0].length, row2 = b.values.length, col2 = b.values[0].length;
        Double[][] result = new Double[row1][col2];


        for(int i = 0; i < result.length; i++) Arrays.fill(result[i], 0d);
        if(row2 == col1){
            for (int i = 0; i < row1; i++) {
                for (int j = 0; j < col2; j++) {
                    for (int k = 0; k < row2; k++)
                        result[i][j] += a.values[i][k] * b.values[k][j];
                }
            }
        } else throw new Exception("Невозможно умножить матрицу A на B");
        return new Matrix(result);
    }
    public static Matrix СoggerDiagonalMatrix(Integer size){
        Matrix result = new Matrix(size, size);
        for(int i = 0; i < size; i++) result.values[i][i] = 1d/(size-i);
        return result;
    }
    public double[][] GetPrimitive(){
        double[][] result = new double[this.values.length][this.values[0].length];
        for(int i = 0; i < result.length; i++) for (int j = 0; j < result[0].length; j++) result[i][j] = this.values[i][j];
        return result;
    }
}
