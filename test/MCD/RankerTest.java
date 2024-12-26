package MCD;

import Jama.Matrix;
import org.junit.Test;

import java.util.Random;


public class RankerTest {
    public static double EPSILON = 0.001;

    @Test
    public void HorizontalNorm(){ //Тестирование нормализации
        Boolean result = false;
        int optimization = new Random().nextInt(0, 2);

        Double[] value = {6.3819, 1.8149, 4d, 1d};

        Double[] vector = Ranker.NormalizeVector(value, optimization);
        Double sum = 0d;
        for(int i = 0; i < vector.length; i++) sum+=vector[i];

        if(EpsilonEqual(sum, 1d, EPSILON)) {
            result = true;
        }
        else {
            result = false;
            System.out.println("Сумма весов не равна 1");
        }

        assert result;
    }

    @Test
    public void RelationTest(){ //Тест получения значения ответа по шкале
        Integer[] answers = {2, 5, 7};
        Double[] expected = {8d, 1d, 0.25};
        Boolean result = false;

        for(int i = 0; i < answers.length; i++){
            if(!EpsilonEqual(Ranker.answerToRelation(answers[i]), expected[i], EPSILON)){
                result = false;
                System.out.println(Ranker.answerToRelation(answers[i])+" != "+expected[i]);
                break;
            }
            else {
                result = true;
            }
        }
        assert result;
    }

    @Test
    public void EigenTest(){ //Тест получения вектора сумм 'a'
        double[][] mat = {{0.25, 1, 0.75, 1.75},
                {0, 0.3333, 0.1111, 1},
                {0, 0, 0.5, 2.5},
                {0, 0, 0, 1}};
        Matrix m = new Matrix(mat);
        double[] expected = {3.75, 1.4444, 3, 1};
        Boolean result = false;

        double[][] buff = Ranker.EigenValues(m);

        for(int i = 0; i < expected.length; i++){
            if(!EpsilonEqual(buff[i][0], expected[i], EPSILON)){
                result = false;
                System.out.println(buff[i][0]+" != "+expected[i]);
                break;
            } else result = true;
        }
    }

    @Test
    public void ProductionTest(){ //Тестирование произведения D^(-1)*S
        double[][] expected = {{0.25, 1, 0.75, 1.75},
                {0, 0.3333, 0.1111, 1},
                {0, 0, 0.5, 2.5},
                {0, 0, 0, 1}};

        double[][] arrayS = {
                {1, 4, 3, 7},
                {0, 1, 0.3333, 3},
                {0, 0, 1, 5},
                {0, 0, 0, 1}
        };
        Boolean result = false;

        Matrix S = new Matrix(arrayS);
        Matrix buff = new Matrix(MCD.Matrix.СoggerDiagonalMatrix(4).GetPrimitive()).times(S);

        for(int i = 0; i < buff.getColumnDimension(); i++)
            for(int j = 0; j < buff.getRowDimension(); j++){
                if(!EpsilonEqual(buff.get(i, j), expected[i][j], EPSILON)){
                    result = false;
                    System.out.println(buff.get(i, j)+" != "+expected[i][j]);
                    break;
                } else result = true;
            }
        assert result;
    }

    @Test
    public void WeightTest(){
        double[][] production = {{0.25, 1, 0.75, 1.75},
                {0, 0.3333, 0.1111, 1},
                {0, 0, 0.5, 2.5},
                {0, 0, 0, 1}};
        double[][] sum = {{3.75}, {1.4444}, {3}, {1}};
        double[] expected = {6.3819, 1.8149, 4d, 1d};

        Boolean result = false;

        Matrix buff = new Matrix(production).times(new Matrix(sum));

        for(int i = 0; i < expected.length; i++){
            if(!EpsilonEqual(buff.get(i, 0), expected[i], EPSILON)){
                System.out.println(buff.get(i, 0)+" != "+expected[i]);
                break;
            } else result = true;
        }
        assert result;
    }

    private Boolean EpsilonEqual(double a, double b, double epsilon){ //Равенство в окрестности Epsilon
        return a > b - epsilon && a < b + epsilon;
    }
}
