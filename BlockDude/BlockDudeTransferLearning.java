package BlockDude;

import TransferLearning.*;
import TransferLearning.TransferLearning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

import burlap.behavior.functionapproximation.sparse.LinearVFA;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;   //tile coding
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.learningrate.ConstantLR;
import burlap.behavior.policy.EpsilonGreedy;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.LearningAgent;
import burlap.behavior.singleagent.learning.tdmethods.QLearning;
import burlap.behavior.singleagent.learning.tdmethods.SarsaLam;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam; //vfa
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
import plot.Plot;

public class BlockDudeTransferLearning implements TransferLearning{
	
	/** BlockDudeTransferLearning variables */
	BlockDude bd;
	OOSADomain domain;
	RewardFunction rf;
	TerminalFunction tf;
	StateConditionTest goalCondition;
	State initialState;
	HashableStateFactory hashingFactory;
	SimulatedEnvironment env;
	int nTilings;

	private static ArrayList<Double> sumReward = new ArrayList<>();  // global variable!!!
	/** ************************************ */
	
	//bastard constant
//	static boolean episode480_1 = false;
//	static boolean episode480_2 = false;
	
	/** Constructor
	 * @param state0
	 * @param numTilings
	 * */
	public BlockDudeTransferLearning(BlockDudeState state0, int numTilings) {//TODO
		
		int [][] currMap = state0.map.map;
		int x = currMap.length;
		int y = currMap[0].length;
		
		tf = new BlockDudeTF();     //init a terminal function
		bd = new BlockDude(x,y);    //init a dude
		bd.setTf(tf);
		
		goalCondition = new TFGoalCondition(tf);
		
		domain = bd.generateDomain();
		
		//REMEMBER: the map has to forbid the agent to put a block outside the world
		initialState = state0;
		
		//instantiate the HashableStateFactory
		hashingFactory = new SimpleHashableStateFactory();
		
		//creation of the actual environment with which our agent will interact
		env = new SimulatedEnvironment(domain, initialState);
		
		nTilings = numTilings;
	}
	
