import java.util.ArrayList;
import java.util.Arrays;

public class PolicyMatcher {

    public static class MatchResult
    {
        public MatchResult(int n)
        {
            position = new int[n];
            wasSuccesfull = false;
            Arrays.fill(position, -1);
        }
        int[] position;
        boolean wasSuccesfull;
    }

    public static MatchResult matchPolicy(Policy p, boolean[] used, ArrayList<Class> classes, boolean direction)
    {
        int c = 0;
        MatchResult matchResult = new MatchResult(p.isBefore.length);
        for (int t = 0; t < classes.size(); t++) {
            int i = (direction) ? t : classes.size()-t-1;
            if (used[i]) {
                continue;
            }
            for (Class c1 : p.classes) {
                if (c1 != classes.get(i) || matchResult.position[c1.id] != -1) {
                    continue;
                }

                boolean take = true;

                for (Class c2 : p.classes) {
                    if (c1 == c2)
                    {
                        continue;
                    }
                    if (direction & p.isBefore(c2, c1)) {
                        take &= matchResult.position[c2.id] != -1;
                    }
                    if (!direction & p.isBefore(c1, c2))
                    {
                        take &= matchResult.position[c2.id] != -1;
                    }
                }

                if (take) {
                    matchResult.position[c1.id] = i;
                    c++;
                }
            }
        }
        matchResult.wasSuccesfull = c == p.classes.size();
        return matchResult;
    }

    public static MatchResult matchPolicy(Policy p, boolean[] used, ArrayList<Class> classes)
    {
        return matchPolicy(p, used, classes, false);
    }


    public static boolean canMatch(Policy p, boolean[] used, ArrayList<Class> classes) {
        return matchPolicy(p, used, classes).wasSuccesfull;
    }

    public static boolean canMatch(Policy p, ArrayList<Class> classes) {
        return matchPolicy(p, new boolean[classes.size()], classes).wasSuccesfull;
    }


    public static boolean canMatch(boolean[] used, ArrayList<Class> classes, ArrayList<Policy> policies) {
        for (Policy p : policies) {
            if (!canMatch(p, used, classes))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean canMatch(ArrayList<Class> classes, ArrayList<Policy> policies) {
        boolean[] used = new boolean[classes.size()];
        return canMatch(used, classes, policies);
    }

    public static int[][] buildIndices(Policy p, ArrayList<Class> classes) {
        MatchResult left = matchPolicy(p, new boolean[classes.size()], classes, true);
        MatchResult right = matchPolicy(p, new boolean[classes.size()], classes, false);
        if (left.wasSuccesfull != right.wasSuccesfull)
        {
            throw  new AssertionError();
        }
        return new int[][]{left.position, right.position};
    }
}
