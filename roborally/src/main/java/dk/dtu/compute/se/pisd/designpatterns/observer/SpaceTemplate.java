package dk.dtu.compute.se.pisd.designpatterns.observer;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.ArrayList;
import java.util.List;

public class SpaceTemplate {

    public int playerNo;
    public int x;
    public int y;
    public List<Heading> walls = new ArrayList<>();
    public List<FieldAction> actions = new ArrayList<>();

    public SpaceTemplate fromSpace(Space space) {
        this.x = space.x;
        this.y = space.y;
        this.playerNo = space.getStartPlayerNo();

        this.walls = space.getWalls();
        this.actions = space.getActions();

        return this;
    }

    public Space toSpace(Board board) {

        Space space = new Space(board, this.x, this.y);
        space.setStartPlayerNo(this.playerNo);

        for (FieldAction action : actions) {
            space.addAction(action);
        }

        for (Heading wall : walls) {
            space.addWall(wall);
        }

        return space;

    }

}
