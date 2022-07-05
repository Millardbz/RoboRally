/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import dk.dtu.compute.se.pisd.httpclient.Client;
import dk.dtu.compute.se.pisd.httpclient.Server;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.view.BoardView;
import dk.dtu.compute.se.pisd.roborally.view.RoboRallyMenuBar;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.StringReader;
import java.net.URISyntaxException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private Stage Stage;
    private BorderPane boardRoot;


    private BoardView boardView;

    private TableView<Server> table = new TableView<>();
    private static ObservableList<Server> data = FXCollections.observableArrayList();
    AppController appController1 , appController2 ,appController3;

    Client client;





    @Override
    public void init() throws Exception {
        super.init();
    }

    // Start Game stage

    @Override
    public void start(Stage primaryStage) {

        this.Stage = primaryStage;
        AppController appController = new AppController(this);

        // create a scene, menu bar and a pane for the primary Stage
        primaryStage.setTitle("RoboRally");

        boardRoot = new BorderPane();
        Scene primaryScene = new Scene(boardRoot);

        primaryStage.setScene(primaryScene);

        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);

        primaryStage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exitGame();} );


        // RoboRally image on the first scene
        Image img = null;
        try {
            img = new Image(SpaceView.class.getClassLoader().getResource("Images/RoboRallyimage.png").toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        ImageView imageView = new ImageView(img);


        // shows Server table and buttons on primary stage

        TableColumn id = new TableColumn("ID");
        id.setMaxWidth(50);
        id.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getId())
        );

        TableColumn serverName = new TableColumn("Server Name");
        serverName.setMaxWidth(200);
        serverName.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, String>, ObservableValue<String>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getTitle())
        );

        TableColumn players = new TableColumn("Players");
        players.setMaxWidth(100);
        players.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getnumberOfPlayers() )
        );
        TableColumn PlayersOnBoard = new TableColumn("Max Players on the board");
        PlayersOnBoard.setMaxWidth(300);
        PlayersOnBoard.setResizable(false);
        PlayersOnBoard.setCellValueFactory((Callback<TableColumn.CellDataFeatures<Server, Integer>, ObservableValue<Integer>>)
                p -> new ReadOnlyObjectWrapper(p.getValue().getPlayersOnBoard() )
        );

        Button hostGame = new Button("Host Game");
        hostGame.setOnAction(e -> {
             appController2.ClientHostGame();


        });
        Button connect_to_server = new Button("Connect to Server");
        connect_to_server.setOnAction(e -> {
            this.appController3.Client_ConnectToServer();

        });

    Button Disconnect_from_server = new Button("Disconnect from Server");
        Disconnect_from_server.setOnAction(e -> {
        this.appController3.Client_ConnectToServer();

    });

        Button button = new Button("Join to a Game");
        button.setOnAction(e -> {appController1.stopGame();
            if (!table.getSelectionModel().isEmpty()) appController1.ClientJoinGame(table.getSelectionModel().getSelectedItem().getId());
        });

        Button refresh = new Button("Refresh Connection");
        refresh.setOnAction(e -> addServer(client.listGames()));



        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 10, 10, 10));
        vbox.getChildren().addAll( table, hostGame,connect_to_server,button, refresh,Disconnect_from_server);


        boardRoot.setTop(menuBar);
        boardRoot.setRight(vbox);
        boardRoot.setCenter(imageView);

        table.setItems(data);
        table.getColumns().addAll(id, players, serverName,PlayersOnBoard );
        table.isEditable();
        table.setEditable(false);
        table.setVisible(true);

        primaryStage.setResizable(false);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void addServer(String s) {
        Gson test = new Gson();
        JsonReader jReader = new JsonReader(new StringReader(s));
        Server[] servers = test.fromJson(jReader, Server[].class);
        data.clear();
        data.addAll(servers);
    }


    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
       // boardRoot.getChildren().clear();


        if (gameController != null) {
            // create and add view for new board
            boardView = new BoardView(gameController);
            boardRoot.setCenter(boardView);

        }

        Stage.sizeToScene();

    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }
    public BoardView getBoardView() {
        return boardView;
    }

    public static void main(String[] args) {
        launch(args);
    }


}