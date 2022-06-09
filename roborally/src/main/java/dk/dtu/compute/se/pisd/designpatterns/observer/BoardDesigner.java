package dk.dtu.compute.se.pisd.designpatterns.observer;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import dk.dtu.compute.se.pisd.designpatterns.observer.BoardDesignController;

public class BoardDesigner extends Application {

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        TextInputDialog dialog = new TextInputDialog();
        dialog.setContentText("Vælg bredden af spillepladen");
        dialog.showAndWait();

        int width = Integer.parseInt(dialog.getResult());

        dialog.setContentText("Vælg højden af spillepladen");
        dialog.showAndWait();

        int height = Integer.parseInt(dialog.getResult());

        BoardDesignController controller = new BoardDesignController(width, height);

        primaryStage.setTitle("Roborally Board Designer");
        BorderPane root = new BorderPane();
        Scene primaryScene = new Scene(root);
        primaryStage.setScene(primaryScene);

        root.setCenter(controller.createView());

        primaryStage.setResizable(false);
        primaryStage.sizeToScene(); // this is to fix a likely bug with the nonresizable stage
        primaryStage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
