package plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;

import java.awt.*;
import java.util.ArrayList;


public class Plot extends ApplicationFrame {

    /**
     * Constructor
     * @param title: the title of the plot window
     */
    private Plot(String title){
        super(title);
    }  // constructor have no return type


    /**Plot the reward over episodes
     * use JFreechart to plot.
     * @param Y: the list with received reward for each episode
     * call callPlot to
     */
    private void PlotLineChart(ArrayList<Double> X,
                               ArrayList<Double> Y,

                               String title,
                               String legend,

                               String xAxisLable,
                               String yAxisLable) {

        // 1) no AA(x axis) is provided
        if (X == null) {
            int length = Y.size();

            XYSeries series = new XYSeries(legend);
            for (int i = 0; i < length; i++) {
                series.add(i + 1, Y.get(i));
            }
            final XYSeriesCollection data = new XYSeriesCollection(series);

            final JFreeChart chart = ChartFactory.createXYLineChart(
                    title,
                    xAxisLable,
                    yAxisLable,
                    data,
                    PlotOrientation.VERTICAL,
                    true,
                    true,
                    false);

            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            setContentPane(chartPanel);

        } else { // 2) AA (x axis is provided)
            if (X.size() != Y.size())
                System.out.println("Size does not match");
            else {
                int length = X.size();

                // 1)create dataset
                XYSeries series = new XYSeries(legend);
                for (int i = 0; i < length; i++) {
                    series.add(X.get(i), Y.get(i));
                }
                XYSeriesCollection data = new XYSeriesCollection(series);
                final JFreeChart chart = ChartFactory.createXYLineChart(
                        title,
                        xAxisLable,
                        yAxisLable,
                        data,
                        PlotOrientation.VERTICAL,
                        true,
                        true,
                        false);
                final ChartPanel chartPanel = new ChartPanel(chart);
                chartPanel.setPreferredSize(new Dimension(1000, 500));
                setContentPane(chartPanel);
            }


        }
    }


    /**
     *
     * @param numPlots
     * @param interval
     * @param title
     * @param zeroX: AA of 0 E learned from source
     * @param zeroY: R of 0 E learned from source
     * @param al
     */
    private void PlotMultipleLineChart(int numPlots,
                                       int interval,
                                       String title,
                                       ArrayList<Double> zeroX,
                                       ArrayList<Double> zeroY,
                                       ArrayList<Double>...al) {

        if(al.length == 0)
            System.out.println("You need input data");
        else {

            for (ArrayList<Double> i : al) {
                if (i == null) {
                    System.out.println("There exists empty arrayList, please check");
                    break;
                }
            }
            //1) create dataset
//            int numPlots = 5;  //!!!!!!!!!!!!!!!!!!!!
//            int interval = 15;  //!!!!!!!!!!!!!!!!!!!!!!!!!

            XYSeriesCollection dataset = new XYSeriesCollection();

            //layer zero
            if (zeroX != null){
                XYSeries seriesZero = new XYSeries("learn 0 Episode");
                for (int i=0;i<zeroX.size(); i++){
                    seriesZero.add(zeroX.get(i), zeroY.get(i));
                }
                dataset.addSeries(seriesZero);
            }
            //layer zero end

            // we need plots in the same window
            for (int k = 0; k < numPlots; k++) {

                XYSeries series = new XYSeries("learn " + (k + 1) * interval + " Episodes");

                for (int i = 0; i < al[k * 2].size(); i++) {

                    series.add(al[k * 2].get(i), al[k * 2 + 1].get(i));

                }
                dataset.addSeries(series);
            }

            //2 ) create chart
            JFreeChart chart = ChartFactory.createXYLineChart(title,
                    "Accumulative Actions",
                    "Rewards",
                    dataset);

            // 3) create panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            setContentPane(chartPanel);
        }
    }

