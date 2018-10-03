package myProj;

import plot.Plot;

import java.io.*;
import java.util.ArrayList;
//Class ReadFiles is in the same package as Class App

//public class App_targetOnly {


//    public static void main(String[] args) throws IOException {
//
//        String parentPath = "/media/junhui/DATA/maven/gridWorld/CurriculumLearning/TESTTL_5/";   //no such file !!!!!!!!!
//        //Read data from file:
//        String filePath_noTransferTarget = parentPath + "outputTarget_noTransfer/return.txt";
//        String filePath_fullyTransfer = parentPath + "outputTarget_full_Transfer/return.txt";
//        String filePath_6_Transfer = parentPath + "outputTarget_6_Transfer/return.txt";
//        String filePath_4_Transfer = parentPath + "outputTarget_4_Transfer/return.txt";
//        String filePath_2_Transfer = parentPath + "outputTarget_2_Transfer/return.txt";
////        String filePath_15_Transfer = parentPath + "outputTarget_15_Transfer/return.txt";
//
//
//
//        ArrayList< ArrayList<Double> > container_noTransferTarget;
//        ArrayList< ArrayList<Double> > container_fullyTransfer;
//
//        ArrayList< ArrayList<Double> > container_6_Transfer;
//        ArrayList< ArrayList<Double> > container_4_Transfer;
//        ArrayList< ArrayList<Double> > container_2_Transfer;
////        ArrayList< ArrayList<Double> > container_15_Transfer;
//
//
//        ReadFiles readfiles_noTransferTarget = new ReadFiles();
//        ReadFiles readfiles_fullyTransfer= new ReadFiles();
//
//        ReadFiles readfiles_6_Transfer = new ReadFiles();
//        ReadFiles readfiles_4_Transfer = new ReadFiles();
//        ReadFiles readfiles_2_Transfer = new ReadFiles();
////        ReadFiles readfiles_15_Transfer = new ReadFiles();
//
//
//
//        container_noTransferTarget = readfiles_noTransferTarget.ReadDataFromFiles(filePath_noTransferTarget,20,500);
//        container_fullyTransfer = readfiles_fullyTransfer.ReadDataFromFiles(filePath_fullyTransfer,20,500);
//        container_6_Transfer = readfiles_6_Transfer.ReadDataFromFiles(filePath_6_Transfer,20,500);
//        container_4_Transfer = readfiles_4_Transfer.ReadDataFromFiles(filePath_4_Transfer,20,500);
//        container_2_Transfer = readfiles_2_Transfer.ReadDataFromFiles(filePath_2_Transfer,20,500);
////        container_15_Transfer = readfiles_15_Transfer.ReadDataFromFiles(filePath_2_Transfer,20,500);
//
//
//
//        //scatter plot: rewardBar over actionBar:
////        container.get(1); //scatter y length 200
////        container.get(0); //scatter x length 200
//
//
//        //Plot scatter
//        Plot.callPlot("TRANSFER MAP_1 TO MAP_2",
//                4,
//                30,
//                5,
//                2,
//
//                container_fullyTransfer.get(0), //x
//                Plot.SmoothBySlide(container_fullyTransfer.get(1), 30), //y
//
//
//                container_6_Transfer.get(0),
//                Plot.SmoothBySlide(container_6_Transfer.get(1),30),
//
//                container_4_Transfer.get(0),
//                Plot.SmoothBySlide(container_4_Transfer.get(1),30),
//
//                container_2_Transfer.get(0),
//                Plot.SmoothBySlide(container_2_Transfer.get(1),30),
//
//                container_noTransferTarget.get(0),  //x
//                Plot.SmoothBySlide(container_noTransferTarget.get(1),30)
//
//
//                );
//
//
//        System.out.println(container_noTransferTarget);

//    }
//
//}