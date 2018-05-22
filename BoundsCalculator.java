import java.util.HashSet;


public class BoundsCalculator {

    public long[] getBounds(Data data)
    {
        HashSet<Class> visited = new HashSet<>();
        long lb = 0, ub = 0;
        for (Policy p : data.policies)
        {
            for (Class c : p.classes)
            {
                ub += c.size;
                if (visited.contains(c))
                {
                    continue;
                }
                lb += c.size;
                visited.add(c);
            }
        }
        return new long[]{lb, ub};
    }
}
