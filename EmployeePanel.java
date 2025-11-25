package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;
import payroll.service.PayrollService;

import javax.swing.*;
import java.awt.*;

/**
 * Read-only employee view showing details and payslip.
 */
public class EmployeePanel extends JPanel {
    private final MainWindow main;
    private final EmployeeDAO dao;
    private final PayrollService payroll;
    private final JTextArea out = new JTextArea();

    public EmployeePanel(MainWindow main, EmployeeDAO dao, PayrollService payroll) {
        this.main = main; this.dao = dao; this.payroll = payroll;
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel(UIResources.loadLogo(120,30)));
        add(top, BorderLayout.NORTH);

        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        out.setEditable(false);
        add(new JScrollPane(out), BorderLayout.CENTER);
    }

    public void loadEmployee(int id) {
        Employee e = dao.getEmployee(id);
        if (e == null) {
            out.setText("Employee not found");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(e.getId()).append("\n");
        sb.append("Name: ").append(e.getName()).append("\n");
        sb.append("Department: ").append(e.getDepartment()).append("\n");
        sb.append("Basic: ").append(e.getBasicSalary()).append("\n");
        sb.append("Allowance: ").append(e.getAllowance()).append("\n");
        sb.append("Deduction: ").append(e.getDeduction()).append("\n");
        sb.append("Days: ").append(e.getDaysPresent()).append("/").append(e.getWorkingDays()).append("\n\n");
        sb.append(payroll.generatePaySlip(e));
        out.setText(sb.toString());
    }
}
