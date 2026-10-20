package roadaccident;

import roadaccident.util.DatabaseConnection;
import java.sql.Connection;

public class TestConnection {

    public static void main(String[] args) {
        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            System.out.println(
                    "Database connected successfully."
            );

        } catch (Exception e) {
            System.out.println(
                    "Database connection failed."
            );
            e.printStackTrace();
        }
    }
}