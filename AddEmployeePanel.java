package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.exception.InvalidSalaryException;
import payroll.model.FullTimeEmployee;
import payroll.util.TransactionLogger;

import javax.swing.*;
import java.awt.*;

/**
 * Form for adding employee from Admin UI.
 */
public class AddEmployeePanel extends JPanel {
    private final AdminPanel parent;
    private final EmployeeDAO dao;

    public AddEmployeePanel(AdminPanel parent, EmployeeDAO dao) {
        this.parent = parent; this.dao = dao;
        init();
    }

    private void init() {
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        JTextField id = new JTextField(8);
        JTextField name = new JTextField(18);
        JTextField user = new JTextField(12);
        JTextField pass = new JTextField(12);
        JTextField basic = new JTextField(8);
        JTextField allowance = new JTextField(8);
        JTextField deduction = new JTextField(8);
        JTextField days = new JTextField(4);
        JTextField work = new JTextField(4);
        JTextField dept = new JTextField(12);
        JTextField role = new JTextField(10);

        int y=0;
        c.gridx=0; c.gridy=y; add(new JLabel("ID:"), c); c.gridx=1; add(id, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Name:"), c); c.gridx=1; add(name, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Username:"), c); c.gridx=1; add(user, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Password:"), c); c.gridx=1; add(pass, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Basic:"), c); c.gridx=1; add(basic, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Allowance:"), c); c.gridx=1; add(allowance, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Deduction:"), c); c.gridx=1; add(deduction, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Days Present:"), c); c.gridx=1; add(days, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Working Days:"), c); c.gridx=1; add(work, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Department:"), c); c.gridx=1; add(dept, c); y++;
        c.gridx=0; c.gridy=y; add(new JLabel("Role:"), c); c.gridx=1; add(role, c); y++;

        JButton addBtn = new JButton("Add Employee");
        c.gridx=0; c.gridy=y; c.gridwidth=2; add(addBtn, c);

        addBtn.addActionListener(ev -> {
            try {
                FullTimeEmployee emp = new FullTimeEmployee(
                        Integer.parseInt(id.getText().trim()),
                        name.getText().trim(),
                        user.getText().trim(),
                        pass.getText().trim(),
                        Double.parseDouble(basic.getText().trim()),
                        Double.parseDouble(allowance.getText().trim()),
                        Double.parseDouble(deduction.getText().trim()),
                        Integer.parseInt(days.getText().trim()),
                        Integer.parseInt(work.getText().trim()),
                        dept.getText().trim(),
                        0
                );
                emp.setRole(role.getText().trim().isEmpty() ? "EMPLOYEE" : role.getText().trim());
                boolean ok = dao.insertEmployee(emp);
                if (ok) {
                    JOptionPane.showMessageDialog(this, "Employee added");
                    TransactionLogger.log("UI: added employee id="+emp.getId());
                    parent.refresh();
                } else JOptionPane.showMessageDialog(this, "Insert failed");
            } catch (InvalidSalaryException ex) {
                JOptionPane.showMessageDialog(this, "Invalid salary: " + ex.getMessage());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });
    }
}
