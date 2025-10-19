import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.PreparedStatement;
import java.util.Scanner;

import io.github.cdimascio.dotenv.Dotenv;

public class HotelReservationSystem {
    private static final Dotenv dotenv= Dotenv.load();

    private static final String url= dotenv.get("DB_HOTELMANAGEMENT_URL");
    private static final String username= dotenv.get("DB_USER");
    private static final String password= dotenv.get("DB_PASSWORD");

    public static void main(String args[])
    {
        Scanner sc= new Scanner(System.in);

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } 
        catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
        }


        try 
        {
            Connection conn = DriverManager.getConnection(url, username, password);

            while (true) {
                System.out.println("Welcome to the Hotel Reservation System");
                System.out.println("1. Reserve Room");
                System.out.println("2. View Reservations");
                System.out.println("3. Get Room Number");
                System.out.println("4. Update Reservation");
                System.out.println("5. Delete Reservation");
                System.out.println("0. Exit");

                System.out.print("Choose an option: ");
                int choice = sc.nextInt();

                switch (choice) {
                    case 1:
                        reserveRooms(conn, sc);
                        break;
                    case 2:
                        viewReservations(conn);
                        break;
                    case 3:
                        getRoomNumber(conn, sc);
                        break;
                    case 4:
                        updateReservations(conn, sc);
                        break;
                    case 5:
                        deleteReservation(conn, sc);
                        break;
                    case 0:
                        exit();
                        sc.close();
                        conn.close();
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("SQL error: " + e.getMessage());
        }
        catch (Exception e) 
        {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private static void reserveRooms(Connection conn, Scanner sc) {
        System.out.print("Enter customer name, room number, contact number: ");
        String customerName = sc.nextLine();
        int roomNumber = sc.nextInt();
        int contactNumber = sc.nextInt();

        try {
            String query = "INSERT INTO reservations (customer_name, room_number, contact_number) VALUES (?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, customerName);
            pstmt.setInt(2, roomNumber);
            pstmt.setInt(3, contactNumber);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Room reserved successfully.");
            } else {
                System.out.println("Failed to reserve room.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    private static void viewReservations(Connection conn) {
        String query = "SELECT * FROM reservations;";

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                // Make sure to replace column names with actual ones from your schema

                System.out.println("Reservation ID: " + rs.getInt("id"));
                System.out.println("Customer Name: " + rs.getString("customer_name"));
                System.out.println("Room Number: " + rs.getInt("room_number"));
                System.out.println("Contact Number: " + rs.getInt("contact_number"));
                System.out.println("-----------------------------");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    private static void getRoomNumber(Connection conn, Scanner sc) {
        System.out.print("Enter reservation ID to get room number: ");
        int reservationId = sc.nextInt();

        try {
            String query = "SELECT room_number FROM reservations WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, reservationId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int roomNumber = rs.getInt("room_number");
                System.out.println("Room Number: " + roomNumber);
            } else {
                System.out.println("Reservation not found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    private static void updateReservations(Connection conn, Scanner sc) {
        System.out.print("Enter reservation ID to update: ");
        int reservationId = sc.nextInt();

        // now implement whatever fields you want to update here
    }

    private static void deleteReservation(Connection conn, Scanner sc) {
        System.out.print("Enter reservation ID to delete: ");
        int reservationId = sc.nextInt();

        try {
            String query = "DELETE FROM reservations WHERE id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, reservationId);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Reservation deleted successfully.");
            } else {
                System.out.println("Reservation not found.");
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        }
    }

    private static void exit() {
        System.out.println("Thank you for using the Hotel Reservation System.");
    }
}