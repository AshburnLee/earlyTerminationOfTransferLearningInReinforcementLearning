package myProj;


import BlockDude.BlockDudeTransferLearning_UTILS;
import TransferLearning.TLLinearVFA;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import plot.Plot;

import java.io.File;
import java.io.IOException;
import java.util.*;

class Runner implements Runnable {

    private String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paperSA/";
    private BlockDudeState BDStateSource = BlockDudeTransferLearning_UTILS.initialState_2;
    private BlockDudeState BDStateTarget = BlockDudeTransferLearning_UTILS.initialState_3_1;

    /**
     * Constructor: accept a parameter
     * @param E: the number of Episodes learned in Source task
     */
    public Runner(int E) {

        boolean file = new File(parentPath + "E_" + E + "/outputSource/return.txt").isFile();

        //if file does not exist, create it
        if(!file) {
            System.out.println();
            System.out.println("(._.): <Creating file......>");
            //1). where to store return.txt
            String outputSource = parentPath + "E_" + E + "/outputSource";
            String outputTarget = parentPath + "E_" + E + "/outputTarget";

            //2). TL
            int numRun=0;
            while (numRun < 40) {

                myBlockDudeTransferLearning source = new myBlockDudeTransferLearning(BDStateSource,8);
                int nEpochs1 = 1;
                int nEpisodes1 = E;

                TLLinearVFA source1VFA = source.PerformLearningAlgorithm(
                        outputSource,2, nEpochs1, nEpisodes1, null);


                //transfer to target
                myBlockDudeTransferLearning target = new myBlockDudeTransferLearning(BDStateTarget,8);
                int nEpochs2 = 1;
                int nEpisodes2 = 500;

                TLLinearVFA sourceVFA2 = target.PerformLearningAlgorithm(
                        outputTarget,2, nEpochs2, nEpisodes2, source1VFA);

                numRun += 1;

            }
            System.out.println();
            System.out.println("(._.): <File has been created, boss>");
        }
        else {
            System.out.println();
            System.out.println("(._.): <File is already there, boss>");
        }

    }

    @Override
    public void run() {
    }
}

public class Algorithm_simulated_annealing {

    public Algorithm_simulated_annealing() {System.out.println("*******Simulated Annealing*******");}

