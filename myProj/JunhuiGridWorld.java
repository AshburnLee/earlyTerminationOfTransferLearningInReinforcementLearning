package myProj;

import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.auxiliary.StateReachability;
import burlap.behavior.singleagent.auxiliary.performance.LearningAlgorithmExperimenter;
import burlap.behavior.singleagent.auxiliary.performance.PerformanceMetric;
import burlap.behavior.singleagent.auxiliary.performance.TrialMode;
import burlap.behavior.singleagent.auxiliary.valuefunctionvis.ValueFunctionVisualizerGUI;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.LearningAgentFactory;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.ValueFunction;
import burlap.domain.singleagent.gridworld.GridWorldDomain;
import burlap.domain.singleagent.gridworld.GridWorldTerminalFunction;
import burlap.domain.singleagent.gridworld.GridWorldVisualizer;
import burlap.domain.singleagent.gridworld.state.GridAgent;
import burlap.domain.singleagent.gridworld.state.GridLocation;
import burlap.domain.singleagent.gridworld.state.GridWorldState;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.FactoredModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import plot.Plot;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class JunhuiGridWorld{

    GridWorldDomain gwd;
    TerminalFunction tf;
    State initialState;

    SADomain domain;
    HashableStateFactory hashFactory;
    SimulatedEnvironment env;
    ArrayList<Double> sumReward = new ArrayList<>();
    int mEpisodes;

    public JunhuiGridWorld(int nEpisodes){
        mEpisodes = nEpisodes;
        gwd = new GridWorldDomain(11,11);
        gwd.setMapToFourRooms();
        tf = new GridWorldTerminalFunction(10,10);
        gwd.setTf(tf);
        domain = gwd.generateDomain();

        initialState = new GridWorldState(new GridAgent(0,0), new GridLocation(10, 10, "Exit"));
        hashFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);
    }

    /* Q learning implementation */
    public void qLearning(String outpath) {
        LearningAgent agent = new QLearning(domain, 0.99, hashFactory, 0.0, 1.0);

        // run leanring for 10 episodes:
        for (int i=0;i<mEpisodes;i++){
            Episode e = agent.runLearningEpisode(env);
            e.write(outpath + "QL" + i);
            System.out.println(i + ": " + e.maxTimeStep());
            System.out.println(i + ": " + e.actionSequence);
            System.out.println(i + ": " + e.rewardSequence);
            System.out.println();

            List<Double> rewardList = e.rewardSequence;  //here is the reward Sequence.
//            System.out.println("Reward in this episode: " + sum(rewardList));
            sumReward.add(sum(rewardList)); //record total rewards for each episode

            //reset env for the next episod
            env.resetEnvironment();
        }

        // visualize all the values in every states.
        visulizeValueFunction((ValueFunction)agent, new GreedyQPolicy((QProvider) agent));
    }

    /* visualize all the values in every states. */
    public void visulizeValueFunction(ValueFunction valueFunction, Policy p){
        List<State> allStates = StateReachability.getReachableStates(initialState, domain, hashFactory);
        ValueFunctionVisualizerGUI gui = GridWorldDomain.getGridWorldValueFunctionVisualization(allStates, 11,11,valueFunction, p);
        gui.initGUI();
    }

    /* Sarsa Learning implementation */
    public void SarsaLearning(String outpath){
        LearningAgent agent = new SarsaLam(domain, 0.99, hashFactory, 0.0, 0.5, 0.3);

        //run 10 episodes
        for (int i=0;i<10;i++){
            Episode e = agent.runLearningEpisode(env);
            e.write(outpath + "Sarsa" + i);
            System.out.println(i+ ": " + e.maxTimeStep());

            //reset
            env.resetEnvironment();
        }
    }

    /* visualize the learning process */
    public void visualize(String outpath){
        Visualizer v = GridWorldVisualizer.getVisualizer(gwd.getMap());
        new EpisodeSequenceVisualizer(v, domain, outpath);
    }

    public double sum(List<Double> list){
        double sum = 0;
        for (double i: list){
            sum += i;
        }
        return sum;
    }


    /* Entry */
    public static void  main(String[] args){
        System.out.println("This is a test of a Grid world");
        JunhuiGridWorld example = new JunhuiGridWorld(100);

        String outpath = "JunhuiGW_outpath/";

        System.out.println("Q-Learning: ");
        example.qLearning(outpath);


//        System.out.println("Sarsa Learning: ");
//        example.SarsaLearning(outpath);

        example.visualize(outpath);

//        Plot.callPlot("Line-original", example.sumReward,  1,1);
//        Plot.callPlot("Line-sliding", example.sumReward,  2,20);
        return;
    }
}
