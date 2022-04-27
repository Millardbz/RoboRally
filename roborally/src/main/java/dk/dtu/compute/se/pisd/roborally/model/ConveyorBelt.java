package dk.dtu.compute.se.pisd.roborally.model;

public class ConveyorBelt {

    private final int level;
    private final Heading heading;

    public ConveyorBelt(int level, Heading heading){
        this.level = level;
        this.heading = heading;
    }
    public int getLevel(){return level;}

    public Heading getHeading() {return heading;}
}
