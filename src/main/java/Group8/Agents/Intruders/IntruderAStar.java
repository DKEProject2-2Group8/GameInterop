package Group8.Agents.Intruders;

import Group9.Game;
import Interop.Action.DropPheromone;
import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Smell.SmellPerceptType;

public class IntruderAStar implements Intruder {
    private double TotalTurn = 0; //total angle between the direction we're looking and the object
    private double angle = 0; //the angle we will actually turn this turn

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        TotalTurn = percepts.getTargetDirection().getRadians();
        System.out.println("To turn = " + TotalTurn);
        TotalTurn = (double)Math.round(TotalTurn * 100000d) / 100000d;

        if(TotalTurn > Math.toRadians(7))
        {
            if(TotalTurn>Math.PI/4){
                angle = Math.PI/4;
                System.out.println("Turning: "+angle);
            }
            else{
                angle = TotalTurn;
            }
            System.out.println("turned");
            return new Rotate(Angle.fromRadians(angle));
        }
        else {
            System.out.println("moved");
            return new Move(new Distance(0.1));
        }
    }
}