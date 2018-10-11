package myProj;

import plot.Plot;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.ArrayList;

import static plot.Plot.callPlot;

/**
 * multiple plots in one window, with source offset
 * @author junhui
 */
public class App_FineGrainPlots_SourceOffset {

    /* CHANGE:
      outputPathTarget_noTransfer
      parentPath
      numPlots
      interval
      callPlot(the number of plots)
      */
    public static void main(String[] args) {

        // layer0 no TL :
        String outputPathTarget_noTransfer =
                "/media/junhui/DATA/project_data_files/Experiment_paperSA_Source/outputTarget_noTransfer/";   //!!!!!!!!!!

        ReadFiles rf = new ReadFiles();
        ArrayList< ArrayList<Double> > box1 = null;
        try {
            box1 = rf.targetAAAndR_noLastElemFromSource(outputPathTarget_noTransfer + "/return.txt",
                    40, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // layer0 end

        // layer 1 TL with different E:
        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_1/"; //!!!!!!!!!!!!!!!!!!!

        ArrayList< ArrayList< ArrayList<Double> > > trunk = new ArrayList<>();

        int numPlots = 15;  //!!!!!!!!!!!
        int interval = 2;  //!!!!!!!!!!!!

        for(int k = 1; k <= numPlots; k++) {

            String outputSource = parentPath + "E_" + interval*k + "/outputSource/return.txt";
            String outputTarget = parentPath + "E_" + interval*k + "/outputTarget/return.txt";

//            System.out.println(outputSource);
//            System.out.println(outputTarget);

            ArrayList< ArrayList<Double> > box = null;
//            ReadFiles readFiles = new ReadFiles();

            //put items into that box
            try{
                box = rf.targetAAPlusLastElemFromSourceANDR(
                        outputSource, outputTarget,40,interval*k, 40,500);
            } catch (IOException e){
                e.printStackTrace();
            }

            //put that into that trunk:
            trunk.add(box);

        }
        //layer1 end

        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(trunk.size());
        System.out.println(trunk.get(1).size());
        System.out.println(trunk.get(1).get(1).size());


        // plot layer0 and layer1
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        callPlot("Plots with source offset",
                null,
                "Accumulated actions",
                "Reward",

                4,
                20,
                numPlots,
                interval,

                box1.get(0),
                Plot.SmoothBySlide(box1.get(1),20),

                trunk.get(0).get(0),
                Plot.SmoothBySlide(trunk.get(0).get(1),20),
                trunk.get(1).get(0),
                Plot.SmoothBySlide(trunk.get(1).get(1),20),
                trunk.get(2).get(0),
                Plot.SmoothBySlide(trunk.get(2).get(1),20),
                trunk.get(3).get(0),
                Plot.SmoothBySlide(trunk.get(3).get(1),20),
                trunk.get(4).get(0),
                Plot.SmoothBySlide(trunk.get(4).get(1),20),

                trunk.get(5).get(0),
                Plot.SmoothBySlide(trunk.get(5).get(1),20),
                trunk.get(6).get(0),
                Plot.SmoothBySlide(trunk.get(6).get(1),20),
                trunk.get(7).get(0),
                Plot.SmoothBySlide(trunk.get(7).get(1),20),
                trunk.get(8).get(0),
                Plot.SmoothBySlide(trunk.get(8).get(1),20),
                trunk.get(9).get(0),
                Plot.SmoothBySlide(trunk.get(9).get(1),20),

                trunk.get(10).get(0),
                Plot.SmoothBySlide(trunk.get(10).get(1),20),
                trunk.get(11).get(0),
                Plot.SmoothBySlide(trunk.get(11).get(1),20),
                trunk.get(12).get(0),
                Plot.SmoothBySlide(trunk.get(12).get(1),20),
                trunk.get(13).get(0),
                Plot.SmoothBySlide(trunk.get(13).get(1),20),
                trunk.get(14).get(0),
                Plot.SmoothBySlide(trunk.get(14).get(1),20)
        );


    }
}
