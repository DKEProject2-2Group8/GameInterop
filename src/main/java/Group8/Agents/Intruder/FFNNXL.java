package Group8.Agents.Intruder;

import Group8.FFNN.VisionPredictor;
import Group8.FFNN.VisionPredictorXL;
import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Percept.IntruderPercepts;

public class FFNNXL implements Intruder {

    private static VisionPredictorXL visionPredictor;

    @Override
    public IntruderAction getAction(IntruderPercepts percepts){
        if(visionPredictor == null){
            visionPredictor = new VisionPredictorXL(percepts);
        }
        return visionPredictor.MakeFFNN();
    }
}
