package Group8.Experiments.AgentCountTest;

import Group8.Agents.AgentFactoryImpl;
import Group8.Utils.WriteToCSV;
import Group9.Game;
import Group9.agent.factories.IAgentFactory;
import Group9.map.GameMap;
import Group9.map.parser.Parser;

import java.util.ArrayList;

public abstract class AgentCountExperiment {

    private static final int RUNS = 100;

    private static int intruderWins = 0;
    private static int guardWins = 0;
    private static int numberIntruders = 0;
    private static int numberGuards = 0;

    private static final String[] mapNames = {"1v1","1v2","2v1","2v2","5v1","1v5","5v5","5v10","10v5","9v10","10v9","9v9","1v10","10v1","10v10",};

    private static final IAgentFactory agentFactory = new AgentFactoryImpl();

    public static void runTest(boolean writeToFile) {
        // Setup algorithms
        AgentFactoryImpl.setGuardAlgorithm(AgentFactoryImpl.AlgoG.RANDOM);
        AgentFactoryImpl.setIntruderAlgorithm(AgentFactoryImpl.AlgoI.RANDOM);

        // Execute tests
        ArrayList<String[]> results = new ArrayList<>();
        for (int m = 0; m < mapNames.length; m++) {

            String mapPath = String.format("./src/main/java/Group8/Experiments/AgentCountTest/AgentCountMaps/%s.map", mapNames[m]);

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
                results.add(new String[]{Integer.toString(numberGuards), Integer.toString(numberIntruders),
                        Integer.toString(guardWins), mapNames[m]});
            }


            // Reset the counters
            intruderWins = 0;
            guardWins = 0;

        }
        if (writeToFile) {
            WriteToCSV.writeOut(results, "winRateTestAgentCountContinuation100" +
                    "");
        }
    }

    public static void executeTests(boolean writeToFile) {
        // Setup algorithms
        AgentFactoryImpl.setGuardAlgorithm(AgentFactoryImpl.AlgoG.RANDOM);
        AgentFactoryImpl.setIntruderAlgorithm(AgentFactoryImpl.AlgoI.RANDOM);


        String mapPath = String.format("./src/main/java/Group8/Experiments/AgentCountTest/AgentCountMaps/%s.map",mapNames[0]);

        ArrayList<String[]> results = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            for (int j = 1; j <= 10; j++) {
                System.out.printf("Configuration: #guards=%d & #intruders=%d\n",i,j);
                for (int k = 0; k < RUNS; k++) {
                    GameMap gameMap = changeMapFile(mapPath,i,j);
                    Game game = new Game(gameMap, agentFactory, false);
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
                }
                System.out.printf("Result: #Guardwins=%d & #Intruderwins=%d\n",guardWins,intruderWins);
                if(writeToFile) {
                    results.add(new String[]{Integer.toString(numberGuards), Integer.toString(numberIntruders),
                            Integer.toString(guardWins)});
                }
                intruderWins = 0;
                guardWins = 0;
            }
        }
        if (writeToFile) {
            WriteToCSV.writeOut(results, "winRateTestAgentCountContinuous100");
        }
    }

    private static GameMap changeMapFile(String mapPath, int numberGuards, int numberIntruders){
        GameMap gameMap = Parser.parseFile(mapPath);
        gameMap.getGameSettings().setNumGuards(numberGuards);
        gameMap.getGameSettings().setNumIntruders(numberIntruders);
        return gameMap;
    }
}