    private void PlotJustForPaper( String title, ArrayList<Double>...al) {

        if(al.length == 0)
            System.out.println("You need input data");
        else {

            for (ArrayList<Double> i : al) {
                if (i == null) {
                    System.out.println("There exists empty arrayList, please check");
                    break;
                }
            }
            //1) create dataset
//            int numPlots = 5;  //!!!!!!!!!!!!!!!!!!!!
//            int interval = 15;  //!!!!!!!!!!!!!!!!!!!!!!!!!


            XYSeriesCollection dataset = new XYSeriesCollection();


            XYSeries series1 = new XYSeries("No TL ");
            for (int i=0;i<al[0].size(); i++){
                series1.add(al[0].get(i), al[1].get(i));
            }
            dataset.addSeries(series1);

            XYSeries series2 = new XYSeries("Fully learned from source");
            for (int i=0;i<al[2].size(); i++){
                series2.add(al[2].get(i), al[3].get(i));
            }
            dataset.addSeries(series2);


//            XYSeriesCollection dataset = new XYSeriesCollection();
//            // we need 5 plots in the same window
//            for (int k = 0; k < numPlots; k++) {
//
//                XYSeries series = new XYSeries("learn " + (k + 1) * interval + " Episodes");
//
//                for (int i = 0; i < al[k * 2].size(); i++) {
//
//                    series.add(al[k * 2].get(i), al[k * 2 + 1].get(i));
//
//                }
//                dataset.addSeries(series);
//            }

            //2 ) create chart
            JFreeChart chart = ChartFactory.createXYLineChart(title,
                    "Accumulative Actions",
                    "Rewards",
                    dataset);

            // 3) create panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1000, 500));
            setContentPane(chartPanel);
        }
    }



    /**
     * scatter plot for a single object
     * @param actionBar: x axis
     * @param rewardBar: y axis
     */
    private void PlotScatterChart(ArrayList<Double> actionBar, ArrayList<Double> rewardBar, String legend){
        if(actionBar.size() != rewardBar.size())
            System.out.println("They don not have equal length");
        else{
            int length = actionBar.size();

            // 1) create dataset
            XYSeries series = new XYSeries(legend);
            for (int i=0;i<length;i++){
                series.add(actionBar.get(i),rewardBar.get(i));
            }
            XYSeriesCollection data = new XYSeriesCollection(series);

            // 2) create line chart
            JFreeChart chart = ChartFactory.createScatterPlot(
                    "Scatter Plot",
                    "Accumulative Action",
                    "Reward",
                    data);



            // 3) create panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1000,500));
            setContentPane(chartPanel);
        }


    }

    /**
     *
     * @param numPlots
     * @param interval
     * @param title
     * @param zeroX: AA of 0 E learned from source
     * @param zeroY: R of o E learned from source
     * @param al
     */
    private void PlotScatterCharts(int numPlots,
                                   int interval,
                                   String title,
                                   ArrayList<Double> zeroX,
                                   ArrayList<Double> zeroY,

                                   ArrayList<Double>...al){

        if(al.length == 0)
            System.out.println("You need input data");
        else{

            for(ArrayList<Double> i:al){
                if(i == null){
                    System.out.println("There exists empty arrayList, please check");
                    break;
                }
            }


            //1) create dataset loop version


            // loop ??
//            XYSeriesCollection dataset = new XYSeriesCollection();
//
//
//            XYSeries series1 = new XYSeries("learn 15 Episodes");
//            for (int i=0;i<al[0].size(); i++){
//                series1.add(al[0].get(i), al[1].get(i));
//            }
//            dataset.addSeries(series1);
//
//            XYSeries series2 = new XYSeries(" Learn 30 Episodes  ");
//            for (int i=0;i<al[2].size(); i++){
//                series2.add(al[2].get(i), al[3].get(i));
//            }
//            dataset.addSeries(series2);




             //* loop version:  the object of dataset can have the same name!!! (^_^)

//            int numPlots = 5;  //!!!!!!!!!!!!!!!!!!!!
//            int interval = 15;  //!!!!!!!!!!!!!!!!!!!!!!!!!

            XYSeriesCollection dataset = new XYSeriesCollection();
            //layer zero
            if (zeroX != null) {
                XYSeries seriesZero = new XYSeries("learn 0 Episode");
                for (int i=0; i<zeroX.size(); i++){
                    seriesZero.add(zeroX.get(i), zeroY.get(i));
                }
                dataset.addSeries(seriesZero);
            }
            //layer zero end

            // we need 5 plots in the same window
            for(int k = 0; k < numPlots;k++ ){

                XYSeries series = new XYSeries("learn " + (k+1)*interval +" Episodes");

                for (int i=0; i<al[k*2].size(); i++){

                    series.add(al[k*2].get(i), al[k*2 + 1].get(i));

                }
                dataset.addSeries(series);
            }


            // 2) create chart
            JFreeChart chart = ChartFactory.createScatterPlot(title,
                    "Accumulative Actions",
                    "Rewards",
                    dataset);


            // 3) create panel
            ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(1000,500));
            setContentPane(chartPanel);

        }

    }


