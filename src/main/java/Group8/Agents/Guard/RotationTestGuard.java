package Group8.Agents.Guard;

import Interop.Action.GuardAction;
import Interop.Action.Rotate;
import Interop.Agent.Guard;
import Interop.Geometry.Angle;
import Interop.Percept.GuardPercepts;

public class RotationTestGuard implements Guard {
    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        return new Rotate(Angle.fromDegrees(-20));
    }
}
