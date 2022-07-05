package com.example.httpclientweb;

import com.google.gson.Gson;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Objects;


@Service
public class ServerService implements ClientWebInterface {
    ArrayList<Server> servers = new ArrayList<>();
    private int GameID = 0;

  //Update the id and game state on the server
    @Override
    public void updateGame(String id, String gameState) {
        Server server = findServer(id);
        server.setGameState(gameState);
        if (server.getPlayersOnBoard() != 0) //if the max amount of player is set, we are done
            return;
        server.setPlayersOnBoard(StringUtils.countOccurrencesOf(gameState, "Player "));
    }


    // get game state from the server and return it in jason file
    @Override
    public String getGameState(String serverId) {
        return (findServer(serverId)).getGameState();
    }


    // use title name of the server and return new gam id
    @Override
    public String hostGame(String title) {
        servers.add(new Server(title, GameID));
        String newServerId = String.valueOf(GameID);
        GameID++;
        return newServerId;
    }

    // return list of server
    @Override
    public String listGames() {
        Gson gson = new Gson();

        ArrayList<Server> server = new ArrayList<>();
        servers.forEach(e -> {
            if (e.getAmountOfPlayers() != e.getPlayersOnBoard()) {
                server.add(e);
            }
        });
        return gson.toJson(server);
    }


    @Override
    public String joinGame(String serverToJoin) {
        Server s = findServer(serverToJoin);
        if (s == null)
            return "Server doesn't exist";
        if (s.getAmountOfPlayers() >= s.getPlayersOnBoard())
            return "Server is full";
        s.addPlayer();
        return String.valueOf(s.getARobot());
    }


    @Override
    public void leaveGame(String serverId, int robot) {
        Server server = findServer(serverId);
        assert server != null;
        server.setPlayerSpotFilled(robot, false);
        server.removePlayer();
        if (server.isEmpty())
            servers.remove(server);
    }


    private Server findServer(String serverId) {
        for (Server e : servers) {
            if (Objects.equals(e.getId(), serverId)) {
                return e;
            }
        }
        return null;
    }
}