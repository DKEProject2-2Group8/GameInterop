package Group8.Experiments;

import Group8.Agents.AgentFactoryImpl;
import Group8.Utils.WriteToCSV;
import Group9.Game;
import Group9.agent.factories.IAgentFactory;
import Group9.map.parser.Parser;

import java.util.ArrayList;

public abstract class AgentCountExperiment {

    private static final int RUNS = 1000;

    private static int intruderWins = 0;
    private static int guardWins = 0;
    private static int numberIntruders = 0;
    private static int numberGuards = 0;

    private static final String[] mapNames = {"1v1","1v2","1v10","2v1","2v2","10v1","10v10"};

    private static final IAgentFactory agentFactory = new AgentFactoryImpl();

    public static void runTest(boolean writeToFile) {
        // Setup algorithms
        AgentFactoryImpl.setGuardAlgorithm(AgentFactoryImpl.AlgoG.FSM);
        AgentFactoryImpl.setIntruderAlgorithm(AgentFactoryImpl.AlgoI.RANDOM);

        // Execute tests
        ArrayList<String[]> results = new ArrayList<>();
        for (int m = 0; m < mapNames.length; m++) {


            String mapPath = String.format("./src/main/java/Group8/Experiments/AgentCountMaps/%s.map", mapNames[m]);


            for (int i = 0; i < RUNS; i++) {
                Game game = new Game(Parser.parseFile(mapPath), agentFactory, false);
                numberGuards = game.getGuards().size();
                numberIntruders = game.getIntruders().size();
                game.run();
                Game.Team winner = game.getWinner();
                if (winner != null) {
                    if (winner == Game.Team.INTRUDERS) {
                        intruderWins++;
                    } else if (winner == Game.Team.GUARDS) {
                        guardWins++;
                    }
                }
                //System.out.println(String.format("Progress: %f%%", ((i + 1d) / RUNS) * 100));
            }

            System.out.println(String.format("Intruders won: #%d games and guards won: #%d games\n" +
                    "Progress: %f%%", intruderWins, guardWins,((double)(m+1)/mapNames.length)*100d));
            if(writeToFile) {
                results.add(new String[]{Integer.toString(numberIntruders), Integer.toString(numberGuards),
                        Integer.toString(guardWins), mapNames[m]});
            }


            // Reset the counters
            intruderWins = 0;
            guardWins = 0;

        }
        if (writeToFile) {
            WriteToCSV.writeOut(results, "winRateTest1000");
        }
    }
}