    /**
     *
     * @param parentPath: experiment path, contains folders like 'E_2', 'E_4',...
     * @param episodes: the collection of Es
     */
    public void simulatedAnnealing(String parentPath,
                                   ArrayList<Integer> episodes,

                                   double temperature,
                                   double coolingRate) {

        //containers
        ArrayList<Integer> storeE = new ArrayList<>();
        ArrayList<Double> storeAA = new ArrayList<>();

        //START the algorithm,
        // init the temperature and cooling rate

        //randomly pick a initial solution:
        int currentE = episodes.get(new Random().nextInt(episodes.size()));
        //TODO
        Thread tCurrent = new Thread(new Runner(currentE));
        tCurrent.start();
        try {
            tCurrent.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        double currentAA = givenEAndReturnAAUsingThresholdAlg(parentPath, currentE, false);

        //store the pairs
        storeE.add(currentE);
        storeAA.add(currentAA);

        //counter
        int count = 0;

//         loop until the stop condition is met
        while(temperature > 1 ) {

            //show info
//            showArray(value);
            System.out.println("Temperature: " + temperature);
            System.out.println("current: " + currentAA);

            //randomly pick a neighbor solution and remove it from list
            int nextE = episodes.get(new Random().nextInt(episodes.size()));
            //TODO
            Thread tNext = new Thread(new Runner(nextE));
            tNext.start();
            try {
                tNext.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            double nextAA = givenEAndReturnAAUsingThresholdAlg(parentPath, nextE, false);

            //store the pairs
            storeE.add(nextE);
            storeAA.add(nextAA);

//            double next = value.get(new Random().nextInt(value.size()));
            System.out.println("next:    " + nextAA);

            //Update dict. remove key value pairs

            //the change of energy
            double delta = currentAA - nextAA;

            //acceptance process
            // if next is a good solution, accept the next,
            if(delta > 0)
                currentAA = nextAA;

                // if delta is not a good solution, calculate following below and
                // then accept the next
            else if( Math.exp(delta/temperature) > Math.random())
                currentAA = nextAA;

            temperature *= 1 - coolingRate;
            count += 1;

        }
        //storage
        System.out.println("**********RESULTS************");
        System.out.println("Sequence of the number of episodes learned in the source: ");
        System.out.println(storeE);
        System.out.println();
        System.out.println("Sequence of the corresponding energy(actions needed to reach the threshold): ");
        System.out.println(storeAA);
        System.out.println();

        //results
        System.out.println("The final energy: " + currentAA);
        int E = search(storeAA, storeE, currentAA);
        System.out.println("The final solution is: Learning " + E + " episodes in the source");
        System.out.println("searched "+count+" times");
    }


    /**
     * print elements of an arraylist
     * @param arr: arraylist
     */
    private static void showArray(ArrayList<Double> arr) {
        for(int i = 0; i < arr.size(); i++){
            System.out.print(" " + arr.get(i));
        }
        System.out.println();
    }

    /**
     * put two arraylists to a dictionary
     * @param value: arraylist for value
     * @param key: arraylist for key
     * @return constructed dict
     */
    private static Map<Integer, Double> arrayListToDict(ArrayList<Integer> key, ArrayList<Double> value) {
        Map<Integer, Double> dict = new HashMap<>();
        for (int i=0; i<key.size(); i++) {
            dict.put(key.get(i), value.get(i));
        }
        return dict;
    }

    private static int search(ArrayList<Double> AA, ArrayList<Integer> E, double findMe) {

        int findE = 0;
        for (int i=0; i<AA.size(); i++) {
            if (AA.get(i) == findMe) {
                findE = E.get(i);
                break;
            }
        }
        return findE;

    }

    /**
     * Input E,
     * output the corresponding AA, satisfying the threshold algorithm
     * used When all the return.txt exist,
     *
     * @param parentPath: existing file path
     * @param E: pick an E
     * @param check: plot or not
     * @return the corresponding AA
     */
    public static double givenEAndReturnAAUsingThresholdAlg(String parentPath, int E, boolean check) {
        //1). set parameters
        //int E = 12; //!!!!!!!!!!!!!!!!!!randomly choose
        int numFileInTXT = 40;  //!!!!!!!!!!!!!!!
        double threshold = -15.0;  //!!!!!!!!!!!!!!

        //1.5). learning and store data

        //2). read file
        String outputSource = parentPath + "E_" + E + "/outputSource/return.txt";
        String outputTarget = parentPath + "E_" + E + "/outputTarget/return.txt";

        //3). get date
        ArrayList<ArrayList<Double>> box = null;
        ReadFiles readFiles = new ReadFiles();

        //read file and get X and Y
        try{
            box = readFiles.targetAAPlusLastElemFromSourceANDR(
                    outputSource, outputTarget,
                    numFileInTXT, E,
                    numFileInTXT, 500
            );
        } catch(IOException e){
            e.printStackTrace();
        }

        //4). smooth the AA
        ArrayList<Double> newBoxGet1;
        newBoxGet1 = Plot.SmoothBySlide(box.get(1), 20);

        //5). got the AA
        double AA;
        AA = Algorithm_threshold.findTheRightAA(newBoxGet1, box.get(0), threshold);
        System.out.println("learning "+E+" episodes, the AA is: " + AA);

        //6). plotting for checking
        if(check) {
            Plot.callPlot("for SA use","learn "+E+" episodes",
                    "AA", "reward",
                    3, 20,1, 0, null,null,
                    box.get(0), box.get(1));
        }
        return AA;

    }
}

