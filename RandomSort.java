import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

public class RandomSort {
    static Random rnd = new Random();

    public static <E> void sort(ArrayList<E> a, Comparator<E> comparator)
    {
        Collections.sort(a, comparator);
        int prev = 0;
        for (int i = 0; i < a.size(); i++)
        {
            if (comparator.compare(a.get(i), a.get(prev)) != 0)
            {
                prev = i;
            }
            int t = prev + rnd.nextInt(i-prev+1);
            E k = a.get(t);
            a.set(t, a.get(i));
            a.set(i, k);
        }
    }
}
