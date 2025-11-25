package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.service.AuthService;
import payroll.service.PayrollService;

import javax.swing.*;

public class MainWindow extends JFrame {
    private final CardPanel cards;

    public MainWindow() {
        super("Employee Payroll System");
        UIResources.setAppTheme();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        EmployeeDAO dao = new EmployeeDAO();
        PayrollService payroll = new PayrollService();
        AuthService auth = new AuthService();

        cards = new CardPanel(this, dao, payroll, auth);
        setContentPane(cards);
    }

    public void showLogin() {
        cards.show("login");
    }

    public void showAdmin() {
        cards.show("admin");
    }

    public void showEmployee(int empId) {
        cards.showEmployee(empId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainWindow w = new MainWindow();
            w.setVisible(true);
        });
    }
}
