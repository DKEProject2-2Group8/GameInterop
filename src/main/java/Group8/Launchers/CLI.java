package Group8.Launchers;

import Group8.Agents.AgentFactoryImpl;
import Group9.Game;

import Group9.map.parser.Parser;

public class CLI {
    public static void main(String[] args) {
        Game game = new Game(Parser.parseFile("./src/main/java/Group9/map/maps/TestBox.map"), new AgentFactoryImpl(), false);
        game.run();
        System.out.printf("The winner is: %s\n", game.getWinner());
    }
}
