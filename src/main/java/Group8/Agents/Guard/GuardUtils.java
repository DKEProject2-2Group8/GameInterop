package Group8.Agents.Guard;

import Interop.Action.GuardAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Action.Sprint;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GuardUtils {
    public static final double EPS = 1e-6;
    public static double THRESHOLD;

    private static boolean init = false;

    /**
     * Creates an ordered list of all actions required to make a rotation of the specified amount
     * @param percepts Description of the environment as observed by the corresponding entity
     * @param rot The required rotation represented as an Angle object
     * @see Angle
     * @return Ordered list of GuardsActions corresponding to rotation specified by the angle
     */
    public static List<GuardAction> generateRotationSequence(GuardPercepts percepts, Angle rot){
        final Angle MAX_ROTATION = percepts.getScenarioGuardPercepts().getScenarioPercepts().getMaxRotationAngle();
        List<GuardAction> actionList = new ArrayList<>();
        double radiansLeft = rot.getRadians();
        if(radiansLeft > 0) {
            while (radiansLeft != 0) {
                if (radiansLeft > MAX_ROTATION.getRadians()) {
                    actionList.add(new Rotate(MAX_ROTATION));
                    radiansLeft -= MAX_ROTATION.getRadians();
                } else {
                    actionList.add(new Rotate(Angle.fromRadians(radiansLeft - EPS)));
                    radiansLeft = 0;
                }
            }
        }
        else if(radiansLeft < 0){
            while (radiansLeft != 0) {
                if (Math.abs(radiansLeft) > MAX_ROTATION.getRadians()) {
                    actionList.add(new Rotate(Angle.fromRadians(-MAX_ROTATION.getRadians())));
                    radiansLeft += MAX_ROTATION.getRadians();
                } else {
                    actionList.add(new Rotate(Angle.fromRadians(radiansLeft + EPS)));
                    radiansLeft = 0;
                }
            }
        }
        return actionList;
    }

    public static final Angle correctDirection(Angle angle){
        Angle rot;
        if(angle.getRadians() >= Math.PI){
            rot = Angle.fromRadians(-((Math.PI * 2) - angle.getRadians()));
        } else {
            rot = angle;
        }
        return rot;
    }

    public static final boolean predictCollision(GuardPercepts percepts){
        if(!init){
            init = true;
            THRESHOLD = percepts.getVision().getFieldOfView().getRange().getValue();
        }
        //System.out.println("Check");
        List<ObjectPercept> objectPercepts = (List<ObjectPercept>) setToList(percepts.getVision().getObjects().getAll());
        List<ObjectPercept> colliders = new ArrayList<>();
        for (ObjectPercept obj:
                objectPercepts) {
            if(obj.getType() != ObjectPerceptType.EmptySpace && !obj.getType().isAgent() && obj.getType() != ObjectPerceptType.TargetArea){
                colliders.add(obj);
            }
        }
        if (!colliders.isEmpty()) {
            for (ObjectPercept o :
                    colliders) {
                Distance d = new Distance(new Point(0, 0), o.getPoint());
                //System.out.println(String.format("Distance to object: %f of type: %s",d.getValue(),o.getType()));
                if (d.getValue() <= THRESHOLD * 0.4) {
                    return true;
                }
            }
        }
        return false;
    }

    // Utility function stolen from g9 code
    public static double getSpeedModifier(GuardPercepts GuardPercepts)
    {
        SlowDownModifiers slowDownModifiers = GuardPercepts.getScenarioGuardPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(GuardPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(GuardPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(GuardPercepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }

    public static Sprint generateMaxSprint(GuardPercepts percepts){
        return new Sprint(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
    }

    public static Move generateMaxMove(GuardPercepts percepts){
        return new Move(new Distance(percepts.getScenarioGuardPercepts().getMaxMoveDistanceGuard().getValue() * getSpeedModifier(percepts)));
    }

    public static List<?> setToList(Set<?> set){
        List list = new ArrayList<>();
        set.forEach((a) -> list.add(a));
        return list;
    }
}
