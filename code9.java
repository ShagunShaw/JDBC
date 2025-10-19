/* Passing Connection Object to Methods in Java JDBC to see how it works with auto-commit mode 
   being set not in the main() function, but in one of the called methods. */


import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;

import io.github.cdimascio.dotenv.Dotenv;

public class code9 {
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

            // Calling the update1 method which manages its own transaction
            update1(conn);

            // Calling the update2 method which uses default auto-commit mode
            update2(conn);
            
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

    private static void update1(Connection conn)        // Here the 'conn' is passed by reference, so means if I set 'conn.setAutoCommit(false);' here, it will reflect in the 'conn' object in main() method and other method using the same 'conn' object as well.
    {
        try {
            conn.setAutoCommit(false);

            try {
                String query1= "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                PreparedStatement pstmt1 = conn.prepareStatement(query1);
                pstmt1.setDouble(1, 100.00);
                pstmt1.setString(2, "Shagun");
                pstmt1.executeUpdate();

                String query2= "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(query2);
                pstmt2.setDouble(1, 100.00);
                pstmt2.setString(2, "Ananta");
                pstmt2.executeUpdate();

                conn.commit();

                pstmt1.close();
                pstmt2.close();
            }
            catch (SQLException e) {
                conn.rollback();
                System.err.println("Error updating account, rolling back: " + e.getMessage());
            }
        } 
        catch (SQLException e) {
            System.err.println("Error setting auto-commit or rolling back: " + e.getMessage());
        }
    }


    private static void update2(Connection conn)        // Because it’s the same connection object, the auto-commit mode is still false, so we need to set it back to true or do conn.commit() manually after the operations. Here I am intentionally not setting it back to true or doing 'conn.commit()' at end to demonstrate that your queries won't be committed to the db this time.
    {
        try {
            String query1= "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            PreparedStatement pstmt1 = conn.prepareStatement(query1);
            pstmt1.setDouble(1, 100.00);
            pstmt1.setString(2, "Shagun");
            pstmt1.executeUpdate();

            String query2= "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";
            PreparedStatement pstmt2 = conn.prepareStatement(query2);
            pstmt2.setDouble(1, 100.00);
            pstmt2.setString(2, "Raunak");
            pstmt2.executeUpdate();

            pstmt1.close();
            pstmt2.close();
        }
        catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
        }
    }
}