import java.sql.*;
import  java .util.*;

public class Main {
    public static void main(String[] args) {

        System.out.println("Welcome to World Bank!");
        System.out.print("Enter your User ID: ");
        UID = sc.next();
        UPin = "";
        balance = "";
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM userAccount WHERE userId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, UID);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String useId = resultSet.getString("userId");
                UPin = resultSet.getString("userPin");
                balance = resultSet.getString("balance");
                System.out.print("Enter your User Pin: ");
                String cpass = sc.next();
                if (Objects.equals(cpass, UPin)){
                    atmfunctionalities();
                }else {
                    System.out.println("Wrong password. Please try again...");
                }
            }else {
                System.out.println("Wrong User ID. Please try again...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    static Scanner sc = new Scanner(System.in);
    static final String DB_URL = "jdbc:mysql://localhost:3306/atminterface";
    static final String USERNAME = "root";
    static final String PASSWORD = "root";
    static String UID = "";
    static String UPin = "";
    static String balance = "";
    private static void atmfunctionalities() {
        System.out.println("Login successful...");
        while (true){
            System.out.println("1. Transactions History.\n2. Withdraw.\n3. Deposit.\n4. Transfer.\n5. Quit.");
            System.out.print("Enter your choice: ");
            int choice = sc.nextInt();

            switch (choice){
                case 1:
                    transactionHistory();
                    break;
                case 2:
                    withdraw();
                    break;
                case 3:
                    deposit();
                    break;
                case 4:
                    transfer();
                    break;
                case 5:
                    System.out.println("See you soon! Exiting the Atm Interface...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice. Please try again...");
            }

        }
    }

    private static void transactionHistory() {
        try{
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
            Statement stmt = conn.createStatement();

            String sql = "SELECT type, amount, balance FROM transactionhistory WHERE userId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, UID);
            ResultSet resultSet = preparedStatement.executeQuery();
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++){
                System.out.print(padRight(metaData.getColumnName(i), 40));
            }
            System.out.println();
            for (int i = 1; i <= columnCount; i++){
                System.out.print("".equals(metaData.getColumnName(i)) ? "+" : "-");
                for (int j = 1; j <= 40; j++){
                    System.out.print("-");
                }
            }
            System.out.println();

            while(resultSet.next()){
                for (int i = 1; i <= columnCount; i++){
                    System.out.print(padRight(resultSet.getString(i), 40));
                }
                System.out.println();
                for (int i = 1; i <= columnCount; i++){
                    System.out.print("".equals(metaData.getColumnName(i)) ? "+" : "-");
                    for (int j = 1; j <= 40; j++){
                        System.out.print("-");
                    }
                }
                System.out.println();
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static String padRight(String s, int length){
        return String.format("%-" + length + "s", s);
    }

    private static void transfer() {
        System.out.print("Enter User ID of beneficiary: ");
        String beneficiary = sc.next();
        int availBal = 0;

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM userAccount WHERE userId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, beneficiary);

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                String useId = resultSet.getString("userId");
                String benefBalance = resultSet.getString("balance");
                System.out.print("Enter amount to transfer: ");
                int transfer = sc.nextInt();
                int ownBal = Integer.parseInt(balance);
                if (transfer < ownBal){
                    availBal = ownBal - transfer;
                    balance = String.valueOf(availBal);
                    int benBalance = Integer.parseInt(benefBalance);
                    int afterTrans = benBalance + transfer;
                    sql = "UPDATE userAccount SET balance = ? WHERE userId = ?";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, String.valueOf(afterTrans));
                    preparedStatement.setString(2, beneficiary);
                    preparedStatement.executeUpdate();
                    sql = "UPDATE userAccount SET balance = ? WHERE userId = ?";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, balance);
                    preparedStatement.setString(2, UID);
                    preparedStatement.executeUpdate();
                    String msgTo = "Transfered to " + beneficiary;
                    sql = "INSERT INTO transactionhistory VALUES (?, ?, ?, ?)";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, UID);
                    preparedStatement.setString(2, msgTo);
                    preparedStatement.setString(3, String.valueOf(transfer));
                    preparedStatement.setString(4, balance);
                    preparedStatement.executeUpdate();
                    String msgFrom = "Transfered from " + UID;
                    sql = "INSERT INTO transactionhistory VALUES (?, ?, ?, ?)";
                    preparedStatement = conn.prepareStatement(sql);
                    preparedStatement.setString(1, beneficiary);
                    preparedStatement.setString(2, msgFrom);
                    preparedStatement.setString(3, String.valueOf(transfer));
                    preparedStatement.setString(4, String.valueOf(afterTrans));
                    preparedStatement.executeUpdate();
                    System.out.println("Your Available Balance is: " + availBal);
                }

            }else {
                System.out.println("Wrong User ID. Please try again...");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void deposit() {
        System.out.print("Enter amount to deposit: ");
        int dep = sc.nextInt();
        int Bal = Integer.parseInt(balance);
        int availBal = dep + Bal;

        try {

            Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

            Statement stmt = conn.createStatement();

            String sql = "UPDATE userAccount SET balance = ? WHERE userId = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, String.valueOf(availBal));
            preparedStatement.setString(2, UID);
            preparedStatement.executeUpdate();
            System.out.println("Your Available Balance is: " + availBal);
            balance = String.valueOf(availBal);
            sql = "INSERT INTO transactionhistory VALUES (?, ?, ?, ?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, UID);
            preparedStatement.setString(2, "Deposited");
            preparedStatement.setString(3, String.valueOf(dep));
            preparedStatement.setString(4, balance);
            preparedStatement.executeUpdate();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static void withdraw() {
        System.out.print("Enter amount to withdraw: ");
        int with = sc.nextInt();
        int Bal = Integer.parseInt(balance);
        int availBal = 0;
        if (with < Bal){
            availBal = Bal - with;
        }else {
            System.out.println("Insufficient Balance");
        }
        try {
            if (availBal != 0){
                Connection conn = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);

                Statement stmt = conn.createStatement();

                String sql = "UPDATE userAccount SET balance = ? WHERE userId = ?";
                PreparedStatement preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, String.valueOf(availBal));
                preparedStatement.setString(2, UID);
                preparedStatement.executeUpdate();
                System.out.println("Your Available Balance is: " + availBal);
                balance = String.valueOf(availBal);
                sql = "INSERT INTO transactionhistory VALUES (?, ?, ?, ?)";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, UID);
                preparedStatement.setString(2, "Withdrawed");
                preparedStatement.setString(3, String.valueOf(with));
                preparedStatement.setString(4, balance);
                preparedStatement.executeUpdate();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}