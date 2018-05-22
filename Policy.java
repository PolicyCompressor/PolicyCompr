import java.util.ArrayList;

public class Policy {

    ArrayList<Class> classes;

    int id;
    boolean[][] isBefore;
    boolean[] isAppears;

    public Policy(Policy policy)
    {
        this.classes = new ArrayList<>(policy.classes);
        this.id = policy.id;
        isAppears = policy.isAppears.clone();
        isBefore = new boolean[policy.isBefore.length][];
        for (int i = 0; i < isBefore.length; i++)
        {
            isBefore[i] = policy.isBefore[i].clone();
        }
    }

    public Policy(int id, int maxClasses) {
        this.id = id;
        isBefore = new boolean[maxClasses][maxClasses];
        isAppears = new boolean[maxClasses];
        for (int i = 0; i < maxClasses; i++) {
            isBefore[i][i] = true;
        }
        classes = new ArrayList<>();
    }

    public void addClass(Class c) {
        classes.add(c);
        isAppears[c.id] = true;
    }

    public void addRelation(Class c1, Class c2) {
        int u = c1.id;
        int v = c2.id;
        if (!isAppears[u] || !isAppears[v]) {
            throw new AssertionError();
        }
        isBefore[u][v] = true;
    }

    public void finishBuilding() {
        for (int k = 0; k < isBefore.length; k++) {
            for (int i = 0; i < isBefore.length; i++) {
                for (int j = 0; j < isBefore.length; j++) {
                    isBefore[i][j] |= isBefore[i][k] && isBefore[k][j];
                }
            }
        }

    }

    public boolean isBefore(Class c1, Class c2) {
        if (!isAppears[c1.id] || !isAppears[c2.id] || c1 == c2) {
            throw new AssertionError();
//            System.err.println("hi");
        }
        return isBefore[c1.id][c2.id];
    }

}
