import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class LoginForm extends JDialog {
    private JTextField tfEmail;
    private JPasswordField pfPassword;
    private JButton btnRegister;
    private JButton btnCancel;
    private JButton btnLogIn;
    private JPanel loginPanel;

    public LoginForm(JFrame parent){
        super(parent);
        setTitle("Login");
        setContentPane(loginPanel);
        setMinimumSize(new Dimension(700, 500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = tfEmail.getText();
                String password = String.valueOf(pfPassword.getPassword());

                user = getAuthenticatedUser(email, password);

                if (user == null) {
                    JOptionPane.showMessageDialog(LoginForm. this,
                            "Email or Password Invalid",
                            "Try again",
                            JOptionPane.ERROR_MESSAGE);
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
    private User getAuthenticatedUser(String email, String password){
        User user = null;
        final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
        final String USERNAME = "root";
        final String PASSWORD = "root";
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM User WHERE (Email = ? OR Id = ?) AND Password = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, email);
            preparedStatement.setString(3, password);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User ();
                user.name = resultSet.getString("name");
                user.email = resultSet.getString("email");
                user.phone = resultSet.getString("phone");
                user.address = resultSet.getString("address");
                user.password = resultSet.getString("password");
                String userName = resultSet.getString("Name");
                String MessID ="Welcome!... " + userName;
                JOptionPane.showMessageDialog(this,
                        MessID,
                        "Login Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
                HomePage myForm = new HomePage(null);

            }

            stmt.close();
            conn.close();

        }catch(Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    public static void main(String[] args) {
        LoginForm loginForm = new LoginForm(null);
        User user = loginForm.user;
    }
}
