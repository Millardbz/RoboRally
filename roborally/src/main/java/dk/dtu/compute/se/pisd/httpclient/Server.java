package dk.dtu.compute.se.pisd.httpclient;


 //This class uses for Json to determine how the string from the server is declared.

public class Server {

    private String id;
    private String title;
    private transient String gameState;
    private int numberOfPlayers;
    private int PlayersOnBoard;
    private transient boolean[] playerSpotFilled;

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getnumberOfPlayers() {
        return numberOfPlayers;
    }

    public int getPlayersOnBoard() {
        return PlayersOnBoard;
    }
}
