package Group8.Launchers.GUIRes;

import Group8.Launchers.GUI;
import Group9.agent.container.AgentContainer;
import Group9.agent.container.GuardContainer;
import Group9.agent.container.IntruderContainer;
import Group9.map.GameMap;
import Group9.map.dynamic.DynamicObject;
import Group9.map.dynamic.Pheromone;
import Group9.map.dynamic.Sound;
import Group9.map.objects.*;
import Group9.map.objects.Window;
import Group9.math.Vector2;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.io.File;
import java.util.List;

public class GameScene extends Scene {

    private int width, height;
    private final int AGENT_RAD = 5;
    private final int SCALE = 4;
    private final int BOTTOM_MENU = 18;

    private Canvas background;
    private Canvas foreground;

    private GraphicsContext gcBackground;
    private GraphicsContext gcForeground;

    private StackPane parent;
    private Stage window;
    private Stage below;

    private GameMap map;
    private List<MapObject> mapObjects;
    private double mapScale = 1;

    private GUI gui;
    private boolean playing = true;

    public GameScene(StackPane parent, GameMap map, GUI gui) {
        super(parent);
        width = map.getGameSettings().getWidth();
        height = map.getGameSettings().getHeight()+(BOTTOM_MENU);///SCALE);
        init(parent,map); // Initializes the needed variables
        constructScene(); // Necessary setup for the scene

        this.gui = gui;
    }

    public void attachWindow(Stage stage){
        this.window = stage;
    }

    public void attachBelow(Stage stage){
        this.below = stage;
    }

    private void constructScene(){
        parent.getChildren().add(background);
        parent.getChildren().add(foreground);

        drawBackgroundLayer();
    }

    private void init(StackPane parent, GameMap map){
        background = new Canvas(width, height);
        foreground = new Canvas(width, height);
        gcBackground = background.getGraphicsContext2D();
        gcForeground = foreground.getGraphicsContext2D();
        this.parent = parent;
        this.map = map;
        this.mapObjects = map.getObjects();
    }

