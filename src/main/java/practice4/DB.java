package practice4;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DB {
    public static final String DB_NAME = "store.db";
    private static Connection connection;
    public static Connection connect(){
        if(connection == null) {
            try {
                connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
                System.out.println("Connection to db was established");
            } catch (SQLException e) {
                throw new RuntimeException(e.getMessage());
            }
        }
        return connection;
    }

    public static void close() {
        try {
            connection.close();

            System.out.println("Connection closed");
            System.out.println();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
}
