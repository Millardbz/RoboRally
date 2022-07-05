package dk.dtu.compute.se.pisd.httpclient;


 //This interface has some methods which we use them on both client and server .

public interface Client_interface {

    void updateGame(String gameState);
    String joinGame(String serverToJoin);

    String hostGame(String title);
    String listGames();

    String getGameState();
    void leaveGame();
}
