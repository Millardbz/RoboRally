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
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted!
     *
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // TODO Assignment V1: method should be implemented by the students:
        //   - the current player should be moved to the given space
        //     (if it is free()
        //   - and the current player should be set to the player
        //     following the current player
        //   - the counter of moves in the game should be increased by one
        //     if the player is moved

    }

    /**
     * This function is void and starts the programming phase of the game.
     *
     * the setPhase function will be set to PROGRAMMING for all the players on the board and
     * no robot is going to be moving during this phase.
     *
     * The for-loops parses through every player present on the gameBoard and provide the players
     * with registers to store there programming cards, and make set programming cards visible to the
     * players. The second for-loop adds cards to players registers.
     */
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    /**
     * This functions creates the command cards and each kind of command card is being created
     * randomly, within the lenghth of the array of different kinds of command cards
     * @return commandCard objects
     */
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * This function set the game from the programming phase to the activation phase for all players.
     * This function stops all the players from moving.
     */
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    /**
     * The purpose of this function is to display to the player, what kind of programming cards is at the players disposal
     * in the register.
     * @param register
     */
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    /**
     * We don't understand the Idea of function...
     */
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * The game provide to options, which were either to execute a single command of the programming cards in the robots
     * register or to execute all the the commands of the programming cards of the register.
     * This method is responsible to provide the player the option of executing just a single
     * command of a single programming card in the register.
     *
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * this method is responsible for executing all of the commands in the register -
     * unlike the executePrograms() method.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    /**
     *  This function invokes the executeNextStep() - as a consequence, this method switches the
     *  game from the activation phase to the programming phase.
     *
     *  In other words, this function executes the programming cards in each players register.
     */
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    /**
     * This method accesses the current player of the game, and moves the player, when the game has "switch back to
     * the activation phase". There is a conditional, that instructs as lang as the number of steps is greather than or qual to
     * zero and that the player has a register, the instructions of set programming will be executed.
     *
     * After the instructions have been carried out, the game switches to the next player.
     */
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    if(command.isInteractive()){
                        board.setPhase(Phase.PLAYER_INTERACTION); //Makes the options for the player after executing command.
                        return;
                    }
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    /**
     * This method is going to carry out the different commands, that each programming card instructs.
     * Those are those specific instructions themselves, unlike the other methods in this class, this method
     * is not responsible for keeping the game progressing, but instead is responsible for carrying out the
     * commands themselves.
     * @param player
     * @param command
     */
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD -> this.moveForward(player);
                case RIGHT -> this.turnRight(player);
                case LEFT -> this.turnLeft(player);
                case FAST_FORWARD -> this.fastForward(player);
                case OPTION_LEFT_RIGHT -> this.optionLeftRight(player);
                default -> {
                }
                // DO NOTHING (for now)
            }
        }

    }

    public void executeCommandAndContinue(Command option){
        board.setPhase(Phase.ACTIVATION);
        executeCommand(board.getCurrentPlayer(), option);
        int step = board.getStep();
        int nextPlayerNumber = board.getPlayerNumber(board.getCurrentPlayer()) + 1;
        if (nextPlayerNumber < board.getPlayersNumber()) {
            board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
        } else {
            step++;
            if (step < Player.NO_REGISTERS) {
                makeProgramFieldsVisible(step);
                board.setStep(step);
                board.setCurrentPlayer(board.getPlayer(0));
            } else {
                startProgrammingPhase();
            }
        }
    }

    public void optionLeftRight(Player player){
      CommandCardField currentCard = player.board.getCurrentPlayer().getCardField(player.board.getStep());
      currentCard.getCard().command.getOptions().add(Command.LEFT);
      currentCard.getCard().command.getOptions().add(Command.RIGHT);
    }

    // TODO Assignment V2
    public void moveForward(@NotNull Player player) { //Moves the robot 1 square in player's heading
        //A statement that makes sure the space in front of the player is within the boundaries of the board
        if (player != null && player.getSpace().x + 1 < board.width && player.getSpace().y + 1 < board.height ||
                player.getSpace().x - 1 >= 0 && player.getSpace().y - 1 >= 0) {
            //Checks if there is a wall in the way of movement
            if(player.getSpace().hasWallAtHeading(player.getHeading())){
                return;
            }else if(player.getSpace().hasConveyorBeltWithHeading()){
                conveyorBeltMovePlayer(player, Heading.SOUTH, 1);
                //check if there is a player in front of current player and then pushes
            } else if(board.getNeighbour(player.getSpace(), player.getHeading()) != null){
                robotPushNeighbour(player);
            }
            //Determines the direction to move depending on player's heading.
            switch (player.getHeading()) {
                case NORTH -> player.setSpace(board.getSpace(player.getSpace().x, player.getSpace().y - 1));
                case EAST -> player.setSpace(board.getSpace(player.getSpace().x + 1, player.getSpace().y));
                case SOUTH -> player.setSpace(board.getSpace(player.getSpace().x, player.getSpace().y + 1));
                case WEST -> player.setSpace(board.getSpace(player.getSpace().x - 1, player.getSpace().y));
            }
        }
    }

    public void robotPushNeighbour(Player player){
       Space neighbourSpace =  board.getNeighbour(player.getSpace(), player.getHeading());
       if(neighbourSpace.getPlayer() != null){
           neighbourSpace.getPlayer().setSpace(board.getNeighbour(neighbourSpace, player.getHeading()));
       }
    }

    // TODO Assignment V2
    //Moves the robot 2 squares in player's heading
    public void fastForward(@NotNull Player player) {for(int i = 0; i < 2; i++){moveForward(player);}}

    // TODO Assignment V2
    public void turnRight(@NotNull Player player) {player.setHeading(player.getHeading().next());}

    // TODO Assignment V2
    public void turnLeft(@NotNull Player player) {player.setHeading(player.getHeading().prev());}

    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    public void conveyorBeltMovePlayer(Player player, Heading heading, int level){
            for(int i = 0; i < level; i++){player.setSpace(board.getNeighbour(player.getSpace(), heading));}
    }


    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }

}
