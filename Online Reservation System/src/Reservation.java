import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.SecureRandom;
import java.sql.*;
import java.util.ArrayList;

public class Reservation extends JDialog {
    private JPanel reservationPanel;
    private JButton btnBook;
    private JButton btnCancel;
    private JTextField tfName;
    private JTextField tfMobileNo;
    private JTextField tfTrainNo;
    private JTextField tfTrainName;
    private JComboBox cbFromStation;
    private JComboBox cbToStation;
    private JComboBox cbClassType;
    private JButton btnCheck;
    private JTextField tfNoOfPassengers;
    private JFormattedTextField ftfDate;
    private JButton btnGetAmmount;
    private JTextField tfAmmount;
    private JLabel lbFromTime;
    private JLabel lbToTime;

    public Reservation(JFrame parent) {
        super(parent);
        setTitle("Reservation");
        setContentPane(reservationPanel);
        setMinimumSize(new Dimension(700,500));
        setModal(true);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        cbClassType.addItem("1AC");
        cbClassType.addItem("2AC");
        cbClassType.addItem("3AC");
        cbClassType.addItem("SLEEPER");
        cbClassType.addItem("2S");
        cbClassType.addItem("GENERAL");

        try {
            MaskFormatter dateFormatter = new MaskFormatter("##/##/####"); // Define your desired date format
            dateFormatter.setPlaceholderCharacter('_'); // Optional: Use underscores as placeholders
            ftfDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        btnBook.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                reservation();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        btnCheck.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                getNameandStation();

            }
        });

        btnGetAmmount.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String seatType = cbClassType.getSelectedItem().toString();
                String passCount = tfNoOfPassengers.getText();
                if (passCount.isEmpty()) {
                    JOptionPane.showMessageDialog(Reservation.this,
                            "Please enter number of passengers!",
                            "",
                            JOptionPane.ERROR_MESSAGE);

                }
                int fpassCount = Integer.parseInt(passCount);
                int fcount = cbFromStation.getSelectedIndex();
                int tcount = cbToStation.getSelectedIndex();
                int avg = (tcount-fcount)*fpassCount;
                int ammount = 0;
                if(seatType == "1AC"){
                    ammount = 975 * avg;
                } else if (seatType == "2AC") {
                    ammount = 745 * avg;
                } else if (seatType == "3AC") {
                    ammount = 450 * avg;
                } else if (seatType == "Sleeper") {
                    ammount = 220 * avg;
                } else if (seatType== "2S") {
                    ammount = 175 * avg;
                }else {
                    ammount = 120 * avg;
                }
                tfAmmount.setText(Integer.toString(ammount));
            }
        });

        cbFromStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedStation = cbFromStation.getSelectedItem().toString();
                String trainNo = tfTrainNo.getText();

                final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
                final String USERNAME = "root";
                final String PASSWORD = "root";

                try{
                    Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

                    Statement stmt = conn.createStatement();
                    String sql = "SELECT Time FROM " + trainNo + " WHERE Stations = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, selectedStation);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String time = resultSet.getString("Time");
                        lbFromTime.setText(time);
                    }else {
                        lbFromTime.setText("Time not found");
                    }

                    stmt.close();
                    conn.close();

                }catch(Exception b) {
                    b.printStackTrace();
                }

            }
        });

        cbToStation.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String selectedStation = cbToStation.getSelectedItem().toString();
                String trainNo = tfTrainNo.getText();

                final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
                final String USERNAME = "root";
                final String PASSWORD = "root";

                try{
                    Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

                    Statement stmt = conn.createStatement();
                    String sql = "SELECT Time FROM " + trainNo + " WHERE Stations = ?";
                    PreparedStatement preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, selectedStation);

                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String time = resultSet.getString("Time");
                        lbToTime.setText(time);
                    }else {
                        lbToTime.setText("Time not Found");
                    }

                    stmt.close();
                    conn.close();

                }catch(Exception b) {
                    b.printStackTrace();
                }
            }
        });
        setVisible(true);
    }


    private void getNameandStation() {
        String trainNo = tfTrainNo.getText();

        final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
        final String USERNAME = "root";
        final String PASSWORD = "root";

        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT Train_Name FROM Train WHERE Train_No = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, trainNo);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String trainName = resultSet.getString("Train_Name");
                tfTrainName.setText(trainName);
            }else {
                JOptionPane.showMessageDialog(Reservation.this,
                        "Cant find the train",
                        "Wrong Train No.",
                        JOptionPane.ERROR_MESSAGE);
            }

            sql = "SELECT Stations FROM " + trainNo;
            preparedStatement = conn.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();
            ArrayList<String> stationNames = new ArrayList<>();

            while (resultSet.next()) {
                String stationName = resultSet.getString("Stations");
                stationNames.add(stationName);
            }

            // Add the data from the list to the JComboBox
            for (String stationName : stationNames) {
                cbFromStation.addItem(stationName);
                cbToStation.addItem(stationName);
            }
            int lastIndex = cbToStation.getItemCount() - 1;
            if (lastIndex >= 0) {
                cbToStation.setSelectedIndex(lastIndex);
            }

            stmt.close();
            conn.close();

        }catch(Exception b) {
            b.printStackTrace();
        }
    }

    private void reservation() {
        String name = tfName.getText();
        String mobileNo = tfMobileNo.getText();
        String trainNo = tfTrainNo.getText();
        String trainName = tfTrainName.getText();
        String classType = cbClassType.getSelectedItem().toString();
        String dateOfJourney = ftfDate.getText();
        String noOfPassengers = tfNoOfPassengers.getText();
        String fromStation = cbFromStation.getSelectedItem().toString();
        String toStation = cbToStation.getSelectedItem().toString();
        String ticketFare = tfAmmount.getText();

        String characters = "0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder prnBuilder = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            prnBuilder.append(randomChar);
        }
        String prn = prnBuilder.toString();

        book = addTicketToDatabase(prn, name, mobileNo, trainNo, trainName, classType, dateOfJourney, noOfPassengers, fromStation, toStation, ticketFare);
        if (book != null) {
            String prnmsg = "Note Your PRN No. for Future use: " + prn;
            JOptionPane.showMessageDialog(this,
                    prnmsg,
                    "Ticked Booked!...",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();

        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to book the ticket",
                    "Try again",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    public Book book;

    private Book addTicketToDatabase(String prn, String name, String mobileNo, String trainNo, String trainName, String classType, String dateOfJourney,
                                     String noOfPassengers, String fromStation, String toStation, String ticketFare) {
        Book book = null;
        final String DB_URL = "jdbc:mysql://localhost:3306/RailwayReservation";
        final String USERNAME = "root";
        final String PASSWORD = "root";

        try{
            Connection conn = DriverManager.getConnection (DB_URL, USERNAME, PASSWORD);
// Connected to database successfully...

            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO Reservation (PRN, Name, MobileNo, TrainNo, TrainName, ClassType, DateOfJourney, NoOfPassengers, FromStation, ToStation, Ammount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, prn);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, mobileNo);
            preparedStatement.setString(4, trainNo);
            preparedStatement.setString(5, trainName);
            preparedStatement.setString(6, classType);
            preparedStatement.setString(7, dateOfJourney);
            preparedStatement.setString(8, noOfPassengers);
            preparedStatement.setString(9, fromStation);
            preparedStatement.setString(10, toStation);
            preparedStatement.setString(11, ticketFare);

            //Insert row into the table
            int addedRows = preparedStatement.executeUpdate();
            if (addedRows > 0) {
                book = new Book();
                book.prn = prn;
                book.name = name;
                book.mobileNo = mobileNo;
                book.trainNo = trainNo;
                book.trainName = trainName;
                book.classType = classType;
                book.dateOfJourney = dateOfJourney;
                book.noOfPassengers = noOfPassengers;
                book.fromStation = fromStation;
                book.toStation = toStation;
                book.ticketFare = ticketFare;
            }

        }catch(Exception e) {
            e.printStackTrace();
        }
        return book;
    }

    public static void main(String[] args) {
        Reservation myForm = new Reservation(null);
        Book book = myForm.book;
    }
}
