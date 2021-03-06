# EarlyTerminationOfTransferLearningInReinforcementLearning

## Summary
Machine learning algorithms tend to consume lots of time and computational resources to complete tasks. 
So finding methods to reduce the execution time and the computational resources consumption is important for the development in this area. 
Some effective methods have been built to optimize it. For instance in the hardware level, multi-core technology of CUP and 
the CUDA of Nvidia GPU have been widely used when working with computation intense tasks. In the system level, 
Hadoop is a popular architecture used in distributed computing systems. In the software level, transfer learning has made huge success. 
The background of this project is using transfer learning to reinforcement learning. And it seems that agents 
do not have to learn in the source task completely.

Therefore the goal of this project is finding a general method to early terminate the learning processes in 
the source tasks of reinforcement learning problems. So that the consumption of resources can be reduced from unnecessary executions.

## How to use these code
1. Download the IDE for java

[Eclipse](https://www.eclipse.org/downloads/) or [intelliJ](https://www.jetbrains.com/idea/download/#section=linux) are recommended. The advantage of using an IDE is that [maven](https://maven.apache.org/download.cgi) is built-in, and users do not have to download it and learn the command line if users are new to maven. 

2. Create a maven project and configure it

Create a maven project. After the project is created, find the pom.xml. add the following in ```<dependencies></dependencies>.```
```HTML
    <dependency>
      <groupId>edu.brown.cs.burlap</groupId>
      <artifactId>burlap</artifactId>
      <version>3.0.0</version>
    </dependency>

    <dependency>
      <groupId>org.jfree</groupId>
      <artifactId>jfreechart</artifactId>
      <version>1.0.19</version>
    </dependency>
```
By doing this, BURLAP and jFreeChart can be used in users projects. Now 

3. Put folders in user's project

Navigate to ```src->main->java```. Put all folders except ```project_data_files``` in here. 
Put ```project_data_files``` outside of this maven project. The reason to do this is because the IDE may get crushed when the size of this data files increase.

Suppose users put this folder in ```/yourPath/project_data_files```. While in my project all the data path are given as: ```/media/junhui/DATA/project_data_files/.../```in all java files.
Therefore users have to change all ```/media/junhui/DATA/project_data_files/``` to ```~/yourPath/project_data_files```.

For instance, in the file *App_myBlockDude.java*, one path is given like this: ```String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paperSA/";```.
users have to change it to ```String parentPath = "/yourPath/project_data_files/Experiment_paperSA/";```. Change all the path like this.

Now users can create a *HelloWorld.java* program for testing.If everything went well, this testing should run successfully. 

4. Code information

In the next section, all the java files are briefly described. And for more information, read the comment of the specific java file.

Bare in mind: change parameters accordingly .
 

## Description of folders and files:
### plot
- *Plot.java*: the class for results visualization.

### myProj
- *Algorithm_area.java*: used for calculate areas enclosed by the Accumulate Actions of the starting point and the Accumulate Actions of the vertical threshold.
- *Algorithm_simulated_annealing.java*: the implementation of simulated annealing.
- *Algorithm_threshold.java*: the implementation of threshold algorithm.
- *App_Areas_Alg.java*: Application of areas algorithm
- *App_FineGrainPlots_noSourceOffset.java*: read, pre-process and plot data set of the target task only
- *App_FineGrainPlots_SourceOffset.java*: read, pre-process and plot data set of both source and target and put source offset into the target accumulated actions
- *App_mergeSourceTask.java*:  the naive way to read file and plot
- *App_myBlockDude.java*: This application is used for performing RL in a source task, transferring what have learned and TL in a target task.
- *App_PlotSourceRewardOverEpisodes2FindOptimalE.java*:  This application is used for find the convergence episodes from a source task
- *App_ReadFileAndPlot.java*: Loop version of read files and plots
- *App_simulatedAnnealing.java*: he application of simulated annealing
- *App_Threshold_Alg.java*: For each plot, see how many Actions it took to first reach the threshold,
- *BlockDudeCurriculumLearning.java*:
- *BlockDudeCurriculumLearning_UTILS.java*:
- *JunhuiBlockDude.java*:  example of applying reinforcement learning for block dude environment using the Burlap library
- *JunhuiGridWorld.java*: the example of reinforcement learning for GridWorld environment using Burlap library
- *myBlockDudeTransferLearning.java*: the class used for transfer learning in BlockDude environment
- *paper_noTl_FullyTL.java*:  this is used for showing the difference of no transfer learning and full transfer learning
- *ReadFiles.java*: This class is used for reading files and operating original data set and put operated data set in containers
- *TestParallel.java*: parallel execution to create data set
- *TestSerial.java*: serial way of execution

### BlockDude
This folder is created by Francesco Foglino, and is used for creating the BlockDude reinforcement learning environment.
### TransferLearning
This folder is created by Francesco Foglino as well, some files in this folder are used in my project.
### GridWorld
This folder is created by Francesco Foglino as well, these files supports other operations.
