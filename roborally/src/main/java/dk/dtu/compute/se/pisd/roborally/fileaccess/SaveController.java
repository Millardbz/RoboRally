package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.designpatterns.observer.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Simple controller to handle saving of different objects.
 * In it's initial (and maybe final) version this just handles the saving of
 * instances of Board.
 *
 * @author Gustav Utke Kauman, s195396@student.dtu.dk
 */
public class SaveController {

    public static void saveBoard(Board board) {

        try {

            SaveBoard sb = new SaveBoard();
            String filename = sb.save();

            GsonBuilder builder = new GsonBuilder().registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                    setPrettyPrinting();
            Gson gson = builder.create();

            FileWriter fw = new FileWriter(filename);
            JsonWriter writer = gson.newJsonWriter(fw);

            BoardTemplate bt = (new BoardTemplate()).fromBoard(board);

            gson.toJson(bt, bt.getClass(), writer);

            writer.close();


        } catch (IOException e) {
            // XXX We should probably do something here...
        }


    }

}
