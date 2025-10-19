// Batch Processing with JDBC


import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class code2 {
    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.load();
        Scanner sc = new Scanner(System.in);

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

            conn.setAutoCommit(false);          // Disable auto-commit for batch processing


            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO employees (id, name, job_title, salary) VALUES (?, ?, ?, ?)");

            while(true)       
            {
                System.out.print("Enter Employee ID (or type 'exit' to finish): ");
                String input = sc.nextLine();
                if (input.equalsIgnoreCase("exit")) {
                    break;
                }
                int id = Integer.parseInt(input);

                System.out.print("Enter Employee Name: ");
                String name = sc.nextLine();

                System.out.print("Enter Job Title: ");
                String jobTitle = sc.nextLine();

                System.out.print("Enter Salary: ");
                double salary = Double.parseDouble(sc.nextLine());

                // Set parameters for the prepared statement
                pstmt.setInt(1, id);
                pstmt.setString(2, name);
                pstmt.setString(3, jobTitle);
                pstmt.setDouble(4, salary);

                // Add to batch
                pstmt.addBatch();       // the benefit of using batch processing is that instead of executing each insert individually (which is less efficient), we can add multiple records in a batch and execute them all at once
            }

            // Execute batch
            int[] results = pstmt.executeBatch();
            conn.commit();                     // Commit the transaction
            System.out.println("Batch executed successfully. Rows affected: " + results.length);
            pstmt.close();
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