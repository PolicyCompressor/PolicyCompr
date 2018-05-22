import java.util.ArrayList;
import java.util.HashSet;

public class CliqueSharingSolver {

    ArrayList<GraphNode> buildGNodeGraph(Data data) {
        ArrayList<Policy>[] policyMap = PolicyMapCreator.createPolicyMap(data);
        GraphBuilder graphBuilder = new GraphBuilder();
        for (Class c : data.classes) {
            if (policyMap[c.id].size() == 0)
            {
                continue;
            }

            if (policyMap[c.id].size() == 1) {
                graphBuilder.addNewVertex(c, c.size, policyMap[c.id]);
                continue;
            }

            int p = 1;//policyMap[c.id].size()*policyMap[c.id].size() / 4;

            for (int i = 0; i < policyMap[c.id].size(); i++) {
                for (int j = i + 1; j < policyMap[c.id].size(); j++) {
                    graphBuilder.addNewVertex(c, 1.0 * c.size / p, policyMap[c.id].get(i), policyMap[c.id].get(j));
                }
            }
        }
        graphBuilder.finishConstruction();
        return graphBuilder.nodes;
    }


    public ArrayList<GraphNode> buildGCrossGraph(Data data, ArrayList<GraphNode> fvs) {

        ArrayList<Policy>[] policyMap = PolicyMapCreator.createPolicyMap(data);
        HashSet<Class>[][] blockedPairs = new HashSet[data.policies.size()][data.policies.size()];
        for (int i = 0; i < data.policies.size(); i++) {
            for (int j = 0; j < data.policies.size(); j++) {
                blockedPairs[i][j] = new HashSet<>();
            }
        }
        for (GraphNode node : fvs) {
            if (node.policies.size() != 2) {
                throw new AssertionError();
            }
            blockedPairs[node.policies.get(0).id][node.policies.get(1).id].add(node.c);
            blockedPairs[node.policies.get(1).id][node.policies.get(0).id].add(node.c);
        }

        GraphBuilder result = new GraphBuilder();

        for (Class c : data.classes) {
            ArrayList<Policy> curPolicies = policyMap[c.id];
            boolean[][] blocked = new boolean[curPolicies.size()][curPolicies.size()];
            for (int i = 0; i < curPolicies.size(); i++) {
                for (int j = i + 1; j < curPolicies.size(); j++) {
                    blocked[i][j] = blockedPairs[curPolicies.get(i).id][curPolicies.get(j).id].contains(c);
                    blocked[j][i] = blocked[i][j];
                }
            }

            ArrayList<Integer> seperation = CliqueMerging.getOptimalSeperation(blocked);
//            System.err.println(blocked.length +" "+seperation.size());
            for (int mask : seperation) {
                ArrayList<Policy> vertexPolicies = new ArrayList<>();
                for (int i = 0; i < curPolicies.size(); i++) {
                    if ((mask & (1 << i)) != 0) {
                        vertexPolicies.add(curPolicies.get(i));
                    }
                }
                result.addNewVertex(c, 0, vertexPolicies);
            }
        }
        result.finishConstruction();
        CycleFinder.validateAcyclity(result.nodes);
        return result.nodes;
    }



    public long[] solve(Data data) {
        System.out.println("Clique sharing algorithm:");
        ArrayList<GraphNode> graph = buildGNodeGraph(data);
        ArrayList<GraphNode> fvs = FVSFinder.getLowestFeedbackVertexSet(graph);
        ArrayList<GraphNode> crossGraph = buildGCrossGraph(data, fvs);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    public long[] solveWithGreedyGluing(Data data) {
        System.out.println("Clique sharing algorithm With Greedy Gluing:");
        ArrayList<GraphNode> graph = buildGNodeGraph(data);
        ArrayList<GraphNode> fvs = FVSFinder.getLowestFeedbackVertexSet(graph);
        ArrayList<GraphNode> crossGraph = buildGCrossGraph(data, fvs);
        crossGraph = new AllOrNothingSolver().greedyCompress(crossGraph);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    public ArrayList<Class> createMinimalSequence(Data data) {
        System.out.println("Clique sharing algorithm With Greedy Gluing:");
        ArrayList<GraphNode> graph = buildGNodeGraph(data);
        ArrayList<GraphNode> fvs = FVSFinder.getLowestFeedbackVertexSet(graph);
        ArrayList<GraphNode> crossGraph = buildGCrossGraph(data, fvs);
        crossGraph = new AllOrNothingSolver().greedyCompress(crossGraph);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().reduce(result, data);
    }

}
