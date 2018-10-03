package myProj;

import java.util.ArrayList;
import java.util.Arrays;

public class App_simulatedAnnealing{

    public static void main(String[] args) {

        //parentPath
        String parentPath = "/media/junhui/DATA/project_data_files/Experiment_paperSA/";

        //collection of E and convert to ArrayList
        Integer[] Es = {2,  4, 6,  8,  10,  12, 14, 16, 18, 20, 22, 24, 26, 28, 30};
        ArrayList<Integer> episodes = new ArrayList<Integer>(Arrays.asList(Es));

        //
        Algorithm_simulated_annealing sa = new Algorithm_simulated_annealing();
        sa.simulatedAnnealing(parentPath, episodes, 100, 0.1);

    }
}

