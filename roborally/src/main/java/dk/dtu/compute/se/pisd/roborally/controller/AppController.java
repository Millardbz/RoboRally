/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.fileaccess.LoadBoard;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    final private List<String> BOARD_SIZES = Arrays.asList("Small","Medium","Large");

    private List<String> BOARDS = null;

    final private RoboRally roboRally;

    private GameController gameController;



    /**
     * This method instantiates a roboRally game object
     * @param roboRally
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }
    /**
     * This method provides the players of the game, the opportunity to begin a new game in the UI.
     * And begins the game, when the number of participants of the game has been chosen, in the if-
     * statements below the UI-functions. Below the if-statements, a new gameboard has been initiated
     * and the players will be added to the board.
     */
    public void newGame() {
        ChoiceDialog<Integer> dialogPlayerAmount = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        dialogPlayerAmount.setTitle("Player number");
        dialogPlayerAmount.setHeaderText("Select number of players");
        Optional<Integer> playerAmount = dialogPlayerAmount.showAndWait();

        ChoiceDialog<String> dialogBoardSize = new ChoiceDialog<>(BOARD_SIZES.get(0), BOARD_SIZES);
        dialogBoardSize.setTitle("Board Size");
        Optional<String> boardSize = dialogBoardSize.showAndWait();

        TextInputDialog dialogBoardName = new TextInputDialog();
        dialogBoardName.setTitle("Board Name");
        dialogBoardName.showAndWait();

        if (playerAmount.isPresent()) {
            if (gameController != null) {
                // The UI should not allow this, but in case this happens anyway.
                // give the user the option to save the game or abort this operation!
                if (!stopGame()) {
                    return;
                }
            }
            int w = 0, h = 0;
            switch (boardSize.get()){
                case "Small" -> {
                    w = 8;
                    h = 8;
                }
                case "Medium" -> {
                    w = 12;
                    h = 8;
                }
                case "Large" -> {
                    w = 16;
                    h = 8;
                }

            }
            // XXX the board should eventually be created programmatically or loaded from a file
            // here we just create an empty board with the required number of players.

            Board board = new Board(w,h,dialogBoardName.getResult());
            gameController = new GameController(board);
            int no = playerAmount.get();
            for (int i = 0; i < no; i++) {
                Player player = new Player(board, PLAYER_COLORS.get(i), "Player " + (i + 1));
                board.addPlayer(player);
                player.setSpace(board.getSpace(i % board.width, i));
            }

            // XXX: V2
            // board.setCurrentPlayer(board.getPlayer(0));
            gameController.startProgrammingPhase();

            roboRally.createBoardView(gameController);
        }
    }
    /**
     * Save current game.
     */
    public void saveGame() {
        LoadBoard.saveBoard(gameController.board, gameController.board.boardName);
    }

    /**
     * Load saved game.
     */
    public void loadGame() {

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            URL url = loader.getResource("boards");
            if(url != null){
                BOARDS = Arrays.asList(Objects.requireNonNull(new File(url.getPath()).list()));
            }
            else{
                System.out.println("Invalid URL");
            }
            ChoiceDialog<String> choice = new ChoiceDialog<>(BOARDS.get(0), BOARDS);
            choice.setTitle("Choose save:");
            Optional<String> save = choice.showAndWait();

            Board board = LoadBoard.loadBoard(save.get());
            gameController = new GameController(board);
            roboRally.createBoardView(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }
    /**
     * The first conditional makes sure, that this function can only be invoked as long as a game
     * has been instantiated. Below that conditional, a UI dialog begins, asking the user, weather
     * the user is sure, that he/she wants to quit the game. The option to quit the game or not to
     * quit the game is being provided by a UI botton function, that performs the termination the game
     * if the user confirms that the do want to quit the game and resumes the game, if the do not
     * want to quit the game.
     *
     * @return an application that has been shut down.
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }
    /**
     * This boolean function determines weather the game is running or not.
     * If this boolean is true, that means that a game is running and makes sure that
     * throughout the process of running the game, the gameController object is going to be
     * present, thereby prevent the game from running the without a gameController object.
     * In other words, nobody is interested in running the game without the game control measures.
     *
     * @return
     */
    public boolean isGameRunning() {
        return gameController != null;
    }

    /**
     * The purpose of this function is to update the events displayed on the game UI.
     * for example, if a robot moves, this motion is going to be shown on the roborally board
     * user interface.
     * @param subject the subject which changed
     */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

}
