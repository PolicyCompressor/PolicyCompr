import java.util.ArrayList;
import java.util.HashSet;

public class AllOrNothingSolver {

    ArrayList<GraphNode> buildGNodeFullGraph(Data data) {
        ArrayList<Policy>[] policyMap = PolicyMapCreator.createPolicyMap(data);
        GraphBuilder graphBuilder = new GraphBuilder();
        for (Class c : data.classes) {
            if (policyMap[c.id].size() == 0) {
                continue;
            }
            graphBuilder.addNewVertex(c, c.size, policyMap[c.id]);
        }
        graphBuilder.finishConstruction();
        return graphBuilder.nodes;
    }

    public ArrayList<GraphNode> buildGCrossAllGraph(Data data, ArrayList<GraphNode> fvs) {
        ArrayList<Policy>[] policyMap = PolicyMapCreator.createPolicyMap(data);

        HashSet<Class> classesInFvs = new HashSet<>();
        for (GraphNode node : fvs) {
            classesInFvs.add(node.c);
        }

        GraphBuilder graphBuilder = new GraphBuilder();
        for (Class c : data.classes) {
            if (!classesInFvs.contains(c)) {
                graphBuilder.addNewVertex(c, 0, policyMap[c.id]);
            } else {
                for (Policy p : policyMap[c.id]) {
                    graphBuilder.addNewVertex(c, 0, p);
                }
            }
        }

        graphBuilder.finishConstruction();
        CycleFinder.validateAcyclity(graphBuilder.nodes);
        return graphBuilder.nodes;
    }

    public long[] solveSimple(Data data)
    {
        System.out.println("Simple All-or-Nothing algorithm:");
        ArrayList<GraphNode> graph = buildGNodeFullGraph(data);
        ArrayList<GraphNode> fvs = FVSFinder.getLowestFeedbackVertexSet(graph);
        ArrayList<GraphNode> crossGraph = buildGCrossAllGraph(data, fvs);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    public ArrayList<GraphNode> compressSomePair(ArrayList<GraphNode> nodes, HashSet<String> bad) {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                GraphNode f = nodes.get(i);
                GraphNode s = nodes.get(j);
                if (f.c != s.c) {
                    continue;
                }

                String label = f.c.id + "," + f.policies.toString() + "," + s.policies.toString();
                if (bad.contains(label)) {
                    continue;
                }
                GraphBuilder temp = new GraphBuilder();
                for (GraphNode old : nodes) {
                    if (old == f || old == s) {
                        continue;
                    }
                    temp.addNewVertex(old.c, old.cost, new ArrayList<>(old.policies));
                }
                ArrayList<Policy> policies = new ArrayList<>();
                policies.addAll(f.policies);
                policies.addAll(s.policies);
                temp.addNewVertex(f.c, f.cost, new ArrayList<>(policies));
                temp.finishConstruction();
                if (!CycleFinder.hasCycleFast(temp.nodes, temp.nodes.get(temp.nodes.size()-1))) {
                    return temp.nodes;
                }
                bad.add(label);

            }
        }
        return nodes;
    }


    public ArrayList<GraphNode> greedyCompress(ArrayList<GraphNode> crossGraph)
    {
        RandomSort.sort(crossGraph, (o1, o2) -> o2.c.size - o1.c.size);
        HashSet<String> connectedPairs = new HashSet<>();
        while (true) {
            int old = crossGraph.size();
            crossGraph = compressSomePair(crossGraph, connectedPairs);
            if (old == crossGraph.size()) {
                break;
            }
        }
        return crossGraph;
    }

    public long[] solveWithGreedyGluing(Data data)
    {
        System.out.println("All-or-Nothing algorithm With Greedy Gluing:");
        ArrayList<GraphNode> graph = buildGNodeFullGraph(data);
        ArrayList<GraphNode> fvs = FVSFinder.getLowestFeedbackVertexSet(graph);
        ArrayList<GraphNode> crossGraph = buildGCrossAllGraph(data, fvs);
        crossGraph = greedyCompress(crossGraph);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    public long[] solveJustGreedyGluing(Data data)
    {
        System.out.println("Greedy Gluing:");
        ArrayList<GraphNode> graph = buildGNodeFullGraph(data);
        ArrayList<GraphNode> crossGraph = buildGCrossAllGraph(data, graph);
        crossGraph = greedyCompress(crossGraph);
//        crossGraph = greedyCompress(crossGraph);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    ArrayList<GraphNode> buildGNodeFullPairGraph(Data data, HashSet<Class> forJoin) {
        ArrayList<Policy>[] policyMap = PolicyMapCreator.createPolicyMap(data);
        GraphBuilder graphBuilder = new GraphBuilder();
        for (Class c : data.classes) {
            if (policyMap[c.id].size() == 0) {
                continue;
            }
            if (forJoin.contains(c) || policyMap[c.id].size() == 1)
            {
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


    public long[] solveWithCliqueSharing(Data data)
    {
        System.out.println("All-or-Nothing algorithm With Clique Sharing:");
        ArrayList<GraphNode> graph = buildGNodeFullGraph(data);
        ArrayList<GraphNode> fvs =  FVSFinder.getLowestFeedbackVertexSet(graph);
        HashSet<Class> classesNotInFVS = new HashSet<>(data.classes);
        for (GraphNode node : fvs)
        {
            classesNotInFVS.remove(node.c);
        }

        ArrayList<GraphNode> graphNew = buildGNodeFullPairGraph(data, classesNotInFVS);
        ArrayList<GraphNode> pairedFvs = FVSFinder.getLowestFeedbackVertexSet(graphNew, classesNotInFVS);
        ArrayList<GraphNode> crossGraph = new CliqueSharingSolver().buildGCrossGraph(data, pairedFvs);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }

    public long[] solveWithCliqueSharingWithGreedyGluing(Data data)
    {
        System.out.println("All-or-Nothing algorithm With Clique Sharing With Greedy Gluing:");
        ArrayList<GraphNode> graph = buildGNodeFullGraph(data);
        ArrayList<GraphNode> fvs =  FVSFinder.getLowestFeedbackVertexSet(graph);
        HashSet<Class> classesNotInFVS = new HashSet<>(data.classes);
        for (GraphNode node : fvs)
        {
            classesNotInFVS.remove(node.c);
        }

        ArrayList<GraphNode> graphNew = buildGNodeFullPairGraph(data, classesNotInFVS);
        ArrayList<GraphNode> pairedFvs = FVSFinder.getLowestFeedbackVertexSet(graphNew, classesNotInFVS);
        ArrayList<GraphNode> crossGraph = new CliqueSharingSolver().buildGCrossGraph(data, pairedFvs);
        crossGraph = greedyCompress(crossGraph);
        ArrayList<Class> result = ResultConstructor.constructResult(crossGraph);
        return new Reducer().processReduce(result, data);
    }


}
