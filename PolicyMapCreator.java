import java.util.ArrayList;

public class PolicyMapCreator {

    public static ArrayList<Policy>[] createPolicyMap(Data data) {
        ArrayList<Policy>[] policyMap = new ArrayList[data.classes.size()];
        for (int i = 0; i < policyMap.length; i++) {
            policyMap[i] = new ArrayList<>();
        }
        for (Policy p : data.policies) {
            for (Class c : p.classes) {
                policyMap[c.id].add(p);
            }
        }
        return policyMap;
    }
}
