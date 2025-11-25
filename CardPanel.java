package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.service.AuthService;
import payroll.service.PayrollService;

import javax.swing.*;
import java.awt.*;

/**
 * CardPanel - central card container for the application.
 */
public class CardPanel extends JPanel {
    private final CardLayout layout = new CardLayout();
    private final LoginPanel loginPanel;
    private final AdminPanel adminPanel;
    private final EmployeePanel employeePanel;

    public CardPanel(MainWindow owner, EmployeeDAO dao, PayrollService payroll, AuthService auth) {
        super(new CardLayout());
        setLayout(layout);

        loginPanel = new LoginPanel(owner, auth);
        adminPanel = new AdminPanel(owner, dao, payroll);
        employeePanel = new EmployeePanel(owner, dao, payroll);

        add(loginPanel, "login");
        add(adminPanel, "admin");
        add(employeePanel, "employee");
    }

    public void show(String name) {
        layout.show(this, name);
    }

    public void showEmployee(int empId) {
        employeePanel.loadEmployee(empId);
        layout.show(this, "employee");
    }
}
