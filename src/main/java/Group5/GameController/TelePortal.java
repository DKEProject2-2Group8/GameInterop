package Group5.GameController;

import Interop.Geometry.Point;
import Interop.Percept.Vision.ObjectPerceptType;

public class TelePortal extends Area {

    protected int yTarget;
    protected int xTarget;
    protected double outOrientation;

    public TelePortal(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int targetX, int targetY){
        super(x1, y1, x2, y2,x3, y3, x4, y4, ObjectPerceptType.Teleport);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = 0.0;
    }

    public TelePortal(int x1, int y1, int x2, int y2, int x3, int y3, int x4, int y4, int targetX, int targetY, double orient){
        super(x1, y1, x2, y2,x3, y3, x4, y4);
        yTarget=targetY;
        xTarget=targetX;
        outOrientation = orient;
    }

    protected Point getNewLocation(){
        Point target = new Point(xTarget,yTarget);
        return target;
    }

    protected double getNewOrientation(){
        return outOrientation;
    }
}