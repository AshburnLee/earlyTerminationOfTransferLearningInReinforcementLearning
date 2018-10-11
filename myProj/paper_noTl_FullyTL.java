package myProj;

import plot.Plot;

import java.io.IOException;
import java.util.ArrayList;

/**
 * For report illustration.
 * plot no transfer and fully transfer
 * @author junhui
 */
public class paper_noTl_FullyTL {

    public static void main(String[] args) {

        // with offset or not: choose
        boolean offset = true;
        String plotTitle;

        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paper/";  //!!!!!!!!!!!!!

        // 1) file path
        // no TL
        String outputPathTarget_noTransfer =  parentPath + "outputTarget_noTransfer/";

        // fully learned
        String outputPathSource_30 = parentPath + "outputSource_30/";
        String outputPathTarget_30 = parentPath + "outputTarget_30_Transfer/";

        // 2) extract data from files
        // extract data from no TL
        ReadFiles rf = new ReadFiles();
        ArrayList< ArrayList<Double> > box1 = null;

        try {
            box1 = rf.targetAAAndR_noLastElemFromSource(outputPathTarget_noTransfer + "/return.txt",
                    40, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(box1.get(0)); // AA
        System.out.println(box1.get(1)); // reward

        // extract data from fully TL
        ArrayList< ArrayList<Double> > box2 = null;

        // plot with offset from source or not
        if (offset){
            plotTitle = "Number of actions for Source & Target";
            try {
                box2 = rf.targetAAPlusLastElemFromSourceANDR(outputPathSource_30+"/return.txt",
                        outputPathTarget_30 + "/return.txt",
                        40, 30,
                        40, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            plotTitle = "Number of actions for Target only";
            try {
                box2 = rf.targetAAAndR_noLastElemFromSource(outputPathTarget_30 + "/return.txt",
                        40, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println(box2.get(0)); // AA
        System.out.println(box2.get(1)); // reward

        // 3) show plots
        Plot.callPlot(plotTitle,
                null,
                "Accumulated actions",
                "Reward",

                110,
                20,
                2,
                1,
                null,
                null,

                box1.get(0),
                Plot.SmoothBySlide(box1.get(1),20),
                box2.get(0),
                Plot.SmoothBySlide(box2.get(1),20));

    }
}
