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
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.*;


public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;
    final public static int ENERGY_START_COUNT = 5;

    final public Board board;

    public String name;
    public String color;
    public int checkPoints;
    public Space space;
    public Heading heading = SOUTH;
    public int energyCount;
    public int priority;




    private int checkpointNumber;
    public CommandCardField[] program;
    public CommandCardField[] cards;

    private final ArrayList<Command> damagecards;

    /**
     * contructor for each player object.
     * <p>
     * Most interesting is that each player has an array, that represents the register that
     * is going to store the programming cards, that the player is going to have throughout the
     * progress of the game.
     * <p>
     * There is another array representing the cards themselves.
     *
     * @param board
     * @param color
     * @param name
     */
    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.energyCount = ENERGY_START_COUNT;
        this.damagecards = new ArrayList<>();
        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public String getColor() {
        return color;
    }

    /*
        public void setColor(String color) {
            this.color = color;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    */
    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void setEnergyCount(int energyCount) {
        this.energyCount = energyCount;
    }

    public int getEnergyCount() {
        return energyCount;
    }


    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }


    public void setDmgcards(Command card) {
        this.damagecards.add(card);
    }

    public ArrayList<Command> getDmgcards() {
        return this.damagecards;
    }




        public int getCheckpointNumber () {
            return checkpointNumber;
        }

}

