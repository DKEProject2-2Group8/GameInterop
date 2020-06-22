package Group8.Experiments.ParametersTest;

import Group8.Agents.AgentFactoryImpl;
import Group8.Utils.WriteToCSV;
import Group9.Game;
import Group9.agent.factories.IAgentFactory;
import Group9.map.parser.Parser;

import java.util.ArrayList;

public abstract class ParameterTests {

    private static final int RUNS = 10;

    private static int intruderWins = 0;
    private static int guardWins = 0;

    private static final String[] mapNames = {
            "BaseLine",
            "captureDistance+20",
            "captureDistance+40",
            "captureDistance-20",
            "captureDistance-40",
            "maxMoveDistanceGuard+20",
            "maxMoveDistanceGuard+40",
            "maxMoveDistanceGuard-20",
            "maxMoveDistanceGuard-40",
            "maxMoveDistanceIntruder+20",
            "maxMoveDistanceIntruder+40",
            "maxMoveDistanceIntruder-20",
            "maxMoveDistanceIntruder-40",
            "maxRotationAngle+20",
            "maxRotationAngle+40",
            "maxRotationAngle-20",
            "maxRotationAngle-40",
            "viewRangeGuardNormal+20",
            "viewRangeGuardNormal+40",
            "viewRangeGuardNormal-20",
            "viewRangeGuardNormal-40",
            "viewRangeIntruderNormal+20",
            "viewRangeIntruderNormal+40",
            "viewRangeIntruderNormal-20",
            "viewRangeIntruderNormal-40",
            "winConditionIntruderRounds+20",
            "winConditionIntruderRounds+50",
            "winConditionIntruderRounds-20",
            "winConditionIntruderRounds-50"
    };

    private static final IAgentFactory agentFactory = new AgentFactoryImpl();

    public static void runTests(boolean writeToFile){
        // Setup algorithms
        AgentFactoryImpl.setGuardAlgorithm(AgentFactoryImpl.AlgoG.RANDOM);
        AgentFactoryImpl.setIntruderAlgorithm(AgentFactoryImpl.AlgoI.RANDOM);

        // Execute tests
        ArrayList<String[]> results = new ArrayList<>();
        for (int m = 0; m < mapNames.length; m++) {

            String mapPath = String.format("./src/main/java/Group8/Experiments/ParametersTest/ParameterTestMaps/%s.map",
                    mapNames[m]);

            for (int i = 0; i < RUNS; i++) {
                Game game = new Game(Parser.parseFile(mapPath), agentFactory, false);
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
                results.add(new String[]{Integer.toString(guardWins), mapNames[m]});
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
