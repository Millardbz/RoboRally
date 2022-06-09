package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

public class CheckPoint extends FieldAction {

    public final int no;

    public CheckPoint(int no) {
        this.no = no;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
            Player player = space.getPlayer();
            if (player != null) {
                // there is actually a player on this space
                player.setLastCheckpoint(this.no);
                if (player.getLastCheckpoint() >= gameController.board.getCheckpoints().size()) {
                    gameController.initiateWin(player);
                }

            }

            return true;
        }
    }