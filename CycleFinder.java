import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


public class CycleFinder {

    static boolean mode = false;


    public static boolean dfs(GraphNode node, int[] color)
    {
        if (color[node.id] == 2)
        {
            return false;
        }
        if (color[node.id] == 1)
        {
            return true;
        }
        color[node.id] = 1;
        for (GraphNode nx : node.nextNodes)
        {
            if (dfs(nx, color))
            {
                return true;
            }
        }
        color[node.id] = 2;
        return false;
    }


    public static boolean hasCycleFast(ArrayList<GraphNode> gGraph, boolean[] isTaken)
    {
        int[] color = new int[gGraph.size()];
        for (int i =0; i < gGraph.size(); i++)
        {
            if (isTaken[i])
            {
                color[i] = 2;
            }
        }
        for (int i = 0; i < gGraph.size(); i++)
        {
            if (dfs(gGraph.get(i), color))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean hasCycleFast(ArrayList<GraphNode> gGraph, GraphNode node)
    {
        int[] color = new int[gGraph.size()];
        return  dfs(node, color);
    }


    public static ArrayList<GraphNode> getRandomShortestCycle(ArrayList<GraphNode> gGraph, boolean[] isTaken)
    {
        ArrayList<GraphNode> nodes = new ArrayList<>(gGraph);
        Collections.shuffle(nodes);
        ArrayList<GraphNode> cycleOfLength2 = findShortestCycleOfLength2(nodes, isTaken);
        if (cycleOfLength2 != null)
        {
            return cycleOfLength2;
        }
        return findShortestCycle(nodes, isTaken);
    }


    public static void validateAcyclity(ArrayList<GraphNode> graphNodes, boolean[] isTaken)
    {
        if (hasCycleFast(graphNodes, isTaken))
        {
            throw new AssertionError();
        }
    }

    public static void validateAcyclity(ArrayList<GraphNode> graphNodes)
    {
        validateAcyclity(graphNodes, new boolean[graphNodes.size()]);
    }

    private static ArrayList<GraphNode> findShortestCycleOfLength2(ArrayList<GraphNode> nodesG, boolean isTaken[])
    {
        ArrayList<GraphNode> nodes = new ArrayList<>(nodesG);
        if (mode)
        {
            Collections.sort(nodes, (o1, o2)->o1.c.size - o2.c.size);
        }
        for (GraphNode node : nodes)
        {
            for (GraphNode next : node.bidirectedNodes)
            {
                if (!isTaken[node.id] && !isTaken[next.id])
                {
                    return new ArrayList<>(Arrays.asList(node, next));
                }
            }
        }
        return null;
    }

    private static ArrayList<GraphNode> findShortestCycle(ArrayList<GraphNode> nodes, boolean[] isTaken)
    {
        // find all distances
        int[][] len = new int[nodes.size()][nodes.size()];
        GraphNode[][] prev = new GraphNode[nodes.size()][nodes.size()];
        for (GraphNode node : nodes)
        {
            if (isTaken[node.id])
            {
                continue;
            }
            len[node.id][node.id] = 1;
            ArrayDeque<GraphNode> queue = new ArrayDeque<>();
            queue.push(node);
            while (queue.size() > 0)
            {
                GraphNode cur = queue.pollFirst();
                for (GraphNode next : cur.nextNodes)
                {
                    if (isTaken[next.id])
                    {
                        continue;
                    }
                    if (len[node.id][next.id] == 0)
                    {
                        len[node.id][next.id] = len[node.id][cur.id]+1;
                        prev[node.id][next.id] = cur;
                        queue.addLast(next);
                    }
                }
            }
        }

        //find ends of path
        int minLen = 10000;
        GraphNode from = null, to = null;

        for (GraphNode node : nodes)
        {
            for (GraphNode next : node.nextNodes)
            {
                if (len[next.id][node.id] !=0 && len[next.id][node.id] < minLen)
                {
                    minLen = len[next.id][node.id];
                    from = next;
                    to = node;
                }
            }
        }
        if (from == null)
        {
            return null;
        }

        //recover cycle;
        ArrayList<GraphNode> ans = new ArrayList<>();
        while (to != from)
        {
            ans.add(to);
            to = prev[from.id][to.id];
        }
        ans.add(from);
        return ans;

    }
}
