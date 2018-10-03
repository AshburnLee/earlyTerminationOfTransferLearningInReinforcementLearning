package myProj;

public class Sort_util {

    public static void performAlgorithm(int[] array, Sort obj, int chooseAlg){
        long start = System.nanoTime();

        if (chooseAlg == 1){
            obj.selectionSort(array);
        }else if(chooseAlg == 2){
            obj.shellSort(array);
        }else if(chooseAlg == 3) {
            obj.insertionSort(array);
        }else if(chooseAlg == 4){
            obj.bubbleSort(array);

        }else{
            System.out.println("Invalid choose of algorithm(1,2,3): ");
        }
//
//

        long end = System.nanoTime();
        System.out.println("spent time ms: " + (end-start)/Math.pow(10, 6));
    }

    public static void printArray(int[] array){
        for(int i=0;i<array.length;i++){
            System.out.println(array[i]);
        }
    }

    public static int[] randomArray(int size){
        int[] array = new int[size];
        for (int i=0;i<size;i++) {
            array[i] = (int) Math.ceil(Math.random() * Integer.MAX_VALUE);
        }
        return array;
    }
}
