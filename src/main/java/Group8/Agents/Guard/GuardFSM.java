package Group8.Agents.Guard;


import Group9.Game;
import Interop.Action.GuardAction;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Utils.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static Group8.Agents.Guard.GuardUtils.*;


/**
 * @author Luc
 * Q-based state machine thingy
 */
public class GuardFSM {

    private double COLLISION_ROT = Math.PI;

    // Represents the queue containing actions with low priority
    private LinkedList<GuardAction> actionQueue;
    // Represents the queue containing actions with high priority
    private LinkedList<GuardAction> prioQueue;

    private GuardPercepts currentPercepts;

    private EscapeStrategy escapeStrategy;
    private Phase phase = Phase.Explore;

    private List<List<ObjectPercept>> generalObstructions;
    private List<ObjectPercept> objectPercepts;
    private List<ObjectPercept> agentObstructions;

    private boolean check = false;




    private enum Phase{
        Explore,Escape,CircumNav
    }


    public GuardFSM(GuardPercepts percepts) {
        actionQueue = new LinkedList<>();
        prioQueue = new LinkedList<>();
        currentPercepts = percepts;
    }

    public GuardAction getMoveGuard(GuardPercepts percepts) {
        currentPercepts = percepts;
        boolean ic = predictCollision(percepts);
        System.out.println(String.format("Collision is imminent: %b",ic));
        if(ic){
            this.phase = Phase.CircumNav;
            actionQueue.clear();
        }
        if(!prioQueue.isEmpty() && percepts.wasLastActionExecuted()){
            actionQueue.clear();
            return prioQueue.poll();
        }
        else {
            check = true;
            if(!percepts.wasLastActionExecuted()){
                generateRotationSequence(percepts, Angle.fromRadians(COLLISION_ROT));
            }
            if(this.phase == Phase.CircumNav){
                // Col is the closest collider
                ObjectPercept col = null;
                generateObstructions();
                for (ObjectPercept o :
                        objectPercepts) {
                    if(col == null){
                        col = o;
                    }
                    else{
                        if(new Distance(new Point(0,0),o.getPoint()).getValue() < new Distance(new Point(0,0),col.getPoint()).getValue()){
                            col = o;
                        }
                    }

                }
                if(col != null){
                    Angle angle = Angle.fromRadians(Utils.clockAngle(col.getPoint().getX(), col.getPoint().getY()));
                    COLLISION_ROT = Math.PI/2 - angle.getRadians();
                    if(angle.getRadians() > Math.PI/4){
                        // Walk parallel to wall to right
                        prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(-COLLISION_ROT)));
                        prioQueue.add(generateMaxMove(percepts));
                    }
                    else if(angle.getRadians() < Math.PI/4){
                        // Walk parallel to wall to left
                        prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(COLLISION_ROT)));
                        prioQueue.add(generateMaxMove(percepts));
                    }
                    else{
                        if(angle.getRadians() == Math.PI/4){
                            if(Game._RANDOM.nextBoolean()){
                                // Go left
                                prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(-COLLISION_ROT)));
                            }
                            else{
                                // Go right
                                prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(COLLISION_ROT)));
                            }
                            prioQueue.add(generateMaxMove(percepts));
                        }
                    }

                }
                else{
                    if(!check) {
                        prioQueue.add(generateMaxMove(percepts));
                        actionQueue.addAll(generateRotationSequence(percepts, Angle.fromRadians(-COLLISION_ROT)));
                    }else{
                        switchState();
                    }
                }

            }
            else if (this.phase == Phase.Explore) {
                // Exploration strategy
                if (actionQueue.peek() != null) {
                    return actionQueue.poll();
                }

                generateObstructions();

                // Handle seeing other agents
                if (!agentObstructions.isEmpty()) {
                    // Obstruction seen
                    for (ObjectPercept o :
                            agentObstructions) {
                        if (o.getType() == ObjectPerceptType.Guard) {
                            // Switch state and generate escape behaviour
                            switchState();
                            // Execute first action generated by escape behaviour
                            return prioQueue.poll();
                            // This already returns an action in order to escape the if statements, next iteration we will execute different code since the phase has shifted
                        } else {
                            // Guard in vision
                            // TODO : Implement
                            // generateSplittingSequence();
                        }
                    }
                }

            } else if (this.phase == Phase.Escape) {
                // Escape strategy
                escapeStrategy.handle(percepts, false);
            }


            // Return action from action queue
            if (actionQueue.peek() == null) {
                //bLine();
                // TODO: figure out a replacement function for this
            }
            return actionQueue.poll();
        }
    }

    private void generateObstructions(){
        // Obstruction management
        generalObstructions = checkObstructions(currentPercepts.getVision().getObjects().getAll());
        objectPercepts = generalObstructions.get(0);
        agentObstructions = generalObstructions.get(1);
    }

    private List<List<ObjectPercept>> checkObstructions(Set<ObjectPercept> obstructions){
        /* Create 2 list where one contains agent-related obstructions and the other contains object-related obstructions   */
        ArrayList<List<ObjectPercept>> lists = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            lists.add(new ArrayList<>());
        }
        for (ObjectPercept obj :
                obstructions) {
            if(obj.getType() != ObjectPerceptType.EmptySpace){
                lists.get(0).add(obj);
            }
            else if(obj.getType().isAgent()){
                lists.get(1).add(obj);
            }
        }
        return lists;
    }

    private void switchState(){
        if(this.phase == Phase.Explore){
            this.phase = Phase.Escape;
            escapeStrategy = new EscapeStrategy(prioQueue);
            escapeStrategy.handle(currentPercepts,true);
        }
        else{
            this.phase = Phase.Explore;
        }
    }

    private void generateSplittingSequence(){
        // Clear the queue since the splitting tactic overrules earlier generated actions
        actionQueue.clear();


    }

