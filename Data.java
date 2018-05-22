import java.lang.reflect.Array;
import java.util.*;
import java.util.regex.Pattern;

public class Data {
    static Random rnd = new Random();

    ArrayList<Class> classes;
    ArrayList<Policy> policies;
    ArrayList<ArrayList<Class>> orders = new ArrayList<>();
    boolean[][] pairs;
    boolean[][] isActive;

    public Data(Policy p, ArrayList<Class> classes) {
        policies = new ArrayList<>(Arrays.asList(p));
        this.classes = new ArrayList<>(classes);
    }

    public Data(Parameters params) {
        classes = new ArrayList<>();
        for (int i = 0; i < params.numberOfClasses; i++) {
            classes.add(new Class(classes.size(), params.minNumberOfRules + rnd.nextInt(params.maxNumberOfRules - params.minNumberOfRules)));
        }

        policies = new ArrayList<>();
        for (int i = 0; i < params.numberOfPolicies; i++) {
            policies.add(null);
        }


        for (int kl = 0; kl < params.numberOfPolicies; kl++) {
            ArrayList<Class> test = new ArrayList<>(classes);
            Collections.shuffle(test, rnd);
            orders.add(new ArrayList<>(test.subList(0, params.numberOfClassesInEachPolicy)));
        }


        int k = params.numberOfMaximumIntersectionsForClass;
        pairs = new boolean[params.numberOfClasses][params.numberOfClasses];
        for (int i = 0; i < pairs.length; i++) {
            int p = rnd.nextInt(k + 1);
            for (int t = 0; t < p; t++) {
                int nx = rnd.nextInt(pairs.length);
                pairs[nx][i] = pairs[i][nx] = true;
            }
        }

        isActive = new boolean[params.numberOfPolicies][params.numberOfClasses];
        for (int i = 0; i < params.numberOfPolicies; i++) {
            Arrays.fill(isActive[i], true);
        }

        constructPolicies();
    }


    void constructPolicies() {
        for (int i = 0; i < policies.size(); i++) {
            reconstructPolicy(i);
        }
    }

    void reconstructPolicy(int i) {
        Policy p = new Policy(i, classes.size());
        ArrayList<Class> curClasses = orders.get(i);
        for (Class c : curClasses) {
            if (isActive[i][c.id]) {
                p.addClass(c);
            }
        }
        for (int i1 = 0; i1 < curClasses.size(); i1++) {
            for (int i2 = i1 + 1; i2 < curClasses.size(); i2++) {
                if (isActive[i][curClasses.get(i1).id] && isActive[i][curClasses.get(i2).id] && pairs[curClasses.get(i1).id][curClasses.get(i2).id]) {
                    p.addRelation(curClasses.get(i1), curClasses.get(i2));
                }
            }
        }

        p.finishBuilding();
        policies.set(i, p);
    }

    boolean insert(Class c, int numPolicy)
    {
        if (isActive[numPolicy][c.id])
        {
            return false;
        }
        isActive[numPolicy][c.id] = true;
        reconstructPolicy(numPolicy);
        return true;
    }

    boolean remove(Class c, int numPolicy)
    {
        isActive[numPolicy][c.id] = false;
        reconstructPolicy(numPolicy);
        return true;
    }

    public void set(ArrayList<ArrayList<Class>> policiesProfile) {
        for (int i = 0; i < policiesProfile.size(); i++)
        {
            Arrays.fill(isActive[i], false);
            for (Class c : policiesProfile.get(i))
            {
                isActive[i][c.id] = true;
            }
        }
        constructPolicies();
    }
}
