package Group8.Launchers.GUIRes;

import Group8.Agents.AgentFactoryImpl;
import Group8.Launchers.GUI;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class StartupWindow extends Application {

    GUI gui;
    AgentFactoryImpl agentFactory = new AgentFactoryImpl();
    String choosenMap = null;

    public void setGUI(GUI gui) {
        this.gui = gui;
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("G8 GameInterop");

        // predefine size of gui objects
        final int OBJ_WIDTH = 140, SLIMAX = 250, SLIMIN = 5, SPACING = 20;

        // define buttons and labels
        Button btnStart = new Button("Start");
        Button btnMap = new Button("Choose Map");
        Button btnExit = new Button("Exit");
        Label lblTick = new Label("Ticks:");
        Label lblHeader = new Label("Multi-agent Surveillance");
        Label lblGuardS = new Label("Guard Algorithm");
        Label lblIntruS = new Label("Intruder Algorithm");

        // design em
        btnStart.setMinWidth(OBJ_WIDTH);
        btnMap.setMinWidth(OBJ_WIDTH);
        btnExit.setMinWidth(OBJ_WIDTH);
        lblHeader.setFont(new Font("Arial", 28));

        // cbGuardAlgoSelect
        String agentAlgos[] = { "Random", "Occupancy", "FSM" };
        ComboBox cbGuardAlgoSelect = new ComboBox(FXCollections.observableArrayList(agentAlgos));
        cbGuardAlgoSelect.setMinWidth(OBJ_WIDTH);

        cbGuardAlgoSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (cbGuardAlgoSelect.getValue().toString()) {
                    case "Random":
                        agentFactory.GUARD_ALGORITHM = AgentFactoryImpl.AlgoG.RAND;
                    break;
                    case "Occupancy":
                        agentFactory.GUARD_ALGORITHM = AgentFactoryImpl.AlgoG.OCCUPANCY_AGENT;
                        break;
                }

            }
        });
        cbGuardAlgoSelect.getSelectionModel().select(0);

        String intruderAlgos[] = { "Random", "FSM", "FFNN", "ASTAR", "Simplepath" };
        ComboBox cbIntruderAlgoSelect = new ComboBox(FXCollections.observableArrayList(intruderAlgos));
        cbIntruderAlgoSelect.setMinWidth(OBJ_WIDTH);

        cbIntruderAlgoSelect.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                switch (cbIntruderAlgoSelect.getValue().toString()) {
                    case "Random":
                        agentFactory.INTRUDER_ALGORITHM = AgentFactoryImpl.AlgoI.RAND;
                        break;
                    case "FSM":
                        agentFactory.INTRUDER_ALGORITHM = AgentFactoryImpl.AlgoI.FSM;
                        break;
                    case "FFNN":
                        agentFactory.INTRUDER_ALGORITHM = AgentFactoryImpl.AlgoI.FFNN;
                        break;
                    case "ASTAR":
                        agentFactory.INTRUDER_ALGORITHM = AgentFactoryImpl.AlgoI.ASTAR;
                        break;
                    case "Simplepath":
                        agentFactory.INTRUDER_ALGORITHM = AgentFactoryImpl.AlgoI.SIMPLE_PATH;
                        break;
                }

            }
        });
        cbIntruderAlgoSelect.getSelectionModel().select(0);

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
        VBox buttonVBox = new VBox(SPACING);
        buttonVBox.setAlignment(Pos.CENTER);
        root.getChildren().add(buttonVBox);
        buttonVBox.getChildren().addAll(lblHeader,btnStart,lblGuardS,cbGuardAlgoSelect,lblIntruS,cbIntruderAlgoSelect,btnMap,lblTick,sliTicks,btnExit);
        primaryStage.setScene(new Scene(root, 320, 450));

        // button actions
        btnExit.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                primaryStage.close();
            }
        });

        btnStart.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                gui.startGame(primaryStage, choosenMap,(int)sliTicks.getValue(),agentFactory);
            }
        });

        btnMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Select a Map");
                File file = fileChooser.showOpenDialog(primaryStage);
                choosenMap = file.getAbsolutePath();
            }
        });

        // launch the window
        primaryStage.show();
    }
}