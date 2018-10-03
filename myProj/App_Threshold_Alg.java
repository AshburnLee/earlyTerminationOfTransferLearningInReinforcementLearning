package myProj;

/**
 * For each plot, see how many Actions it took to first reach the threshold,
 * the smaller the better.
 */
public class App_Threshold_Alg {

    public static void main(String[] args) {

        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paperSA/";  //!!!!!!!!!!!!
        int numPlots = 15;   //!!!!!!!!!!
        int interval = 2;    //!!!!!!!!!!!!!!!!!
        double threshold = -15.0;

        //call threshold algorithm:
        Algorithm_threshold ta = new Algorithm_threshold();
        double epiosdes = ta.findIndexOfMin(true, threshold, parentPath, numPlots, 40, interval,true);

    }
}

