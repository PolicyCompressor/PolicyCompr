import javafx.util.Pair;

import javax.xml.soap.Node;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

public class SCSFinder {

    void updatePrevState(int state, int takenMask, ArrayList<ArrayList<Class>> classes, long[] dp, int[] prev, Class[] prevClass)
    {
        int stateCopy = state;
        Class prevC = null;
        int prevS = 0;
        long mul = 1;
        for (int i = classes.size()-1; i >=0; i--)
        {
            int curIndex = state % (classes.get(i).size()+1);
            state /= (classes.get(i).size() + 1);
            prevS += mul * curIndex;

            if (((1 << i) & takenMask) == 0)
            {
                mul *= (classes.get(i).size()+1);
                continue;
            }

            prevS -= mul;
            mul *= (classes.get(i).size()+1);

            if (curIndex == 0)
            {
                return;
            }
            if (prevC != null && prevC != classes.get(i).get(curIndex-1))
            {
                return;
            }
            prevC = classes.get(i).get(curIndex-1);
        }

        if (dp[prevS] + prevC.size < dp[stateCopy])
        {
            dp[stateCopy] = dp[prevS] + prevC.size;
            prev[stateCopy] = prevS;
            prevClass[stateCopy] = prevC;
        }
    }

    ArrayList<Class> getSCS(ArrayList<ArrayList<Class>> classes)
    {
        int m = 1;
        for (int i = 0; i < classes.size(); i++)
        {
            m = m*(classes.get(i).size()+1) + classes.get(i).size();
        }

        long[] dp = new long[m+1];
        int[] prev = new int[m+1];
        Class[] prevClass = new Class[m+1];

        for (int i = 1; i<=m; i++)
        {
            dp[i] = Integer.MAX_VALUE;
            for (int take = 1; take < (1 << classes.size()); take++)
            {
                updatePrevState(i, take, classes, dp, prev, prevClass);
            }
        }

        ArrayList<Class> res = new ArrayList<>();
        while (m > 0)
        {
            res.add(prevClass[m]);
            m = prev[m];
        }
        Collections.reverse(res);
        if (dp[dp.length-1] != Scorer.getScore(res))
        {
            System.err.println(dp[m] +" "+Scorer.getScore(res));
            throw new AssertionError();
        }
//        System.err.println(Scorer.getScore(res));
        return res;
    }

    public long[] solve(Data data)
    {
        System.out.println("SCS solver:");
        ArrayList<ArrayList<Class>> sequences = new ArrayList<>();

        for (Policy p : data.policies)
        {
            sequences.add(p.classes);
        }

        while (sequences.size() > 3)

        {
            long min = 10000000;
            int b1 = 0,  b2 = 0, b3 = 0;
            for (int i = 0; i < sequences.size(); i++)
            {
                for (int j = i+1; j < sequences.size(); j++)
                {
                    for (int k = j+1; k < sequences.size(); k++)
                    {
                        long len = Scorer.getScore(getSCS(new ArrayList<>(Arrays.asList(sequences.get(i), sequences.get(j), sequences.get(k))))) - Scorer.getScore(sequences.get(i)) - Scorer.getScore(sequences.get(j))-Scorer.getScore(sequences.get(k));
                        if (len < min)
                        {
                            min = len;
                            b1 = i;
                            b2 = j;
                            b3 = k;
                        }
                    }
                }
            }
            ArrayList<Class> opt = getSCS(new ArrayList<>(Arrays.asList(sequences.get(b1), sequences.get(b2), sequences.get(b3))));
            sequences.remove(b3);
            sequences.remove(b2);
            sequences.remove(b1);
            sequences.add(opt);
        }

        ArrayList<Class> result = getSCS(sequences);
        return new Reducer().processReduce(result, data);
    }
}
