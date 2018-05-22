import com.sun.org.apache.regexp.internal.RE;
import javafx.util.Pair;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.*;

public class DynamicSolver {

    public static ArrayList<Class> performOperations(ArrayList<Pair<Class, Integer>> operations, Data data, ArrayList<Class> result, boolean isInTheEnd, boolean isSort)
    {
        result = new ArrayList<>(result);
        for (Pair<Class, Integer> pair : operations)
        {
            Class c = pair.getKey();
            Policy p = new Policy(data.policies.get(pair.getValue()));
            int[][] indices = PolicyMatcher.buildIndices(p, result);
            if (p.classes.contains(c))
            {
                data.remove(c, p.id);
                int k = indices[0][c.id];
                boolean canRemove = true;
                for (Policy p1 : data.policies)
                {
                    if (PolicyMatcher.buildIndices(p1, result)[0][c.id] == k)
                    {
                        canRemove = false;
                        break;
                    }
                }
                if (canRemove)
                {
                    result = new ArrayList<>(result);
                    result.remove(k);
                    if (!PolicyMatcher.canMatch(result, data.policies)) {
                        throw new AssertionError();
                    }
                }
                if (!isInTheEnd)
                {
                    result = Reducer.reduce(result, data, isSort);
                }
                continue;
            }
            data.insert(c, pair.getValue());
            Policy nwP = data.policies.get(pair.getValue());

            int pos = -1, bestCost = Integer.MAX_VALUE;
            for (int i = -1; i < result.size(); i++)
            {
                int cost =(i < 0 || result.get(i).id != c.id) ? c.size : 0;
                for (Class c1 : p.classes)
                {
                    if (c1 == c)
                    {
                        continue;
                    }
                    if (nwP.isBefore(c1, c) && indices[0][c1.id] > i)
                    {
                        cost+= c1.size;
                    }
                    if (nwP.isBefore(c, c1) && indices[1][c1.id] <= i)
                    {
                        cost += c1.size;
                    }
                }
                if (cost < bestCost)
                {
                    bestCost = cost;
                    pos = i;
                }
            }

            boolean isSimilar = (pos >= 0 && result.get(pos).id == c.id);

            ArrayList<Class> insert = new ArrayList<>();
            for (int j = pos+1; j < result.size(); j++)
            {
                if (indices[0][result.get(j).id] == j && result.get(j).id != c.id && nwP.isBefore(result.get(j), c))
                {
                    insert.add(result.get(j));
                }
            }
            insert.add(c);
            for (int j = 0; j <= pos; j++)
            {
                if (indices[1][result.get(j).id] == j && result.get(j).id != c.id && nwP.isBefore(c, result.get(j)))
                {
                    insert.add(result.get(j));
                }
            }
            if (isSimilar)
            {
                result.remove(pos);
                pos--;
            }
            result.addAll(pos+1, insert);
            if (!isInTheEnd)
            {
                result = Reducer.reduce(result, data, isSort);
            }
        }
        return result;
    }


    public static ArrayList<Pair<Class, Integer>> createOrderForInsert(Data data)
    {
        ArrayList<Pair<Class, Integer>> toInsert = new ArrayList<>();
        for (int i = 0; i < data.orders.size(); i++)
        {
            for (Class c : data.orders.get(i))
            {
                if (data.policies.get(i).classes.contains(c))
                {
                    continue;
                }
                toInsert.add(new Pair<>(c, i));
            }
        }
        return toInsert;
    }

    public static ArrayList<Pair<Class, Integer>> createOrderForRemove(Data data, ArrayList<ArrayList<Class>> policiesAfter)
    {
        ArrayList<Pair<Class, Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < data.orders.size(); i++)
        {
            List<Class> toRem = new ArrayList<>(data.policies.get(i).classes);
            toRem.removeAll(policiesAfter.get(i));
            for (Class c : toRem)
            {
                toRemove.add(new Pair<>(c, i));
            }
        }
        return toRemove;
    }

    public static ArrayList<Pair<Class, Integer>> createOrderForBoth(Data data, ArrayList<ArrayList<Class>> policiesAfter)
    {
        ArrayList<Pair<Class, Integer>> toRemove = new ArrayList<>();
        for (int i = 0; i < data.orders.size(); i++)
        {
            for (Class c : data.classes)
            {
                boolean fl1 = data.policies.get(i).classes.contains(c);
                boolean fl2 = policiesAfter.get(i).contains(c);
                if (fl1 != fl2)
                {
                    toRemove.add(new Pair<>(c, i));
                }
            }
        }
        return toRemove;
    }

    public static long[] perform(Data data, ArrayList<Class> result, ArrayList<ArrayList<Class>> start,  ArrayList<Pair<Class, Integer>> op)
    {
        Collections.shuffle(op);
        ArrayList<Class> classicResult = new ArrayList<>(result);
        data.set(start);
        result = Reducer.reduce(performOperations(op, data, classicResult, false,  false), data, false);
        long[] ans = new long[3];
        ans[0] = Scorer.getScore(result);
        data.set(start);
        result = performOperations(op, data, classicResult, true, false);
        ans[1] = Scorer.getScore(result);
        ans[2] = Scorer.getScore(Reducer.reduce(result, data, false));
        return ans;
    }

}
