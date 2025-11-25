package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.db.EmployeeDAO.SalaryHistoryRecord;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Panel to view salary history for an employee.
 */
public class SalaryHistoryPanel extends JPanel {
    private final EmployeeDAO dao;
    private final JTextField idField = new JTextField(6);
    private final JTextArea out = new JTextArea(20,60);

    public SalaryHistoryPanel(EmployeeDAO dao) {
        this.dao = dao;
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.add(new JLabel("Employee ID:"));
        top.add(idField);
        JButton load = new JButton("Load History");
        top.add(load);
        add(top, BorderLayout.NORTH);

        out.setEditable(false);
        add(new JScrollPane(out), BorderLayout.CENTER);

        load.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                List<SalaryHistoryRecord> list = dao.getSalaryHistory(id);
                StringBuilder sb = new StringBuilder();
                for (SalaryHistoryRecord r : list) {
                    sb.append(r.getChangedAt()).append(" | OLD=").append(r.getOldSalary())
                            .append(" NEW=").append(r.getNewSalary()).append(" BY=").append(r.getChangedBy())
                            .append("\nReason=").append(r.getReason()).append("\n\n");
                }
                out.setText(sb.toString());
            } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Invalid id"); }
        });
    }
}
