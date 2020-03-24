package tech.cheating.chaireco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database {

    private Connection connection = null;
    private Economy plugin;


    public Database(Economy eco) {
        plugin = eco;
    }

    public void connect(String dbName) throws SQLException {
        if (connection == null)
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".sqlite");
    }

    public void disconnect() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to close database connection: " + e.getMessage());
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public void setup() throws SQLException {
        Statement s = connection.createStatement();
        s.executeUpdate("CREATE TABLE IF NOT EXISTS balances(player TEXT PRIMARY KEY, balance INTEGER)");
        s.executeUpdate("CREATE TABLE IF NOT EXISTS history(id INTEGER PRIMARY KEY AUTOINCREMENT, player TEXT, value INTEGER, reason TEXT)");
    }

}
