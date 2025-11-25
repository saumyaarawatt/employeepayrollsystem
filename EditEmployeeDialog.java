package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;

import javax.swing.*;
import java.awt.*;

/**
 * Simple dialog to edit basic fields of an employee.
 */
public class EditEmployeeDialog extends JDialog {
    public EditEmployeeDialog(Window owner, EmployeeDAO dao, int empId) {
        super(owner, "Edit Employee", ModalityType.APPLICATION_MODAL);
        Employee e = dao.getEmployee(empId);
        if (e == null) { JOptionPane.showMessageDialog(this, "Employee not found"); dispose(); return; }

        setLayout(new GridLayout(0,2,6,6));
        JTextField name = new JTextField(e.getName());
        JTextField basic = new JTextField(String.valueOf(e.getBasicSalary()));
        JTextField allowance = new JTextField(String.valueOf(e.getAllowance()));
        JTextField deduction = new JTextField(String.valueOf(e.getDeduction()));
        JTextField role = new JTextField(e.getRole());

        add(new JLabel("Name:")); add(name);
        add(new JLabel("Basic:")); add(basic);
        add(new JLabel("Allowance:")); add(allowance);
        add(new JLabel("Deduction:")); add(deduction);
        add(new JLabel("Role:")); add(role);

        JButton save = new JButton("Save");
        save.addActionListener(ev -> {
            e.setName(name.getText().trim());
            e.setBasicSalary(Double.parseDouble(basic.getText().trim()));
            e.setAllowance(Double.parseDouble(allowance.getText().trim()));
            e.setDeduction(Double.parseDouble(deduction.getText().trim()));
            e.setRole(role.getText().trim().isEmpty() ? "EMPLOYEE" : role.getText().trim());
            boolean ok = dao.updateEmployee(e);
            JOptionPane.showMessageDialog(this, ok ? "Saved" : "Save failed");
            if (ok) dispose();
        });

        add(new JLabel()); add(save);
        pack();
    }
}
