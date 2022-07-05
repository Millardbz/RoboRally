package dk.dtu.compute.se.pisd.roborally.exceptions;

import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

// move is not possible

public class MoveNotPossibleException extends Exception {

    public MoveNotPossibleException(Player player, Space space, Heading heading) {
        super("Move is not possible");
    }
}
