package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

/**

 */
public class Pit extends FieldAction {
    public int boardNum;
    RebootToken reboot = new RebootToken();
    @Override
    public boolean doAction(GameController gameController, Space space) {
        Pit pit = (Pit) space.getActions().get(0);
        Board board = gameController.board;

        if (space.getActions().size() > 0) {
            Player player = space.getPlayer();

            if (player != null) {
                switch (pit.boardNum) {
                    case 1:

                        board.getSpace(0,6).setPlayer(player);
                        reboot.doAction(gameController, player.getSpace());
                        player.setDmgcards(Command.SPAM);
                        player.setDmgcards(Command.SPAM);
                        break;

                    case 2:
                        board.getSpace(0,3).setPlayer(player);
                        reboot.doAction(gameController, player.getSpace());
                        player.setDmgcards(Command.SPAM);
                        player.setDmgcards(Command.SPAM);
                        break;



                }
            }

        }
        return true;
    }
}
