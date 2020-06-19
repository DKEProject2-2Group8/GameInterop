package Group8.Launchers;

import Group8.Agents.AgentFactoryImpl;
import Group9.Game;
import Group9.agent.factories.IAgentFactory;
import Group9.map.parser.Parser;

public class ExperimentLauncher {

    private static final int RUNS = 100;

    private static int intruderWins = 0;
    private static int guardWins = 0;

    private static final String MAP_PATH = "./src/main/java/Group9/map/maps/TestBox.map";
    private static final IAgentFactory agentFactory = new AgentFactoryImpl();


    public static void main(String[] args) {
        for (int i = 0; i < RUNS; i++) {
            Game game = new Game(Parser.parseFile(MAP_PATH), agentFactory, false);
            game.run();
            Game.Team winner = game.getWinner();
            if(winner != null){
                if(winner == Game.Team.INTRUDERS){
                    intruderWins++;
                }
                else if(winner == Game.Team.GUARDS){
                    guardWins++;
                }
            }
            System.out.println(String.format("Progress: %f%%, current iteration: %d",((i+1d)/RUNS) * 100,i));
        }

        System.out.println(String.format("Intruders won: #%d games and guards won: #%d games",intruderWins,guardWins));
    }
}
