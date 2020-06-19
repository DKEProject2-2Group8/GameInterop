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

    public static final boolean VERBOSE = true;

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

    private boolean isCircumNavigating = false;

    private enum Phase{
        Explore, Chase,CircumNav
    }

    public GuardFSM(GuardPercepts percepts) {
        actionQueue = new LinkedList<>();
        prioQueue = new LinkedList<>();
        currentPercepts = percepts;
        angle = Angle.fromRadians(Math.PI*2*Game._RANDOM.nextDouble());
        chaseStrategy = new ChaseStrategy(prioQueue);
        startAngle = false;
        stepsToTarget = 0;
    }

    public GuardAction getMoveGuard(GuardPercepts percepts, Angle currentAngle) {

        if (VERBOSE) {
            System.out.println("--------------- New guard move query ---------------\n");
        }

        currentPercepts = percepts;

        // Check for imminent collision
        boolean predictCollision = predictCollision(percepts);
        if (VERBOSE) {
            System.out.println(String.format("Predicting collision: %b",predictCollision));
        }
        if(predictCollision){
            if(this.phase != Phase.Chase) {
                this.phase = Phase.CircumNav;
                if (VERBOSE) {
                    System.out.println(String.format("Transition to Circumnavigation because predictCollision: %b",predictCollision));
                }

            }
            // Reset exploration
            resetExploration();
        } else {
            isCircumNavigating = false;
            getIntruderVisionPercepts(percepts);
            if(intruderPercepts.size() > 0){
                this.phase = Phase.Chase;
                prioQueue.clear();
            }
            else{
                this.phase = Phase.Explore;
            }
            if (VERBOSE) {
                System.out.println(
                        String.format("No longer CircumNavigating (isCircumNavigating: %b), current phase: %s"
                                ,isCircumNavigating,phase.toString()));
            }
        }
        if(!percepts.wasLastActionExecuted() && this.phase != Phase.Chase){
            if (VERBOSE) {
                System.out.println(
                        String.format("Either the last action was not executed(%b) or the current phase is chasing(%b)",
                                !percepts.wasLastActionExecuted(),this.phase != Phase.Chase));
            }
            prioQueue.clear();
            prioQueue.addAll(generateRotationSequence(percepts,Angle.fromRadians(COLLISION_ROT)));
            return prioQueue.poll();
        }
        else {

            if (!prioQueue.isEmpty()) {
                if (VERBOSE) {
                    System.out.println(
                            String.format("PrioQueue was not empty, popping action"));
                }
                return prioQueue.poll();
            } else if (!actionQueue.isEmpty() && !isCircumNavigating) {
                if (VERBOSE) {
                    System.out.println(
                            String.format("actionQueue was not empty (Also not CircumNavigating, popping action)"));
                }
                return actionQueue.poll();
            } else {
                if (VERBOSE) {
                    System.out.println(
                            String.format("Either all queues are empty or we are CircumNavigating (isCircumNavigating: %b)"
                                    ,isCircumNavigating));
                }
                if (this.phase == Phase.CircumNav) {
                    isCircumNavigating = true;
                    if (VERBOSE) {
                        System.out.println(
                                String.format("Staring CircumNavigation calculations"));
                    }
                    // If a collision was predicted and it has not yet been handled, it needs to be handled first here!
                    ObjectPercepts visionPercepts = percepts.getVision().getObjects();
                    List<ObjectPercept> longEmpty = getLongEmptyList(visionPercepts,percepts);
                    if(longEmpty.isEmpty()){
                        // No rays that have maxlength point to EmptySpace
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("No maximum length rays pointing to empty space, rotating away from colliders"));
                        }

                        // Rotate away from colliders
                        prioQueue.addAll(generateRotationSequence(percepts, Angle.fromRadians(COLLISION_ROT)));
                    } else {
                        // There is at least on ray of maxlength that points to EmptySpace
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("At least one ray of maximum length pointing to empty space"));
                        }
                        ObjectPercept target = longEmpty.get(0);
                        Angle rotationAngle = Angle.fromRadians(Utils.clockAngle(target.getPoint().getX(),target.getPoint().getY()));
                        prioQueue.addAll(generateRotationSequence(percepts,rotationAngle));
                        prioQueue.add(generateMaxMove(percepts));
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("Generated actions to go towards the long ray pointing to empty space, prioQueue length: %d"
                                            ,prioQueue.size()));
                        }
                        return prioQueue.poll();
                    }

                } else if (this.phase == Phase.Chase) {
                    if (VERBOSE) {
                        System.out.println(
                                String.format("Entered Chasing state"));
                    }
                    getIntruderVisionPercepts(percepts);
                    // Check if there is still a intruder in vision
                    if(intruderPercepts.size() == 0){
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("No intruder in vision, transitioning to Exploration"));
                        }
                        this.phase = Phase.Explore;
                    } else {
                        // Intruder in vision
                        // Handle chasing
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("Intruder in vision, chasing"));
                        }
                        chaseStrategy.handle(percepts, intruderPercepts);
                    }

                } else if (this.phase == Phase.Explore) {
                    if (VERBOSE) {
                        System.out.println(
                                String.format("Entering exploration phase"));
                    }
                    // Do we see an intruder? If so start chasing

                    getIntruderVisionPercepts(percepts);
                    if(intruderPercepts.size() > 0){
                        if (VERBOSE) {
                            System.out.println(
                                    String.format("Intruder in vision while in phase: %s",phase.toString()));
                        }
                        this.phase = Phase.Chase;
                        getIntruderVisionPercepts(percepts);
                        chaseStrategy.handle(percepts,intruderPercepts);
                        return prioQueue.poll();
                    }

                    if (VERBOSE) {
                        System.out.println(
                                String.format("Deploying exploration strategy"));
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

                if (VERBOSE) {
                    System.out.println(
                            String.format("Backup deployed"));
                }
                return generateMaxMove(percepts);
            }
        }
    }

    private void getIntruderVisionPercepts(GuardPercepts percepts) {
        ObjectPercepts visionPercepts = percepts.getVision().getObjects();
        ObjectPercepts intruderObjectPercepts = visionPercepts.filter(p -> p.getType() == ObjectPerceptType.Intruder);
        intruderPercepts = perceptsToArrayList(intruderObjectPercepts);
    }

    private void resetExploration(){
        startAngle = false;
        stepsToTarget = 0;
        updateTargetAngle();
        actionQueue.clear();
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
        if (GuardFSM.VERBOSE) {
            System.out.println(
                    String.format("Actions for chasing strategy generated, prioQueue length: %d",actionQueue.size()));
        }
    }
}