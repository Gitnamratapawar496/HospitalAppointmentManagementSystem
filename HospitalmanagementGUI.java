package HospitalManagementSystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class HospitalmanagementGUI extends JFrame {

    private static final String URL =
            "jdbc:mysql://localhost:3306/hospital";
    private static final String USER = "root";

    // Replace with your actual MySQL password
    private static final String PASSWORD = "namratapawar496@gmail.com";

    private Connection connection;

    private final Color NAVY = new Color(28, 43, 68);
    private final Color BLUE = new Color(48, 110, 190);
    private final Color BG = new Color(245, 247, 251);

    private JLabel patientCount;
    private JLabel doctorCount;
    private JLabel appointmentCount;

    private DefaultTableModel patientModel;
    private DefaultTableModel doctorModel;
    private DefaultTableModel appointmentModel;

    private JTable patientTable;
    private JTable doctorTable;
    private JTable appointmentTable;

    public HospitalmanagementGUI() {
        setTitle("Hospital Management System");
        setSize(1100, 680);
        setMinimumSize(new Dimension(850, 550));
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                closeApplication();
            }
        });

        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            createTables();
            initializeGUI();
            refreshAll();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "Database connection failed!\n\n"
                            + e.getMessage()
                            + "\n\nCheck MySQL service, password and database.",
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );
            closeConnection();
            dispose();
        }
    }

    // Create tables if they do not already exist
    private void createTables() throws SQLException {

        String patients =
                "CREATE TABLE IF NOT EXISTS patients (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "age INT NOT NULL," +
                "gender VARCHAR(20) NOT NULL)";

        String doctors =
                "CREATE TABLE IF NOT EXISTS doctors (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(100) NOT NULL," +
                "specialization VARCHAR(100) NOT NULL)";

        String appointments =
                "CREATE TABLE IF NOT EXISTS appointments (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "patient_id INT NOT NULL," +
                "doctor_id INT NOT NULL," +
                "appointment_date DATE NOT NULL," +
                "FOREIGN KEY (patient_id) REFERENCES patients(id)," +
                "FOREIGN KEY (doctor_id) REFERENCES doctors(id))";

        try (Statement st = connection.createStatement()) {
            st.executeUpdate(patients);
            st.executeUpdate(doctors);
            st.executeUpdate(appointments);
        }
    }

    // Main GUI
    private void initializeGUI() {

        setLayout(new BorderLayout());
        getContentPane().setBackground(BG);

        JLabel heading = new JLabel("   HOSPITAL MANAGEMENT SYSTEM");
        heading.setOpaque(true);
        heading.setBackground(NAVY);
        heading.setForeground(Color.WHITE);
        heading.setFont(new Font("SansSerif", Font.BOLD, 24));
        heading.setPreferredSize(new Dimension(100, 70));

        add(heading, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.BOLD, 14));

        tabs.addTab("Dashboard", createDashboard());
        tabs.addTab("Patients", createPatientsPanel());
        tabs.addTab("Doctors", createDoctorsPanel());
        tabs.addTab("Appointments", createAppointmentsPanel());

        tabs.addChangeListener(e -> refreshAll());

        add(tabs, BorderLayout.CENTER);

        JLabel footer = new JLabel(
                "  Hospital Management System");
        footer.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        add(footer, BorderLayout.SOUTH);
    }

    // Dashboard
    private JPanel createDashboard() {

        JPanel panel = new JPanel(new BorderLayout(15, 20));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel title = new JLabel("Dashboard Overview");
        title.setFont(new Font("SansSerif", Font.BOLD, 25));

        panel.add(title, BorderLayout.NORTH);

        JPanel cards = new JPanel(new GridLayout(1, 3, 20, 20));
        cards.setBackground(BG);

        patientCount = new JLabel("0", SwingConstants.CENTER);
        doctorCount = new JLabel("0", SwingConstants.CENTER);
        appointmentCount = new JLabel("0", SwingConstants.CENTER);

        cards.add(createCard("Total Patients", patientCount, BLUE));
        cards.add(createCard("Total Doctors", doctorCount,
                new Color(25, 145, 105)));
        cards.add(createCard("Total Appointments", appointmentCount,
                new Color(140, 85, 180)));

        panel.add(cards, BorderLayout.CENTER);

        JButton refresh = new JButton("Refresh Dashboard");
        styleButton(refresh);
        refresh.addActionListener(e -> refreshAll());

        JPanel bottom = new JPanel();
        bottom.setBackground(BG);
        bottom.add(refresh);

        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createCard(String title, JLabel value, Color color) {

        JPanel card = new JPanel(new BorderLayout(10, 15));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 235)),
                BorderFactory.createEmptyBorder(25, 10, 25, 10)));

        JLabel heading = new JLabel(title, SwingConstants.CENTER);
        heading.setFont(new Font("SansSerif", Font.BOLD, 17));

        value.setFont(new Font("SansSerif", Font.BOLD, 34));
        value.setForeground(color);

        card.add(heading, BorderLayout.NORTH);
        card.add(value, BorderLayout.CENTER);

        return card;
    }

    // Patient management
    private JPanel createPatientsPanel() {

        JPanel panel = createPagePanel();

        JTextField nameField = new JTextField(9);
        JTextField ageField = new JTextField(4);

        JComboBox<String> genderBox =
                new JComboBox<>(new String[]{"Female", "Male", "Other"});

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 10));
        form.setBackground(BG);

        form.add(new JLabel("Name:"));
        form.add(nameField);
        form.add(new JLabel("Age:"));
        form.add(ageField);
        form.add(new JLabel("Gender:"));
        form.add(genderBox);

        JButton add = new JButton("Add Patient");
        JButton edit = new JButton("Edit Selected");
        JButton delete = new JButton("Delete Selected");
        JButton refresh = new JButton("Refresh");

        styleButton(add);
        styleButton(edit);
        styleButton(delete);
        styleButton(refresh);

        form.add(add);
        form.add(edit);
        form.add(delete);
        form.add(refresh);

        patientModel = createModel("ID", "Name", "Age", "Gender");
        patientTable = new JTable(patientModel);
        styleTable(patientTable);

        add.addActionListener(e -> {

            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                showError("Please enter patient name.");
                return;
            }

            try {
                int age = Integer.parseInt(ageField.getText().trim());

                if (age < 0 || age > 150) {
                    showError("Age must be between 0 and 150.");
                    return;
                }

                String sql =
                        "INSERT INTO patients(name, age, gender) VALUES (?, ?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setString(1, name);
                    ps.setInt(2, age);
                    ps.setString(3, (String) genderBox.getSelectedItem());
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                        "Patient added successfully!");

                nameField.setText("");
                ageField.setText("");
                refreshAll();

            } catch (NumberFormatException ex) {
                showError("Please enter a valid numeric age.");
            } catch (SQLException ex) {
                showError("Could not add patient: " + ex.getMessage());
            }
        });

        edit.addActionListener(e -> editPatient());

        delete.addActionListener(e -> deletePatient());

        refresh.addActionListener(e -> refreshAll());

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(patientTable), BorderLayout.CENTER);

        return panel;
    }

    private void editPatient() {

        int row = patientTable.getSelectedRow();

        if (row == -1) {
            showError("Select a patient from the table first.");
            return;
        }

        int r = patientTable.convertRowIndexToModel(row);

        int id = ((Number) patientModel.getValueAt(r, 0)).intValue();
        String oldName = patientModel.getValueAt(r, 1).toString();
        String oldAge = patientModel.getValueAt(r, 2).toString();
        String oldGender = patientModel.getValueAt(r, 3).toString();

        JTextField name = new JTextField(oldName);
        JTextField age = new JTextField(oldAge);

        JComboBox<String> gender =
                new JComboBox<>(new String[]{"Female", "Male", "Other"});
        gender.setSelectedItem(oldGender);

        JPanel form = new JPanel(new GridLayout(3, 2, 8, 8));

        form.add(new JLabel("Name:"));
        form.add(name);
        form.add(new JLabel("Age:"));
        form.add(age);
        form.add(new JLabel("Gender:"));
        form.add(gender);

        int choice = JOptionPane.showConfirmDialog(
                this, form, "Edit Patient",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        if (name.getText().trim().isEmpty()) {
            showError("Patient name cannot be empty.");
            return;
        }

        try {
            int newAge = Integer.parseInt(age.getText().trim());

            if (newAge < 0 || newAge > 150) {
                showError("Age must be between 0 and 150.");
                return;
            }

            String sql =
                    "UPDATE patients SET name=?, age=?, gender=? WHERE id=?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name.getText().trim());
                ps.setInt(2, newAge);
                ps.setString(3, (String) gender.getSelectedItem());
                ps.setInt(4, id);
                ps.executeUpdate();
            }

            JOptionPane.showMessageDialog(this,
                    "Patient updated successfully!");

            refreshAll();

        } catch (NumberFormatException ex) {
            showError("Age must be a number.");
        } catch (SQLException ex) {
            showError("Could not update patient: " + ex.getMessage());
        }
    }

    private void deletePatient() {

        int row = patientTable.getSelectedRow();

        if (row == -1) {
            showError("Select a patient to delete.");
            return;
        }

        int r = patientTable.convertRowIndexToModel(row);

        int id = ((Number) patientModel.getValueAt(r, 0)).intValue();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete selected patient (ID: " + id + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM patients WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Patient deleted successfully!");

            refreshAll();

        } catch (SQLException ex) {
            showError("Patient may have linked appointments. "
                    + "Delete or reassign those appointments first.\n"
                    + ex.getMessage());
        }
    }

    // Doctor management
    private JPanel createDoctorsPanel() {

        JPanel panel = createPagePanel();

        JTextField nameField = new JTextField(9);
        JTextField specializationField = new JTextField(9);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 10));
        form.setBackground(BG);

        form.add(new JLabel("Doctor Name:"));
        form.add(nameField);
        form.add(new JLabel("Specialization:"));
        form.add(specializationField);

        JButton add = new JButton("Add Doctor");
        JButton edit = new JButton("Edit Selected");
        JButton delete = new JButton("Delete Selected");
        JButton refresh = new JButton("Refresh");

        styleButton(add);
        styleButton(edit);
        styleButton(delete);
        styleButton(refresh);

        form.add(add);
        form.add(edit);
        form.add(delete);
        form.add(refresh);

        doctorModel = createModel("ID", "Doctor Name", "Specialization");
        doctorTable = new JTable(doctorModel);
        styleTable(doctorTable);

        add.addActionListener(e -> {

            String name = nameField.getText().trim();
            String specialization = specializationField.getText().trim();

            if (name.isEmpty() || specialization.isEmpty()) {
                showError("Enter doctor name and specialization.");
                return;
            }

            String sql =
                    "INSERT INTO doctors(name, specialization) VALUES (?, ?)";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, specialization);
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this,
                        "Doctor added successfully!");

                nameField.setText("");
                specializationField.setText("");
                refreshAll();

            } catch (SQLException ex) {
                showError("Could not add doctor: " + ex.getMessage());
            }
        });

        edit.addActionListener(e -> editDoctor());

        delete.addActionListener(e -> deleteDoctor());

        refresh.addActionListener(e -> refreshAll());

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(doctorTable), BorderLayout.CENTER);

        return panel;
    }

    private void editDoctor() {

        int row = doctorTable.getSelectedRow();

        if (row == -1) {
            showError("Select a doctor from the table first.");
            return;
        }

        int r = doctorTable.convertRowIndexToModel(row);

        int id = ((Number) doctorModel.getValueAt(r, 0)).intValue();

        JTextField name =
                new JTextField(doctorModel.getValueAt(r, 1).toString());

        JTextField specialization =
                new JTextField(doctorModel.getValueAt(r, 2).toString());

        JPanel form = new JPanel(new GridLayout(2, 2, 8, 8));

        form.add(new JLabel("Doctor Name:"));
        form.add(name);
        form.add(new JLabel("Specialization:"));
        form.add(specialization);

        int choice = JOptionPane.showConfirmDialog(
                this, form, "Edit Doctor",
                JOptionPane.OK_CANCEL_OPTION);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        if (name.getText().trim().isEmpty()
                || specialization.getText().trim().isEmpty()) {
            showError("Name and specialization are required.");
            return;
        }

        String sql =
                "UPDATE doctors SET name=?, specialization=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, name.getText().trim());
            ps.setString(2, specialization.getText().trim());
            ps.setInt(3, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Doctor updated successfully!");

            refreshAll();

        } catch (SQLException ex) {
            showError("Could not update doctor: " + ex.getMessage());
        }
    }

    private void deleteDoctor() {

        int row = doctorTable.getSelectedRow();

        if (row == -1) {
            showError("Select a doctor to delete.");
            return;
        }

        int r = doctorTable.convertRowIndexToModel(row);

        int id = ((Number) doctorModel.getValueAt(r, 0)).intValue();

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Delete selected doctor (ID: " + id + ")?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM doctors WHERE id=?")) {

            ps.setInt(1, id);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Doctor deleted successfully!");

            refreshAll();

        } catch (SQLException ex) {
            showError("Doctor may have linked appointments. "
                    + "Delete or reassign those appointments first.\n"
                    + ex.getMessage());
        }
    }

    // Appointment management
    private JPanel createAppointmentsPanel() {

        JPanel panel = createPagePanel();

        JTextField patientIdField = new JTextField(5);
        JTextField doctorIdField = new JTextField(5);

        JTextField dateField = new JTextField(
                java.time.LocalDate.now().toString(), 10);

        JPanel form = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 10));
        form.setBackground(BG);

        form.add(new JLabel("Patient ID:"));
        form.add(patientIdField);
        form.add(new JLabel("Doctor ID:"));
        form.add(doctorIdField);
        form.add(new JLabel("Date (YYYY-MM-DD):"));
        form.add(dateField);

        JButton book = new JButton("Book Appointment");
        JButton refresh = new JButton("Refresh");

        styleButton(book);
        styleButton(refresh);

        form.add(book);
        form.add(refresh);

        appointmentModel = createModel(
                "Appointment ID", "Patient ID", "Doctor ID", "Date");

        appointmentTable = new JTable(appointmentModel);
        styleTable(appointmentTable);

        book.addActionListener(e -> {

            try {
                int patientId =
                        Integer.parseInt(patientIdField.getText().trim());

                int doctorId =
                        Integer.parseInt(doctorIdField.getText().trim());

                Date date = Date.valueOf(dateField.getText().trim());

                if (!recordExists("patients", patientId)) {
                    showError("Patient ID does not exist.");
                    return;
                }

                if (!recordExists("doctors", doctorId)) {
                    showError("Doctor ID does not exist.");
                    return;
                }

                String check =
                        "SELECT COUNT(*) FROM appointments " +
                        "WHERE doctor_id=? AND appointment_date=?";

                try (PreparedStatement ps = connection.prepareStatement(check)) {
                    ps.setInt(1, doctorId);
                    ps.setDate(2, date);

                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next() && rs.getInt(1) > 0) {
                            showError(
                                    "This doctor already has an appointment "
                                    + "on that date.");
                            return;
                        }
                    }
                }

                String sql =
                        "INSERT INTO appointments" +
                        "(patient_id, doctor_id, appointment_date) " +
                        "VALUES (?, ?, ?)";

                try (PreparedStatement ps = connection.prepareStatement(sql)) {
                    ps.setInt(1, patientId);
                    ps.setInt(2, doctorId);
                    ps.setDate(3, date);
                    ps.executeUpdate();
                }

                JOptionPane.showMessageDialog(this,
                        "Appointment booked successfully!");

                refreshAll();

            } catch (NumberFormatException ex) {
                showError("Patient ID and Doctor ID must be numbers.");
            } catch (IllegalArgumentException ex) {
                showError("Enter date in YYYY-MM-DD format.");
            } catch (SQLException ex) {
                showError("Could not book appointment: " + ex.getMessage());
            }
        });

        refresh.addActionListener(e -> refreshAll());

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(appointmentTable), BorderLayout.CENTER);

        return panel;
    }

    // Shared utilities
    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BG);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        return panel;
    }

    private DefaultTableModel createModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void styleTable(JTable table) {
        table.setRowHeight(27);
        table.setFont(new Font("SansSerif", Font.PLAIN, 13));
        table.getTableHeader().setFont(
                new Font("SansSerif", Font.BOLD, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
    }

    private boolean recordExists(String table, int id)
            throws SQLException {

        if (!table.equals("patients") && !table.equals("doctors")) {
            throw new IllegalArgumentException("Invalid table name");
        }

        String sql = "SELECT id FROM " + table + " WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int getCount(String table) throws SQLException {

        if (!table.equals("patients")
                && !table.equals("doctors")
                && !table.equals("appointments")) {
            throw new IllegalArgumentException("Invalid table");
        }

        String sql = "SELECT COUNT(*) FROM " + table;

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    private void loadPatients() {

        patientModel.setRowCount(0);

        String sql =
                "SELECT id, name, age, gender FROM patients ORDER BY id";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                patientModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender")
                });
            }

        } catch (SQLException e) {
            showError("Could not load patients: " + e.getMessage());
        }
    }

    private void loadDoctors() {

        doctorModel.setRowCount(0);

        String sql =
                "SELECT id, name, specialization FROM doctors ORDER BY id";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                doctorModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("specialization")
                });
            }

        } catch (SQLException e) {
            showError("Could not load doctors: " + e.getMessage());
        }
    }

    private void loadAppointments() {

        appointmentModel.setRowCount(0);

        String sql =
                "SELECT id, patient_id, doctor_id, appointment_date " +
                "FROM appointments ORDER BY appointment_date DESC";

        try (Statement st = connection.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                appointmentModel.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getInt("patient_id"),
                        rs.getInt("doctor_id"),
                        rs.getDate("appointment_date")
                });
            }

        } catch (SQLException e) {
            showError("Could not load appointments: " + e.getMessage());
        }
    }

    private void refreshAll() {

        if (connection == null || patientModel == null) {
            return;
        }

        loadPatients();
        loadDoctors();
        loadAppointments();

        try {
            patientCount.setText(
                    String.valueOf(getCount("patients")));

            doctorCount.setText(
                    String.valueOf(getCount("doctors")));

            appointmentCount.setText(
                    String.valueOf(getCount("appointments")));

        } catch (SQLException e) {
            showError("Could not update dashboard: " + e.getMessage());
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(
                this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
            connection = null;
        }
    }

    private void closeApplication() {
        closeConnection();
        dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HospitalmanagementGUI gui = new HospitalmanagementGUI();

            if (gui.connection != null) {
                gui.setVisible(true);
            }
        });
    }
}