	/** 
	 * function to merge in one the various examples available on 
	 * the web site http://burlap.cs.brown.edu/tutorials/bpl/p4.html#sarsa
	 * @param outputPath
	 * @param algorithmChoice
	 * @param nEpisodes
	 * @param nEpochs
	 * @param sourceVFAs
	 */
	public TLLinearVFA PerformLearningAlgorithm(String outputPath, int algorithmChoice, int nEpochs, int nEpisodes, TLLinearVFA...sourceVFAs){
		
		File outputFile = new File(outputPath);
		outputFile.mkdirs();
		
		TLLinearVFA vfa = null;
		
		State stateToPrint = initialState;
		
		/* Declaration of output files of diverse purposes */
		//writer for the Action-Value of a particular state
		PrintWriter osAV = null;
		try {
			osAV = new PrintWriter(outputPath+"/ActionValueFunction.txt");  //file
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		
		//writer for the Return
		PrintWriter osR = null;
		try {
			osR = new PrintWriter(outputPath+"/Return.txt");   //file
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

		/* ******************************************** */


		/* IN EACH EPOCH*********/
		for(int epoch=0; epoch<nEpochs; ++epoch){	
			
			//Learning Parameters
			double gamma = 0.999;
			double alpha = 0.1;
			double alphaTilingWise = 0;//this is the actual alpha you want to associate with the tilings in order to spread the general alpha
			double lambda = 0.9;
			double qInit = 0.; //Q-values initialisation
			
			GradientDescentSarsaLam agent = null;
			String writeString = "";
			
			if(algorithmChoice == 2){
//				System.out.println("You have chosen Sarsa(lambda) for Transfer Learning");
				
				TLTileCodingFeatures tileCoding = BlockDudeTransferLearning_UTILS.CreateTransferLearningTCFeatures(initialState, nTilings); //(2)!!!!!!!!!!!!!!!!!!

				 /*  param tileCoding: feature database
				  *  return: the approximate value for any state. The linear combination of state features and weights.
				  */
				vfa = new TLLinearVFA(tileCoding);      //(3)value function approximation
				
				/* All the Transfer happens here
				* If sourceVFAs exits, transfer the knowledge then.
				* */
				if(sourceVFAs != null){
					//These two lines fix the problem with the input sourceVFAs when passed as a variable X = null
					if(sourceVFAs.length == 1 && sourceVFAs[0] == null)
						sourceVFAs = null;
					else{
					lambda = 0.9;
					
					for(TLLinearVFA sVFA : sourceVFAs) //loops over all the sources for transferring knowledge in parallel
						sVFA.transferVFA(vfa, null);         //!(4)transfer knowledge.!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
					
					//stateToPrint = BlockDudeTransferLearning_UTILS.initialState_2_hard_firstStep;
					}
				}



				alphaTilingWise = alpha/((TLTileCodingFeatures)vfa.sparseStateFeatures).numTIlings();
				
				agent = new GradientDescentSarsaLam(domain, gamma, vfa, alphaTilingWise, lambda);       //(5)!!!!!!!!!!!!!!!!!!!!!!!!!!
				
				//to comment in case of checking the optimal target policy trajectory
//				if(sourceVFAs != null){
//					BlockDudeTransferLearning_UTILS.PerformLearntPolicy(agent, env, outputPath, "transferredPolicy", -1);
//					agent.setLearningRate( new ConstantLR(alphaTilingWise));
//				}	
				
				writeString = "TL_";
			}else{
				System.out.println("Your algorithm choice (" + writeString + ") is not available. "
						+ "Please select one of the followings:"
						+ "\n - 0 for Q-Learning"
						+ "\n - 1 for Sarsa(lambda)");
				return null;
			}
			
			QProvider qp = (QProvider)agent;
			
			double epsilon = 0.1;//starting epsilon value from which we start lowering it
//			LoggingEpsilonGreedy lEpsGrdy = new LoggingEpsilonGreedy(agent, epsilon, outputPath, epoch);
//			agent.setLearningPolicy(lEpsGrdy);

			/* EpsilonGreedy():
			* This class defines a an epsilon-greedy policy over Q-values and requires a QComputable valueFunction to be specified.
			* With probability epsilon the policy will return a random action (with uniform distribution over all possible action).
			* With probability 1 - epsilon the policy will return the greedy action. If multiple actions tie for the highest Q-value,
			* then one of the tied actions is randomly selected.
			* */
			EpsilonGreedy lEpsGrdy = new EpsilonGreedy(agent, epsilon);   //(6)return a policy
			//setLearningPolicy: Sets which policy this agent should use for learning.
			agent.setLearningPolicy(lEpsGrdy);  // apply that policy

			/***** In one epoch, do the following****  */
			//one loop for each episode
			for(long episode = 0; episode < nEpisodes; episode++){
				
				/**
				 * Trade off between Exploration and Exploitation
				 * kill exploration after a specified number of episodes
				 * */
				double explorationRatio = 15./20;      //the fraction of episodes we do with normal exploration
				double lowerExplorationRatio = 4./20;  //the fraction of episodes we do lowering exploration

				double stopExp = (explorationRatio+lowerExplorationRatio)*nEpisodes;  //the episode when we stop the exploration
				double startExp = nEpisodes*explorationRatio;  //when the lowering starts to be applied
				double intervalEp = stopExp - startExp;  //interval of episodes where we decrease the value of epsilon

				if(episode>=(startExp) && episode <= stopExp){
					// The difference is the epsilon
					epsilon = epsilon * (stopExp - episode)/intervalEp;

					if(agent instanceof GradientDescentSarsaLam){
						GradientDescentSarsaLam agentQLearning = (GradientDescentSarsaLam)agent;
//						LoggingEpsilonGreedy newLEpsGrdy = new LoggingEpsilonGreedy(agentQLearning, epsilon, outputPath, epoch);
//						agentQLearning.setLearningPolicy(newLEpsGrdy);

//						newLEpsGrdy.setCurrEp((int)episode);

						EpsilonGreedy newLEpsGrdy = new EpsilonGreedy(agent, epsilon);
						agent.setLearningPolicy(newLEpsGrdy);
					}else{
						System.out.println("ERROR! You are trying to change the learning policy to an invalid agent!");
					}
				}else if(episode > stopExp){
					epsilon = 0.;

					GradientDescentSarsaLam agentQLearning = (GradientDescentSarsaLam)agent;
//					LoggingEpsilonGreedy newLEpsGrdy = new LoggingEpsilonGreedy(agentQLearning, epsilon, outputPath, epoch);
//					agentQLearning.setLearningPolicy(newLEpsGrdy);

//					newLEpsGrdy.setCurrEp((int)episode);

					EpsilonGreedy newLEpsGrdy = new EpsilonGreedy(agent, epsilon);
					agent.setLearningPolicy(newLEpsGrdy);
				}else {System.out.println("episode > stopExp");}
//					lEpsGrdy.setCurrEp((int)episode);
				
				//System.out.println(episode + "," + epsilon + ";");//epsilon check
				/** ***************************************************** */



				
				Episode e = agent.runLearningEpisode(env,80);   //object for storing the episode at each loop
				
				//PRINT EPISODE BY EPISODE
				e.write(outputPath + writeString + "_" + epoch + "_" + episode);
				
				if(outputPath != null)
					osR.println(episode + " " + e.discountedReturn(1) + " " + agent.getLastNumSteps());



				List<Double> rewardList = e.rewardSequence;
				sumReward.add(sum(rewardList));

				//reset environment for next learning
				env.resetEnvironment();
				
				if(episode % 10 == 0 || episode == nEpisodes-1){
					
					((TLTileCodingFeatures)vfa.sparseStateFeatures).setLearning(false);
					
					BlockDudeTransferLearning_UTILS.PerformLearntPolicy(agent, env, outputPath, "policyEvaluation.txt", episode);
					agent.setLearningRate( new ConstantLR(alphaTilingWise));
					agent.setLearningPolicy(lEpsGrdy);
					//agent.setLearningPolicy(new EpsilonGreedy(epsilon));
					
					((TLTileCodingFeatures)vfa.sparseStateFeatures).setLearning(true);
				}
				
//				if(episode == nEpisodes-1)
//					BlockDudeTransferLearning_UTILS.PerformLearntPolicy(agent, env, outputPath, "finalPolicy", -1);
			}

//			if(vfa.sparseStateFeatures instanceof TLTileCodingFeatures)
//				System.out.println(((TLTileCodingFeatures)vfa.sparseStateFeatures).getTransferCovering());

		}
		//close files
		osAV.close();
		osR.close();
		
		return vfa;
		
	}
	
	/** tool for visualising the result of the learning */
	public void visualize(String outputPath){
		System.out.println(this.bd.getMaxx() + " "+this.bd.getMaxy());
		Visualizer v = BlockDudeVisualizer.getVisualizer(this.bd.getMaxx(), this.bd.getMaxy());
		new EpisodeSequenceVisualizer(v, domain, outputPath);
	}
	/** *********************************************** */
	
	/** Transfer Learning from @param this to @param target */
//	public void transferLearning(BlockDudeTransferLearning target, boolean visualizer){
//		
//		//learning process length
//		int nEpochs = 1;		//The number of epochs for a source task must be 1
//		int nEpisodes = 100;	
//		
//		//perform the learning of the source task
//		TLLinearVFA learntVFA = this.PerformLearningAlgorithm("", 2, nEpochs, nEpisodes, null);
//		
//		if(visualizer){
//			this.visualize("");
//		}
//		
//		return;
//	}
	public double sum(List<Double> list){
		double sum = 0;
		for (double i: list){
			sum += i;
		}
		return sum;
	}


	public static void main(String[] args) {
		
		System.out.println("Welcome to BlockDudeTransferLearning!");
		
		String outputPathSource1 = "BDTL/outputSource1/";
		String outputPathSource2 = "BDTL/outputSource2/";
		String outputPathSource3 = "BDTL/outputSource3/";
		String outputPathTarget = "BDTL/outputTarget/";
		String outputPathNoTransferTarget = "BDTL/outputNoTransferTarget/";
		
		BlockDudeTransferLearning_UTILS.delete(new File(outputPathSource1));
		BlockDudeTransferLearning_UTILS.delete(new File(outputPathSource2));
		BlockDudeTransferLearning_UTILS.delete(new File(outputPathSource3));
		BlockDudeTransferLearning_UTILS.delete(new File(outputPathTarget));
		BlockDudeTransferLearning_UTILS.delete(new File(outputPathNoTransferTarget));		





		/* source 1 */
		BlockDudeTransferLearning source1 = new BlockDudeTransferLearning(BlockDudeTransferLearning_UTILS.initialState_3_1, 8);

		//learning process length
		int nEpochs1 = 1;
		int nEpisodes1 = 100;

		//learning algorithms here
		TLLinearVFA source1VFA = source1.PerformLearningAlgorithm(outputPathSource1, 2, nEpochs1, nEpisodes1, null);

		//run the visualiser
		source1.visualize(outputPathSource1);

//		Plot.callPlot("source-original", sumReward, 1,1);
//		Plot.callPlot("source-sliding", sumReward, 2,20);




//		/** TRANSFER FROM HERE */
//		/* source 2 */
//		BlockDudeTransferLearning source2 = new BlockDudeTransferLearning(BlockDudeTransferLearning_UTILS.initialState_2_L, 8);
//
//		//learning process length
//		int nEpochs2 = 1;
//		int nEpisodes2 = 40;
//
//		//learning algorithms here
//		TLLinearVFA source2VFA = source2.PerformLearningAlgorithm(outputPathSource2, 2, nEpochs2, nEpisodes2, source1VFA);
//
//		//run the visualiser
//		source2.visualize(outputPathSource2);
//
//
//
//
//
//		/** TRANSFER FROM HERE */
//		/* source 3 */
//		BlockDudeTransferLearning source3 = new BlockDudeTransferLearning(BlockDudeTransferLearning_UTILS.initialState_2_L2, 1);
//
//		//learning process length
//		int nEpochs3 = 1;
//		int nEpisodes3 = 100;
//
//		//learning algorithms here
//		TLLinearVFA source3VFA = source3.PerformLearningAlgorithm(outputPathSource3, 2, nEpochs3, nEpisodes3, source2VFA);
//
//		//run the visualiser
//		source3.visualize(outputPathSource3);





//		/** TRANSFER FROM HERE */
//		/* target */
//		BlockDudeTransferLearning target = new BlockDudeTransferLearning(BlockDudeTransferLearning_UTILS.initialState_1_xL, 1);
//
//		//learning process length
//		int nEpochsT = 1;
//		int nEpisodesT = 100;
//
//		//learning algorithms here
//		TLLinearVFA targetVFA = target.PerformLearningAlgorithm(outputPathTarget, 2, nEpochsT, nEpisodesT, source3VFA);
//
//		//run the visualiser
//		target.visualize(outputPathTarget);


		

//		/** TARGET TASK WITHOUT TRANSFER*/
//		BlockDudeTransferLearning targetNoTransfer = new BlockDudeTransferLearning(BlockDudeTransferLearning_UTILS.map4_OPS_0, 1);
//		//learning algorithms here
//     	TLLinearVFA notImportantVFA = targetNoTransfer.PerformLearningAlgorithm(outputPathNoTransferTarget,
//				2, nEpochsT, nEpisodesT, null);
//
//		//run the visualiser
//		targetNoTransfer.visualize(outputPathNoTransferTarget);

	}

}