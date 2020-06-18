package Group8.Agents.Guard;


import Group9.Game;
import Interop.Action.GuardAction;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Geometry.Point;
import Interop.Percept.GuardPercepts;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;
import Interop.Percept.Vision.ObjectPercepts;
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

    private ChaseStrategy chaseStrategy;
    private Phase phase = Phase.Explore;

    private ArrayList<ObjectPercept> intruderPercepts;

    private Angle angle;
    private int stepsToTarget;
    private boolean startAngle;
    private final int REQ_STEPS = 5;

    private boolean check = false;
    private boolean isCircumNavigating = false;

    private enum Phase{
        Explore, Chase,CircumNav
    }

    public GuardFSM(GuardPercepts percepts) {
        actionQueue = new LinkedList<>();
        prioQueue = new LinkedList<>();
        currentPercepts = percepts;
        angle = Angle.fromRadians(Math.PI*2*Game._RANDOM.nextDouble());
        startAngle = false;
        stepsToTarget = 0;
    }

    public GuardAction getMoveGuard(GuardPercepts percepts) {
        currentPercepts = percepts;

        if(this.phase == Phase.Chase){
            System.out.println("Chasing");
        }

        // Check for imminent collision
        boolean predictCollision = predictCollision(percepts);
        if(predictCollision){
            if(this.phase != Phase.Chase) {
                this.phase = Phase.CircumNav;
            }
            actionQueue.clear();
        }
        if(!percepts.wasLastActionExecuted() && this.phase != Phase.Chase){
            prioQueue.clear();
            prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(COLLISION_ROT)));
            return prioQueue.poll();
        }
        else {

            if (!prioQueue.isEmpty()) {
                return prioQueue.poll();
            } else if (!actionQueue.isEmpty() && !predictCollision) {
                if(isCircumNavigating){
                    isCircumNavigating = false;
                }
                return actionQueue.poll();
            } else {
                if(isCircumNavigating){
                    isCircumNavigating = false;
                    if(this.phase == Phase.CircumNav){
                        switchState();
                    }
                }
                if (this.phase == Phase.CircumNav) {
                    isCircumNavigating = true;
                    // If a collision was predicted and it has not yet been handled, it needs to be handled first here!
                    ObjectPercepts visionPercepts = percepts.getVision().getObjects();
                    ObjectPercepts colliders = visionPercepts.filter(p -> p.getType() != ObjectPerceptType.EmptySpace);
                    List<ObjectPercept> longEmpty = getLongEmptyList(visionPercepts,percepts);
                    if(longEmpty.isEmpty()){
                        // No rays that have maxlength point to EmptySpace

                    } else {
                        // There is at least on ray of maxlength that points to EmptySpace
                        ObjectPercept target = longEmpty.get(0);
                        Angle rotationAngle = Angle.fromRadians(Utils.clockAngle(target.getPoint().getX(),target.getPoint().getY()));
                        prioQueue.addAll(generateRotationSequence(percepts,rotationAngle));
                        prioQueue.add(generateMaxMove(percepts));
                        return prioQueue.poll();
                    }

                } else if (this.phase == Phase.Chase) {
                    ObjectPercepts visionPercepts = percepts.getVision().getObjects();
                    ObjectPercepts intruderObjectPercepts = visionPercepts.filter(p -> p.getType() == ObjectPerceptType.Intruder);
                    ArrayList<ObjectPercept> intruderPercepts = perceptsToArrayList(intruderObjectPercepts);
                    if(intruderPercepts.size() == 0){
                        switchState();
                    }

                } else if (this.phase == Phase.Explore) {
                    // Do we see an intruder? If so start chasing
                    ObjectPercepts visionPercepts = percepts.getVision().getObjects();
                    ObjectPercepts intruderObjectPercepts = visionPercepts.filter(p -> p.getType() == ObjectPerceptType.Intruder);
                    ArrayList<ObjectPercept> intruderPercepts = perceptsToArrayList(intruderObjectPercepts);
                    this.intruderPercepts = intruderPercepts;
                    if(intruderPercepts.size() > 0){
                        switchState();
                        return prioQueue.poll();
                    }

                    // Getting here means there is no intruder in vision
                    // This is where the exploring method is defined
                    if(!startAngle){
                        startAngle = true;
                        actionQueue.addAll(generateRotationSequence(percepts,angle));
                        actionQueue.add(generateMaxMove(percepts));
                        stepsToTarget++;
                    }else{
                        if(stepsToTarget >= REQ_STEPS){
                            startAngle = false;
                            stepsToTarget = 0;
                            updateTargetAngle();
                        } else{
                            actionQueue.add(generateMaxMove(percepts));
                            stepsToTarget++;
                        }
                    }
                    actionQueue.add(generateMaxMove(percepts));
                }
                if(!actionQueue.isEmpty()){
                    return actionQueue.poll();
                }
                return generateMaxMove(percepts);
            }
        }
    }

    private void updateTargetAngle(){
        angle = Angle.fromRadians(Math.PI*2*Game._RANDOM.nextDouble());
    }

    private ArrayList<ObjectPercept> perceptsToArrayList(ObjectPercepts objectPercepts){
        ArrayList<ObjectPercept> result = new ArrayList<>();
        for (ObjectPercept op :
                objectPercepts.getAll()) {
            result.add(op);
        }
        return result;
    }


    private List<ObjectPercept> getLongEmptyList(ObjectPercepts objectPercepts, GuardPercepts percepts){
        List<ObjectPercept> longEmpty = new ArrayList<>();
        Distance viewRange = percepts.getVision().getFieldOfView().getRange();
        Point p = new Point(0,0);

        for (ObjectPercept o :
                objectPercepts.getAll()) {
            if(p.getDistance(o.getPoint()).getValue() == viewRange.getValue()
                    && o.getType() == ObjectPerceptType.EmptySpace){
                longEmpty.add(o);
            }
        }
        return longEmpty;
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
            this.phase = Phase.Chase;
            prioQueue.clear();
            chaseStrategy = new ChaseStrategy(prioQueue);
            chaseStrategy.handle(currentPercepts,intruderPercepts);
        }
        else{
            this.phase = Phase.Explore;
        }
    }

    private void generateSplittingSequence(){
        // Clear the queue since the splitting tactic overrules earlier generated actions
        actionQueue.clear();


    }

}

/**
 * @Author Luc
 * Class that represents the strategy that can be used to escape
 */
class ChaseStrategy {

    private LinkedList<GuardAction> actionQueue;

    public ChaseStrategy(LinkedList<GuardAction> actionQueue) {
        this.actionQueue = actionQueue;
    }

    public void handle(GuardPercepts percepts, ArrayList<ObjectPercept> intruderPercepts){
        // Implement a strategy to chase an intruder in sight
        ObjectPercept intruder = intruderPercepts.get(0);
        actionQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(Utils.clockAngle(intruder.getPoint().getX(),intruder.getPoint().getY()))));
        actionQueue.add(generateMaxMove(percepts));
    }
}
