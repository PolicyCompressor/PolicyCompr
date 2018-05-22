import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Runner {

    public void runEqualSizeRandomTest()
    {
        Data data = new Data(new Parameters());
        long[] minStupid = new StupidSolver().solve(data);
        long[] minAll = new SCSFinder().solve(data);
        long[] minMJ =  new MajorityMerge().solveMajorityMerge(data);
        long[] minANS = new AllOrNothingSolver().solveSimple(data);
        long[] minANG = new AllOrNothingSolver().solveWithGreedyGluing(data);
        long[] minANC = new AllOrNothingSolver().solveWithCliqueSharing(data);
        long[] minANCGluing = new AllOrNothingSolver().solveWithCliqueSharingWithGreedyGluing(data);
        long[] minGluing = new AllOrNothingSolver().solveJustGreedyGluing(data);
        long[] minCl =  new CliqueSharingSolver().solve(data);
        long[] minClGluing = new CliqueSharingSolver().solveWithGreedyGluing(data);
        long[] bounds = new BoundsCalculator().getBounds(data);



        f.println(bounds[0]+"--"+bounds[1]+"  | " + (minStupid[0] - bounds[0]) + " " + (minAll[0] - bounds[0]) +" "+(minMJ[0] - bounds[0]) +" "+(minANS[0]-bounds[0]) +" "+(minANG[0]-bounds[0]) + " "+(minANC[0]-bounds[0])+ " "+(minANCGluing[0] - bounds[0])+ " "+(minGluing[0] - bounds[0])+" "+(minCl[0] - bounds[0])+" "+(minClGluing[0] - bounds[0])+
                                            " | " + (minStupid[1] - bounds[0]) + " " + (minAll[1] - bounds[0]) +" "+(minMJ[1] - bounds[0]) +" "+(minANS[1]-bounds[0]) +" "+(minANG[1]-bounds[0]) + " "+(minANC[1]-bounds[0])+ " "+(minANCGluing[1] - bounds[0])+ " "+(minGluing[1] - bounds[0])+" "+(minCl[1] - bounds[0])+" "+(minClGluing[1] - bounds[0]));
        f.flush();


    }


    static PrintWriter f;


    public void runAll() {
        for (int i = 0; i < 100; i++) {
            runEqualSizeRandomTest();
        }
        //printStat();
        f.flush();
    }

    public static void main(String[] args) throws FileNotFoundException {
            f = new PrintWriter("ans_dyn.txt");
            new Runner().runAll();
            f.close();
    }
}
