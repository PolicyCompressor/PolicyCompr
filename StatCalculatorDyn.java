import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class StatCalculatorDyn {

    static int cntAlg = 4, numberOfPoints = 10, coef = 1;
    static String prefix = "remove_0_";

    //static double[] vals = new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.55, 0.6, 0.625, 0.65, 0.675, 0.7, 0.725, 0.75, 0.775, 0.8, 0.825, 0.85, 0.875, 0.9, 0.925, 0.95, 0.975, 1};
    static int[] vals = new int[]{65, 70, 75, 80, 85, 90, 95, 100};
    static int[][] a = new int[numberOfPoints][vals.length*cntAlg+3];

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(new File("ans_"+prefix+".txt"));
        for (int i = 0; i < a.length; i++)
        {
            StringTokenizer str = new StringTokenizer(in.nextLine(), " -|");
            for (int t = 0; t < a[i].length; t++)
            {
                a[i][t] = Integer.parseInt(str.nextToken());
            }
        }
        PrintWriter out = new PrintWriter("table_"+prefix+".csv");
        for (int i = 0; i < vals.length; i++)
        {
            out.printf("%d\t%.4f",(int)(vals[i]*coef), calcR(a, 2));
            for (int j = 0; j < cntAlg; j++)
            {
                out.printf("\t%.4f", calcR(a, 3+i*cntAlg+j));
            }
            out.println();
        }
        out.close();
    }

    public static double calcVariety(ArrayList<Double> values)
    {
        double sum = 0;
        for (double d : values)
        {
            sum += d;
        }
        sum = sum / values.size();
        return sum;
    }

    private static double calcR(int[][] a, int j) {
        ArrayList<Double> values = new ArrayList<>();
        for (int i = 0; i < a.length; i++)
        {
            values.add(1.0* a[i][j] / a[i][0]);
        }
        return calcVariety(values);
    }


}
