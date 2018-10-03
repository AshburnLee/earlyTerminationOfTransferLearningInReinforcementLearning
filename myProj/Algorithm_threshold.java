package myProj;

import plot.Plot;
import static plot.Plot.callPlot;

import java.io.IOException;
import java.util.*;


public class Algorithm_threshold {

    public double findIndexOfMin(boolean offset,
                                 double threshold,
                                 String parentPath,
                                 int numPlots,
                                 int numFileInTXT,
                                 int interval,
                                 boolean sort) {

        //container to store the AA-r and the number of episodes learned from source
        ArrayList<Double> AA_r = new ArrayList<>();
        ArrayList<Integer> E = new ArrayList<>();
        int index = 0;

        if(offset) {

            for(int k=1; k<=numPlots; k++){

                String outputSource = parentPath + "E_" + interval*k + "/outputSource/return.txt";
                String outputTarget = parentPath + "E_" + interval*k + "/outputTarget/return.txt";

                ArrayList< ArrayList<Double> > box = null;
                ReadFiles readFiles = new ReadFiles();

                //put items into that box
                try{
                    box = readFiles.targetAAPlusLastElemFromSourceANDR(
                            outputSource, outputTarget,
                            numFileInTXT,interval*k,
                            numFileInTXT,500);
                } catch (IOException e){
                    e.printStackTrace();
                }


                // for each box, find the AA' (R must be smoothed first)
                double tmp = 0;
                ArrayList<Double> newBoxGet1;
                newBoxGet1 = Plot.SmoothBySlide(box.get(1), 20);

                tmp = findTheRightAA(newBoxGet1, box.get(0), threshold);

                AA_r.add(tmp);
                E.add(interval*k);

            }

            System.out.println(AA_r);
            System.out.println(E);


            //STEP final:
            double min = AA_r.get(0);

            for (int i=0; i< AA_r.size(); i++) {
                if(min > AA_r.get(i)){
                    min = AA_r.get(i);
                    index = i;

                }

            }

            System.out.println("************************RESULT*************************");
            System.out.println("Threshold: " + threshold);
            System.out.println("The Min of AA: " +  min);
            System.out.println("Shared index: " + index);
            System.out.println("Optimal solution: Learn source " + E.get(index) + " episodes");

            if (sort) {

                //1. create a dict
                Map<Integer, Double> dict = new HashMap<>();
                for (int i = 0; i<E.size(); i++) {
                    dict.put(E.get(i), AA_r.get(i));
                }

                //2. sort
                Map<Integer, Double> sortedMap = sortByValue(dict);

                //3. print the collection of keys
                System.out.println(sortedMap.keySet());
                System.out.println(sortedMap.values());
            }

        }
        else {
            for(int k=1; k<=numPlots; k++){

                String outputTarget = parentPath + "E_" + interval*k + "/outputTarget/return.txt";

                ArrayList< ArrayList<Double> > box = null;
                ReadFiles readFiles = new ReadFiles();

                //put items into that box
                try{
                    box = readFiles.targetAAAndR_noLastElemFromSource(outputTarget, numFileInTXT, 500);

                } catch (IOException e){
                    e.printStackTrace();
                }


                // for each box, find the AA'  (AA must be smoothed first)
                double tmp = 0;
                ArrayList<Double> newBoxGet1;
                newBoxGet1 = Plot.SmoothBySlide(box.get(1), 20);

                tmp = findTheRightAA(newBoxGet1, box.get(0), threshold);


                AA_r.add(tmp);
                E.add(interval*k);

            }

            System.out.println(AA_r);
            System.out.println(E);


            //STEP final:
            double min = AA_r.get(0);
//            int index = 0;

            for (int i=0; i< AA_r.size(); i++) {
                if(min > AA_r.get(i)){
                    min = AA_r.get(i);
                    index = i;

                }
            }

            System.out.println("RESULT*************************");
            System.out.println("Threshold: " + threshold);
            System.out.println("The Min of AA_r: " +  min);
            System.out.println("Shared index: " + index);
            System.out.println("Optimal solution: Learn source " + E.get(index) + " episodes");

            // sort or not
            if (sort) {

                //1. create a dict
                Map<Integer, Double> dict = new HashMap<>();
                for (int i = 0; i<E.size(); i++) {
                    dict.put(E.get(i), AA_r.get(i));
            }

                //2. sort
                Map<Integer, Double> sortedMap = sortByValue(dict);

                //3. print the collection of keys
                System.out.println(sortedMap.keySet());
                System.out.println(sortedMap.values());
            }

        }

        //plot
        globalOrLocal(AA_r, numPlots, interval);

        return E.get(index);
    }

    private static void globalOrLocal(ArrayList<Double> AA_r, int numPlots, int interval) {

        callPlot("Threshold vs E",
                "Number of actions over episodes",
                "Episodes/2",
                "Actions to reach the Threshold",
                6,
                0,
                numPlots,
                interval,
                null,null, AA_r);

    }


    /**
     * STEP 3: for each box (AA & R) find the AA' whose R first reaches or first goes above the threshold.
     * @param R: reward; NOTE: you must smooth it first!!!!!!!!!!
     * @param AA: accumulated actions
     * @param threshold:
     * @return the specific AA
     */
    public static double findTheRightAA(ArrayList<Double> R, ArrayList<Double> AA, double threshold) {

        int index = 0;

        for (int i=0; i<R.size();i++) {

            if(R.get(i) >= threshold) {

                index = i;
                break;
            }
        }

        return AA.get(index);
    }

    /**
     * NO use here
     * @param AA_r:
     * @param l:
     * @param r:
     * @param x:
     * @return the index of x
     */
    private static int BinarySearch(ArrayList<Double> AA_r, int l, int r, double x) {
        if (r > l){
            int mid = l + (r-l)/2;

            // mid is the element
            if(AA_r.get(mid) == x)
                return mid;
            //if x is smaller than mid, check the left sub-array
            if(AA_r.get(mid) > x)
                return BinarySearch(AA_r, l, mid-1, x);

            //if x is larger than the mid, then check the right sub-array
            return BinarySearch(AA_r, mid+1, r, x);
        }

        //x is not in AA_r
        return -1;
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
