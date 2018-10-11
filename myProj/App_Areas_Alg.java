package myProj;


/**
 *
 * teh application of using area metrics to evaluate the goodness of
 * learning different number of episodes in the source.
 *
 * @author junhui
 */
public class App_Areas_Alg {

    public static void main(String[] args) {

        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_1/";  //!!!!!!!!!!
        int numPlots = 15;   //!!!!!!!!!!
        int interval = 2;    //!!!!!!!!!!
        int threshold = 800;

        Algorithm_area aa = new Algorithm_area();

        //call main method
        aa.getAllAreasForAllE(parentPath, numPlots, interval, 40 ,threshold, false, true);

    }

}
