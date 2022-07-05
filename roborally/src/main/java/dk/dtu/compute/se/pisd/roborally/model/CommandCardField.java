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

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class CommandCardField extends Subject {

    final public Player player;

    public CommandCard card;

    public boolean visible;
    private boolean active;
    /**
     * CommandCardField constructor.
     * @param player
     */
    public CommandCardField(Player player) {
        this.player = player;
        this. card = null;
        this.visible = true;
    }

    public CommandCard getCard() {
        return card;
    }

    public void setCard(CommandCard card) {
        if (card != this.card) {
            this.card = card;
            notifyChange();
        }
    }
    /**
     * this methods makes sure that the commandCards are visible on the UI.
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }
    /**
     * notifies the player that the command cards at the players disposal has changed,
     * which it will throughtout the progress of the game.
     * @param visible
     */
    public void setVisible(boolean visible) {
        if (visible != this.visible) {
            this.visible = visible;
            notifyChange();
        }
    }
        public boolean isActive () {
            return active;
        }

        public void setActive ( boolean active){
            if (active != this.active) {
                this.active = active;
                notifyChange();
            }
        }
    }