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
    int i = 0;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        //System.out.println(percepts.getTargetDirection().getRadians());
        if(i<=10)
        {
            System.out.println("true");
            i++;
            return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()));
        }
        else {
            return new Move(new Distance(0.1));
        }
    }
}