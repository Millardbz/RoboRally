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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

/*  RoboRally MenuBar shows File and Server menu and some menu items that allows player to
    create a game, load and save the game and connect to a server for a multiplayer game.
*/
public class RoboRallyMenuBar extends MenuBar {

    private AppController appController;

    private Menu controlMenu;

    private MenuItem saveGame;
    private MenuItem newGame;
    private MenuItem loadGame;
    private MenuItem stopGame;
    private MenuItem exitApp;

    private  MenuItem MultiplayerGame;
    private  MenuItem ConnectServer;
    private  MenuItem DisconnectServer;
    private  MenuItem Connecthostgame;


    // RoboRally MenuBar constructor

    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        controlMenu = new Menu("File");
        this.getMenus().add(controlMenu);

        newGame = new MenuItem("New Game");
        newGame.setOnAction( e -> this.appController.newGame());
        controlMenu.getItems().add(newGame);

        stopGame = new MenuItem("Stop Game");
        stopGame.setOnAction( e -> this.appController.stopGame());
        controlMenu.getItems().add(stopGame);

        saveGame = new MenuItem("Save Game");
        saveGame.setOnAction( e -> this.appController.saveGame());
        controlMenu.getItems().add(saveGame);

        loadGame = new MenuItem("Load Game");
        loadGame.setOnAction( e -> this.appController.loadGame());
        controlMenu.getItems().add(loadGame);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction( e -> this.appController.exitGame());
        controlMenu.getItems().add(exitApp);


        Menu serverMenu = new Menu("Network Game");
        this.getMenus().add(serverMenu);

        ConnectServer = new MenuItem("Server connection");
        ConnectServer.setOnAction(e -> this.appController.Client_ConnectToServer());
        serverMenu.getItems().add(ConnectServer);

        Connecthostgame = new MenuItem("Host game");
        Connecthostgame.setOnAction(e -> {
            this.appController.stopGame();
            this.appController.ClientHostGame();
        });
        serverMenu.getItems().add(Connecthostgame);

        DisconnectServer = new MenuItem("server disconnection");
        DisconnectServer.setOnAction(e -> {
            this.appController.Client_Disconnect_Server();
            this.appController.stopGame();
        });
        serverMenu.getItems().add(DisconnectServer);


        controlMenu.setOnShowing(e -> update());
        controlMenu.setOnShown(e -> this.updateBounds());
        serverMenu.setOnShowing(e -> update());
        serverMenu.setOnShown(e -> this.updateBounds());
        update();


    }

    public void update() {
        if (appController.isGameRunning()) {
            newGame.setVisible(false);
            stopGame.setVisible(true);
            saveGame.setVisible(true);
            loadGame.setVisible(false);

            ConnectServer.setVisible(true);
            Connecthostgame.setVisible(true);
            DisconnectServer.setVisible(true);

        } else {
            newGame.setVisible(true);
            stopGame.setVisible(false);
            saveGame.setVisible(false);
            loadGame.setVisible(true);
        }

    }

}
