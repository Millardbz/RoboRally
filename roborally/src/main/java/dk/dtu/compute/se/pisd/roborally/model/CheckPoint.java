package dk.dtu.compute.se.pisd.roborally.model;

public class CheckPoint {

    public final Board board;

    public final int x;
    public final int y;

    public CheckPoint(Board board, int x, int y){
        this.board = board;
        this.x = x;
        this.y = y;
    }
}