import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel loginPanel;

    public LoginForm(JFrame parent) {
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(450, 490));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);


        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticatedUser(email, password);

                if (user != null) {
                    dispose();
                }
                else {
                    JOptionPane.showMessageDialog(LoginForm.this,"Wrong email or password!", "Try again", JOptionPane.ERROR_MESSAGE);
                }
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

    public User user;
    private User getAuthenticatedUser(String email, String password) {
        User user = null;

        final String URL = "jdbc:mysql://localhost/user_records?serverTimeZone = UTC";
        final String USERNAME = "root";
        final String PASSWORD = "";

        try {
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Statement stat = conn.createStatement();
            String sql = "SELECT * FROM users WHERE email =? AND password =?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = new User();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.password = resultSet.getString("password");
            }

            //close connection
            stat.close();
            conn.close();

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
        if (user != null) {
            System.out.println("Successful Authentication of " + user.name + "'s account");
            System.out.println("            Email:  " + user.email);
            System.out.println("            Phone:  " + user.phone);
            System.out.println("            Password:  " + user.password);
        }
        else {
            System.out.println("Authentication cancelled");
        }
    }
}
