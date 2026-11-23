package bookbridge.database;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseTest {

    public static void main(String[] args) {

        try (Connection connection =
                DatabaseConnection.getConnection()) {

            System.out.println("Database connected successfully!");
            System.out.println("Database: bookbridge_tsa4");

        } catch (SQLException ex) {
            System.out.println("Connection failed: "
                    + ex.getMessage());
        }
    }
}
