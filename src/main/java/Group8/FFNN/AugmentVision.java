package Group8.FFNN;

import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.List;

public class AugmentVision {

    List<Point> cornerPoint;
    ArrayList<LinePercept> linePercepts;

    public AugmentVision(ObjectPercept[] rays){

        linePercepts = new ArrayList<LinePercept>();
        checkIfOnSameLine(rays);

    }

    public void checkIfOnSameLine(ObjectPercept[] rays){

        boolean isLine = false;

        for(int i=2; i<rays.length; i++){

            if(rays[i-2].getType() == ObjectPerceptType.Wall
            && rays[i-1].getType() == ObjectPerceptType.Wall
            && rays[i].getType() == ObjectPerceptType.Wall
            ){
                Point A = rays[i-2].getPoint();
                Point B = rays[i-1].getPoint();
                Point C = rays[i].getPoint();

                Distance AB = new Distance(A, B);
                Distance AC = new Distance(A, C);
                Distance BC = new Distance(B, C);

                if( Math.abs(AB.getValue() + AC.getValue() - BC.getValue()) <= 0.01
                || Math.abs(AB.getValue() + BC.getValue() - AC.getValue()) <= 0.01
                || Math.abs(BC.getValue() + AC.getValue() - AB.getValue()) <= 0.01
                ){  //There is a line, create that information
                  if(!isLine){  //New line
                      System.out.println("There is a new line between A: " + A + ", B: " + B + ", and C: " + C);
                      LinePercept line = new LinePercept(A, B, C);
                      linePercepts.add(line);
                      isLine = true;
                  } else {      //Existing line needing to be extended
                      System.out.println("There is an extended line between A: " + A + ", B: " + B + ", and C: " + C);
                      linePercepts.get(linePercepts.size()-1).adjustLine(A, B, C);
                  }

                } else { //Not a line, breaks the true loop
                    isLine = false;
                }

            }

        }



    }



}
