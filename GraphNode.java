import java.util.ArrayList;
import java.util.Arrays;

public class GraphNode {
    int id;
    Class c;
    ArrayList<Policy> policies;
    double cost;

    public GraphNode(int id, Class c, double cost, ArrayList<Policy> policies) {
        this.id = id;
        this.c = c;
        this.policies = new ArrayList<>(policies);
        this.cost = cost;
        nextNodes = new ArrayList<>();
        bidirectedNodes = new ArrayList<>();
        prevNodes = new ArrayList<>();
    }

    public GraphNode(int id, Class c, double cost, Policy... policies) {
        this(id, c, cost, new ArrayList<>(Arrays.asList(policies)));
    }

    ArrayList<GraphNode> nextNodes;
    ArrayList<GraphNode> prevNodes;
    ArrayList<GraphNode> bidirectedNodes;
}
