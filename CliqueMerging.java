import java.util.ArrayList;
import java.util.Arrays;

public class CliqueMerging {

    public static ArrayList<Integer> getOptimalSeperation(boolean[][] blocked) {
        int n = blocked.length;
        boolean[] canTake = new boolean[1 << n];
        Arrays.fill(canTake, false);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (!blocked[i][j]) {
                    canTake[(1 << i) + (1 << j)] = true;
                }
            }
        }

        for (int mask = 0; mask < 1 << n; mask++) {
            if ((mask & (mask - 1)) == 0) {
                canTake[mask] = true;
                continue;
            }

            int lMask = mask & (mask - 1);
            int rMask = (lMask & (lMask - 1)) + (mask ^ lMask);
            canTake[mask] |= canTake[lMask] & canTake[rMask] & canTake[(mask ^ lMask) + (mask ^ rMask)];
        }

        int[] dp = new int[1 << n];
        int[] par = new int[1 << n];
        for (int m = 1; m < 1 << n; m++) {
            dp[m] = 1000;
            for (int s = m; s > 0; s = (s - 1) & m) {
                if (canTake[s] && dp[m] > dp[m - s] + 1) {
                    par[m] = s;
                    dp[m] = dp[m - s] + 1;
                }
            }
        }
        ArrayList<Integer> ans = new ArrayList<>();
        int last = (1 << n) - 1;
        while (last != 0) {
            ans.add(par[last]);
            last = last - par[last];
        }
        return ans;
    }


}
