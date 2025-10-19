// Prepared Statements in JDBC


import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import io.github.cdimascio.dotenv.Dotenv;

public class code6 {
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


            /*   Creating our Query Statement using PreparedStatement instead of Statement   */
            String Query = "select * from employees where id = ?";

            // Statement stmt = conn.createStatement();                 instead of this we use PreparedStatement
            PreparedStatement pstmt = conn.prepareStatement(Query);

            pstmt.setInt(1, 2);

            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                System.out.println("Employee found:");
                System.out.println("ID: " + rs.getInt("id"));
                System.out.println("Name: " + rs.getString("name"));
                System.out.println("Job Title: " + rs.getString("job_title"));
                System.out.println("Salary: " + rs.getInt("salary"));
            } 
            else {
                System.out.println("No employee found with ID 2.");
            }

            // Closing the resources
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