import java.util.ArrayList;

public class StupidSolver {

    public long[] solve(Data data)
    {
        System.out.println("Stupid solver:");

        ArrayList<Class> result = new ArrayList<>();
        for (Policy p : data.policies)
        {
            for (Class c : p.classes)
            {
                result.add(c);
            }
        }

        return new Reducer().processReduce(result, data);
    }
}
