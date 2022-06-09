package dk.dtu.compute.se.pisd.designpatterns.observer;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SaveController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;

import java.util.ArrayList;
import java.util.List;

public class BoardDesignerView extends VBox {

    private Board board;
    private GridPane mainBoardPane;
    private SpaceView[][] spaces;
    private Label statusLabel;
    private SpaceEventHandler spaceEventHandler;

    private List fieldOptions = new ArrayList<String>();

    public BoardDesignerView(Board board) {

        this.board = board;

        addOptions();

        mainBoardPane = new GridPane();

        this.getChildren().add(mainBoardPane);

        spaces = new SpaceView[board.width][board.height];

        spaceEventHandler = new SpaceEventHandler(this.board);

        for (int x = 0; x < this.board.width; x++) {
            for (int y = 0; y < this.board.height; y++) {
                Space space = this.board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        Button saveButton = new Button("Gem pladen!");
        saveButton.setOnAction(e -> {
            SaveController.saveBoard(this.board);
        });

        VBox buttonsPane = new VBox(saveButton);
        buttonsPane.setSpacing(3.0);
        buttonsPane.setAlignment(Pos.CENTER);

        this.getChildren().add(buttonsPane);

    }

    private void addOptions() {
        this.fieldOptions.add("Antenna");
        this.fieldOptions.add("Conveyor Belt");
        this.fieldOptions.add("Spiller startfelt");
        this.fieldOptions.add("Walls");
        this.fieldOptions.add("Checkpoint");
        this.fieldOptions.add("Gear");
        this.fieldOptions.add("Pit");
    }

    private class SpaceEventHandler implements EventHandler<MouseEvent> {

        private Board board;

        public SpaceEventHandler(Board board) {
            this.board = board;
        }

        @Override
        public void handle(MouseEvent event) {

            Object source = event.getSource();

            if (source instanceof SpaceView) {

                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;

                ChoiceDialog dialog = new ChoiceDialog();
                dialog.setContentText("Hvad vil du tilføje?");
                dialog.getItems().addAll(fieldOptions);


                dialog.showAndWait();

                if (dialog.getSelectedItem() == null) {
                    return;
                }

                switch ((String) dialog.getSelectedItem()) {

                    case "Antenna":
                        Antenna antenna = new Antenna(this.board, space.x, space.y);
                        this.board.setAntenna(antenna);
                        break;

                    case "Conveyor Belt":
                        addConveyorBelt(space);
                        break;

                    case "Spiller startfelt":
                        addPlayerStart(space);
                        break;

                    case "Walls":
                        addWalls(space);
                        break;

                    case "Checkpoint":
                        addCheckpoint(space);
                        break;

                    case "Gear":
                        addGear(space);
                        break;

                    case "Pit":
                        addPit(space);
                        break;
                }

            }

            event.consume();
        }

        private void addConveyorBelt(Space space) {

            for (FieldAction action : space.getActions()) {
                if (action instanceof ConveyorBelt) {
                    showWarning("There is already an instance of conveyor belt on this space");
                    return;
                }
            }

            ChoiceDialog dialog = new ChoiceDialog();
            dialog.setContentText("Hvilken vej skal båndet flytte spilleren?");
            dialog.getItems().add(Heading.NORTH);
            dialog.getItems().add(Heading.EAST);
            dialog.getItems().add(Heading.SOUTH);
            dialog.getItems().add(Heading.WEST);

            dialog.showAndWait();

            if (dialog.getSelectedItem() != null) {

                ConveyorBelt belt = new ConveyorBelt();
                belt.setHeading((Heading) dialog.getSelectedItem());

                space.addAction(belt);
            }

        }

        private void addPlayerStart(Space space) {

            TextInputDialog dialog = new TextInputDialog();
            dialog.setContentText("Vælg hvilket spillernr., der skal starte her (1 til 6)");
            dialog.showAndWait();

            if (dialog.getResult() != null) {
                space.setStartPlayerNo(Integer.parseInt(dialog.getResult()));
            }

        }

        private void addWalls(Space space) {

            List<Heading> currentWalls = space.getWalls();
            List<Heading> availableWalls = new ArrayList<>();
            List<Heading> headings = new ArrayList<>();

            headings.add(Heading.NORTH);
            headings.add(Heading.EAST);
            headings.add(Heading.SOUTH);
            headings.add(Heading.WEST);

            for (Heading heading : headings) {
                if ( ! currentWalls.contains(heading)) {
                    availableWalls.add(heading);
                }
            }

            if ( ! availableWalls.isEmpty()) {
                ChoiceDialog dialog = new ChoiceDialog();
                dialog.setContentText("Vælg hvilken retning, der skal tilføjes en væg");
                dialog.getItems().addAll(availableWalls);
                dialog.showAndWait();

                if (dialog.getSelectedItem() != null) {
                    space.addWall((Heading) dialog.getSelectedItem());
                }

            }


        }

        private void addCheckpoint(Space space) {
            for (FieldAction action : space.getActions()) {
                if (action instanceof CheckPoint) {
                    showWarning("There is already an instance of checkpoint on this space");
                    return;
                }
            }

            TextInputDialog dialog = new TextInputDialog();
            dialog.setContentText("Hvilket nummer checkpoint skal dette være?");
            dialog.showAndWait();

            if (dialog.getResult() != null) {
                int no = Integer.parseInt(dialog.getResult());

                if (no <= board.getCheckpoints().size()) {
                    String msg = "Du har indtastet et tal der allerede findes. Du skal mindst indtaste " + (board.getCheckpoints().size() + 1);
                    Alert alert = new Alert(Alert.AlertType.WARNING, msg);
                    alert.showAndWait();

                    addCheckpoint(space);
                }

                CheckPoint checkpoint = new CheckPoint(no);
                space.addAction(checkpoint);

            }

        }

        private void addGear(Space space) {
            for (FieldAction action : space.getActions()) {
                if (action instanceof Gear) {
                    showWarning("There is already an instance of gear on this space");
                    return;
                }
            }

            List<Direction> directions = new ArrayList<>();

            directions.add(Direction.LEFT);
            directions.add(Direction.RIGHT);

            ChoiceDialog dialog = new ChoiceDialog();
            dialog.setContentText("Vælg hvilken retning, gearet skal vende");
            dialog.getItems().addAll(directions);
            dialog.showAndWait();

            if (dialog.getSelectedItem() != null) {
                space.addGear((Direction) dialog.getSelectedItem());
            }

        }

        private void addPit(Space space) {
            for (FieldAction action : space.getActions()) {
                if (action instanceof Pit) {
                    showWarning("There is already an instance of pit on this space");
                    return;
                }
            }

            space.addAction(new Pit());

        }

    }

    private void showWarning(String text) {
        Alert alert = new Alert(Alert.AlertType.WARNING, text);
        alert.showAndWait();
    }


}
