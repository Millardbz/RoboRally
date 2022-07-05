package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;


public class Checkpoint extends FieldAction {

    private static int LastCheckpointNumber = 0;
    private int checkpointNumber;

    public Checkpoint() {
        LastCheckpointNumber++;
    }


    public int getCheckpointNumber() {
        return checkpointNumber;
    }

    public static void setlastCheckpointNumber(int highestCheckpointNumber) {
        Checkpoint.LastCheckpointNumber = highestCheckpointNumber;
    }



    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            Checkpoint checkpoint = (Checkpoint) space.getActions().get(0);
            Player player = space.getPlayer();

            // Checks if the player step on the checkpoint in correct order.
            if (player != null && player.checkPoints + 1 == checkpoint.checkpointNumber) {
                player.checkPoints++;
                // If player has won
                if (player.checkPoints == LastCheckpointNumber) {
                    gameController.Winner_Massage(space);
                    LastCheckpointNumber = 0; // Needs because the static variable is never resat
                    gameController.board.gameOver = true;
                    gameController.pushGameState();
                    gameController.endGame();


                }
                return true;
            }
        }
        return false;
    }


}

