package dk.dtu.compute.se.pisd.roborally.model.database;

import javafx.scene.control.Alert;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.sql.*;
import java.util.Scanner;

public class DatabaseConnection {

    private String driver = "com.mysql.cj.jdbc.Driver";
    private String host = "localhost";
    private String port = "3306";
    private String user = "root";
    private String password = "";
    private String database;

    private static final String OPTIONS = "characterEncoding=latin1&serverTimezone=Europe/Copenhagen";

    /**
     * This pulls out the information needed from the settings file
     */
        public DatabaseConnection() {

            try {
                InputStream is = this.getClass().getClassLoader().getResourceAsStream("settings/settings.json");
                StringBuilder jsonString = new StringBuilder();

                if (is != null) {
                    Scanner reader = new Scanner(is);
                    while (reader.hasNext()) {
                        jsonString.append(reader.next());
                    }
                }

                JSONObject dbSettings = new JSONObject(jsonString.toString()).getJSONObject("database-settings");

                this.database = dbSettings.getString("database");
                this.host = dbSettings.getString("host");
                this.port = dbSettings.getString("port");
                this.user = dbSettings.getString("user");
                this.password = dbSettings.getString("password");

                createSchema();


            } catch (JSONException e) {
                e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.WARNING, "Du har ikke korrekt oprettet settings filen. Se README.md for mere information");
            alert.showAndWait();

            System.exit(-1);

        }

    }

    public DatabaseConnection(@NotNull String database) {
        this.database = database;
    }

    public DatabaseConnection(@NotNull String database, @NotNull String user, @NotNull String password) {
        this.database = database;
        this.user = user;
        this.password = password;
    }

    public DatabaseConnection(@NotNull String database, @NotNull String host, @NotNull String port, @NotNull String user, @NotNull String password) {
        this.database = database;
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
    }

    /**
     * Method to get the Connection instance.
     *
     * @return Connection. Returns the connection.
     */
    public Connection getConnection() {

        try {

            Class.forName(this.driver);

            String url = "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database + "?" + OPTIONS;

            Connection connection = DriverManager.getConnection(url, this.user, this.password);

            return connection;

        } catch (ClassNotFoundException | SQLException e) {

            e.printStackTrace();

        }

        return null;

    }

    /**
     * Creates the schemas in the database
     */
    private void createSchema() {

        try {
            Class.forName(this.driver);

            String url = "jdbc:mysql://" + this.host + ":" + this.port + "?" + OPTIONS;
            Connection connection = DriverManager.getConnection(url, this.user, this.password);

            connection.setAutoCommit(false);

            ResultSet catalogs = connection.getMetaData().getCatalogs();

            while (catalogs.next()) {
                if (catalogs.getString("TABLE_CAT").toLowerCase().equals(this.database.toLowerCase())) {
                    return;
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "The database does not exist and will now be created");
            alert.showAndWait();

            Statement stmt = connection.createStatement();

            // Create DB
            stmt.addBatch("CREATE SCHEMA IF NOT EXISTS " + this.database );
            stmt.addBatch("USE " + this.database);

            // Create tables
            stmt.addBatch("CREATE TABLE IF NOT EXISTS `game` ( `ID` INT NOT NULL AUTO_INCREMENT, `name` VARCHAR(100) NOT NULL, `phase` ENUM(\"INITIALISATION\", \"PROGRAMMING\", \"ACTIVATION\", \"PLAYER_INTERACTION\") NOT NULL, `step` INT NOT NULL, `boardLayout` BLOB NOT NULL, `currentPlayer` INT NULL DEFAULT NULL, PRIMARY KEY (`ID`), INDEX `fk_game_player_idx` (`currentPlayer` ASC) VISIBLE) ENGINE = InnoDB;");
            stmt.addBatch("CREATE TABLE IF NOT EXISTS `player` ( `ID` INT NOT NULL, `gameID` INT NOT NULL, `playerName` VARCHAR(45) NOT NULL, `posX` INT NOT NULL, `posY` INT NOT NULL, `heading` ENUM(\"NORTH\", \"EAST\", \"SOUTH\", \"WEST\") NOT NULL, `colour` VARCHAR(50) NULL DEFAULT NULL, `order` INT NOT NULL, `last_checkpoint` INT NOT NULL DEFAULT 0, PRIMARY KEY (`ID`, `gameID`), INDEX `fk_player_game1_idx` (`gameID` ASC) VISIBLE) ENGINE = InnoDB;");
            stmt.addBatch("CREATE TABLE IF NOT EXISTS `playerHand` ( `playerID` INT NOT NULL, `gameID` INT NOT NULL, `card0` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card1` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card2` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card3` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card4` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card5` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card6` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card7` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, PRIMARY KEY (`playerID`, `gameID`), INDEX `fk_playerHand_game1_idx` (`gameID` ASC) VISIBLE) ENGINE = InnoDB;");
            stmt.addBatch("CREATE TABLE IF NOT EXISTS `playerRegister` ( `playerID` INT NOT NULL, `gameID` INT NOT NULL, `card0` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card1` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card2` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card3` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, `card4` ENUM(\"FORWARD\", \"RIGHT\", \"LEFT\", \"FAST_FORWARD\", \"OPTION_LEFT_RIGHT\") NULL, PRIMARY KEY (`playerID`, `gameID`), INDEX `fk_playerHand_game1_idx` (`gameID` ASC) VISIBLE) ENGINE = InnoDB;");

            // Create foreign keys
            stmt.addBatch("ALTER TABLE `player` ADD CONSTRAINT `fk_player_game` FOREIGN KEY (`gameID`) REFERENCES `game` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            stmt.addBatch("ALTER TABLE `game` ADD CONSTRAINT `fk_game_player` FOREIGN KEY (`currentPlayer`) REFERENCES `player` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            stmt.addBatch("ALTER TABLE `playerHand` ADD CONSTRAINT `fk_playerHand_player` FOREIGN KEY (`playerID`) REFERENCES `player` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION, ADD CONSTRAINT `fk_playerHand_game` FOREIGN KEY (`gameID`) REFERENCES `game` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;");
            stmt.addBatch("ALTER TABLE `playerRegister` ADD CONSTRAINT `fk_playerRegister_player1` FOREIGN KEY (`playerID`) REFERENCES `player` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION, ADD CONSTRAINT `fk_playerRegister_game1` FOREIGN KEY (`gameID`) REFERENCES `game` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION;");

            stmt.executeBatch();

            // Close connection
            connection.close();

            alert.setContentText("The database has been created");
            alert.showAndWait();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while creating the database");
            alert.showAndWait();
        }
    }

    public DatabaseConnection setDriver(String driver) {
        this.driver = driver;
        return this;
    }

    public DatabaseConnection setHost(String host) {
        this.host = host;
        return this;
    }

    public DatabaseConnection setPort(String port) {
        this.port = port;
        return this;
    }

    public DatabaseConnection setUser(String user) {
        this.user = user;
        return this;
    }

    public DatabaseConnection setPassword(String password) {
        this.password = password;
        return this;
    }
}
