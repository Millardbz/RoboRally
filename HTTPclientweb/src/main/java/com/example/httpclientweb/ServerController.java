package com.example.httpclientweb;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class ServerController {

    @Autowired
    private ClientWebInterface webStatus;



    @PostMapping(value = "/game")
    public ResponseEntity<String> createGame(@RequestBody String s) {
        String newServerID = webStatus.hostGame(s);
        if (newServerID == null) //something went wrong
            return ResponseEntity.internalServerError().body("Server couldn't start");
        return ResponseEntity.ok().body(newServerID);
    }


    @GetMapping(value = "/game")
    public ResponseEntity<String> listOfGame() {
        return ResponseEntity.ok().body(webStatus.listGames());
    }


    @PutMapping(value = "/game/{id}")
    public ResponseEntity<String> joinGame(@PathVariable String id) {
        String response = webStatus.joinGame(id);
        if (response.equals("Server doesn't exist"))
            return ResponseEntity.status(404).body(response);
        if (response.equals("Server is full"))
            return ResponseEntity.badRequest().body(response);
        return ResponseEntity.ok().body(response);
    }


    @PostMapping(value = "/game/{id}/{robot}")
    public void leaveGame(@PathVariable String id, @PathVariable String robot) {
        webStatus.leaveGame(id, Integer.parseInt(robot));
    }


    @GetMapping(value = "/gameState/{id}")
    public ResponseEntity<String> getGameState(@PathVariable String id) {
        return ResponseEntity.ok().body(webStatus.getGameState(id));
    }


    @PutMapping(value = "/gameState/{id}")
    public ResponseEntity<String> setGameState(@PathVariable String id, @RequestBody String game) {
        webStatus.updateGame(id, game);
        return ResponseEntity.ok().body("ok");
    }
}
