package Group8.Agents;

import Group8.Agents.Intruders.IntruderAStar;
import Group9.agent.RandomAgent;
import Group9.agent.factories.IAgentFactory;
import Interop.Agent.Guard;
import Interop.Agent.Intruder;
import java.util.ArrayList;
import java.util.List;

public class ExampleAgentFactory implements IAgentFactory {
    @Override
    public List<Intruder> createIntruders(int num) {

        List<Intruder> intruders = new ArrayList<>();
        for(int i = 0 ; i < num; i++){
            intruders.add(new IntruderAStar());
        }
        return intruders;// Return the proper agent here
    }

    @Override
    public List<Guard> createGuards(int num) {

        List<Guard> guards = new ArrayList<>();
        for(int i = 0; i < num; i++)
        {
            guards.add(new RandomAgent());
            //guards.add(new DeepSpace());
        }
        return guards; // Return the proper agent here
    }
}
