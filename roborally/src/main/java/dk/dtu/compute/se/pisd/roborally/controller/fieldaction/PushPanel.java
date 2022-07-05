package dk.dtu.compute.se.pisd.roborally.controller.fieldaction;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

// push the robot one step forward
public class PushPanel extends FieldAction {
    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    @Override
    public boolean doAction(GameController gameController, Space space) {
        if (space.getActions().size() > 0) {
            PushPanel pushPanel = (PushPanel) space.getActions().get(0);
            Heading direction = pushPanel.getHeading();

            Player player = space.getPlayer();
            if (player != null) {
                Heading playerHeading = player.getHeading();
                player.setHeading(direction);
                gameController.moveForward(player, 1);
                player.setHeading(playerHeading);

                return true;
            }
        }

        return false;
    }
}
