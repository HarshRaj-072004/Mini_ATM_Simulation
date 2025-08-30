import java.sql.*;
import java.util.Scanner;

class Account {
    private String accountNumber;
    private String pin;
    private double balance;

    public Account(String accountNumber, String pin, double balance) {
        this.accountNumber = accountNumber;
        this.pin = pin;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public double getBalance() {
        return balance;
    }
}

class ATM {
    private Connection conn;
    private Scanner scanner;

    public ATM() {
        scanner = new Scanner(System.in);
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm_simulation", "root", "your_password");
        } catch (SQLException e) {
            System.out.println("Database Connection Failed: " + e.getMessage());
            System.exit(1);
        }
    }

    public void start() {
        System.out.println("===== Welcome to Mini ATM =====");
        System.out.println("1. User Login");
        System.out.println("2. Admin Login");
        System.out.print("Choose: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        if (choice == 1) {
            userLogin();
        } else if (choice == 2) {
            adminLogin();
        } else {
            System.out.println("Invalid choice!");
        }
    }

    private void userLogin() {
        try {
            System.out.print("Enter Account Number: ");
            String accNum = scanner.nextLine();

            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();

            PreparedStatement ps = conn.prepareStatement("SELECT * FROM accounts WHERE account_number=? AND pin=?");
            ps.setString(1, accNum);
            ps.setString(2, pin);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Account account = new Account(rs.getString("account_number"), rs.getString("pin"), rs.getDouble("balance"));
                System.out.println("Login Successful!");
                showMenu(account);
            } else {
                System.out.println("Invalid Account Number or PIN!");
            }
        } catch (SQLException e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private void showMenu(Account account) {
        while (true) {
            System.out.println("\n===== ATM Menu =====");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit Money");
            System.out.println("3. Withdraw Money");
            System.out.println("4. Transaction History");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> checkBalance(account);
                case 2 -> deposit(account);
                case 3 -> withdraw(account);
                case 4 -> showTransactions(account);
                case 5 -> {
                    System.out.println("Thank you for using ATM!");
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private void checkBalance(Account account) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number=?");
            ps.setString(1, account.getAccountNumber());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("Your Balance: " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error checking balance: " + e.getMessage());
        }
    }

    private void deposit(Account account) {
        try {
            System.out.print("Enter amount to deposit: ");
            double amount = scanner.nextDouble();

            PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance=balance+? WHERE account_number=?");
            ps.setDouble(1, amount);
            ps.setString(2, account.getAccountNumber());
            ps.executeUpdate();

            logTransaction(account.getAccountNumber(), "Deposit", amount);
            System.out.println("Deposited Successfully.");
        } catch (SQLException e) {
            System.out.println("Error depositing: " + e.getMessage());
        }
    }

    private void withdraw(Account account) {
        try {
            System.out.print("Enter amount to withdraw: ");
            double amount = scanner.nextDouble();

            PreparedStatement checkBalance = conn.prepareStatement("SELECT balance FROM accounts WHERE account_number=?");
            checkBalance.setString(1, account.getAccountNumber());
            ResultSet rs = checkBalance.executeQuery();
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                if (amount <= balance) {
                    PreparedStatement ps = conn.prepareStatement("UPDATE accounts SET balance=balance-? WHERE account_number=?");
                    ps.setDouble(1, amount);
                    ps.setString(2, account.getAccountNumber());
                    ps.executeUpdate();

                    logTransaction(account.getAccountNumber(), "Withdraw", amount);
                    System.out.println("Withdrawal Successful.");
                } else {
                    System.out.println("Insufficient Balance!");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error withdrawing: " + e.getMessage());
        }
    }

    private void showTransactions(Account account) {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM transactions WHERE account_number=? ORDER BY timestamp DESC");
            ps.setString(1, account.getAccountNumber());
            ResultSet rs = ps.executeQuery();
            System.out.println("\nTransaction History:");
            while (rs.next()) {
                System.out.println(rs.getString("type") + " - " + rs.getDouble("amount") + " at " + rs.getTimestamp("timestamp"));
            }
        } catch (SQLException e) {
            System.out.println("Error showing transactions: " + e.getMessage());
        }
    }

    private void logTransaction(String accountNumber, String type, double amount) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO transactions (account_number, type, amount) VALUES (?, ?, ?)");
        ps.setString(1, accountNumber);
        ps.setString(2, type);
        ps.setDouble(3, amount);
        ps.executeUpdate();
    }

    // ============ Admin Functions ============
    private void adminLogin() {
        System.out.print("Enter Admin Password: ");
        String password = scanner.nextLine();
        if (password.equals("admin123")) {
            System.out.println("Admin Login Successful!");
            adminMenu();
        } else {
            System.out.println("Wrong Password!");
        }
    }

    private void adminMenu() {
        while (true) {
            System.out.println("\n===== Admin Menu =====");
            System.out.println("1. Create Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> viewAllAccounts();
                case 3 -> {
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    private void createAccount() {
        try {
            System.out.print("Enter Account Number: ");
            String accNum = scanner.nextLine();
            System.out.print("Enter PIN: ");
            String pin = scanner.nextLine();
            System.out.print("Enter Initial Balance: ");
            double balance = scanner.nextDouble();

            PreparedStatement ps = conn.prepareStatement("INSERT INTO accounts VALUES (?, ?, ?)");
            ps.setString(1, accNum);
            ps.setString(2, pin);
            ps.setDouble(3, balance);
            ps.executeUpdate();

            System.out.println("Account Created Successfully!");
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    private void viewAllAccounts() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM accounts");
            System.out.println("\nAccounts List:");
            while (rs.next()) {
                System.out.println("AccNo: " + rs.getString("account_number") +
                        " | Balance: " + rs.getDouble("balance"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing accounts: " + e.getMessage());
        }
    }
}

public class MiniATM {
    public static void main(String[] args) {
        ATM atm = new ATM();
        atm.start();
    }
}
