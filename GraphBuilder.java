import java.util.ArrayList;

public class GraphBuilder {
    ArrayList<GraphNode> nodes;

    public GraphBuilder() {
        nodes = new ArrayList<>();
    }

    public void addNewVertex(Class c, double cost, ArrayList<Policy> policies) {
        nodes.add(new GraphNode(nodes.size(), c, cost, policies));
    }

    public void addNewVertex(Class c, double cost, Policy... policies) {
        nodes.add(new GraphNode(nodes.size(), c, cost, policies));
    }

    public void addNewVertex(GraphNode old) {
        nodes.add(new GraphNode(nodes.size(), old.c, old.cost, old.policies));
    }

    public void finishConstruction() {
        for (GraphNode from : nodes) {
            for (GraphNode to : nodes) {
                if (from.c == to.c) {
                    continue;
                }

                boolean shouldAddForward = false;
                boolean shouldAddBackward = false;

                for (Policy p1 : from.policies) {
                    for (Policy p2 : to.policies) {
                        shouldAddForward |= (p1 == p2 && p1.isBefore(from.c, to.c));
                        shouldAddBackward |= (p1 == p2 && p1.isBefore(to.c, from.c));
                    }
                }
                if (shouldAddForward) {
                    from.nextNodes.add(to);
                }
                if (shouldAddBackward) {
                    from.prevNodes.add(to);
                }

                if (shouldAddForward && shouldAddBackward) {
                    from.bidirectedNodes.add(to);
                }
            }
        }
    }
}
