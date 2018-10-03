package myProj;

import BlockDude.BlockDudeTransferLearning_UTILS;
import TransferLearning.TLLinearVFA;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;

import java.io.File;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static myProj.Algorithm_simulated_annealing.givenEAndReturnAAUsingThresholdAlg;



public class TestParallel {
    private static final int MYTHREADS = 30;

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(MYTHREADS);

        long startTime = System.currentTimeMillis();

        //read from this path parentPath
        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_TestParallel/"; //!!!!!!!!!!!!!!

        //collection of E and convert to ArrayList
        Integer[] Es = {2,  4, 6,  8,  10,  12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
//        ArrayList<Integer> episodes = new ArrayList<Integer>(Arrays.asList(Es));

        //1) randomly pick one from Es:
        int currenE;

        //2) launch a thread to create return.txt
        for (int i = 2; i <= 30; i+=2) {

            //2)
            Runnable workers = new Runner2(i);
            double currentAA1 = givenEAndReturnAAUsingThresholdAlg(parentPath, i, false);

            //3)
            executor.execute(workers);
        }
        //4)
        executor.shutdown();

        //5)  Wait until all threads are finish
        while (!executor.isTerminated()) {}

        long elapsedTime = (new Date()).getTime() - startTime;
        System.out.println(elapsedTime/1000 + " seconds to run");


    }

    public static class Runner2 implements Runnable {

        private String parentPath = "/media/junhui/DATA/project_data_files/Experiment_TestParallel/"; //!!!!!!!!!!!!!
        private BlockDudeState BDStateSource = BlockDudeTransferLearning_UTILS.initialState_2;
        private BlockDudeState BDStateTarget = BlockDudeTransferLearning_UTILS.initialState_3_1;

        //constructor passing parameters
        public Runner2(int E) {

            boolean file = new File(parentPath + "E_" + E + "/outputSource/return.txt").isFile();
            //if file does not exist, create it
            if(!file) {
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
                System.out.println("(._.): <File has been created, boss>");
            }
            else{
                System.out.println("(._.): <File is already there, boss>");
            }
        }

        @Override
        public void run() {
        }
    }

}
