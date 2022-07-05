package dk.dtu.compute.se.pisd.roborally.exceptions;


public class BoardNotFoundException extends Exception {
    private final String boardPath;

    // exception uses for when a board game does not exist

    public BoardNotFoundException(String boardPath){
        this.boardPath = boardPath;
    }

    public String getBoardPath(){
        return this.boardPath;
    }
}
