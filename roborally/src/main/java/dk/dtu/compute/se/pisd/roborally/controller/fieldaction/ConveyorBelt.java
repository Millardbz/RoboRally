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
package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

// this class controls both blue and green ConveyorBelt on the board
public class ConveyorBelt extends FieldAction {

    private Heading heading;
    private int numberOfMoves;

    public int getNumberOfMoves() {
        return numberOfMoves;
    }

    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        if (space.getActions().size() > 0) {
            ConveyorBelt action = (ConveyorBelt) space.getActions().get(0);
            Player player = space.getPlayer();
            if (player != null) {
                Heading playerHeading = player.getHeading();
                player.setHeading(action.heading);

                for (int i = 1; i <= action.numberOfMoves; i++) {
                    if (i > 1 &&
                            player.getSpace().getActions().size() > 0 &&
                            player.getSpace().getActions().get(0) instanceof ConveyorBelt nextBelt) {
                        player.setHeading(nextBelt.heading);
                    }

                    Space neighbour = gameController.board.getNeighbour(player.getSpace(), player.getHeading());
                    if (neighbour != null && neighbour.getPlayer() != null) {
                        player.setHeading(playerHeading);
                        return false;
                    }

                    gameController.moveForward(player, 1);
                }

                player.setHeading(playerHeading);
            }
        } else {
            return false;
        }

        return true;
    }

}
