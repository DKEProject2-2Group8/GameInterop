package Group8.Agents.Intruder;

import Group8.FFNN.GeneticFFNN;
import Group8.FFNN.VisionPredictor;
import Interop.Action.IntruderAction;
import Interop.Agent.Intruder;
import Interop.Percept.IntruderPercepts;

import java.io.IOException;

public class FFNNGenetic implements Intruder {

    private static GeneticFFNN visionPredictor;

    /*@Override
    public IntruderAction getAction(IntruderPercepts percepts) {
        return new VisionPredictor(percepts).MakeFFNN();
    }*/

    @Override
    public IntruderAction getAction(IntruderPercepts percepts){
        if(visionPredictor == null){
            visionPredictor = new GeneticFFNN(percepts);
        }
        return visionPredictor.MakeFFNN();
    }
}