//    /**Smooth the line by averaging
//     * @param list: the target to be smoothed
//     * @param groupLength: calculate the average value of every 'groupLength' elements.
//     * @return a new list.
//     */
//    private ArrayList<Double> SmoothByAverage(ArrayList<Double> list, int groupLength){
//        ArrayList<Double> newList = new ArrayList<>();
//        int length = list.size();
//
//        for (int j=0; j<(length/groupLength); j++){
//
//            double average;
//            double sum =0;
//
//            for (int i=0;i<groupLength;i++){
//                /*
//                 * operation here:
//                 * how do you want to do with the list elements
//                 */
//                sum += list.get(j * groupLength + i);
//            }
//            average = sum/groupLength;
//
//            for (int i = 0; i<groupLength; i++){
//                newList.add(j*groupLength+i, average);
//            }
//
//        }
//        return newList;
//
//    }

    /**Smooth the line by sliding
     * @param oldList: original list
     * @param slidingWindow: the size of sliding window
     * @return: a new list
     */
    public static ArrayList<Double> SmoothBySlide(ArrayList<Double> oldList, int slidingWindow){
        ArrayList<Double> newList = new ArrayList<>(); // vector

        int length = oldList.size();

        /* phase 1:
         * Slide the fixed window.
         * Slide (length-group+1) times.
         */
        for(int i=0;i<(length-slidingWindow+1); i++){

            double sumTmp = 0;
            double average;
            for(int j=0; j<slidingWindow; j++){
                sumTmp += oldList.get(i+j);
            }
            average = sumTmp/slidingWindow;
            newList.add(i, average);
        }

        /* phase 2:
         * Slide the window with decreased size
         */
        for(int j=1;j<slidingWindow;j++){
            double sumTmp = 0;
            double average;
            for(int k=j;k<slidingWindow;k++){
                sumTmp += oldList.get(length-k);
            }
            average = sumTmp/(slidingWindow-j);
            newList.add(length-(slidingWindow -j), average);
        }

        return newList;

    }

//    /**
//     *Split an ArrayList into sub-lists
//     * @param al: an ArrayList to be operated
//     * @param n: the number of elements in one sublist
//     */
//    private void SplitThenOperate(ArrayList<Double> al, int...n){
//
//        // for the first subList:
//        System.out.println(al.subList(0, n[0]));
//        //operations on that sublist...
//        int tmp = n[0];
//
//        //for the rest sub lists
//        for(int i = 1;i<n.length;i++){
//            System.out.println(al.subList(tmp, tmp + n[i]));
//            //operations on that sublist...
//            tmp += n[i];
//        }
//
//    }

    /**
     *
     * @param title
     * @param legend
     * @param xAxisLabel
     * @param yAxisLabel
     * @param chooseMethod
     * @param slidingWindow
     * @param numPlots
     * @param interval
     * @param zeroX
     * @param zeroY
     * @param al
     */
    public static void callPlot(String title,
                                String legend,

                                String xAxisLabel,
                                String yAxisLabel,

                                int chooseMethod,
                                int slidingWindow,
                                int numPlots,
                                int interval,

                                ArrayList<Double> zeroX,
                                ArrayList<Double> zeroY,
                                ArrayList<Double>...al){

        Plot demo = new Plot(title);

        if(chooseMethod == 1)
            demo.PlotLineChart(null, al[1], title, legend, xAxisLabel, yAxisLabel);

//        else if(chooseMethod == 2)
//            //X is [0,1,2,...]; Y is provided and smoothed
//            demo.PlotLineChart(null, SmoothBySlide(al[1], slidingWindow), title);

        else if(chooseMethod == 3)
            //X is actionBar, Y is smoothed rewardBar
            demo.PlotScatterChart(al[0], SmoothBySlide(al[1], slidingWindow), legend);

        else if(chooseMethod == 4)
            demo.PlotScatterCharts(numPlots, interval, title, zeroX, zeroY, al);

        else if(chooseMethod == 6)
            demo.PlotLineChart(null, al[0], title, legend, xAxisLabel, yAxisLabel);

        else if(chooseMethod == 7)
            demo.PlotMultipleLineChart(numPlots, interval, title, zeroX, zeroY,al);

        else if(chooseMethod == 110)
            demo.PlotJustForPaper(title, al);

        else
            System.out.println("Choice between 1, 2, 3  and 4");

        demo.pack();
        RefineryUtilities.positionFrameOnScreen(demo,1,0.5);
        demo.setVisible(true);
    }

}
