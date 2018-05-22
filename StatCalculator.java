import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class StatCalculator {

    static String[] names = new String[]{"Upper bound + LD", "SCS", "Majority Merge", "All or Nothing", "All or Nothing + GreedyGluing", "All or Nothing + Clique Sharing", "All or Nothing + Clique Sharing + Greedy Gluing", "Greedy Gluing", "Clique Sharing", "Clique Sharing + Greedy Gluing"};
    static int[][] a = new int[100][names.length*2+2];

    public static void main(String[] args) throws FileNotFoundException {
        Scanner in = new Scanner(System.in);
        for (int i = 0; i < a.length; i++)
        {
            StringTokenizer str = new StringTokenizer(in.nextLine(), " -|");
            for (int t = 0; t < a[i].length; t++)
            {
                a[i][t] = Integer.parseInt(str.nextToken());
            }
        }
        PrintWriter out = new PrintWriter(System.out);
        for (int i = 0; i<names.length; i++)
        {
            print(a, i, out);
        }
        out.close();

    }

    public static void printVariety(ArrayList<Double> values, PrintWriter f)
    {
        double sum = 0;
        for (double d : values)
        {
            sum += d;
        }
        sum = sum / values.size();
        f.println(sum);
    }

    private static void print(int[][] a, int alg1, PrintWriter out) {
        int i1 = 2+names.length + alg1;
        ArrayList<Double> values = new ArrayList<>();
        for (int i = 0; i < a.length; i++)
        {
            values.add(1.0* a[i][i1] / a[i][0]);
        }
        out.print(names[alg1]+" : ");
        printVariety(values, out);
    }


}
