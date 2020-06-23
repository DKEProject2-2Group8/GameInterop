package Group8.Agents;

import Group8.Agents.Intruder.FSM;
import Group8.Agents.Intruder.SimplePathfindingIntruder;
import Group9.agent.RandomAgent;
import Group9.agent.RandomIntruderAgent;
import Group9.agent.factories.IAgentFactory;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

public class AgentFactoryImpl implements IAgentFactory{
    public AlgoG GUARD_ALGORITHM = AlgoG.RAND;
    public AlgoI INTRUDER_ALGORITHM = AlgoI.FSM;

    public enum AlgoI {
        SIMPLE_PATH,FSM,FFNN,RAND,ASTAR
    }
    public enum AlgoG {
        RAND,OCCUPANCY_AGENT,FSM
    }

    @Override
     public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();

        for(int i=0; i<number; i++){
            switch(INTRUDER_ALGORITHM) {
                case FSM:
                    intruders.add(new FSM()); // fix them
                    break;
                case SIMPLE_PATH:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
                case FFNN:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
                case RAND:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
                case ASTAR:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
            }
        }

        return intruders;
    }

    @Override
     public List<Guard> createGuards(int number) {
        List<Guard> guards = new ArrayList<>();

        for(int i=0; i<number; i++){
            switch(GUARD_ALGORITHM) {
                case RAND:
                    // This was stolen from the controller
                    guards.add(new RandomAgent());
                    break;
                case OCCUPANCY_AGENT:
                    guards.add(new OccupancyAgent());
                    break;
                case FSM:
                    //guards.add(new GuardFSMAgent());
                    break;
            }
        }

        return guards;
    }
}
