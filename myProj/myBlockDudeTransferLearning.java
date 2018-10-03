package myProj;

import TransferLearning.*;
import TransferLearning.TransferLearning;
import BlockDude.BlockDudeTransferLearning_UTILS;

import java.io.*;
import java.util.*;


import burlap.behavior.functionapproximation.sparse.LinearVFA;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.learningrate.ConstantLR;
import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.behavior.valuefunction.QProvider;
import burlap.behavior.valuefunction.QValue;
import burlap.domain.singleagent.blockdude.BlockDude;
import burlap.domain.singleagent.blockdude.BlockDudeTF;
import burlap.domain.singleagent.blockdude.BlockDudeVisualizer;
import burlap.domain.singleagent.blockdude.state.BlockDudeAgent;
import burlap.domain.singleagent.blockdude.state.BlockDudeCell;
import burlap.domain.singleagent.blockdude.state.BlockDudeMap;
import burlap.domain.singleagent.blockdude.state.BlockDudeState;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.auxiliary.stateconditiontest.TFGoalCondition;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.state.State;
import burlap.mdp.core.state.vardomain.VariableDomain;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.statehashing.HashableStateFactory;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;


public class myBlockDudeTransferLearning implements TransferLearning{

    BlockDude bd;
    OOSADomain domain;
    State initialState;
    TerminalFunction tf;
    StateConditionTest goalCondition;
    HashableStateFactory hashingFactory;
    SimulatedEnvironment env;
    int nTilings;

    PrintWriter osR = null;

    ArrayList<Double> sumReward = new ArrayList<>();  // global variable!!!
    ArrayList<Double> sumRewardSource = new ArrayList<>();  // global variable!!!
    ArrayList<Double> sumRewardTarget = new ArrayList<>();  // global variable!!!

    /**
     * Constructor
     * @param state0:  map states
     * @param numTilings:  number of tiles, for creating tile coding features
     */
    public myBlockDudeTransferLearning(BlockDudeState state0, int numTilings){
        // current map
        int [][] currMap = state0.map.map;
        int x = currMap.length;
        int y = currMap[0].length;

        tf = new BlockDudeTF();
        bd = new BlockDude(x,y);
        bd.setTf(tf);

        goalCondition = new TFGoalCondition(tf);

        domain = bd.generateDomain();
        initialState = state0;
        hashingFactory = new SimpleHashableStateFactory();

        env = new SimulatedEnvironment(domain, initialState);

        nTilings = numTilings;

    }

