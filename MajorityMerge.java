import java.util.*;

public class MajorityMerge {

    public long[] solveMajorityMerge(Data data)
    {
        ArrayList<GraphNode[]> graphs = new ArrayList<>();
        for (Policy p : data.policies)
        {
            ArrayList<GraphNode> g = new AllOrNothingSolver().buildGNodeFullGraph(new Data(p,data.classes));
            GraphNode[] graphNodes = new GraphNode[new Parameters().numberOfClasses];
            for (GraphNode node: g)
            {
                graphNodes[node.c.id] = node;
            }
            graphs.add(graphNodes);
        }

        ArrayList<Class> result = new ArrayList<>();

        while (true)
        {
            long max3 = -1;
            long max1 = -1;
            Class opt = null;

            ArrayList<Class> forIter = new ArrayList<>(data.classes);
            Collections.shuffle(forIter);
            for (Class c1 : forIter)
            {
                int d1 = 0;
                for (GraphNode[] g : graphs)
                {
                    if (g[c1.id] != null && g[c1.id].prevNodes.isEmpty())
                    {
                        d1++;
                    }
                }
                if (d1 == 0)
                {
                    continue;
                }

                for (Class c2 : data.classes)
                {
                    if (c1 == c2)
                    {
                        continue;
                    }
                    int d2 = 0;
                    for (GraphNode[] g : graphs)
                    {
                        if (g[c2.id] != null && g[c2.id].prevNodes.size() < 2)
                        {
                            HashSet<GraphNode> nodes = new HashSet<>(g[c2.id].prevNodes);
                            nodes.removeIf(o -> o.c == c1);
                            d2 += nodes.size() == 0 ? 1 : 0;
                        }
                    }

                    long win3 = Math.max(d1 - 1, 0) * c1.size + Math.max(d2 - 1, 0) * c2.size;
                    long win1 = Math.max(d1 - 1, 0) * c1.size;
                    if (win3 > max3 || (win3 == max3 && win1 > max1))
                    {
                        opt = c1;
                        max1 = win1;
                        max3 = win3;
                    }

                    if (d2 == 0)
                    {
                        continue;
                    }

                    for (Class c3 : data.classes)
                    {
                        if (c1 == c3 || c2 == c3)
                        {
                            continue;
                        }
                        int d3 = 0;

                        for (GraphNode[] g : graphs)
                        {
                            if (g[c3.id] != null && g[c3.id].prevNodes.size() < 3)
                            {
                                HashSet<GraphNode> nodes = new HashSet<>(g[c3.id].prevNodes);
                                nodes.removeIf(o -> (o.c == c1));
                                nodes.removeIf(o -> (o.c == c2));
                                d3 += nodes.size() == 0 ? 1 : 0;
                            }

                        }

                        win3 = Math.max(d1 - 1, 0) * c1.size + Math.max(d2 - 1, 0) * c2.size + Math.max(d3 - 1, 0) * c3.size;
                        win1 = Math.max(d1 - 1, 0) * c1.size;
                        if (win3 > max3 || (win3 == max3 && win1 > max1))
                        {
                            opt = c1;
                            max1 = win1;
                            max3 = win3;
                        }
                    }
                }
            }

            if (opt == null) {
                break;
            }
            result.add(opt);
            for (GraphNode[] g : graphs) {
               if (g[opt.id] == null || g[opt.id].prevNodes.size() > 0)
               {
                   continue;
               }
               for (GraphNode nx : g[opt.id].nextNodes)
                {
                    if (!nx.prevNodes.contains(g[opt.id]))
                    {
                        new AssertionError();
                    }
                    nx.prevNodes.remove(g[opt.id]);
                }
                g[opt.id] = null;
            }
        }

        return new Reducer().processReduce(result, data);

    }
}







