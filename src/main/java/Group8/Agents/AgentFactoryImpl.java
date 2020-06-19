package Group8.Agents;

import Group8.Agents.Guard.GuardFSMAgent;
import Group8.Agents.Guard.OccupancyAgent;
import Group8.Agents.Intruder.IntruderFSMAgent;
import Group8.Agents.Intruder.RandomIntruderAgent;
import Group8.Agents.Intruder.SimplePathfindingIntruder;
import Group9.agent.RandomAgent;
import Group9.agent.factories.IAgentFactory;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

public class AgentFactoryImpl implements IAgentFactory{
    public static final AlgoG GUARD_ALGORITHM = AlgoG.FSM;
    public static final AlgoI INTRUDER_ALGORITHM = AlgoI.FSM;

    public enum AlgoI {
        SIMPLE_PATH,FSM,RANDOM
    }
    public enum AlgoG {
        RANDOM,OCCUPANCY_AGENT,FSM

    }

    @Override
    public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();

        for(int i=0; i<number; i++){
            switch(INTRUDER_ALGORITHM) {
                case FSM:
                    intruders.add(new IntruderFSMAgent());
                    break;
                case SIMPLE_PATH:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
                case RANDOM:
                    intruders.add(new RandomIntruderAgent());
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
                case RANDOM:
                    // This was stolen from the controller
                    guards.add(new RandomAgent());
                    break;
                case OCCUPANCY_AGENT:
                    guards.add(new OccupancyAgent());
                    break;
                case FSM:
                    guards.add(new GuardFSMAgent());
                    break;
            }
        }

        return guards;
    }
}
