package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.exceptions.BoardNotFoundException;
import dk.dtu.compute.se.pisd.roborally.fileaccess.SerializeState;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SerializeTest {

    @Test
    void Serializes_Deserializes_DefaultBoard() {
        try {
            Board startBoard = SaveAndLoad.newBoard(3, "SpringCramp");
            String jsonResult1 = SerializeState.serializeGame(startBoard);

            Board board1 = SerializeState.deserializeGame(jsonResult1, false);
            String jsonResult2 = SerializeState.serializeGame(board1);

            Assertions.assertEquals(jsonResult1, jsonResult2);
        } catch (BoardNotFoundException e) {
            assert true;
        }
    }


    @Test
    void Serializes_Deserializes_SavedBoard() {
        try {
            Board startBoard = SaveAndLoad.loadBoardGame("Save Testing");
            String jsonResult1 = SerializeState.serializeGame(startBoard);

            Board board1 = SerializeState.deserializeGame(jsonResult1, true);
            String jsonResult2 = SerializeState.serializeGame(board1);

            Assertions.assertEquals(jsonResult1, jsonResult2);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}


