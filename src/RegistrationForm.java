import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class RegistrationForm extends JDialog{
    private JPanel registerPanel;
    private JTextField tfName;
    private JTextField tfEmail;
    private JTextField tfPhone;
    private JPasswordField pfPassword;
    private JPasswordField pfConfirmPassword;
    private JButton btnRegister;
    private JButton btnCancel;

    public RegistrationForm (JFrame parent){
        super(parent);
        setTitle("Create new account");
        setContentPane(registerPanel);
        setMinimumSize(new Dimension(450, 474));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerUser();
            }
        });
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setVisible(true);
    }

    private void registerUser() {
        String name = tfName.getText();
        String email = tfEmail.getText();
        String phone = tfPhone.getText();
        String password = String.valueOf(pfPassword.getPassword());
        String confirmPassword = String.valueOf(pfConfirmPassword.getPassword());

        //check if password and confirmPassword are similar
        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,"All fields are required", "Try again", JOptionPane.ERROR_MESSAGE);
        }
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Try again", JOptionPane.ERROR_MESSAGE);
        }

        user = addUserToDatabase(name, email, phone, password);
        if (user != null)  {
            dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Failed to create Account",  "Try again", JOptionPane.ERROR_MESSAGE);
        }
    }

    public User user;
    private User addUserToDatabase(String name, String email, String phone, String password) {
        User user = null;

        final String URL = "jdbc:mysql://localhost/user_records?serverTimeZone = UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Statement stat = conn.createStatement();
            String sql = "INSERT INTO users (name, email, phone, password)" + "VALUES (?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, phone);
            preparedStatement.setString(4, password);

            // insert a row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                user = new User();
                user.name = name;
                user.email = email;
                user.phone = phone;
                user.password = password;
            }

            stat.close();
            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public static void main(String[] args) {
        RegistrationForm regForm = new RegistrationForm(null);
        User user = regForm.user;
        if (user != null) {
            System.out.println(user.name +"'s account successfully registered =)");
        }
        else {
            System.out.println("Registration cancelled =(");
        }
    }

}
