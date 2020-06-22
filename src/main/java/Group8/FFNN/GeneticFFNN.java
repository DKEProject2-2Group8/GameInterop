package Group8.FFNN;

import Group8.FFNN.Perceptron;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.AreaPercepts;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.awt.geom.Area;
import java.io.*;
import java.util.*;

public class GeneticFFNN {

    IntruderPercepts percepts;
    Set<ObjectPercept> perception;
    private final Angle MAX_ROTATION;
    private int genesIndex;


    public GeneticFFNN(IntruderPercepts percepts) {

        percepts = percepts;
        perception = percepts.getVision().getObjects().getAll();
        MAX_ROTATION = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
        genesIndex = 1;

    }
//throws IOException here
    public GeneticFFNN(IntruderPercepts percepts, int index)  {

        percepts = percepts;
        perception = percepts.getVision().getObjects().getAll();
        MAX_ROTATION = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
        genesIndex = index;

        if(genesIndex<0 && genesIndex > -99){
            System.out.println("_____________________________WRITE_GENES_INDEX_______________________________");
            String path = "/home/lucas/IdeaProjects/GameInterop6/src/main/java/Group8/FFNN/GenePool/Genes" + genesIndex +  ".txt";
            writeGenes(genesIndex, readGenes(5, 3, path));
        } else if(genesIndex<-99){
            winGame(Math.abs(genesIndex/100));
        }
        genesIndex = Math.abs(genesIndex);

    }

