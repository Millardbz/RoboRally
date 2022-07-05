package dk.dtu.compute.se.pisd.httpclient;

import dk.dtu.compute.se.pisd.roborally.exceptions.IPNotValidException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;


 //Create a http client that can interact with the RoboRally game server

public class Client implements Client_interface {
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder().version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10)).build();
    private String server = "http://localhost:8080";    //server URL, can later be changed to get this data from a DNS request or pointing directly to a server IP.
    private String serverID = "";                       //will be used after creating the connection, to inform the server what game we are in.
    private boolean connectedToServer = false;          //used to easily check if we already connected to a server, so that we can disconnect from that one first.
    private int robotNumber;                            //Is only used to free up our given robot in case we want to leave a game that has not yet concluded.

    public boolean isConnectedToServer() {
        return connectedToServer;
    }


    // Uses Json and update the game state on the server and server can store this state
    @Override
    public void updateGame(String gameState) {
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(gameState))   //Http .put
                .uri(URI.create(server + "/gameState/" + serverID))
                .setHeader("User-Agent", "RoboRally Client")
                .setHeader("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            String result = response.thenApply(HttpResponse::body).get(5, HOURS);
            // Result ignorer for now
        } catch (InterruptedException | TimeoutException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


     // Gets the current game state from Json and deserialized

    @Override
    public String getGameState() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()                                               // http .Get request for game state
                .uri(URI.create(server + "/gameState/" + serverID))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }

        return result;
    }


     //Hosts a new game on the server and sets the server id

    @Override
    public String hostGame(String title) {
        if (!Objects.equals(serverID, ""))
            leaveGame();
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(title))
                .uri(URI.create(server + "/game"))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            serverID = response.thenApply(HttpResponse::body).get(5, SECONDS);
            if (response.get().statusCode() == 500)
                return response.get().body();
            connectedToServer = true;
            robotNumber = 0;
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            serverID = "";
            return "Service timeout";
        }

        return "success";
    }


     //Lists all games available on the server
    // list data in the server's table

    @Override
    public String listGames() {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(server + "/game"))
                .setHeader("User-Agent", "RoboRally Client")
                .header("Content-Type", "application/json")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        String result;
        try {
            result = response.thenApply(HttpResponse::body).get(5, SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "server timeout";
        }
        return result;

    }


     // select an id and joins a game and get the current game state

    @Override
    public String joinGame(String serverToJoin) {
        if (!Objects.equals(serverToJoin, ""))
            leaveGame();
        HttpRequest request = HttpRequest.newBuilder()
                .PUT(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(server + "/game/" + serverToJoin))
                .header("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        CompletableFuture<HttpResponse<String>> response =
                HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
        try {
            HttpResponse<String> message = response.get(5, SECONDS); //gets the message back from the server
            if (message.statusCode() == 404)
                return message.body();
            robotNumber = Integer.parseInt(message.body());
            serverID = serverToJoin;

        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return "service timeout";
        }
        return "ok";
    }


     //  leave our current game from server

    @Override
    public void leaveGame() {
        if (Objects.equals(serverID, ""))
            return;
        HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .uri(URI.create(server + "/game/" + serverID + "/" + robotNumber))
                .header("User-Agent", "RoboRally Client")
                .header("Content-Type", "text/plain")
                .build();
        new Thread(() -> {
            int tries = 0;
            CompletableFuture<HttpResponse<String>> response =
                    HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            do {
                try {
                    response.get(5, SECONDS);
                    break;
                } catch (ExecutionException | InterruptedException e) {
                    break;
                } catch (TimeoutException e) {
                    tries++;
                }
            } while (tries != 10);
        }).start();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        serverID = "";
    }


    // the methode goes throw the ip exception. if the ip is valid, set the ip of the server in the process
    public void setServer(String server) throws IPNotValidException {
        // Simple regex pattern to check for string contains ip
        Pattern pattern = Pattern.compile("^(?:\\d{1,3}\\.){3}\\d{1,3}$");
        Matcher matcher = pattern.matcher(server);
        if (matcher.find())
            this.server = "http://" + server + ":8080";
        else
            throw new IPNotValidException();
    }


     //Return robot number given by the server
    public int getRobotNumber() {
        return robotNumber;
    }
}
