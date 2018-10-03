package myProj;

import plot.Plot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Loop version of read files and plots
 * Naive version: see App_mergeSourceTask.java
 */
public class App_ReadFileAndPlot {

    public static void main(String[] args) throws IOException {

        String parentPath = "/media/junhui/DATA/project_data_files/TESTTL_4_/";  //!!!!!!!!!!!!

        ArrayList< ArrayList< ArrayList<Double> > >  trunk = new ArrayList<>();
        int numPlots = 5;  //!!!!!!!!!!!!!!!!!!!!!!!!!!!
        int interval = 15;  //!!!!!!!!!!!!!!!!!!!!!!!!!!

        for (int k = 0; k < numPlots; k++ ){

            String outputSource = parentPath + "outputSource_"+ interval*(k+1) +"/return.txt";  //!!!!!
            String outputTarget = parentPath + "outputTarget_"+ interval*(k+1) +"_Transfer/" + "return.txt";  //!!!!!

            //create a box:
            ArrayList< ArrayList<Double> > box;

            ReadFiles readFiles = new ReadFiles();

            //create a trunk:
            box = readFiles.targetAAPlusLastElemFromSourceANDR(
                    outputSource, outputTarget, 10,(k+1)*interval,10,500);

            //put that box into a trunk:

            trunk.add(box);

        }

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(trunk.size());
        System.out.println(trunk.get(1).size());
        System.out.println(trunk.get(1).get(1).size());

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        Plot.callPlot("LOOP",
                null,
                "change",
                "change",

                7,
                20,
                numPlots,
                interval,

                null,
                null,
                trunk.get(0).get(0),
                Plot.SmoothBySlide(trunk.get(0).get(1),20),
                trunk.get(1).get(0),
                Plot.SmoothBySlide(trunk.get(1).get(1),20),
                trunk.get(2).get(0),
                Plot.SmoothBySlide(trunk.get(2).get(1),20),
                trunk.get(3).get(0),
                Plot.SmoothBySlide(trunk.get(3).get(1),20),
                trunk.get(4).get(0),
                Plot.SmoothBySlide(trunk.get(4).get(1),20));


    }
}
