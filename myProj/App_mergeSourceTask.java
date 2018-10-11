package myProj;

import plot.Plot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * naive way to read file and plot
 * Loop version: see App_ReadFileAndPlot.java
 */
public class App_mergeSourceTask {

    public static void main(String[] args) throws IOException {

        String parentPath = "/media/junhui/DATA/project_data_files/TESTTL_4_/";


        //learn 15
        String outputPathSource_15 = parentPath + "outputSource_15/return.txt";
        String outputPathTarget_15 = parentPath + "outputTarget_15_Transfer/return.txt";

        //learn 30
        String outputPathSource_30 = parentPath + "outputSource_30/return.txt";
        String outputPathTarget_30 = parentPath + "outputTarget_30_Transfer/return.txt";

        //learn 45
        String outputPathSource_45 = parentPath + "outputSource_45/return.txt";
        String outputPathTarget_45 = parentPath + "outputTarget_45_Transfer/return.txt";

        //learn 60
        String outputPathSource_60 = parentPath + "outputSource_60/return.txt";
        String outputPathTarget_60 = parentPath + "outputTarget_60_Transfer/return.txt";

        //learn 15
        String outputPathSource_75 = parentPath + "outputSource_75/return.txt";
        String outputPathTarget_75 = parentPath + "outputTarget_75_Transfer/return.txt";


        ArrayList< ArrayList<Double> > container_15;
        ArrayList< ArrayList<Double> > container_30;
        ArrayList< ArrayList<Double> > container_45;
        ArrayList< ArrayList<Double> > container_60;
        ArrayList< ArrayList<Double> > container_75;


        ReadFiles readFiles = new ReadFiles();

        //create containers that contains Xs and Ys
        container_15 = readFiles.targetAAPlusLastElemFromSourceANDR(
                outputPathSource_15, outputPathTarget_15, 10,15,10,500);
        container_30 = readFiles.targetAAPlusLastElemFromSourceANDR(
                outputPathSource_30, outputPathTarget_30, 10,30,10,500);
        container_45 = readFiles.targetAAPlusLastElemFromSourceANDR(
                outputPathSource_45, outputPathTarget_45, 10,45,10,500);
        container_60 = readFiles.targetAAPlusLastElemFromSourceANDR(
                outputPathSource_60, outputPathTarget_60, 10,60,10,500);
        container_75 = readFiles.targetAAPlusLastElemFromSourceANDR(
                outputPathSource_75, outputPathTarget_75, 10,75,10,500);


        //call plot
        Plot.callPlot("plot source and target",
                null,
                "Accumulated actions",
                "Reward",

                4,
                20,
                5,
                15,
                null,
                null,

                container_15.get(0),
                Plot.SmoothBySlide(container_15.get(1),20),

                container_30.get(0),
                Plot.SmoothBySlide(container_30.get(1),20),

                container_45.get(0),
                Plot.SmoothBySlide(container_45.get(1),20),

                container_60.get(0),
                Plot.SmoothBySlide(container_60.get(1),20),

                container_75.get(0),
                Plot.SmoothBySlide(container_75.get(1),20));

    }
}
