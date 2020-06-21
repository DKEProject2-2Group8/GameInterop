package Group8.Agents.Intruder;

import Group8.FFNN.VisionPredictor;
import Group8.PathFinding.SimplePathfinding;
import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Percept.IntruderPercepts;

public class FFNN implements Intruder {

    private static VisionPredictor visionPredictor;

    /*@Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return new VisionPredictor(percepts).MakeFFNN();
    }*/

    @Override
    public IntruderAction getAction(IntruderPercepts percepts){
        if(visionPredictor == null){
            visionPredictor = new VisionPredictor(percepts);
        }
        return visionPredictor.MakeFFNN();
    }
}
