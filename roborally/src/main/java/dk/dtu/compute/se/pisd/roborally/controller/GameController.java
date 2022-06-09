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

    // XXX: V2
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
                DamageCardField damageField = player.getDamageCardField(0);
                damageField.setCard(new DamageCard(Damage.SPAM));
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

    // XXX: V2
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
     * This method is responsible to provide the player the option of executing just a single command of a single
     * programming card in the register.
     *
     */
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * this method is responsible for executing all of the commands in the register - unlike the executePrograms() method.
     */
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();

    }

    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    private void executeNextStep() {
        conveyorBelts();
        lasers();
        Player currentPlayer = board.getCurrentPlayer();
        if(currentPlayer.getSpace().getConveyorBelt() != null) {
            conveyorBeltMovePlayer(currentPlayer);
        } else if (currentPlayer.getSpace().hasLaser){
            damageFromLaser(currentPlayer);
        }
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

    // XXX: V2
    private void executeCommand(@NotNull Player player, Command command) {
        if (player != null && player.board == board && command != null) {

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

    /**
     * The method executes the chosen heading and continues the program
     * @param option
     */
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

    public void moveForward(@NotNull Player player) { //Moves the robot 1 square in player's heading
        //A statement that makes sure the space in front of the player is within the boundaries of the board
        if (player != null && player.getSpace().x + 1 < board.width && player.getSpace().y + 1 < board.height ||
                player.getSpace().x - 1 >= 0 && player.getSpace().y - 1 >= 0) {
            //Checks if there is a wall in the way of movement
            if(player.getSpace().hasWallAtHeading(player.getHeading())){
                return;
            }else if(board.getNeighbour(player.getSpace(), player.getHeading()) != null){ //check if there is a player in front of current player and then pushes
                pushNeighbourRobot(player);
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

    //Moves the robot 2 squares in player's heading
    public void fastForward(@NotNull Player player) {for(int i = 0; i < 2; i++){moveForward(player);}}

    public void turnRight(@NotNull Player player) {player.setHeading(player.getHeading().next());}

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

    /**
     *
     */
    void conveyorBelts(){
        board.getSpace(5, 1).setConveyorBelt("1S");
        board.getSpace(5, 2).setConveyorBelt("1S");
        board.getSpace(5, 3).setConveyorBelt("1S");
        board.getSpace(2, 4).setConveyorBelt("1S");
        board.getSpace(2, 5).setConveyorBelt("1S");
        board.getSpace(2, 6).setConveyorBelt("1S");
    }

    /**
     *
     * @param player
     */
    public void conveyorBeltMovePlayer(Player player){
        String cvb = player.getSpace().getConveyorBelt();
        int level = 0;
        Heading heading = null;
        switch (cvb.charAt(0)){
            case '1' -> level = 1;
            case '2' -> level = 2;
            case '3' -> level = 3;
            default -> System.out.println("Number for level is invalid.");
        }
        switch (cvb.charAt(1)){
            case 'N' -> heading = Heading.NORTH;
            case 'S' -> heading = Heading.SOUTH;
            case 'E' -> heading = Heading.EAST;
            case 'W' -> heading = Heading.WEST;
            default -> System.out.println("Character for heading is invalid.");
        }
        if(heading != null){
            for(int i = 0; i < level; i++){
                player.setSpace(board.getNeighbour(player.getSpace(), heading));
            }
        }else{
            System.out.println("Heading cannot be null!" + "\n" +
                    "Must be the first character of a heading");
        }
    }

    public void pushNeighbourRobot(Player player){
        Space neighbourSpace =  board.getNeighbour(player.getSpace(), player.getHeading());
        if(neighbourSpace.getPlayer() != null){
            neighbourSpace.getPlayer().setSpace(board.getNeighbour(neighbourSpace, player.getHeading()));
        }
    }


    void lasers(){
        board.getSpace(7,5).setLaser(true);
        board.getSpace(6,5).setLaser(true);
        board.getSpace(5,5).setLaser(true);
    }

    public void damageFromLaser(Player player){
        player.getDamageCardField(0).getCard().updateAmount();
    }


    public void moveCurrentPlayerToSpace(Space space) {

    }

}
