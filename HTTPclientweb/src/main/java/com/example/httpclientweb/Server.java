package com.example.httpclientweb;

public class Server {
    private final String id;
    private  String title;
    private transient String gameState;
    private int numberOfPlayers;
    private int PlayersOnBoard;
    private transient boolean[] playerSpotFilled;

    public Server(String title, int id) {
        this.title=String.valueOf(title);
        this.id = String.valueOf(id);
        this.numberOfPlayers = 1;
    }

    public void addPlayer() {
        numberOfPlayers++;
    }

    public void removePlayer() {
        numberOfPlayers--;
    }

    public boolean isEmpty() {
        return numberOfPlayers == 0;
    }

    public int getAmountOfPlayers() {
        return numberOfPlayers;
    }

    public String getGameState() {
        return gameState;
    }

    public void setGameState(String gameState) {
        this.gameState = gameState;
    }

    public String getId() {
        return id;
    }
    public String getTitle() {
        return title;
    }

    public int getPlayersOnBoard() {
        return PlayersOnBoard;
    }

    public void setPlayersOnBoard(int amountOfPlayers) {
        this. PlayersOnBoard = amountOfPlayers;
        this.playerSpotFilled = new boolean[amountOfPlayers];
        playerSpotFilled[0] = true;
    }

    public int getARobot() {
        for (int i = 0; i <  PlayersOnBoard; i++)
            if (!playerSpotFilled[i]) {
                playerSpotFilled[i] = true;
                return i;
            }
        return 0;
    }

    public void setPlayerSpotFilled(int i, boolean flag) {
        playerSpotFilled[i] = flag;
    }
}
