package Group8.Experiments;

import Group8.Agents.AgentFactoryImpl;
import Group8.Utils.WriteToCSV;
import Group9.Game;
import Group9.agent.factories.IAgentFactory;
import Group9.map.parser.Parser;

import java.util.ArrayList;

public abstract class AgentCountExperiment {

    private static final int RUNS = 100;

    private static int intruderWins = 0;
    private static int guardWins = 0;
    private static int numberIntruders = 0;
    private static int numberGuards = 0;

    private static final String[] mapNames = {"1v1","1v2","1v10","2v1","2v2","10v1","10v10"};

    private static final IAgentFactory agentFactory = new AgentFactoryImpl();

    public static void runTest(boolean writeToFile) {
        ArrayList<String[]> results = new ArrayList<>();
        for (String mapName : mapNames) {


            String mapPath = String.format("./src/main/java/Group8/Experiments/AgentCountMaps/%s.map", mapName);


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
                System.out.println(String.format("Progress: %f%%", ((i + 1d) / RUNS) * 100));
            }

            System.out.println(String.format("Intruders won: #%d games and guards won: #%d games", intruderWins, guardWins));
            if(writeToFile) {
                results.add(new String[]{Integer.toString(numberIntruders), Integer.toString(numberGuards),
                        Integer.toString(RUNS), Integer.toString(guardWins), mapName});
            }


            // Reset the counters
            intruderWins = 0;
            guardWins = 0;

        }
        if (writeToFile) {
            WriteToCSV.writeOut(results, "winRateTest");
        }
    }
}
