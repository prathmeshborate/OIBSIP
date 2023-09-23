import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardForm extends JFrame {
    private JPanel dashboardPanel;
    private JButton btnRegister;
    private JButton btnLogIn;

    public DashboardForm(){
        setTitle("Dashborad");
        setContentPane(dashboardPanel);
        setMinimumSize(new Dimension(500, 429));
        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        btnRegister.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                RegistrationForm registrationForm = new RegistrationForm(DashboardForm.this);
            }
        });
        setVisible(true);
        btnLogIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                LoginForm loginForm = new LoginForm(DashboardForm.this);

            }
        });
    }

    public static void main(String[] args) {
        DashboardForm myForm = new DashboardForm();
    }
}
