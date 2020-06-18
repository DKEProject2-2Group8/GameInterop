package Group8.Agents.Guard;

import Interop.Action.GuardAction;
import Interop.Action.NoAction;
import Interop.Agent.Guard;
import Interop.Percept.GuardPercepts;

import static Group8.Agents.Guard.GuardFSM.VERBOSE;

public class GuardFSMAgent implements Guard {
    private GuardFSM guardFSM;
    private static int guardCount = 0;
    private static int currentGuard = 1;
    private boolean init;
    private static boolean doneInit = false;

    private void next(){
        if(init){
            System.out.println(String.format("Currently active guard: %d", currentGuard++));
        }
        else {
            if(!doneInit){
                currentGuard = 1;
                System.out.println(String.format("Currently active guard: %d", currentGuard));
                doneInit = true;
            }
            else {
                if (currentGuard == guardCount) {
                    currentGuard = 1;
                } else {
                    currentGuard++;
                }
                System.out.println(String.format("Currently active guard: %d", currentGuard));
            }
        }
    }

    @Override
    public GuardAction getAction(GuardPercepts percepts) {
        init = false;
        if(guardFSM == null){
            guardFSM = new GuardFSM(percepts);
            guardCount++;
            init = true;
        }


        //next();
        GuardAction action = guardFSM.getMoveGuard(percepts);
        if(action == null){
            if (VERBOSE) {
                System.out.println(
                        String.format("returning NoAction since FSM produces null\n"));
            }
            return new NoAction();
        }
        if (VERBOSE) {
            System.out.println(
                    String.format("return action produced by FSM\n"));
        }
        return action;
    }
}



