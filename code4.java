// Learn to execute a UPDATE request using JDBC in Java


import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;

import io.github.cdimascio.dotenv.Dotenv;

public class code4 {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();

        // Database url
        String url = dotenv.get("DB_URL");

        // Database credentials
        String username = dotenv.get("DB_USER");
        String password = dotenv.get("DB_PASSWORD");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver loaded successfully.");
        } 
        catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }


        Connection conn = null;
        try 
        {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connected to the database.");
            System.out.println("Connection Object: " + conn);


            // Creating our Query Statement
            Statement stmt = conn.createStatement();
            String Query = "update employees set salary = 105000 where id = 2;";

            int rowsAffected = stmt.executeUpdate(Query);
            if(rowsAffected > 0) {
                System.out.println("Update successful, rows affected: " + rowsAffected);
            } 
            else {
                System.out.println("Update failed, no rows affected.");
            }

            // Closing the resources
            stmt.close();
        } 
        catch (SQLException e) 
        {
            System.err.println("Error connecting to the database: " + e.getMessage());
        }
        finally     // Ensuring the connection is closed
        {
            try 
            {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
                System.out.println("Connection closed.");
            } 
            catch (SQLException e) 
            {
                System.err.println("Error closing the connection: " + e.getMessage());
            }
        }
    }
}