    private void initButtons(){
        // add and adjust a hbox for the buttons
        HBox bottomMenuHBox = new HBox();
        bottomMenuHBox.setMaxHeight(BOTTOM_MENU);
        bottomMenuHBox.setAlignment(Pos.BASELINE_LEFT);
        bottomMenuHBox.setPadding(new Insets(5,15,5,15));
        bottomMenuHBox.setStyle( "-fx-border-style: solid;"
               + "-fx-border-width: 2;" + "-fx-border-insets: 5;"
                + "-fx-border-radius: 5;" + "-fx-border-color: black;");

        // add and set correct pos in stackpane
        parent.getChildren().add(bottomMenuHBox);
        parent.setAlignment(bottomMenuHBox, Pos.BOTTOM_CENTER);

        // Buttons
        Button btnPlayPause = new Button("Pause");
        btnPlayPause.setMinWidth(width-80);

        bottomMenuHBox.getChildren().add(btnPlayPause);

        btnPlayPause.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(playing) {
                    playing = false;
                    gui.playpause(false);
                    btnPlayPause.setText("Play");
                } else {
                    playing = true;
                    gui.playpause(true);
                    btnPlayPause.setText("Pause");
                }
            }
        });

    }

    public void drawRect(){
        gcBackground.setFill(Color.GREY);
        gcBackground.fillRect(0,0, width, height);
    }


    public void drawEntities(List<GuardContainer> guards, List<IntruderContainer> intruders, List<DynamicObject<?>> objects){
        for (DynamicObject<?> obj : objects) {
            //System.out.println(obj.toString());
            if (obj instanceof Sound) {
                // Draw sound
                gcForeground.setFill(Color.GREEN);
                gcForeground.fillOval(obj.getCenter().getX()*SCALE,obj.getCenter().getY()*SCALE,obj.getRadius()*mapScale,obj.getRadius()*mapScale);
                //final int OVAL_RAD = AGENT_RAD*4;
                //Vector2 position = guards.get(0).getPosition().mul(SCALE);
                //gcForeground.fillOval(position.getX()-AGENT_RAD*1.5,position.getY()-AGENT_RAD*1.5,OVAL_RAD,OVAL_RAD);
            } else if (obj instanceof Pheromone) {
                // Draw pheromone
                gcForeground.setFill(Color.YELLOW);
                gcForeground.fillOval(obj.getCenter().getX()*SCALE,obj.getCenter().getY()*SCALE,obj.getRadius()*2*mapScale,obj.getRadius()*2*mapScale);
            } else {
                // Draw remaining
                System.out.println("?");
                gcForeground.setFill(Color.BLACK);
                gcForeground.fillOval(obj.getCenter().getX()*SCALE,obj.getCenter().getY()*SCALE,obj.getRadius(),obj.getRadius());
            }
        }

        for (GuardContainer guard:
                guards) {
            drawAgent(guard,Presets.GUARD_COL);
        }
        for(IntruderContainer intruder:
                intruders){
            drawAgent(intruder,Presets.INTRUDER_COL);
        }
    }

    private void drawAgent(AgentContainer<?> agent, Color color){
        gcForeground.setFill(color);
        Vector2 position = agent.getPosition().mul(SCALE);
        gcForeground.fillOval(position.getX(),position.getY(),AGENT_RAD,AGENT_RAD);
    }

    private void drawBackgroundLayer(){
        clearBackground();
        drawRect();
        initButtons();

        // Draw static components
        for (MapObject mo :
                mapObjects) {
            StaticDrawable staticDrawable = getStaticDrawable(mo);
            Vector2[] verts = mo.getArea().getAsPolygon().getPoints();
            double[] scaledX = new double[verts.length];
            double[] scaledY = new double[verts.length];

            for (int i = 0; i < verts.length; i++) {
                scaledX[i] = verts[i].getX() * SCALE;
                scaledY[i] = verts[i].getY() * SCALE;
            }

            gcBackground.setFill(staticDrawable.getColor());
            if(staticDrawable.isFill()){
                gcBackground.fillPolygon(scaledX,scaledY,4);
            }
            else{
                gcBackground.setLineWidth(3);
                gcBackground.strokePolygon(scaledX,scaledY,4);
            }
        }
    }
    // Inspired by group 9 implementation
    private StaticDrawable getStaticDrawable(MapObject mo) {
        if(mo instanceof Wall){
            return new StaticDrawable(Presets.WALL_COL,true);
        }
        else if(mo instanceof TargetArea){
            return new StaticDrawable(Presets.TARGET_COL,false);
        }
        else if(mo instanceof Spawn.Guard){
            return new StaticDrawable(Presets.SPAWN_GUARD_COL,false);
        }
        else if(mo instanceof Spawn.Intruder){
            return new StaticDrawable(Presets.SPAWN_INTRUDER_COL,false);
        }
        else if(mo instanceof ShadedArea){
            return new StaticDrawable(Presets.SHADED_COL,true);
        }
        else if(mo instanceof Door){
            return new StaticDrawable(Presets.DOOR_COL,true);
        }
        else if(mo instanceof Window){
            return new StaticDrawable(Presets.WINDOW_COL,true);
        }
        else if(mo instanceof SentryTower){
            return new StaticDrawable(Presets.SENTRY_COL,true);
        }
        else if(mo instanceof TeleportArea){
            return new StaticDrawable(Presets.TELEPORT_COL,true);
        }

        return new StaticDrawable(Presets.UNKNOWN,true);
    }

    public void clearForeground(){
        gcForeground.clearRect(0,0, width, height); // Clear the canvas
    }

    public void clearBackground(){
        gcBackground.clearRect(0,0, width, height); // Clear the canvas
    }

    public void rescale(){
        if(window == null){
            System.out.println("Cant rescale, window is not attached!");
            return;
        }

        // Clear the screen
        clearForeground();
        clearBackground();

        // Scale variables
        width = width * SCALE;
        height = height * SCALE;

        window.setWidth(width);
        window.setHeight(height);

        parent.setPrefWidth(width);
        parent.setPrefHeight(height);

        background.setWidth(width);
        background.setHeight(height);
        foreground.setWidth(width);
        foreground.setHeight(height);

        // Redraw background / Static components
        drawBackgroundLayer();

    }

}

class Presets{
    public static final Color GUARD_COL = Color.BLUE;
    public static final Color INTRUDER_COL = Color.RED;
    public static final Color WALL_COL = Color.WHITE;
    public static final Color TARGET_COL = Color.BLACK;
    public static final Color SPAWN_INTRUDER_COL = Color.RED;
    public static final Color SPAWN_GUARD_COL = Color.BLUE;
    public static final Color SHADED_COL = Color.DARKGRAY;
    public static final Color DOOR_COL = Color.GREEN;
    public static final Color WINDOW_COL = Color.LIGHTBLUE;
    public static final Color SENTRY_COL = Color.FIREBRICK;
    public static final Color TELEPORT_COL = Color.PURPLE;
    public static final Color UNKNOWN = Color.LIMEGREEN;
}