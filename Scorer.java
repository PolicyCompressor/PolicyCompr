import java.util.ArrayList;

public class Scorer {
    static long getScore(ArrayList<Class> classes)
    {
        long ans = 0;
        for (Class c : classes)
        {
            ans += c.size;
        }
        return ans;
    }
}
