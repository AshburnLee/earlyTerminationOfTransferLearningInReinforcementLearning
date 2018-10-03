package myProj;


public class App_Areas_Alg {

    public static void main(String[] args) {

        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_1/";  //!!!!!!!!!!
        int numPlots = 15;   //!!!!!!!!!!
        int interval = 2;    //!!!!!!!!!!
        int threshold = 800;

        Algorithm_area aa = new Algorithm_area();

        aa.getAllAreasForAllE(parentPath, numPlots, interval, 40 ,threshold, false, true);

    }

}
