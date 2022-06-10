package dk.dtu.compute.se.pisd.designpatterns.observer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.fileaccess.Adapter;
import dk.dtu.compute.se.pisd.roborally.model.Antenna;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.util.ArrayList;
import java.util.List;

public class BoardTemplate {

    public int width;
    public int height;

    public int antennaX, antennaY;

    public List<SpaceTemplate> spaces = new ArrayList<SpaceTemplate>();
    public String name;

    public BoardTemplate fromBoard(Board board) {
        this.width = board.width;
        this.height = board.height;

        if (board.getAntenna() != null) {
            this.antennaX = board.getAntenna().x;
            this.antennaY = board.getAntenna().y;
        }

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                if (!board.getSpace(i,j).getWalls().isEmpty() || !board.getSpace(i,j).getActions().isEmpty() || board.getSpace(i,j).getStartPlayerNo() != 0) {
                    // only convert the spaces that actually have some relevant data
                    spaces.add((new SpaceTemplate()).fromSpace(board.getSpace(i,j)));
                }
            }
        }

        return this;
    }

    public Board toBoard() {

        Board board = new Board(this.width, this.height);
        Antenna antenna = new Antenna(board, this.antennaX, this.antennaY);
        board.setAntenna(antenna);

        for (SpaceTemplate spaceTemplate : spaces) {
            Space space = spaceTemplate.toSpace(board);
            board.getSpaces()[space.x][space.y] = space;
        }

        for (int i = 0; i < board.width; i++) {
            for (int j = 0; j < board.height; j++) {
                if (board.getSpace(i,j) == null) {
                    // fill out the "empty" spaces
                    board.getSpaces()[i][j] = new Space(board,i,j);
                }
            }
        }

        return board;

    }

    @Override
    public String toString() {

        GsonBuilder builder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = builder.create();


        return gson.toJson(this, this.getClass());

    }
}
