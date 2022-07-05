package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import javafx.application.Platform;


// this class update the board on the server when the game state is received it
// the deserializes the json file and sends an async message to update the ui.

public class UpdateMultiplayerBoard extends Thread {
    GameController gameController;
    Client client;
    boolean update = true;
    boolean runable = true;

    public void run() {
        while (runable) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (update) {
                gameController.refreshUpdater();
                updateBoarOnServer();
            }
        }
    }

    public void updateBoarOnServer() {
        if (!gameController.board.gameOver) {
            gameController.board = SerializeState.deserializeGame(client.getGameState(), true);
            Platform.runLater(gameController::updateBoard);
        }
    }



    public void setRun(boolean run) {
        this.runable= run;
    }
    public void setClient(Client client) {
        this.client = client;
    }

    public void setGameController(GameController gameController) {
        this.gameController = gameController;
    }

    public boolean getUpdate() {
        return update;
    }
    public void setUpdate(boolean update) {
        this.update = update;
    }
}
