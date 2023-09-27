package org.example;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionProvider {
    private static Connection con;

    public static Connection getConnection() {
        try {
            if (con == null) {
                Class.forName("org.postgresql.Driver");

                String url = "jdbc:postgresql://localhost:1099/aims";
                String username = "postgres";
                String password = "5250";
                con = DriverManager.getConnection(url, username, password);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return con;
    }
}
