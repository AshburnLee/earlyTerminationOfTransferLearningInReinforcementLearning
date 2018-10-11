package myProj;

import burlap.domain.singleagent.blockdude.state.BlockDudeState;

import TransferLearning.TLLinearVFA;
import BlockDude.BlockDudeTransferLearning_UTILS;

import plot.Plot;

import java.io.IOException;
import java.util.ArrayList;
import java.io.File;

/**
 * this is used for finding how many actions an agent takes to reach the maximum rewards.
 * decide how many actions taken represents completely learning in the source.
 * @author junhui
 */
public class App_PlotSourceRewardOverEpisodes2FindOptimalE{


    /**
     * Change the outputSource, BDStateSource(map), nEpochs, nEpisode
     * @param args
     */
    public static void main(String[] args) {

        //plot the Episodes vs Reward of a source task to find the optimal episode
        String outputSource = "/media/junhui/DATA/project_data_files/Observe_E_exp_1/output/"; //!!!!!!!!


        BlockDudeTransferLearning_UTILS.delete(new File(outputSource));

        //chose maps:
        BlockDudeState BDStateSource = BlockDudeTransferLearning_UTILS.initialState_2; //!!!!!!!!!!!

        //perform reinforcement learning
        myBlockDudeTransferLearning source = new myBlockDudeTransferLearning(BDStateSource, 8);
        int nEpochs = 20;
        int nEpisodes = 200;

        TLLinearVFA sourceVFA = source.PerformLearningAlgorithm(
                outputSource, 2, nEpochs, nEpisodes,null);


        //set nEpochs1 as 20, and average it:
        ArrayList< ArrayList<Double> > container = null;

        ReadFiles rf = new ReadFiles();
        try{
            container = rf.ReadDataFromFiles(outputSource+"return.txt", 20,200);
        } catch (IOException e) {
            e.printStackTrace();
        }



        Plot.callPlot("Find the optimal episode for source task",
                "Reward over episodes",
                "Episodes","Reward",

                1,
                20,
                1,
                1,
                null,
                null,

                container.get(0),
                Plot.SmoothBySlide(container.get(1),20));

    }


}
