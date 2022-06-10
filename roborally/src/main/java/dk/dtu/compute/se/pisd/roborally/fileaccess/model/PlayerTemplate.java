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

}
