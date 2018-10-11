package myProj;

import BlockDude.BlockDudeTransferLearning_UTILS;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import plot.Plot;

import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.domain.singleagent.blockdude.BlockDude;
import burlap.domain.singleagent.blockdude.BlockDudeTF;
import burlap.domain.singleagent.blockdude.BlockDudeVisualizer;
import burlap.domain.singleagent.blockdude.state.BlockDudeAgent;
import burlap.domain.singleagent.blockdude.state.BlockDudeCell;
import burlap.domain.singleagent.blockdude.state.BlockDudeMap;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.domain.singleagent.blockdude.BlockDudeLevelConstructor;


import org.jfree.ui.RefineryUtilities;

import java.io.File;
import java.util.*;


/**
 * testing for blockdude
 * @author junhui
 */
public class JunhuiBlockDude {

    BlockDude bd;
    OOSADomain domain;
    HashableStateFactory hashFactory;
    SimulatedEnvironment env;
    TerminalFunction tf;
    State initialState;
    StateConditionTest goalCondition;
    ArrayList<Double> sumReward = new ArrayList<>(); //store Reward received in each episode
    int mEpisods;

    public JunhuiBlockDude(int nEpisods){
        mEpisods = nEpisods;
        bd = new BlockDude();
        hashFactory = new SimpleHashableStateFactory();

        domain = bd.generateDomain();

        /* create environment with movable blocks */
        int[][] MAP = {{1,1,1,1,1,1,1,1,0,0},
                {1,0,0,0,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,0,0,1,1,1,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,0,0,1,0,0,0,0,0,0},
                {1,1,1,1,1,1,1,1,0,0}};
        initialState = new BlockDudeState(new BlockDudeAgent(8,4,0,false),
                new BlockDudeMap(MAP),
                new BlockDudeCell(8,1, BlockDude.CLASS_EXIT, "Exit"),
                new BlockDudeCell(6,4, BlockDude.CLASS_BLOCK, "Block"));


        // try different block state


        env = new SimulatedEnvironment(domain, BlockDudeTransferLearning_UTILS.initialState_3);

    }

    public void qLearning(String output) {
        LearningAgent agent = new QLearning(domain, 0.99, hashFactory, 0.0, 1.0);


        //run learning 50 episodes
        for (int i=0;i<mEpisods;i++){
            Episode e = agent.runLearningEpisode(env); // 2nd param: maxsteps
            e.write(output + "QL" + i);
            System.out.println(i + ": \t" + e.maxTimeStep());

            System.out.println(i + ": " + e.actionSequence);
            System.out.println(i + ": " + e.rewardSequence);

//            System.out.println(i + ": " + e.stateSequence);
            System.out.println();

            List<Double> rewardList = e.rewardSequence;  //here is the reward Sequence.
//            System.out.println("Reward in this episode: " + sum(rewardList));
            sumReward.add(sum(rewardList));

            env.resetEnvironment();
        }

//        System.out.println(sumReward + " " + sumReward.size());
//        System.out.println(env.getClass());
    }

    public void sarsaLearning(String output) {
        LearningAgent agent = new SarsaLam(domain, 0.99, hashFactory, 0.0, 1.0, 0);


        //run learning 50 episodes
        for (int i=0;i<mEpisods;i++){
            Episode e = agent.runLearningEpisode(env); // 2nd param: maxsteps
            e.write(output + "QL" + i);

            System.out.println(i + ": \n" + e.maxTimeStep());


            List<Double> rewardList = e.rewardSequence;  //here is the reward Sequence.
//            System.out.println("Reward in this episode: " + sum(rewardList));
            sumReward.add(sum(rewardList));

            env.resetEnvironment();
        }

//        System.out.println(sumReward + " " + sumReward.size());
//        System.out.println(env.getClass());
    }

    public void visualize(String output){
        Visualizer v = BlockDudeVisualizer.getVisualizer(10, 10);
        new EpisodeSequenceVisualizer(v, domain, output);
    }


    public double sum(List<Double> list){
        double sum = 0;
        for (double i: list){
            sum += i;
        }
        return sum;
    }


    /**
     * Entry
     * @param args: parameter list
     */
    public static void main(String[] args){

        System.out.println("check points...");
        String output = "JunhuiBD_output/";
        BlockDudeTransferLearning_UTILS.delete(new File(output));

        JunhuiBlockDude example = new JunhuiBlockDude(100);

        example.qLearning(output);
//        example.sarsaLearning(output);
        example.visualize(output);
        Plot.callPlot("Line-original", "Accumulated actions over reward","Accumulated actions","Reward",
                1,1, 0,0, null, example.sumReward);
        Plot.callPlot("Line-sliding", "Accumulated actions over reward","Accumulated actions","Reward",
                2,20,0,0,null, example.sumReward);
//        Plot.callPlot("Line-averaging", example.sumReward,  3,5);

//        System.out.println(18/5*5);

    }

}
