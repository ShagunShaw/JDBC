// Transaction Handling in JDBC (IMPORTANT)

/* This code demonstrates how to manage transactions in JDBC by disabling auto-commit mode i.e. executing commits() 
   and rollbacks() manually to ensure data integrity during multiple related database operations. */


import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;

import io.github.cdimascio.dotenv.Dotenv;

public class code8 {
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

            // (Imp) Disabling auto-commit mode, so that we can manage transactions manually and can commit or rollback as needed
            conn.setAutoCommit(false);


            try {
                String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
                String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

                PreparedStatement debitStmt = conn.prepareStatement(debitQuery);
                debitStmt.setDouble(1, 100.00);  // Amount to debit
                debitStmt.setString(2, "account123");          

                PreparedStatement creditStmt = conn.prepareStatement(creditQuery);
                creditStmt.setDouble(1, 100.00);   // Amount to credit
                creditStmt.setString(2, "account456");          

                debitStmt.executeUpdate();
                creditStmt.executeUpdate();

                // Committing the transaction iff both 'debitStmt.executeUpdate()' and 'creditStmt.executeUpdate()' are successful.
                conn.commit();
                System.out.println("Transaction committed successfully.");


                // Closing the resources
                debitStmt.close();
                creditStmt.close();
            }
            catch (SQLException e) 
            {
                // Rolling back the transaction in case of any error
                conn.rollback();
                System.err.println("Transaction rolled back due to error: " + e.getMessage());
            }

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