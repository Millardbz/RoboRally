package dk.dtu.compute.se.pisd.roborally.model.database;


import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseQuery {

    private Connection connection;

    public DatabaseQuery(@NotNull Connection connection) {
        this.connection = connection;
    }

    /**
     *
     * @param query The SQL query to be executed
     * @return ResultSet. A ResultSet with the data that got selected from the database.
     */
    public ResultSet executeQuery(@NotNull String query) {

        try {

            Statement statement = this.connection.createStatement();
            ResultSet result = statement.executeQuery(query);

            return result;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * This method allows you to manipulate the database.
     * NB! Use with caution as there's no validation on your SQL query implemented.
     *
     * @param query The SQL query to be executed
     * @return boolean. Returns true if the manipulation was successful and false if it wasn't successful.
     */
    public boolean executeManipulation(@NotNull String query) {

        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            return false;
        }

    }

    public void closeConnection() {
        try {
            this.connection.close();
        } catch (SQLException e) {
            // Connection is already closed or
            // maybe the connection is not present.
        }
    }


}
