package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.fieldaction.StartGear;
import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.IOUtil;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


 // Saving and Loading games of Roborally

public class SaveAndLoad {

    final static private List<String> PLAYERCOLORS = Arrays.asList("red", "green", "blue", "orange", "grey", "magenta");
    private static final String JSONFile = "Json";
    private static final String SAVEDBOARDS = "SaveGames";
    private static final String BOARDS = "GameBoardsJson";
    private static boolean NewBoard = false;


    // Get the players board and it's data and seves the game in a file
    public static void SaveBoardGame(Board board, String name) {

        // Setting up the board template
        String json = SerializeState.serializeGame(board);

        IOUtil.writeGameJson(name, json);

    }


    // player can load a board game
    public static Board loadBoardGame(String name) throws BoardNotFoundException {
        String resourcePath = SAVEDBOARDS + "/" + name + "." + JSONFile;
        String json = IOUtil.readGameJson(resourcePath);

        return SerializeState.deserializeGame(json, true);

    }


    public static Board newBoard(int numPlayers, String boardName) throws BoardNotFoundException {
        NewBoard = true;


        String resourcePath = BOARDS + "/" + boardName + "." + JSONFile;
        String json = IOUtil.readGameJson(resourcePath);

        Board board = SerializeState.deserializeGame(json, false);

        // Create the players and place them
        for (int i = 0; i < numPlayers; i++) {
            Player newPlayer = new Player(board, PLAYERCOLORS.get(i), "Player " + (i + 1));
            board.addPlayer(newPlayer);
        }

        List<Space> startGears = getSpacesFieldAction(board, new StartGear());
        PlayersPlace(board.getPlayers(), startGears);

        return board;
    }



    public static boolean getNewBoardCreated() {
        return NewBoard;
    }

    private static void PlayersPlace(List<Player> players, List<Space> possibleSpaces) {

        for (Player currentPlayer : players) {
            Space currentSpace = possibleSpaces.get(0);

            currentPlayer.setSpace(currentSpace);
            possibleSpaces.remove(currentSpace);

            currentPlayer.setHeading(Heading.EAST);
        }
    }

    // all spaces on the board get a Field action
    private static List<Space> getSpacesFieldAction(Board board, FieldAction action) {
        List<Space> spaces = new ArrayList<>();

        for (int y = 0; y < board.height; y++) {
            for (int x = 0; x < board.width; x++) {
                Space curSpace = board.getSpace(x, y);
                List<FieldAction> curSpaceActions = curSpace.getActions();

                if (curSpaceActions.size() == 0)
                    continue;

                String curFieldActionName = curSpaceActions.get(0).getClass().getSimpleName();
                if (curFieldActionName.equals(action.getClass().getSimpleName())) {
                    spaces.add(curSpace);
                }
            }
        }
        return spaces;
    }

}
