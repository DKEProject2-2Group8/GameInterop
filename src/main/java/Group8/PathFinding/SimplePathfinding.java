package Group8.PathFinding;

import Interop.Action.IntruderAction;
import Interop.Action.Move;
import Interop.Action.Rotate;
import Interop.Geometry.Angle;
import Interop.Geometry.Distance;
import Interop.Percept.IntruderPercepts;
import Interop.Percept.Scenario.SlowDownModifiers;
import Interop.Percept.Vision.ObjectPercept;
import Interop.Percept.Vision.ObjectPerceptType;


public class SimplePathfinding {

    private final Angle MAX_ROTATION;
    private final double TURN_CONSTANT = 20;

    public SimplePathfinding(IntruderPercepts percepts) {
        MAX_ROTATION = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
        System.out.println(MAX_ROTATION.getDegrees());
    }

    boolean moveForward = false; boolean firstTime = true; boolean startIncr = false;
    int counter = 1;
    public IntruderAction getMoveIntruder(IntruderPercepts percepts) {

        /** Each n times, given by TURN_CONSTANT, the agent will rotate towards the target area,
         *  This makes the circumnavigation works, maybe make it random.
         */
        if(counter % TURN_CONSTANT == 0){
            counter++;
            if(percepts.getTargetDirection().getRadians() >= MAX_ROTATION.getRadians()){
                return new Rotate(Angle.fromRadians(-MAX_ROTATION.getRadians()));
            }
            else {
                return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()+0.0001));
            }
        }
        /**
         * If the Agent has a wall in its field of view it is going to rotate given an angle. This angle could be adjusted
         * To the angle of the wall itself
         */
        for(ObjectPercept obj : percepts.getVision().getObjects().getAll()) {
            if(obj.getType()== ObjectPerceptType.Wall && !firstTime){
                moveForward = true;   startIncr = true;
                return new Rotate(Angle.fromRadians(MAX_ROTATION.getRadians()));
            }
        }
        /**
         * Calls a move forward if the agent is facing the target area OR it is circumnavigating
         */
        if(((Math.abs(percepts.getTargetDirection().getRadians())) <= 0.001) || moveForward){
            if(startIncr){
                counter++;
            }
            firstTime = false;
            return new Move(new Distance(0.25));
        }
        else if(percepts.getTargetDirection().getRadians() >= MAX_ROTATION.getRadians()){
            return new Rotate(Angle.fromRadians(MAX_ROTATION.getRadians()));
        }
        else{
            return new Rotate(Angle.fromRadians(percepts.getTargetDirection().getRadians()+0.0001));
        }
    }
    private double getSpeedModifier(IntruderPercepts intruderPercepts)
    {
        SlowDownModifiers slowDownModifiers = intruderPercepts.getScenarioIntruderPercepts().getScenarioPercepts().getSlowDownModifiers();
        if(intruderPercepts.getAreaPercepts().isInWindow())
        {
            return slowDownModifiers.getInWindow();
        }
        else if(intruderPercepts.getAreaPercepts().isInSentryTower())
        {
            return slowDownModifiers.getInSentryTower();
        }
        else if(intruderPercepts.getAreaPercepts().isInDoor())
        {
            return slowDownModifiers.getInDoor();
        }

        return 1;
    }
}
    //private final LinkedList<IntruderAction> actionQueue;
//    private final Angle MAX_ROT;
//
//
//    public SimplePathfinding(IntruderPercepts percepts) {
//        MAX_ROT = percepts.getScenarioIntruderPercepts().getScenarioPercepts().getMaxRotationAngle();
//        actionQueue = new LinkedList<>();
//    }
//
//    public IntruderAction getMoveIntruder(IntruderPercepts percepts) {
//        while (!actionQueue.isEmpty()){
//            return actionQueue.poll();
//        }
//
//        boolean corAngle = checkAngle(percepts);
//        System.out.println(corAngle);
//        if(corAngle) {
//            // Walk straight forwards as long as you can
////            if (percepts.wasLastActionExecuted()) {
////                return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
////            }
////            else{
////                // Generate the rotational actions
////                generateRotationSequence(Angle.fromRadians(Math.PI * Game._RANDOM.nextDouble()));
////                // Return the first one
////                return actionQueue.poll();
////            }
//                            return new Move(new Distance(percepts.getScenarioIntruderPercepts().getMaxMoveDistanceIntruder().getValue() * getSpeedModifier(percepts)));
//        }
//        else{
//            // Rotate towards the correct angle
//            generateRotationSequence(Angle.fromRadians(-percepts.getTargetDirection().getRadians()));
//            if(!actionQueue.isEmpty()){
//                return actionQueue.poll();
//            }
//            return new NoAction();
//        }
//    }
//
//    private void generateRotationSequence(Angle rot){
//        double degreesLeft = rot.getDegrees();
//        if(degreesLeft > 0) {
//            while (degreesLeft != 0) {
//                if (degreesLeft >= MAX_ROT.getDegrees()) {
//                    actionQueue.add(new Rotate(MAX_ROT));
//                    degreesLeft -= MAX_ROT.getDegrees();
//                } else {
//                    actionQueue.add(new Rotate(Angle.fromDegrees(degreesLeft)));
//                    degreesLeft = 0;
//                }
//            }
//        }
//        else if(degreesLeft < 0){
//            while (degreesLeft != 0) {
//                if (Math.abs(degreesLeft) >= MAX_ROT.getDegrees()) {
//                    actionQueue.add(new Rotate(Angle.fromDegrees(-MAX_ROT.getDegrees())));
//                    degreesLeft += MAX_ROT.getDegrees();
//                } else {
//                    actionQueue.add(new Rotate(Angle.fromDegrees(-degreesLeft)));
//                    degreesLeft = 0;
//                }
//            }
//        }
//        else{
//            // Rotating 0 degrees?
//            return;
//        }
//    }
//
//    private boolean checkAngle(IntruderPercepts percepts){
//        System.out.println(percepts.getTargetDirection().getRadians());
//        if(percepts.getTargetDirection().getRadians() == 0){
//            return true;
//        }
//        return false;
//    }
