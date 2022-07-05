package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;


public class Laser extends FieldAction {
    private int numberOfLasers;
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    public void setNumberOfLasers(int numberOfLasers) {
        this.numberOfLasers = numberOfLasers;
    }

    public int getNumberOfLasers() {
        return numberOfLasers;
    }


    @Override
    public boolean doAction(GameController gameController, Space space) {

        return true;
    }
}
