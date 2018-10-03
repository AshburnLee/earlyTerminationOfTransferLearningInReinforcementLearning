package myProj;

import BlockDude.BlockDudeTransferLearning_UTILS;
import TransferLearning.TLLinearVFA;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;

import java.io.File;

public class App_myBlockDude {

    public static void main(String[] args) {

        /*CHANGE : MAPs, parentPath, optimal, interval.*/


        //parent path:
        String parentPath_learn0 = "/media/junhui/DATA/project_data_files/Experiment_paperSA_Source/";  //!!!!!!!!!!!!!
        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paperSA/";  //!!!!!!!!!!!!!

        //choose source and target maps
        BlockDudeState BDStateSource = BlockDudeTransferLearning_UTILS.initialState_2;  //!!!!!!!!!!!
        BlockDudeState BDStateTarget = BlockDudeTransferLearning_UTILS.initialState_3_1;   //!!!!!!!!!!!

        //clear files:
        BlockDudeTransferLearning_UTILS.delete(new File(parentPath_learn0));
        BlockDudeTransferLearning_UTILS.delete(new File(parentPath));

        //Where to store return.txt
        String outputPathTarget_noTransfer =  parentPath_learn0 + "outputTarget_noTransfer/";

        //
        //
        //LEARN Target only Transfer zero
        int numTime = 0;
        while (numTime < 40) {


            myBlockDudeTransferLearning target = new myBlockDudeTransferLearning(BDStateTarget, 8);
            int nEpochs2 = 1;
            int nEpisodes2 = 500;

            TLLinearVFA sourceVFA2 = target.PerformLearningAlgorithm(
                    outputPathTarget_noTransfer, 2, nEpochs2, nEpisodes2, null);

            numTime += 1;
        }


        //
        //
        //LEARN non-zero E
        int optimal = 30;  //max effective num E learned from source!!!!!!!!!
        int interval = 2;  //!!!!!!!!!

        int numRun = 40;

        /**
         * loop execution
         */
        for(int i = 0; i<=optimal; i+=interval) {


            String outputSource = parentPath + "E_" + i + "/outputSource";
            String outputTarget = parentPath + "E_" + i + "/outputTarget";

            int numTime_non0 = 0;
            while (numTime_non0 < numRun) {

                //learn source
                myBlockDudeTransferLearning source = new myBlockDudeTransferLearning(BDStateSource,8);
                int nEpochs1 = 1;
                int nEpisodes1 = i;

                TLLinearVFA source1VFA = source.PerformLearningAlgorithm(
                        outputSource,2, nEpochs1, nEpisodes1, null);


                //transfer to target
                myBlockDudeTransferLearning target = new myBlockDudeTransferLearning(BDStateTarget,8);
                int nEpochs2 = 1;
                int nEpisodes2 = 500;

                TLLinearVFA sourceVFA2 = target.PerformLearningAlgorithm(
                        outputTarget,2, nEpochs2, nEpisodes2, source1VFA);

                numTime_non0 += 1;

            }


        }
    }
}
