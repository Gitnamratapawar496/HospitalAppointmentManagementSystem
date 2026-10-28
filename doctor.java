package HospitalManagementSystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
public class doctor {
    private Connection connection;
    public doctor(Connection connection) {
        this.connection = connection;
    }
    public void viewDoctors() {
        String query = "SELECT * FROM doctors";
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            System.out.println("\nDoctors:");
            System.out.println("------------------------------------------------------------");
            System.out.printf("%-12s %-25s %-25s%n",
                    "Doctor ID", "Name", "Specialization");
            System.out.println("------------------------------------------------------------");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization =
                        resultSet.getString("specialization");

                System.out.printf("%-12d %-25s %-25s%n",
                        id, name, specialization);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException e) {
            System.out.println("Error viewing doctors: " + e.getMessage());
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";

        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement(query);

            preparedStatement.setInt(1, id);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();
            preparedStatement.close();

            return exists;

        } catch (SQLException e) {
            System.out.println("Error finding doctor: " + e.getMessage());
        }

        return false;
    }
}
