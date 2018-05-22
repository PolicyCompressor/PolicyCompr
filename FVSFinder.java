import java.util.*;

public class FVSFinder {

    public static ArrayList<GraphNode> getLowestFeedbackVertexSet(ArrayList<GraphNode> gGraph)
    {
        return getLowestFeedbackVertexSet(gGraph, new HashSet<>());
    }

    public static ArrayList<GraphNode> getLowestFeedbackVertexSet(ArrayList<GraphNode> gGraphOut, HashSet<Class> notTouch) {
        ArrayList<GraphNode> gGraph = new ArrayList<>(gGraphOut);
        Collections.shuffle(gGraph);
        double[] realCost = new double[gGraph.size()];
        for (GraphNode graphNode : gGraph) {
            realCost[graphNode.id] = graphNode.cost;
            if (notTouch.contains(graphNode.c))
            {
                realCost[graphNode.id] = Double.MAX_VALUE;
            }
        }

        boolean[] isTaken = new boolean[gGraph.size()];

        boolean[] isTaken2 = new boolean[gGraph.size()];
        for (GraphNode node : gGraph)
        {
            isTaken2[node.id] = !notTouch.contains(node.c);
        }

        ArrayList<GraphNode> cands = new ArrayList<>();

        if (CycleFinder.hasCycleFast(gGraph, isTaken2))
        {
            throw new AssertionError();
        }

        for (GraphNode node : gGraph)
        {
            if (!isTaken2[node.id])
            {
                continue;
            }
            isTaken2[node.id] = false;
            if (CycleFinder.hasCycleFast(gGraph, isTaken2))
            {
                isTaken[node.id] = true;
                cands.add(node);
            }

            isTaken2[node.id] = true;
        }


        while (true) {
            ArrayList<GraphNode> cycle = CycleFinder.getRandomShortestCycle(gGraph, isTaken);
            if (cycle == null) {
                break;
            }

//            if (cycle.size() > 2)
//            {
//                System.err.println("Cycle: "+cycle.size());
//            }


            double best = Double.MAX_VALUE;
            for (GraphNode node : cycle) {
                if (isTaken[node.id]) {
                    throw new AssertionError();
                }
                best = Math.min(best, realCost[node.id]);
            }
            if (Double.isInfinite(best))
            {
                throw new AssertionError();
            }

            for (GraphNode node : cycle) {
                realCost[node.id] -= best;
                if (realCost[node.id] < 1e-5) {
                    cands.add(node);
                    isTaken[node.id] = true;
                }
            }
        }

        RandomSort.sort(cands, (p1, p2) -> Double.compare(p2.cost, p1.cost));

        ArrayList<GraphNode> ans = new ArrayList<>();

        for (GraphNode node : cands) {
            if (!isTaken[node.id]) {
                throw new AssertionError();
            }
            isTaken[node.id] = false;
            if (CycleFinder.hasCycleFast(gGraph, isTaken)) {
                isTaken[node.id] = true;
                ans.add(node);
            }

        }

        CycleFinder.validateAcyclity(gGraph, isTaken);

        return ans;
    }
}