    /**
     *
     * @param outputPath
     * @param algorithmChoice
     * @param nEpochs
     * @param nEpisodes
     * @param sourceVFAs: the value function approximation of the previous source(task)
     * @return : tile coding linear value function approximation
     */
    public TLLinearVFA PerformLearningAlgorithm(String outputPath,
                                                int algorithmChoice,
                                                int nEpochs,
                                                int nEpisodes,
                                                TLLinearVFA...sourceVFAs){

        double gamma = 0.99;
        double lambda = 0.9;
        double alphaTilingWise = 0;
        double alpha = 0.1;
        GradientDescentSarsaLam agent = null;





        File outputFile = new File(outputPath);   //output path
        outputFile.mkdirs();     //create output file;

        TLLinearVFA vfa = null;   //value function approximation

        /* output files declaration */
        // writer for the action value function
        PrintWriter osAV = null;
//        PrintWriter osR = null;


//        File f = new File(outputPath + "/return.txt");
//        try{
//            osR = new PrintWriter(new BufferedWriter(new FileWriter(f, true)));
//        } catch (IOException e){
//            e.printStackTrace();
//        }


        try{
            osAV = new PrintWriter(outputPath + "/ActionValueFucntion.txt");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        // writer for the Return
        try{
//            osR = new PrintWriter(outputPath + "/return.txt");
            osR = new PrintWriter(new BufferedWriter(new FileWriter(outputPath + "/return.txt", true)));
        } catch(IOException e2){
            e2.printStackTrace();
        }





        /* For each iteration */
        for(int epoch = 0; epoch<nEpochs; epoch++){

            // this snippet return an Agent
            if(algorithmChoice == 2){

                TLTileCodingFeatures tileCoding = BlockDudeTransferLearning_UTILS.CreateTransferLearningTCFeatures(
                        initialState, nTilings);

                vfa = new TLLinearVFA(tileCoding); // use the features to approximate the VF.

                /*
                * All transfer happens here
                * */
                if(sourceVFAs != null){
                    if(sourceVFAs.length == 1 && sourceVFAs[0] == null) {
                        sourceVFAs = null;
                    }else{
                        lambda = 0.9;
                        for (TLLinearVFA svfa : sourceVFAs)
                            svfa.transferVFA(vfa, null);
//                        System.out.println("transferred...");
                    }
                }

                alphaTilingWise = alpha/((TLTileCodingFeatures)vfa.sparseStateFeatures).numTIlings();

                agent = new GradientDescentSarsaLam(domain,gamma,vfa, alphaTilingWise, lambda); //!!!


            }else{
                System.out.println("your algorithm choice is not available" );
                return null;
            }

            QProvider qp = (QProvider)agent;

            double epsilon = 0.1;

            EpsilonGreedy lEpsGrdy = new EpsilonGreedy(agent, epsilon); //return a policy
            agent.setLearningPolicy(lEpsGrdy);  //apply that policy





            /* In each epoch , do the following */
            for(long episode = 0; episode < nEpisodes; episode++){

                /*
                 Trade of  between exploitation and exploration
                 */
                double explorationRatio = 15./20;
                double lowerExplorationRatio = 4./20;

                double stopExp = (explorationRatio + lowerExplorationRatio)*nEpisodes;
                double startExp = nEpisodes * explorationRatio;
                double intervalEp = stopExp - startExp;

                if(episode>=(startExp) && episode<=stopExp){
                    epsilon = epsilon * (stopExp - episode)/intervalEp;

                    if(agent instanceof GradientDescentSarsaLam) {
                        GradientDescentSarsaLam agentQlearning = (GradientDescentSarsaLam) agent;

                        EpsilonGreedy newLEpsGrdy = new EpsilonGreedy(agent, epsilon);
                        agent.setLearningPolicy(newLEpsGrdy);  //apply that policy
//                        System.out.println("Episode: "+episode + " " +"Epoch: "+ epoch + "");
                    }else{
                        System.out.println("ERROR. you are trying to change the learning policy to an invalid agent");
                    }
                }else if(episode > stopExp){
                    epsilon = 0.;
                    GradientDescentSarsaLam agentQLearning = (GradientDescentSarsaLam)agent;

                    EpsilonGreedy newLEpsGrdy = new EpsilonGreedy(agent, epsilon);
                    agent.setLearningPolicy(newLEpsGrdy);
                }else{
//                    System.out.println("Episode: "+episode + " " +"Epoch: "+ epoch + "");
                }



                Episode e = agent.runLearningEpisode(env, 50);


                //Print episode by episode
//                e.write(outputPath + "__"+ epoch + "__" + episode);

                //write record into file osR
                if (outputFile != null){
                    osR.append(episode + " " + e.discountedReturn(1) + " " + agent.getLastNumSteps());
                    osR.println();
                }

                /*!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                * get the episode reward
                * ***/
                List<Double> rewardList = e.rewardSequence;
                sumRewardSource.add(sum(rewardList));
                // end getting reward!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//                if (sourceVFAs == null){
//                    List<Double> rewardListSource = e.rewardSequence;
//                    sumRewardSource.add(sum(rewardListSource));
//                }else if(sourceVFAs != null){
//                    List<Double> rewardListTarget = e.rewardSequence;
//                    sumRewardTarget.add(sum(rewardListTarget));
//                }
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!



                env.resetEnvironment();

                if (episode % 10 == 0 || episode == nEpisodes-1){
                    ((TLTileCodingFeatures)vfa.sparseStateFeatures).setLearning(false);

                    BlockDudeTransferLearning_UTILS.PerformLearntPolicy(
                            agent, env, outputPath, "policyEvaluation.txt", episode);

                    agent.setLearningRate( new ConstantLR(alphaTilingWise));
                    agent.setLearningPolicy(lEpsGrdy);
                    //agent.setLearningPolicy(new EpsilonGreedy(epsilon));

                    ((TLTileCodingFeatures)vfa.sparseStateFeatures).setLearning(true);
                }

            }

        }
        osAV.close();
        osR.close();

        return vfa;
    }

    /**
     * show blockDude visualizer
     * @param outputPath: output path
     */
    public void visualize(String outputPath){

        Visualizer v = BlockDudeVisualizer.getVisualizer(this.bd.getMaxx(), this.bd.getMaxy());
        new EpisodeSequenceVisualizer(v, domain, outputPath);
    }


    public double sum(List<Double> list){
        double sum = 0;
        for (double i: list){
            sum += i;
        }
        return sum;
    }

}
