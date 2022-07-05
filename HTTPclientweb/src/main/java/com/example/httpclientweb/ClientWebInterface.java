package com.example.httpclientweb;


 //Interface between client and server

public interface ClientWebInterface {
    void updateGame(String id, String gameState);

    String joinGame(String serverToJoin);

    void leaveGame(String serverId, int i);

    String getGameState(String serverId);

    String hostGame(String title);

    String listGames();
}


