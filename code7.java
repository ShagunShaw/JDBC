// Image Handling with JDBC
// For this we are creating another table in our 'mydatabase' database named 'image_table'  from this program only.

// Also write here the code for retrieving the image from the image_table and saving it to local disk.

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import io.github.cdimascio.dotenv.Dotenv;

public class code7 {
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


            // checking if our image_table is created successfully or not
            Statement stmt = conn.createStatement();
            String checkTableQuery = "SHOW TABLES LIKE 'image_table'";
            
            ResultSet rs = stmt.executeQuery(checkTableQuery);
            if (!rs.next()) {                // if image_table do not exists
                String createTableQuery = "CREATE TABLE image_table (image_id INT AUTO_INCREMENT PRIMARY KEY, " +
                                            "image_data LONGBLOB NOT NULL, upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP);";
                
                try {
                    stmt.executeUpdate(createTableQuery);
                    System.out.println("Table was not present, and is now 'image_table' created successfully.");
                } 
                catch (SQLException e) {
                    throw new RuntimeException("Failed to create table 'image_table': " + e.getMessage());
                }
            } 

            // insert image into image_table
            String imagePath = "F:\\Development Docs\\cow.jpg";        //   Use \\ instead of \ in file path

            try {
                // Now before inserting our image into the table, we need to convert it from jpg/jpeg/png to binary format
                FileInputStream file = new FileInputStream(imagePath);
                byte[] imageData = new byte[file.available()];
                file.read(imageData);       // reading the image data into byte array

                String insertImageQuery = "INSERT INTO image_table (image_data) VALUES (?)";
                PreparedStatement pstmt = conn.prepareStatement(insertImageQuery);
                pstmt.setBytes(1, imageData);
                int rowsAffected = pstmt.executeUpdate();

                if(rowsAffected > 0) {
                    System.out.println("Image inserted successfully, rows affected: " + rowsAffected);
                } 
                else {
                    System.out.println("No image inserted.");
                }

                file.close();
            }
            catch (FileNotFoundException e) {
                System.err.println("Image file not found: " + e.getMessage());
            }
            catch (IOException e) {
                System.err.println("Error reading image file: " + e.getMessage());
            }
            
            

            // Retrieve image from image_table and save to local disk
            String selectImageQuery = "SELECT image_data FROM image_table WHERE image_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(selectImageQuery);
            pstmt.setInt(1, 1);  
            ResultSet res = pstmt.executeQuery();

            if (res.next()) {
                byte[] imageData = res.getBytes("image_data");
                // Save the image data to a file
                String outputPath = "F:\\Development Docs\\retrieved_cow.jpg";          //   Use \\ instead of \ in file path
                try {
                    FileOutputStream fos = new FileOutputStream(outputPath);
                    fos.write(imageData);
                    System.out.println("Image retrieved successfully and saved to: " + outputPath);
                } catch (IOException e) {
                    System.err.println("Error saving image file: " + e.getMessage());
                }
            } 
            else {
                System.out.println("No image found with the specified ID.");
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