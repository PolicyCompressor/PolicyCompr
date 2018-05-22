import java.util.ArrayList;

public class Reducer {

    public static ArrayList<Class> reduce(ArrayList<Class> ans, Data data, boolean isSort) {
        boolean[] taken = new boolean[ans.size()];
        if (!PolicyMatcher.canMatch(taken, ans, data.policies)) {
            throw new AssertionError();
        }

        ArrayList<Integer> order = new ArrayList<>();

        for (int i = 0; i < ans.size(); i++)
        {
            order.add(i);
        }

        if (isSort)
        {
            RandomSort.sort(order, (o1, o2)->ans.get(o2).size - ans.get(o1).size);
        }
        for (int j = 0; j < ans.size(); j++) {
            int i = order.get(j);
            taken[i] = true;
            if (!PolicyMatcher.canMatch(taken, ans, data.policies)) {
                taken[i] = false;
            }
        }

        if (!PolicyMatcher.canMatch(taken, ans, data.policies)) {
            throw new AssertionError();
        }

        ArrayList<Class> reduced = new ArrayList<>();
        for (int i = 0; i < ans.size(); i++) {
            if (!taken[i]) {
                reduced.add(ans.get(i));
            }
        }
        return reduced;
    }

    public static ArrayList<Class> reduce(ArrayList<Class> ans, Data data)
    {
        return reduce(ans, data, true);
    }

    public long[] processReduce(ArrayList<Class> result, Data data)
    {

        long oldScore = Scorer.getScore(result);
        System.out.println("Before LD: "+Scorer.getScore(result));
        result = Reducer.reduce(result, data);
        long newScore = Scorer.getScore(result);
        System.out.println("After LD: " + Scorer.getScore(result));
        return new long[]{oldScore, newScore};
    }

}
