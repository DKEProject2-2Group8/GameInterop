package Group8.FFNN;

import Interop.Geometry.Point;

public class LinePercept {

    Point A;
    Point B;
    boolean first = true;

    public LinePercept(Point a, Point b, Point c){

        adjustLine(a, b, c);

    }

    public void adjustLine(Point a, Point b, Point c){
        double min = 9999;
        double max = -9999;
        int indexMin = -1;
        int indexMax = -1;

        Point[] points = {a, b, c};

        for(int i=0; i< points.length; i++){
            if(min >= points[i].getX()){
                min = points[i].getX();
                indexMin = i;
            }
            if(max <= points[i].getX()){
                max = points[i].getX();
                indexMax = i;
            }
        }

        if(indexMin != indexMax){

            if(first){
                System.out.println("Create new Line...");
                A = points[indexMin];
                B = points[indexMax];
                first = false;
                return;
            }else{
                if (min < A.getX()) {
                    A = points[indexMin];
                    System.out.println("New Start Point is " + A);
                }
                if (max > B.getX()) {
                    B = points[indexMax];
                    System.out.println("New End Point is " + B);
                }
                return;
            }
        } else{
            //Do something
        }


    }

    public Point getA(){return A;}

    public Point getB(){return B;}

}
