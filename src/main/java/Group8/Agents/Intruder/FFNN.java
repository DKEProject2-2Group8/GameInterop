package Group8.Agents.Intruder;

import Group8.FFNN.VisionPredictor;
import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Percept.IntruderPercepts;

import java.io.FileNotFoundException;
import java.io.IOException;

public class FFNN implements Intruder {

    private static VisionPredictor visionPredictor;

    /*@Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return new VisionPredictor(percepts).MakeFFNN();
    }*/

    @Override
    public IntruderAction getAction(IntruderPercepts percepts, int finalCount) throws IOException {
        if(visionPredictor == null){
            visionPredictor = new VisionPredictor(percepts);
        }
        return visionPredictor.MakeFFNN(1);
    }


   /* public IntruderAction getAction(IntruderPercepts percepts, int i) throws IOException {
        if(visionPredictor == null){
            visionPredictor = new VisionPredictor(percepts, i);
        }
        return visionPredictor.MakeFFNN(i);
    }*/

   // public void win()

}