//    private void bLine(){
//        if (!((Math.abs(currentPercepts.getTargetDirection().getRadians())) <= EPS)) {
//            List<GuardAction> actions = GuardUtils.generateRotationSequence(currentPercepts, currentPercepts.getTargetDirection());
//            actionQueue.addAll(actions);
//        }
//        actionQueue.add(generateMaxMove(currentPercepts));
//    }

}

/**
 * @Author Luc
 * Class that represents the strategy that can be used to escape
 */
class EscapeStrategy{

    private GuardPercepts percepts;
    private LinkedList<GuardAction> actionQueue;

    // Initial variable rotation
    private final double IRV = Math.PI/4;
    private final double DEFAULT_ROT_RAD = Math.PI;


    private EscapeStrategy.InitialRotation initialRotation = EscapeStrategy.InitialRotation.Full;

    public EscapeStrategy(LinkedList<GuardAction> actionQueue) {
        this.actionQueue = actionQueue;
    }

    public void handle(GuardPercepts percepts, boolean init){
        // Update percepts
        this.percepts = percepts;
        if(init){
            List<GuardAction> actions = GuardUtils.generateRotationSequence(percepts,getInitialRotation());

            // Add all generated actions to the action queue
            actionQueue.addAll(actions);
        }
        else{
            actionQueue.add(generateMaxMove(percepts));
        }
    }

    // Rotation upon first sight of guard
    private enum InitialRotation{
        Full,Var,Calculated,None
    }

    private Angle getInitialRotation(){
        switch (initialRotation){
            case Full:
                return Angle.fromRadians(Math.PI);
            case Var:
                return Angle.fromRadians(IRV);
            case Calculated:
                return calcEscAngle();
            case None:
                return Angle.fromRadians(0);
        }
        return Angle.fromRadians(DEFAULT_ROT_RAD);
    }

    private Angle calcEscAngle() {
        // TODO: Calculate the angle at which you see the guard and then rotate away
        return Angle.fromRadians(DEFAULT_ROT_RAD);
    }
}
