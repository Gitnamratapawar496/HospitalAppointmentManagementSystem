package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class Hospitalmanagement {

    private static final String url =
            "jdbc:mysql://localhost:3306/hospital";
    private static final String username = "root";
    private static final String password = "namratapawar496@gmail.com";

    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL JDBC Driver not found!");
            e.printStackTrace();
            return;
        }

        try (Scanner scanner = new Scanner(System.in);
             Connection connection =
                     DriverManager.getConnection(url, username, password)) {

            patient patient = new patient(connection, scanner);
            doctor doctor = new doctor(connection);

            while (true) {
                System.out.println("\n===== HOSPITAL MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Patient");
                System.out.println("2. View Patients");
                System.out.println("3. View Doctors");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter your choice: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Please enter a valid number!");
                    scanner.next();
                    continue;
                }

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        patient.addPatient();
                        break;

                    case 2:
                        patient.viewPatients();
                        break;

                    case 3:
                        doctor.viewDoctors();
                        break;

                    case 4:
                        bookAppointment(patient, doctor, connection, scanner);
                        break;

                    case 5:
                        System.out.println(
                                "THANK YOU FOR USING HOSPITAL MANAGEMENT SYSTEM");
                        return;

                    default:
                        System.out.println("Enter a valid choice!");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database connection error!");
            System.out.println(e.getMessage());
        }
    }

    public static void bookAppointment(
            patient patient,
            doctor doctor,
            Connection connection,
            Scanner scanner) {

        System.out.print("Enter Patient ID: ");
        int patientId = scanner.nextInt();

        System.out.print("Enter Doctor ID: ");
        int doctorId = scanner.nextInt();

        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();

        if (!patient.getPatientById(patientId)) {
            System.out.println("Patient does not exist!");
            return;
        }

        if (!doctor.getDoctorById(doctorId)) {
            System.out.println("Doctor does not exist!");
            return;
        }

        if (!checkDoctorAvailability(
                doctorId, appointmentDate, connection)) {
            System.out.println("Doctor is not available on this date!");
            return;
        }

        String query =
                "INSERT INTO appointments " +
                "(patient_id, doctor_id, appointment_date) VALUES (?, ?, ?)";

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {

            preparedStatement.setInt(1, patientId);
            preparedStatement.setInt(2, doctorId);
            preparedStatement.setDate(
                    3, java.sql.Date.valueOf(appointmentDate));

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Appointment Booked Successfully!");
            } else {
                System.out.println("Failed to book appointment!");
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error booking appointment: "
                    + e.getMessage());
        }
    }

    public static boolean checkDoctorAvailability(
            int doctorId,
            String appointmentDate,
            Connection connection) {

        String query =
                "SELECT COUNT(*) FROM appointments " +
                "WHERE doctor_id = ? AND appointment_date = ?";

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(query)) {

            preparedStatement.setInt(1, doctorId);
            preparedStatement.setDate(
                    2, java.sql.Date.valueOf(appointmentDate));

            try (ResultSet resultSet =
                         preparedStatement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) == 0;
                }
            }

        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error checking availability: "
                    + e.getMessage());
        }

        return false;
    }
}