    /*
    *   Some rests of the genetic training are still present, so some parts of the code may look weird but works
     */
    public IntruderAction MakeFFNN(){
        Iterator<ObjectPercept> iterator = perception.iterator();

        this.genesIndex = Math.abs(1);

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
        new AugmentVision(rays);
        //Future input layer
        /**
         * Bag 1 -X : small Dist
         * Bag 2 X : small Dist
         * Bag 3 -X : large dist
         * Bag 4 X : large Dist
         * Bag 5 X near 0 : small and large dist
         */
        double[] bags = new double[5];
        for(int i=0; i<bags.length; i++){
            bags[i] = 0;
        }

        for(int i=0; i<rays.length; i++){
            if(rays[i].getPoint().getX() < -0.2 && rays[i].getType() == ObjectPerceptType.Wall){
                if(testDistance(rays[i].getPoint(), false) < 3){
                    bags[0] += 1;
                } else {
                    bags[1] += 1;
                }
            } else if(rays[i].getPoint().getX() > 0.2  && rays[i].getType() == ObjectPerceptType.Wall){
                if(testDistance(rays[i].getPoint(), false) < 3){
                    bags[2] += 1;
                } else {
                    bags[3] += 1;
                }
            } else if(rays[i].getType() == ObjectPerceptType.Wall){
                bags[4] += 1;
            }
        }

        double max = 0;
        for(int i=0; i<bags.length; i++){
            if(max<bags[i]){
                max = bags[i];
            }
        }
        //Scaling to 1
        if(max != 0) {
            for (int i = 0; i < bags.length; i++) {
                bags[i] = bags[i] / max;
            }
        }

        printBags(bags);

        int choice = -1;


        for(int i=bags.length-1; i>=0; i--){
            if(bags[i]==0){
                System.out.println("Bag is empty: " + i);
                if(i==4){
                    choice = 1;
                } else if(i==3 || i==2){
                    choice = 2;
                } else if(i==1 || i==0){
                    choice = 0;
                }
                break;
            }
        }
        if(choice<0 || true) {
            choice = FFNN(bags, genesIndex);
        }
        /**
         * If choice is 0 --> left
         * If choice is 1 --> straight
         * If choice is 2 --> right
         */

        dispChoice(choice);

        //System.out.println(new Perceptron().Activation(1));   //Tests the perceptron
        //return choice;

        IntruderAction action;

        System.out.println(Angle.fromRadians(percepts.getTargetDirection().getRadians()+0.0001));
        /*if(Math.abs(percepts.getTargetDirection().getRadians()) <= 0.001){
            System.out.println("Works");
        }*/


        if(choice == 1 || Math.abs(percepts.getTargetDirection().getRadians()) <= 0.001){
            return new Move(new Distance(0.25));
        } else if(choice==0){
            return new Rotate(Angle.fromRadians(MAX_ROTATION.getRadians()));
        } else if(choice==2){
            return new Rotate(Angle.fromRadians(-MAX_ROTATION.getRadians()));
        } else {
            return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()+0.0001));
        }

    }

    public static int FFNN(double[] bags, int genesIndex) {


        //Perceptron[] hiddenLayer = new Perceptron[3];
        double[] sumHidden = new double[3];
        int[] summaHidden = new int[3];
        Perceptron[] outputLayer = new Perceptron[3];
        double[] sumOutput = new double[3];
        double[] summaOutput = new double[3];

        double[][] weights1 = {
                {0.81, 0.11, 0.08},
                {0.79, 0.7, 0.14},
                {0.10, 0.1, 0.89},
                {0.8, 0.25, 0.67},
                {0.32, 0.57, 0.11}
        };
        String path = "/home/lucas/IdeaProjects/GameInterop6/src/main/java/Group8/FFNN/GenePool/Genes" + genesIndex +  ".txt";
      //  double[][] weights1 = readGenes(5, 3, path);
        dispGenes(weights1);
        weights1 = mutate(weights1);
       // writeGenes(genesIndex, weights1);

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

    public static void winGame(int index){
        String path = "/home/lucas/IdeaProjects/GameInterop6/src/main/java/Group8/FFNN/GenePool/Genes" + index +  ".txt";
        double[][] weights1 = readGenes(5, 3, path);
        dispGenes(weights1);
        writeGenes(index, weights1);
    }

    public static void writeGenes(int index, double[][] genes){
        int genesIndex = Math.abs(index);
        String path = "/home/lucas/IdeaProjects/GameInterop6/src/main/java/Group8/FFNN/GenePool/Genes" + genesIndex +  ".txt";

        //Uncomment for training, comment for API to discard exception
       // BufferedWriter writer = new BufferedWriter(new FileWriter(path));

        String newGenes = "";
        for(int i=0; i<5;i++){
            for(int j=0; j<3; j++){
                newGenes += " " + genes[i][j];
            }
        }

       // writer.write(newGenes);//save the string representation of the board
       // writer.close();
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

    public static void dispGenes(double[][] weights){

        for(int i=0; i< 5; i++){
            for(int j=0; j< 3; j++){
                System.out.print(weights[i][j] + ", ");
            }
            System.out.println();
        }

    }

    public static double[][] readGenes(int dimX, int dimY, String path) {
        double[][] a = new double[dimX][dimY];

        //Uncomment for training, comment for API to discard exception
       /* Scanner input = new Scanner (new File(path));
        for(int i = 0; i < dimX; ++i)
        {
            for(int j = 0; j < dimY; ++j)
            {
                if(input.hasNextDouble())
                {
                    a[i][j] = input.nextDouble();
                }
            }
        }*/
        return a;
    }

    public static double[][] mutate(double[][] weights){
        Random rand = new Random();
        double mutationRate = 0.05;
        for(int i=0; i<5; i++){
            for(int j=0; j<3; j++){
                int hazard = rand.nextInt(1);
                if (hazard < 0.34) {
                    weights[i][j] += mutationRate;
                } else if(hazard >= 0.34 && hazard <0.7){
                    weights[i][j] -= mutationRate;
                } else{
                    //Nothing
                }
                if(weights[i][j] > 1){
                    weights[i][j] -= mutationRate;
                } else if(weights[i][j] < -1){
                    weights[i][j] += mutationRate;
                }
                System.out.print(weights[i][j] + ", ");
            }
            System.out.println();
        }
        return weights;
    }

    public static void printBags(double[] bags){

        System.out.println("_____________________________________________________________________");
        System.out.println("Display Bags");
        for(int i=0; i<bags.length; i++){
            System.out.println("Bag " + i + " has " + bags[i] + " elements.");
        }

    }

    public static void dispChoice(int choice){

        if(choice==0){
            System.out.println("Agent goes left");
        } else if(choice == 1){
            System.out.println("Agent goes straight");
        } else if(choice == 2){
            System.out.println("Agent goes right");
        } else {
            System.out.println("Agent has a bug");
        }

    }

    public void createLine(IntruderPercepts percepts){

        Point first = percepts.getVision().getObjects().getAll().iterator().next().getPoint();

    }

}