package myProj;

import plot.Plot;

import java.io.IOException;
import java.util.ArrayList;

import static plot.Plot.callPlot;

/**
 * multiple plots in one window, without source offset
 *
 * @author junhui
 */
public class App_FineGrainPlots_noSourceOffset {


    /* CHANGE:
     outputPathTarget_noTransfer
     parentPath
     numPlots
     interval
     callPlot(the number of plots)
     */
    public static void main(String[] args){

        // layer0 no TL :
        String outputPathTarget_noTransfer =
                "/media/junhui/DATA/project_data_files/Experiment_paperSA_Source/outputTarget_noTransfer/"; //!!!!!!!!!!

        ReadFiles rf = new ReadFiles();
        ArrayList< ArrayList<Double> > box1 = null;
        try {
            box1 = rf.targetAAAndR_noLastElemFromSource(outputPathTarget_noTransfer + "/return.txt",
                    40, 500);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // layer0 end


        // layer1 with different E
        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_1/";  //!!!!!!!!!!!!!!!!!!!!!
        int numPlots = 15; //!!!!!!!!!!!!!!!!!!!!!!
        int interval = 2;  //!!!!!!!!!!!!!!!!!!!!!!
        ArrayList< ArrayList< ArrayList<Double> > > trunk = new ArrayList<>();

        for (int k=1; k<=numPlots; k++) {

            //file path
            String outputFile = parentPath + "E_" + interval*k + "/outputTarget/return.txt";

            //read date from file
//            ReadFiles rf = new ReadFiles();
            ArrayList< ArrayList<Double> > box = null;

            try{
                box = rf.targetAAAndR_noLastElemFromSource(outputFile, 40, 500);   //num files !!!!!!!
            } catch (IOException e) {
                e.printStackTrace();
            }


            //put boxes into trunk
            trunk.add(box);

        }

        //check
        System.out.println(trunk.size());
        System.out.println(trunk.get(0).size());
        System.out.println(trunk.get(1).size());
        System.out.println(trunk.get(0).get(0).size());
        System.out.println(trunk.get(1).get(1).size());

        //chooseMethod: 7: lines
//                     110: scatter
        callPlot("Target Only",
                null,
                "Accumulated Actions",
                "Rewards",

                4,
                5,
                numPlots,
                interval,

                box1.get(0),
                Plot.SmoothBySlide(box1.get(1),20),

                trunk.get(0).get(0),
                Plot.SmoothBySlide(trunk.get(0).get(1), 20),
                trunk.get(1).get(0),
                Plot.SmoothBySlide(trunk.get(1).get(1), 20),
                trunk.get(2).get(0),
                Plot.SmoothBySlide(trunk.get(2).get(1), 20),
                trunk.get(3).get(0),
                Plot.SmoothBySlide(trunk.get(3).get(1), 20),
                trunk.get(4).get(0),
                Plot.SmoothBySlide(trunk.get(4).get(1), 20),

                trunk.get(5).get(0),
                Plot.SmoothBySlide(trunk.get(5).get(1), 20),
                trunk.get(6).get(0),
                Plot.SmoothBySlide(trunk.get(6).get(1), 20),
                trunk.get(7).get(0),
                Plot.SmoothBySlide(trunk.get(7).get(1), 20),
                trunk.get(8).get(0),
                Plot.SmoothBySlide(trunk.get(8).get(1), 20),
                trunk.get(9).get(0),
                Plot.SmoothBySlide(trunk.get(9).get(1), 20),

                trunk.get(10).get(0),
                Plot.SmoothBySlide(trunk.get(10).get(1), 20),
                trunk.get(11).get(0),
                Plot.SmoothBySlide(trunk.get(11).get(1), 20),
                trunk.get(12).get(0),
                Plot.SmoothBySlide(trunk.get(12).get(1), 20),
                trunk.get(13).get(0),
                Plot.SmoothBySlide(trunk.get(13).get(1), 20),
                trunk.get(14).get(0),
                Plot.SmoothBySlide(trunk.get(14).get(1), 20)
        );

    }
}
