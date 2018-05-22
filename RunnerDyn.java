import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RunnerDyn {

    public static ArrayList<ArrayList<Class>> generatePolicies(Data data, int num) {
        ArrayList<ArrayList<Class>> policies = new ArrayList<>();
        for (ArrayList<Class> p : data.orders) {
            ArrayList<Class> classes = new ArrayList<>(p);
            Collections.shuffle(classes);
            classes = new ArrayList<>(classes.subList(0, num));
            policies.add(classes);
        }
        return policies;
    }

    public static void runInsertTest(PrintWriter f) {
        Data data = new Data(new Parameters());
        long[] bounds = new BoundsCalculator().getBounds(data);
        long minClGluing = new CliqueSharingSolver().solveWithGreedyGluing(data)[1] - bounds[0];
        f.print(bounds[0] + "--" + bounds[1] + " | " + minClGluing);

        for (double p : new double[]{0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.55, 0.6, 0.625, 0.65, 0.675, 0.7, 0.725, 0.75, 0.775, 0.8, 0.825, 0.85, 0.875, 0.9, 0.925, 0.95, 0.975, 1}) {
            ArrayList<ArrayList<Class>> policiesProfile = generatePolicies(data, (int) (p * Parameters.numberOfClassesInEachPolicy));
            data.set(policiesProfile);
            ArrayList<Class> result = new CliqueSharingSolver().createMinimalSequence(data);
            ArrayList<Pair<Class, Integer>> toOperate = DynamicSolver.createOrderForInsert(data);
            long[] ans = DynamicSolver.perform(data, result, policiesProfile, toOperate);
            f.print(" | " + (ans[0] - bounds[0]) + " " + (ans[1] - bounds[0]) +" "+(ans[2] - bounds[0]));
        }
        f.println();
        f.flush();
    }


    public static void runRemoveTest(PrintWriter f) {
        Data data = new Data(new Parameters());
        ArrayList<ArrayList<Class>> inFinalPolicies = generatePolicies(data, 60);
        data.set(inFinalPolicies);
        long[] bounds = new BoundsCalculator().getBounds(data);
        long minClGluing = new CliqueSharingSolver().solveWithGreedyGluing(data)[1] - bounds[0];
        f.print(bounds[0] + "--" + bounds[1] + " | " + minClGluing);
        for (int cl = 60; cl <= 100; cl += 5) {
            ArrayList<ArrayList<Class>> list = generatePolicies(data, cl, inFinalPolicies);
            data.set(list);
            ArrayList<Class> result = new CliqueSharingSolver().createMinimalSequence(data);
            ArrayList<Pair<Class, Integer>> toOperate = DynamicSolver.createOrderForRemove(data, inFinalPolicies);
            long[] ans = DynamicSolver.perform(data, result, list, toOperate);
            f.print(" | " + (ans[0] - bounds[0]) + " " + (ans[1] - bounds[0]) +" "+(ans[2] - bounds[0]));
        }
        f.println();
        f.flush();
    }


    public static void runBothTest(PrintWriter f) {
        Data data = new Data(new Parameters());
        ArrayList<ArrayList<Class>> inFinalPolicies = generatePolicies(data, 60);
        data.set(inFinalPolicies);
        long[] bounds = new BoundsCalculator().getBounds(data);
        long minClGluing = new CliqueSharingSolver().solveWithGreedyGluing(data)[1] - bounds[0];
        f.print(bounds[0] + "--" + bounds[1] + " | " + minClGluing);
        for (int cnt : new int[]{0,2,4,6,8,10, 15, 20, 25, 30, 35, 40}) {
            ArrayList<ArrayList<Class>> newInstances = new ArrayList<ArrayList<Class>>();
            for (ArrayList<Class> inOther : inFinalPolicies)
            {
                ArrayList<Class> copy = new ArrayList<>(inOther);
                Collections.shuffle(copy);
                ArrayList<Class> left = new ArrayList<>(copy.subList(cnt, copy.size()));
                ArrayList<Class> ans = new ArrayList<>(data.classes);
                ans.removeAll(copy);
                Collections.shuffle(ans);
                ans = new ArrayList<>(ans.subList(0, cnt));
                ans.addAll(left);
                newInstances.add(ans);
            }
            data.set(newInstances);
            ArrayList<Class> result = new CliqueSharingSolver().createMinimalSequence(data);
            ArrayList<Pair<Class, Integer>> toOperate = DynamicSolver.createOrderForBoth(data, inFinalPolicies);
            long[] ans = DynamicSolver.perform(data, result, newInstances, toOperate);
            f.print(" | " + (ans[0] - bounds[0]) + " " + (ans[1] - bounds[0]) +" "+(ans[2] - bounds[0]));
            f.flush();
        }
        f.println();
        f.flush();
    }


    public static ArrayList<ArrayList<Class>> generatePolicies(Data data, int num, ArrayList<ArrayList<Class>> mustBe) {
        ArrayList<ArrayList<Class>> policies = new ArrayList<>();
        for (int i = 0; i < data.policies.size(); i++) {
            ArrayList<Class> classes = new ArrayList<>(data.orders.get(i));
            Collections.shuffle(classes);
            classes.removeAll(mustBe.get(i));
            classes = new ArrayList<>(classes.subList(0, num - mustBe.get(i).size()));
            classes.addAll(mustBe.get(i));
            policies.add(classes);
        }
        return policies;
    }


    public static void runInsertTest(int id, int num) throws FileNotFoundException {
        PrintWriter f = new PrintWriter("ans_insert.txt");
        for (int i = 0; i < num; i++) {
            runInsertTest(f);
        }
        f.close();
    }

    public static void runRemoveTest(int id, int num) throws FileNotFoundException {
        PrintWriter f = new PrintWriter("ans_remove.txt");
        int k = Parameters.numberOfClassesInEachPolicy;
        Parameters.numberOfClassesInEachPolicy = 100;
        for (int i = 0; i < num; i++) {
            runRemoveTest(f);
        }
        f.close();
        Parameters.numberOfClassesInEachPolicy = k;
    }

    public static void runBothTest(int id, int num) throws FileNotFoundException {
        PrintWriter f = new PrintWriter("ans_both.txt");
        int k = Parameters.numberOfClassesInEachPolicy;
        Parameters.numberOfClassesInEachPolicy = 100;
        for (int i = 0; i < num; i++) {
            runBothTest(f);
        }
        Parameters.numberOfClassesInEachPolicy = k;
        f.close();
    }


    public static void runAll(int id, int num) throws FileNotFoundException {
        runRemoveTest(id, num);
        runBothTest(id, num);
        runInsertTest(id, num);
    }

    public static void main(String[] args) throws FileNotFoundException {
        args = new String[]{"1", "100"};
        RunnerDyn.runAll(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}
