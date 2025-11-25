package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;
import payroll.service.PayrollService;

import javax.swing.*;
import java.awt.*;

/**
 * Small panel to enter employee id and generate payslip.
 */
public class PayslipPanel extends JPanel {
    private final EmployeeDAO dao;
    private final PayrollService payroll;
    private final JTextField idField = new JTextField(6);
    private final JTextArea out = new JTextArea(20,60);

    public PayslipPanel(EmployeeDAO dao, PayrollService payroll) {
        this.dao = dao; this.payroll = payroll;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.add(new JLabel("Employee ID:"));
        top.add(idField);
        JButton gen = new JButton("Generate Payslip");
        top.add(gen);
        add(top, BorderLayout.NORTH);

        out.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        out.setEditable(false);
        add(new JScrollPane(out), BorderLayout.CENTER);

        gen.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Employee emp = dao.getEmployee(id);
                if (emp == null) { JOptionPane.showMessageDialog(this, "Not found"); return; }
                out.setText(payroll.generatePaySlip(emp));
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Enter valid id"); }
        });
    }
}
