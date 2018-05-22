import java.util.ArrayList;
import java.util.Collections;

public class ResultConstructor {

    public static void dfs(GraphNode node, ArrayList<Class> ans, boolean[] visited) {
        if (visited[node.id]) {
            return;
        }
        visited[node.id] = true;
        for (GraphNode next : node.nextNodes) {
            dfs(next, ans, visited);
        }
        ans.add(node.c);
    }

    public static ArrayList<Class> constructResult(ArrayList<GraphNode> nodes) {
        Collections.shuffle(nodes);
        ArrayList<Class> ans = new ArrayList<>();
        boolean[] visited = new boolean[nodes.size()];
        for (GraphNode node : nodes) {
            dfs(node, ans, visited);
        }
        Collections.reverse(ans);
        return ans;
    }

}
