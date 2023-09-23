import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Cancelletion extends JDialog{
    private JPanel cancelletionPanel;
    private JTextField tfPRN;
    private JTextField tfName;
    private JTextField tfMobileNo;
    private JButton btnCheck;
    private JTextField tfDateOfJourney;
    private JButton btnCancelTicket;
    private JButton btnCancel;
    private JTextField tfTrainNo;
    private JTextField tfRefAmmount;
    private JTextField tfTrainName;
    private JTextField tfClassType;
    private JTextField tfNumberOfPassengers;
    private JTextField tfFromStation;
    private JTextField tfToStation;
    private JTextField tfAmmountPaid;

    public Cancelletion (JFrame parent){
        super(parent);
        setTitle("Ticket Cancelletion");
        setContentPane(cancelletionPanel);
        setMinimumSize(new Dimension(850,700));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        btnCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getInfoOfTicket();

            }
        });
        btnCancelTicket.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelTicket();
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

    private void cancelTicket() {
        String prn = tfPRN.getText();

        final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
        final String USERNAME = "root";
        final String PASSWORD = "root";

        try{
            Connection conn = DriverManager.getConnection (DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "DELETE FROM Reservation WHERE PRN = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, prn);

            //Insert row into the table
            int rowDeleted = preparedStatement.executeUpdate();
            if (rowDeleted > 0) {
                String prnmsg = "Ticket successfully cancelled of prn " + prn;
                JOptionPane.showMessageDialog(this,
                        prnmsg,
                        "Ticket Cancelled!",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void getInfoOfTicket() {
        String prn = tfPRN.getText();

        final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
        final String USERNAME = "root";
        final String PASSWORD = "root";

        try{
            Connection conn = DriverManager.getConnection (DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM Reservation WHERE PRN = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, prn);

            //Insert row into the table
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String Name = resultSet.getString("Name");
                String mobileNo = resultSet.getString("MobileNo");
                String trainNo = resultSet.getString("TrainNo");
                String trainName = resultSet.getString("TrainName");
                String classType = resultSet.getString("ClassType");
                String dateOfJourney = resultSet.getString("DateOfJourney");
                String noOfPassengers = resultSet.getString("NoOfPassengers");
                String fromStation = resultSet.getString("FromStation");
                String toStation = resultSet.getString("ToStation");
                String ammount = resultSet.getString("Ammount");
                tfName.setText(Name);
                tfMobileNo.setText(mobileNo);
                tfTrainNo.setText(trainNo);
                tfTrainName.setText(trainName);
                tfClassType.setText(classType);
                tfDateOfJourney.setText(dateOfJourney);
                tfNumberOfPassengers.setText(noOfPassengers);
                tfFromStation.setText(fromStation);
                tfToStation.setText(toStation);
                tfAmmountPaid.setText(ammount);
                int intammount = Integer.parseInt(ammount);
                int refAmmount = intammount-((intammount/100)*30);
                tfRefAmmount.setText(Integer.toString(refAmmount));
                if (Name.isEmpty()){
                    JOptionPane.showMessageDialog(this,
                            "Can't find ticket.Please check your PRN",
                            "Wrong PRN",
                            JOptionPane.ERROR_MESSAGE);
                }
               else {
                   btnCancelTicket.setEnabled(true);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Cancelletion myForm = new Cancelletion(null);
    }
}
