package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

/**
 * Representation of a pit on the game board.
 * Once activated it will loop through all the spaces on the game board
 * to find the space where it should place the player.
 *
 */
public class Pit extends FieldAction {

    @Override
    public boolean doAction(GameController gameController, Space space) {

        Board board = space.board;
        Player player = space.getPlayer();

        if (player == null) {
            return false;
        }

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                Space newSpace = board.getSpace(i,j);
                if (newSpace.getStartPlayerNo() == (player.getDbNo() + 1) && newSpace.getPlayer() == null) {
                    player.setSpace(newSpace);
                    return true;
                }
            }
        }

        return false;

    }
}
