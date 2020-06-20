package Group8.PathFinding;

import Interop.Agent.Intruder;
import Interop.Geometry.Point;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;

import java.util.*;

public class VisionPredictor {

    Set<ObjectPercept> perception;

    public VisionPredictor(IntruderPercepts percepts) {

        perception = percepts.getVision().getObjects().getAll();

    }

    public int MakeFFNN(){
        Iterator<ObjectPercept> iterator = perception.iterator();


        /*while(iterator.hasNext()){
            System.out.println(i + " " +iterator.next().getPoint());
            i++;
        }*/

        ObjectPercept[] rays = new ObjectPercept[perception.size()];
        for(int i=0; i<perception.size(); i++){
            rays[i] = iterator.next();
        }

        for(int i=0; i<rays.length; i++){
            System.out.println(i + ": " + rays[i]);
        }

        System.out.println("____________________________________________________");
        System.out.println("Sorted:");
        rays = bubbleSort(rays);
        for(int i=0; i<rays.length; i++){
            System.out.print(i + ": " + rays[i]);
            testDistance(rays[i].getPoint(), true);
        }

        //Future input layer
        /**
         * Bag 1 -X : small Dist
         * Bag 2 X : small Dist
         * Bag 3 -X : large dist
         * Bag 4 X : large Dist
         * Bag 5 X near 0 : small and large dist
         */
        double[] bags = new double[5];

        for(int i=0; i<rays.length; i++){
            if(rays[i].getPoint().getX() < -0.2){
                if(testDistance(rays[i].getPoint(), false) < 3){
                    bags[0] += 1;
                } else {
                    bags[1] += 1;
                }
            } else if(rays[i].getPoint().getX() > 0.2){
                if(testDistance(rays[i].getPoint(), false) < 3){
                    bags[2] += 1;
                } else {
                    bags[3] += 1;
                }
            } else {
                bags[4] += 1;
            }
        }

        double max = 0;
        for(int i=0; i<bags.length; i++){
            if(max<bags[i]){
                max = bags[i];
            }
        }
        for(int i=0; i<bags.length; i++){
            bags[i] = bags[i] / max;
        }

        printBags(bags);

        int choice = FFNN(bags);
        /**
         * If choice is 0 --> left
         * If choice is 1 --> straight
         * If choice is 2 --> right
         */

        //System.out.println(new Perceptron().Activation(1));   //Tests the perceptron
        return choice;
    }

    public static int FFNN(double[] bags){


        //Perceptron[] hiddenLayer = new Perceptron[3];
        double[] sumHidden = new double[3];
        int[] summaHidden = new int[3];
        Perceptron[] outputLayer = new Perceptron[3];
        double[] sumOutput = new double[3];
        double[] summaOutput = new double[3];

        double[][] weights1 = {
                {0.81, 0.11, 0.8},
                {0.79, 0.7, 0.14},
                {0.10, 0.1, 0.89},
                {0.8, 0.25, 0.67},
                {0.32, 0.57, 0.11}
        };

        double[][] weights2 = {
                {0.7, 0.8, 0.85},
                {0.45, 0.11, 0.44},
                {0.91, 0.4, 0.5}
        };
        System.out.println("Hidden Layer 1 output: ");

        for(int i=0; i<sumHidden.length; i++){
            for(int j=0; j<weights1.length; j++){
                if(new Perceptron().Activation(weights1[j][i] * new Perceptron().Activation(bags[j])) > 0.5){
                    sumHidden[i] += new Perceptron().Activation(weights1[j][i] * new Perceptron().Activation(bags[j]));
                    summaHidden[i] += 1;
                }
            }
            if(summaHidden[i]==0){
                sumHidden[i] = 0;
            } else {
                sumHidden[i] = sumHidden[i] / summaHidden[i];
            }
            System.out.print(i + " " + sumHidden[i] + ", ");
        }
        System.out.println();

        //Loop 2
        for(int i=0; i<sumOutput.length; i++){
            for(int j=0; j<weights2.length; j++){
                if(new Perceptron().Activation(weights2[j][i] * new Perceptron().Activation(sumHidden[j])) > 0.5){
                    sumOutput[i] += new Perceptron().Activation(weights2[j][i] * new Perceptron().Activation(sumHidden[j]));
                    summaOutput[i] += 1;
                }
            }
            if(summaOutput[i]==0){
                sumOutput[i] = 0;
            } else {
                sumOutput[i] = sumOutput[i] / summaOutput[i];
            }
            System.out.print(i + " " + sumOutput[i] + ", ");
        }
        System.out.println();

        double max = 0;
        int choice = 0;
        for(int i=0; i<sumOutput.length; i++){
            if(sumOutput[i] >= max){
                max = sumOutput[i];
                choice = i;
            }
        }

        System.out.println("The agent decides to go: " + choice);

        return choice;

    }

    public static ObjectPercept[] bubbleSort(ObjectPercept[] arr) {
        int n = arr.length;
        ObjectPercept temp;
        for (int i = 0; i < n; i++) {
            for (int j = 1; j < (n - i); j++) {
                if (arr[j - 1].getPoint().getX() > arr[j].getPoint().getX()) {
                    temp = arr[j - 1];
                    arr[j - 1] = arr[j];
                    arr[j] = temp;
                }

            }
        }

        return arr;
    }

    public static double testDistance(Point point, boolean print){

        double x = point.getX();
        double y = point.getY();

        double dist = Math.sqrt( Math.abs(x*x + y*y));

        if(print) {
            System.out.println(" Distance from Agent to point is: " + dist);
        }
        return dist;

    }

    public static void printBags(double[] bags){

        System.out.println("_____________________________________________________________________");
        System.out.println("Display Bags");
        for(int i=0; i<bags.length; i++){
            System.out.println("Bag " + i + " has " + bags[i] + " elements.");
        }

    }

    public void createLine(IntruderPercepts percepts){

        Point first = percepts.getVision().getObjects().getAll().iterator().next().getPoint();

    }

}