package Group8.Agents.Intruders;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Agent.Intruder;
import Interop.Geometry.Angle;
import Interop.Geometry.Direction;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;

public class IntruderAStar implements Intruder {
    int i = 0;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        //System.out.println(percepts.getTargetDirection().getRadians());

        if(i==0) {
            i = 1;
            return new Rotate(Angle.fromDegrees(300));
        }
        else{
            return new Move(new Distance(0.1));
        }
    }
}
