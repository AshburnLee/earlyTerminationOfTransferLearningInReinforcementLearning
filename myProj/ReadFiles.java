package myProj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadFiles {

    /**
     * see LOG for algorithm design
     * @param al: a big ArrayList get from a file
     * @param numFiles: nEpoche
     * @param numElem: nEpisodes
     * @return
     */
    private ArrayList<Double> GetActionAndRewardBar(ArrayList<Double> al, int numFiles, int numElem){

        //an arrayList to store the average value
        ArrayList<Double> average = new ArrayList<>();


        for (int i = 0; i<numElem; i++){
            double tmp = 0;
            for (int j=1 ; j<=numFiles; j++){
                tmp += al.get(i + (numFiles - j)*numElem);
            }

            average.add(i, tmp/numFiles);
        }
        return average;
    }



    /**
     * See LOG for algorithm design
     * no need for additional space
     * @param al: the input array list that need to be operated.
     */
    public void Accumulation(ArrayList<Double> al){

        for (int i = 1;i < al.size(); i++){

            al.set( i , al.get(i) + al.get(i - 1));

        }
    }



    /**
     * Function: Read a file and get two columns that we need.
     * See LOG for algorithm design
     *
     * @param filePath : input file
     * @param numFiles: num of files
     * @param numElements: num of elements
     * @return: An 2D ArrayList that contains Actions and Rewards
     * @throws IOException
     */
    public ArrayList< ArrayList<Double> > ReadDataFromFiles(String filePath,
                                                            int numFiles,
                                                            int numElements) throws IOException {

        //prepare containers
        ArrayList<Double> actionSeq = new ArrayList<>();
        ArrayList<Double> rewardSeq = new ArrayList<>();
        ArrayList< ArrayList<Double> > return2D = new ArrayList<>();

        //read that file (which contains 3 cols: Index, Reward, Actions):
        BufferedReader fileInBuffer = new BufferedReader(new FileReader(filePath));
        String line;
        String[] columns;
        while((line = fileInBuffer.readLine()) != null) {

            columns = line.split(" "); //split the string into array

            //items in Reward and Actions to containers respectively.
            rewardSeq.add(Double.parseDouble(columns[1]));  //convert String to Double and add it to a Array
            actionSeq.add(Double.parseDouble(columns[2]));  //convert String to Double and add it to a Array

        }
        fileInBuffer.close();

        //get the averages for both actions and rewards:
        ArrayList<Double> rewardBar = GetActionAndRewardBar(rewardSeq, numFiles, numElements);
        ArrayList<Double> actionBar = GetActionAndRewardBar(actionSeq, numFiles, numElements);


//        System.out.println(rewardBar.size());
        return2D.add(actionBar); // add X
        return2D.add(rewardBar); // add Y

        return return2D;
    }


    /**
     * SEE LOG FOR DESGINE
     * Add the last element of AA from sourceTask to the AA of targetTask
     * @param sourceFile:
     * @param targetFile:
     * @param numSourceFile:
     * @param numSourceElem:
     * @param numTargetFile:
     * @param numTargetElem:
     * @return : an 2D ArrayList that stores the FINAL AA and reward(only targetTask)
     * @throws IOException
     */
    public ArrayList< ArrayList<Double> > targetAAPlusLastElemFromSourceANDR(String sourceFile,
                                                                            String targetFile,
                                                                            int numSourceFile,
                                                                            int numSourceElem,
                                                                            int numTargetFile,
                                                                            int numTargetElem) throws IOException {
        ArrayList< ArrayList<Double> > return2D = new ArrayList<>();

        /**SOURCE
         */
        // container for storing columns:
        ArrayList< ArrayList<Double> > container_;
        container_ = ReadDataFromFiles(sourceFile, numSourceFile, numSourceElem);

//        System.out.println(container_.get(0));
        //accumulation
        Accumulation(container_.get(0));
//        System.out.println(container_.get(0));

        //get ceiling value for every items;
        ArrayList<Double> ceilingActions = new ArrayList<>();
        for(int i=0; i<container_.get(0).size(); i++){
            ceilingActions.add(i, Math.ceil(container_.get(0).get(i)));
        }

//        System.out.println(ceilingActions);

        //get the last element:
        double lastElem = ceilingActions.get(ceilingActions.size() - 1);
//        System.out.println(lastElem + "   the last elem is");

        //">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>(1)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        /**
         * TARGET
         */
        //container
        ArrayList< ArrayList<Double> > targetContainer_;
        targetContainer_ = ReadDataFromFiles(targetFile, numTargetFile, numTargetElem);

//        System.out.println(targetContainer_.get(0));
        //accumulation
        Accumulation(targetContainer_.get(0));
//        System.out.println(targetContainer_.get(0));

        //get ceiling value of every elem:
        ArrayList<Double> ceilingActionTarget = new ArrayList<>();
        for (int i=0; i<targetContainer_.get(0).size(); i++){
            ceilingActionTarget.add(i, Math.ceil(targetContainer_.get(0).get(i)));
        }
//        System.out.println(ceilingActionTarget);

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>(2)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        /**
         * ADD the last for source to TARGET
         */
        ArrayList<Double> addSourceLast2Target = new ArrayList<>();
        for(int i=0; i< ceilingActionTarget.size(); i++){
            addSourceLast2Target.add(i, ceilingActionTarget.get(i) + lastElem);
        }
//        System.out.println(addSourceLast2Target);

        //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>(3)>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"
        /**
         * Store them in the 2D arrayList
         */
        System.out.println("size:");
        System.out.println(addSourceLast2Target.size());
        System.out.println(targetContainer_.get(1).size());

        return2D.add(addSourceLast2Target);
        return2D.add(targetContainer_.get(1));

        return return2D;


    }

    /**See log for design details
     * read target return.txt, accumulate and ceil the Actions(1st column of the container)
     * @param targetFilePath
     * @param numFiles
     * @param numElem
     * @return
     * @throws IOException
     */
    public ArrayList< ArrayList<Double> > targetAAAndR_noLastElemFromSource(String targetFilePath,
                                                                            int numFiles,
                                                                            int numElem) throws IOException {

        //initial container
        ArrayList< ArrayList<Double> > container;

        //read date from target file
        container = ReadDataFromFiles(targetFilePath, numFiles, numElem);

//        System.out.println(container.get(0));

        //Accumulation the Action
        Accumulation(container.get(0));
//        System.out.println(container.get(0));

        //get ceiling value for every items;
        ArrayList<Double> ceilingValue = new ArrayList<>();
        for(int i=0; i<container.get(0).size(); i++){
            ceilingValue.add(i, Math.ceil(container.get(0).get(i)));
        }
//        System.out.println(ceilingValue);

        //replace the container.get(0) as ceilingValue
        container.set(0, ceilingValue);

        //add two lists to a bigger container:
//        System.out.println(container.get(0));
        return container;
    }


}
