package dk.dtu.compute.se.pisd.roborally.fileaccess.model;

import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.List;

public class PlayerTemplate {

    public Board board;

    public String name;
    public String color;

    public Space space;
    public Heading heading;

    public List<CommandCardField> program;
    public List<CommandCardField> cards;

    public PlayerTemplate(Board board, String name, String color,
                          Space space, Heading heading, List<CommandCardField> program,
                          List<CommandCardField> cards) {
        this.board = board;
        this.name = name;
        this.color = color;
        this.space = space;
        this.heading = heading;
        this.program = program;
        this.cards = cards;
    }
}
