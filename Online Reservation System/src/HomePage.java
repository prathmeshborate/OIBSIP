import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HomePage extends JDialog{
    private JButton bookTicketButton;
    private JButton cancelTicketButton;
    private JLabel lbName;
    private JPanel homePagePanel;

    public HomePage(JFrame parent){
        super(parent);
        setTitle("Home Pagen");
        setContentPane(homePagePanel);
        setMinimumSize(new Dimension(700,500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        bookTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Reservation dialog = new Reservation(null);
            }
        });
        cancelTicketButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Cancelletion myForm = new Cancelletion(null);
            }
        });
        setVisible(true);
    }


    public static void main(String[] args) {
        HomePage myForm = new HomePage(null);
    }
}
