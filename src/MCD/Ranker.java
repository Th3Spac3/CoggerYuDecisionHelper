package MCD;

import Jama.Matrix;

import java.util.Arrays;
import java.util.Scanner;

public class Ranker {
    public static Double P = 2d;

    public static Boolean AdditionalInfo = false;

    public static ChoiceMethod method = ChoiceMethod.CoggerYu;

    public static Double answerToRelation(Integer answer){
        return Math.pow(P, -answer+5);
    }

    private static void printMatrix(Double[][] matrix, String message){
        System.out.println(message);
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++) System.out.print(matrix[i][j]+" ");
            System.out.println();
        }
    }
    private static void printMatrix(double[][] matrix, String message){
        System.out.println(message);
        for(int i = 0; i < matrix.length; i++){
            for(int j = 0; j < matrix[0].length; j++) System.out.print(matrix[i][j]+" ");
            System.out.println();
        }
    }
    private static void AskIntro(){
        System.out.println("Необходимо ответить нанесколько вопросов, в качестве ответов принимаются числа от 1 до 9, где");
        System.out.println("|1 - абсолютно важнее |2 - очень сильно важнее |3 - сильно важнее |4 - слабо важнее |5 - равноважен |6 - слабо уступает |7 - сильно уступает 8 - очень сильно уступает |9 - абсолютно уступает|");
    }
    private static void SimpleRankObjects(Double[][] data, String[] head, Scanner in, Integer[]attributeAnswers, Integer[] optimization){

        Double[] attributeWeights = new Double[head.length];
        for(int i = 0; i < head.length; i++){
            attributeWeights[i] = i == 0 ? 1d : attributeWeights[i-1]/answerToRelation(attributeAnswers[i-1]);
        }
        attributeWeights = NormalizeVector(attributeWeights);
        if (AdditionalInfo) {
            System.out.println("Нормализованные веса признаков");
            for(Double i : attributeWeights) System.out.printf("%f ",i);
            System.out.println();
        }
        Double[][] normData = new Double[head.length][data.length];

        for(int i = 0; i < head.length; i++) normData[i] = NormalizeVector(GetColumn(data, i), optimization[i]);

        if(AdditionalInfo) printMatrix(normData, "Нормализованные значения признаков");

        Double[] rating = new Double[data.length];
        Arrays.fill(rating, 0d);
        for(int i = 0; i < data.length; i++){
            for(int j = 0; j < head.length; j++) rating[i] += normData[j][i]*attributeWeights[j];
        }
        for(int i = 0; i < rating.length; i++) System.out.printf("g%d score: %f\n",i+1,rating[i]);
    }

    private static void CoggerYuRankObjects(Double[][] data, String[] head, Scanner in, Integer[] attributeAnswers, Integer[] optimization){
        double[][] pairComparison = new double[head.length][head.length];
        for(int i = 0; i < head.length; i++){
            for(int j = i; j >= 0; j--){
                if(j == i) pairComparison[j][i] = 1d;
                else {
                    if(j == i - 1) pairComparison[j][i] = answerToRelation(attributeAnswers[i-1]);
                    else pairComparison[j][i] = pairComparison[j][i-1]*pairComparison[j+1][i];
                }
            }
        }

        Matrix mulmat = new Matrix(MCD.Matrix.СoggerDiagonalMatrix(head.length).GetPrimitive()).times(new Matrix(pairComparison));
        Matrix attributeWeights = new Matrix(VerticalNormalize(mulmat.times(new Matrix(EigenValues(mulmat))).getArray()));
        if(AdditionalInfo) printMatrix(pairComparison, "Матрица парных сравнений");
        if(AdditionalInfo) printMatrix(attributeWeights.getArray(), "Веса признаков");

        double[][][] objectsPairComparison = new double[head.length][data.length][data.length];
        Matrix[] objectsMulMat = new Matrix[head.length];
        Matrix[] objectWeights = new Matrix[head.length];
        for(int k = 0; k < head.length; k++){
            /*for(int i = 0; i < data.length; i++){
                for(int j = i; j >= 0; j--){
                    if(j == i) objectsPairComparison[k][j][i] = 1d;
                    else {
                        if(j == i - 1) objectsPairComparison[k][j][i] = data[i-1][k]/data[i][k];
                        else objectsPairComparison[k][j][i] = objectsPairComparison[k][j][i-1]*objectsPairComparison[k][j+1][i];
                    }
                }
            }*/
            for(int i = 0; i < data.length; i++)
                for(int j = i; j < data.length; j++)
                    objectsPairComparison[k][i][j] = data[i][k]/data[j][k];

            objectsMulMat[k] = new Matrix(objectsPairComparison[k]);
            if(AdditionalInfo){
                System.out.println("Матрица парных сравнений объектов по "+head[k]);
                objectsMulMat[k].print(5, 5);
            }
            objectsMulMat[k] = new Matrix(MCD.Matrix.СoggerDiagonalMatrix(data.length).GetPrimitive()).times(objectsMulMat[k]);
            objectWeights[k] = new Matrix(VerticalNormalize(objectsMulMat[k].times(new Matrix(EigenValues(objectsMulMat[k]))).getArray()));
            objectWeights[k] = new Matrix(VerticalNormalize(objectWeights[k].getArray(), optimization[k]));
        }

        double[][] normalizedObjects = new double[head.length][data.length];
        for (int i = 0; i < head.length; i++){
            var buff1 = objectWeights[i].getArray();
            double[] buff2 = new double[data.length];
            for (int j = 0; j < buff1.length; j++)
                for (int k = 0; k < buff1[0].length; k++) buff2[j] = buff1[j][k];
            normalizedObjects[i] = buff2;
        }

        var result = new Matrix(normalizedObjects).transpose().times(attributeWeights).getColumnPackedCopy();
        for(int i = 0; i < result.length; i++) System.out.printf("g%d score: %f\n", i+1, result[i]);
    }

    public static double[][] EigenValues(Matrix matrix){
        double[][] buff = matrix.getArray();
        double[][] result = new double[buff.length][1];
        for(int i = 0; i < buff.length; i++){
            result[i][0] = 0d;
            for(int j = 0; j < buff.length; j++){
                result[i][0]+=matrix.get(i, j);
            }
        }
        return result;
    }
    private static Double[] NormalizeVector(Double[] vector){
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+= vector[i];
        for(int i = 0; i < vector.length; i++) vector[i] /= sum;
        return vector;
    }
    public static Double[] NormalizeVector(Double[] vector, Integer optimization){
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+= vector[i];
        for(int i = 0; i < vector.length; i++) vector[i] = optimization*(1 - (vector[i]/sum) - (vector.length-2d)/vector.length)+(1-optimization)*(vector[i]/sum);
        return vector;
    }
    private static double[] NormalizeVector(double[] vector, Integer optimization){
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+= vector[i];
        for(int i = 0; i < vector.length; i++) vector[i] = optimization*(1 - (vector[i]/sum) - (vector.length-2d)/vector.length)+(1-optimization)*(vector[i]/sum);
        return vector;
    }
    private static double[][] VerticalNormalize(double[][] vector){
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+= vector[i][0];
        for(int i = 0; i < vector.length; i++) vector[i][0] /= sum;
        return vector;
    }
    private static double[][] VerticalNormalize(double[][] vector, Integer optimization){
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+= vector[i][0];
        for(int i = 0; i < vector.length; i++) vector[i][0] = optimization*(1 - (vector[i][0]/sum) - (vector.length-2d)/vector.length)+(1-optimization)*(vector[i][0]/sum);
        return vector;
    }
    private static Double[] GetColumn(Double[][] matrix, Integer iterator){
        Double[] result = new Double[matrix.length];
        for(int i = 0; i < result.length; i++) result[i] = matrix[i][iterator];
        return result;
    }
    public static void Survey(Double[][] data, String[] head, Scanner in){
        Integer[] attributeAnswers = new Integer[head.length-1];
        AskIntro();
        for(int i = 0; i < head.length-1; i++){
            System.out.println("Для вас "+head[i]+" важнее, чем "+head[i+1]+"? Насколько?");
            attributeAnswers[i] = in.nextInt();
        }
        in.nextLine();
        Integer[] optimization = new Integer[head.length];
        for(int i = 0; i < head.length; i++){
            System.out.println("Для вас предпочтительнее увеличение "+head[i]+" (0) или уменьшение (1)?");
            Integer b = in.nextInt();
            if(b <= 1 && b >= 0){
                optimization[i] = b;
            } else {
                i--;
                System.out.println("Неверный аргумент. Необходимо выбрать увеличение (0), либо уменьшение (1)");
            }
        }
        switch (method){
            case Simple:
                SimpleRankObjects(data, head, in, attributeAnswers, optimization);
                break;
            case CoggerYu:
                CoggerYuRankObjects(data, head, in, attributeAnswers, optimization);
        }
    }
}
