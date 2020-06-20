package Group8.Agents.Intruder;

import Group8.FFNN.VisionPredictor;
import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Percept.IntruderPercepts;

public class FFNN implements Intruder {

    @Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return new VisionPredictor(percepts).MakeFFNN();
    }
}
