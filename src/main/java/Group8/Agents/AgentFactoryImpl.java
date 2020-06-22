package Group8.Agents;

import Group8.Agents.Intruder.FFNNGenetic;
import Group8.Agents.Intruder.FFNNXL;
import Group8.Agents.Intruder.SimplePathfindingIntruder;
import Group8.Agents.Intruder.FFNN;
import Group8.FFNN.GeneticFFNN;
import Group9.agent.RandomAgent;
import Group9.agent.factories.IAgentFactory;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;

import java.util.ArrayList;
import java.util.List;

public class AgentFactoryImpl implements IAgentFactory{
    public static final AlgoG GUARD_ALGORITHM= AlgoG.AI1;
    public static final AlgoI INTRUDER_ALGORITHM = AlgoI.FFNNXL;

    public enum AlgoI {
        AI1,SIMPLE_PATH,FFNN,FFNNXL,GENETICFFNN
    }
    public enum AlgoG {
        AI1,AI2

    }

    @Override
     public List<Intruder> createIntruders(int number) {
        List<Intruder> intruders = new ArrayList<>();

        for(int i=0; i<number; i++){
            switch(INTRUDER_ALGORITHM) {
                case AI1:
                    intruders.add(null);
                    break;
                case SIMPLE_PATH:
                    intruders.add(new SimplePathfindingIntruder());
                    break;
                case FFNN:
                    intruders.add(new FFNN());
                case FFNNXL:
                    intruders.add(new FFNNXL());
                case GENETICFFNN:
                    intruders.add(new FFNNGenetic());
            }
        }

        return intruders;
    }

    @Override
     public List<Guard> createGuards(int number) {
        List<Guard> guards = new ArrayList<>();

        for(int i=0; i<number; i++){
            switch(GUARD_ALGORITHM) {
                case AI1:
                    // TODO: remove this class
                    guards.add(new RandomAgent());
                    break;
                case AI2:
                    //guards.add(new GuardAlgo2());
                    break;
            }
        }

        return guards;
    }
}
