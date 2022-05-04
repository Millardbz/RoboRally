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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;
import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    public final Board board;

    public final int x;
    public final int y;
    private ConveyorBelt cvb = null;
    public boolean hasLaser;
    private Player player;

    public Space(Board board, int x, int y) {
        this.board = board;
        this.x = x;
        this.y = y;
        player = null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);
            }
            notifyChange();
        }
    }

    public void setConveyorBelt(int level, Heading heading){cvb = new ConveyorBelt(level, heading);}
    public ConveyorBelt getConveyorBelt(){return cvb;}

    public void setLaser(){this.hasLaser = true;}

    public boolean hasWallAtHeading(Heading heading){
        int x = player.getSpace().x;
        int y = player.getSpace().y;
        boolean[] wall = new boolean[]{
                x == 3 && y == 4 && heading == NORTH,
                x == 4 && y == 5 && heading == EAST,
                x == 1 && y == 2 && heading == SOUTH,
                x == 1 && y == 6 && heading == WEST,
                x == 6 && y == 3 && heading == NORTH,
                //The spaces on the other side of the walls with opposite heading.
                x == 3 && y == 3 && heading == SOUTH,  // y - 1
                x == 4 && y == 6 && heading == WEST,   // y + 1
                x == 1 && y == 3 && heading == NORTH,  // y + 1
                x == 0 && y == 6 && heading == EAST,   // x - 1
                x == 6 && y == 2 && heading == SOUTH}; // y - 1
        for (boolean b : wall) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
