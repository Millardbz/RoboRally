package dk.dtu.compute.se.pisd.roborally.fileaccess;

import javafx.stage.FileChooser;

import java.io.File;

/**
 * Simple extension of the Java FX library to save a file
 * and return the path of that file
 *
 */
public class SaveBoard {

    public String save() {

        FileChooser c = new FileChooser();
        c.getExtensionFilters().add(new FileChooser.ExtensionFilter("Json Files", "*.json"));
        File selectedFile = c.showSaveDialog(null);

        return selectedFile.getAbsolutePath();

    }

}
