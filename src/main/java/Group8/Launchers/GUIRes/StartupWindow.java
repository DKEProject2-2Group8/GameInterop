package Group8.Launchers.GUIRes;

import Group8.Launchers.GUI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class StartupWindow extends Application {

    GUI gui;
    String choosenMap = null;

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("G8 GameInterop");

        // predefine size of gui objects
        final int OBJ_WIDTH = 140, SLIMAX = 250,SLIMIN = 5;

        // define the buttons
        Button btnStart = new Button("Start");
        Button btnMap = new Button("Choose Map");
        Button btnExit = new Button("Exit");

        // design em
        btnStart.setMinWidth(OBJ_WIDTH);
        btnMap.setMinWidth(OBJ_WIDTH);
        btnExit.setMinWidth(OBJ_WIDTH);

        // define and design the slider
        Slider sliTicks = new Slider();
        sliTicks.setMax(SLIMAX);
        sliTicks.setMin(SLIMIN);
        sliTicks.setMaxWidth(OBJ_WIDTH);
        sliTicks.setShowTickMarks(true);
        sliTicks.setShowTickLabels(true);
        sliTicks.setValue(30);

        // create an window and a vbox to place the buttons upon
        StackPane root = new StackPane();
        VBox buttonVBox = new VBox(20);
        buttonVBox.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonVBox);
        buttonVBox.getChildren().addAll(btnStart,btnMap,sliTicks,btnExit);
        primaryStage.setScene(new Scene(root, 300, 200));

        // button actions
        btnExit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //Stage stage = (Stage) btnExit.getScene().getWindow();
                primaryStage.close(); // works?
            }
        });

        btnStart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gui.startGame(primaryStage, choosenMap,(int)sliTicks.getValue());
            }
        });

        btnMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a Map");
                File file = fileChooser.showOpenDialog(primaryStage);
                choosenMap = file.getAbsolutePath();
                int i = 1;
            }
        });

        // launch the window
        primaryStage.show();
    }
}