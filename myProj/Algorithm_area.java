package myProj;

import plot.Plot;

import java.io.IOException;
import java.util.*;

/**
 * the areas metrics
 *
 * @author junhui
 */
public class Algorithm_area {


    /**
     * calculate all areas for different number of learning in the source and store them
     * in two ArrayLists, for further operation
     *
     * @param parentPath: the parent path
     * @param numPlots: the numbe rof files
     * @param interval: the interval in the x-axis
     * @param numFileInTXT: the number of file in one return.txt
     * @param ME: the vertical threshold
     * @param check: check the approximate point or mot
     * @param sort: sort by value
     */
    public void getAllAreasForAllE(String parentPath,
                                   int numPlots,
                                   int interval,
                                   int numFileInTXT,
                                   double ME,
                                   boolean check,
                                   boolean sort) {

        ArrayList<Double> Areas = new ArrayList<>();
        ArrayList<Integer> E = new ArrayList<>();

        //Get each AA & R :
        for (int k = 1; k<=numPlots; k++) {
            String outputTarget = parentPath + "E_" + interval*k + "/outputTarget/return.txt";

            ArrayList< ArrayList<Double> > box = null;
            ReadFiles rf = new ReadFiles();

            //put items into a box:
            try{
                box = rf.targetAAAndR_noLastElemFromSource(outputTarget, numFileInTXT, 500);

            } catch (IOException e){
                e.printStackTrace();
            }
            Algorithm_area aa = new Algorithm_area();

            double area;

            area = aa.sumArea(box.get(0), box.get(1), ME, check);

            Areas.add(-area);   //areas = [, , , , , , ]
            E.add(interval*k);    //E =  [, , , , , , ]

//            System.out.println("***************" + interval*k + "*****************");

        }
        System.out.println(Areas);
        System.out.println(E);

        //STEP final:
        int index = 0;
        double min = Areas.get(0);

        for (int i=0; i< Areas.size(); i++) {
            if(min > Areas.get(i)){
                min = Areas.get(i);
                index = i;

            }

        }

        System.out.println("******************RESULT*************************");
        System.out.println("Vertical threshold: " + ME);
        System.out.println("The Min of Areas: " +  min);
        System.out.println("Shared index: " + index);
        System.out.println("Optimal solution: Learn source " + E.get(index) + " episodes");

        //sort or not
        if (sort) {
            //1. create a dict
            Map<Integer, Double> dict = new HashMap<>();
            for (int i = 0; i<E.size(); i++) {
                dict.put(E.get(i), Areas.get(i));
            }

            //2. sort
            Map<Integer, Double> sortedMap = sortByValue(dict);

            //3. print the collection of keys
            System.out.println(sortedMap.keySet());
            System.out.println(sortedMap.values());
        }

        //plot areas vs E
        Plot.callPlot("Areas vs E", "Areas over episodes","Episodes/2","Areas",
                6, 0, numPlots, interval,
                null,null,Areas);

    }

    /**
     * add all areas to get the final area.
     * @param AA: Accumulative actions
     * @param R: reward
     * @param ME: the specific AA a user choose
     * @param check: weather you want check the final points
     * @return the sum of area
     */
    public double sumArea(ArrayList<Double> AA, ArrayList<Double> R, double ME, boolean check) {

        boolean exist = existInOrNot(AA, ME);

        double appR = approximateOfR(ME, AA, R, exist);

        int index = returnIndex(AA, ME);

        //check the ending point
        if(check) {
            System.out.println("Check the ending point (x, y): ");

            if(exist)
                System.out.println("( " + AA.get(index) + ", " + R.get(index) + " )" );
            else
                System.out.println("( " + ME + ", " + appR+" )");
        }

        //calculate the total area:
        double finalArea = 0;
        if(exist) {
            for (int i=0; i < index; i++) {
                finalArea += partialArea(AA.get(i), AA.get(i + 1), R.get(i), R.get(i + 1));
            }
        }
        else {
            for (int i = 0; i < index-1; i++) {
                finalArea += partialArea(AA.get(i), AA.get(i + 1), R.get(i), R.get(i + 1));
            }
            //add the final part , Hint: check the output "index"
            finalArea += partialArea(AA.get(index - 1), ME, R.get(index -1), appR);
            if (check) {
                System.out.println("Two points of the final part: ");
                System.out.println(
                        "( "+AA.get(index -1)+","+ R.get(index-1)+" )"+","+
                        "( "+ME+","+ appR+" )");
            }
        }

        return finalArea;
    }


    /**
     * If ME exists in AA, get the corresponding R;
     * If not, return the right index of the value that is the first item bigger than ME.
     * @param me:
     * @param AA:
     * @param R:
     * @return the approximate R
     */
    private static double approximateOfR(double me, ArrayList<Double> AA, ArrayList<Double> R, boolean exist) {

        int index;
        int rIndex;
        double rBar;

        if(exist) {
            index = returnIndex(AA, me);
            return R.get(index);
        }

        else {
            rIndex = returnIndex(AA, me);
            rBar = 0.5 * (R.get(rIndex - 1) + R.get(rIndex));
        }
        return rBar;

    }

    /**
     * Check if ME exists in AA
     * NOTE: it works only for monotone increasing AA, which truly is
     *
     * @param AA: list
     * @param me: object needed to find in AA
     * @return true or false,
     */
    private static boolean existInOrNot(ArrayList<Double> AA, double me) {

        for(int i=0; i<AA.size(); i++) {

            if(AA.get(i) == me)
                return true;
            else if (AA.get(i) > me)
                break;
        }
        return false;
    }

    /**
     *If ME exists in AA, return the index of AA;
     * If not, return the index of the right value that is the first bigger than ME
     * NOTE: it works only for monotone increasing AA, fortunately AA is monotone increasing.
     *
     * @param AA: list
     * @param me: object that is needed to find in AA
     * @return index in the AA
     */
    private static int returnIndex(ArrayList<Double> AA, double me){

        int i;
        for(i=0; i<AA.size(); i++) {

            if(AA.get(i) == me)
                return i;   //the index of me in that list
            else if(AA.get(i) > me)
                break;
        }
        return i;  //the right index of the me
    }

    /**
     * calculate the partial area given two points
     *
     * @param x1: current x
     * @param x2: next x
     * @param y1: current y
     * @param y2: next y
     * @return the area between two points
     */
    private static double partialArea(double x1, double x2, double  y1, double y2) {

        return 0.5 * (x2 - x1) * (y2 + y1);

    }

    private static Map<Integer, Double> sortByValue(Map<Integer, Double> originalMap) {

        //1. convert map to list of map
        List<Map.Entry<Integer, Double>> list = new LinkedList<Map.Entry<Integer, Double>>(originalMap.entrySet());

        //2. sort list with Collections.sort()
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return (o1.getValue().compareTo(o2.getValue()));
            }
        });

        //3. Loop the sorted list and put it into a new insertion order map linkedHashMap
        Map<Integer, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Integer,Double> entry: list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;

    }

}
