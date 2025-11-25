package payroll.ui;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;
import payroll.service.PayrollService;
import payroll.service.BonusProcessor;
import payroll.util.TransactionLogger;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminPanel extends JPanel {
    private final MainWindow main;
    private final EmployeeDAO dao;
    private final PayrollService payroll;
    private BonusProcessor bonusProcessor;

    private final CardLayout cards = new CardLayout();
    private final JPanel center = new JPanel(cards);
    private final DefaultTableModel tableModel = new DefaultTableModel();
    private final JTable table = new JTable(tableModel);

    public AdminPanel(MainWindow main, EmployeeDAO dao, PayrollService payroll) {
        this.main = main;
        this.dao = dao;
        this.payroll = payroll;
        setLayout(new BorderLayout());
        init();
    }

    private void init() {
        JPanel left = new JPanel(new GridLayout(0,1,6,6));
        left.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        JButton listBtn = new JButton("List Employees");
        JButton addBtn = new JButton("Add Employee");
        JButton payslipBtn = new JButton("Payslip");
        JButton historyBtn = new JButton("Salary History");
        JButton startBonus = new JButton("Start Bonus Processor");
        JButton stopBonus = new JButton("Stop Bonus Processor");
        left.add(listBtn); left.add(addBtn); left.add(payslipBtn); left.add(historyBtn); left.add(startBonus); left.add(stopBonus);
        add(left, BorderLayout.WEST);

        // center cards
        center.add(new JScrollPane(table), "list");
        center.add(new AddEmployeePanel(this, dao), "add");
        center.add(new PayslipPanel(dao, payroll), "payslip");
        center.add(new SalaryHistoryPanel(dao), "history");
        add(center, BorderLayout.CENTER);

        // table model
        tableModel.setColumnIdentifiers(new Object[]{"ID","Name","Dept","Basic","Role"});
        table.setModel(tableModel);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // actions
        listBtn.addActionListener(e -> { refresh(); cards.show(center, "list"); });
        addBtn.addActionListener(e -> cards.show(center, "add"));
        payslipBtn.addActionListener(e -> cards.show(center, "payslip"));
        historyBtn.addActionListener(e -> cards.show(center, "history"));

        startBonus.addActionListener(e -> startBonusProcessor());
        stopBonus.addActionListener(e -> stopBonusProcessor());

        table.addMouseListener(new java.awt.event.MouseAdapter(){
            public void mouseClicked(java.awt.event.MouseEvent evt){
                if (evt.getClickCount() == 2) {
                    int row = table.getSelectedRow();
                    if (row >= 0) {
                        int id = Integer.parseInt(tableModel.getValueAt(row,0).toString());
                        EditEmployeeDialog dlg = new EditEmployeeDialog(SwingUtilities.getWindowAncestor(AdminPanel.this), dao, id);
                        dlg.setLocationRelativeTo(AdminPanel.this);
                        dlg.setVisible(true);
                        refresh();
                    }
                }
            }
        });

        refresh();
    }

    public void refresh() {
        List<Employee> list = dao.getAllEmployees();
        tableModel.setRowCount(0);
        for (Employee e : list) tableModel.addRow(new Object[]{e.getId(), e.getName(), e.getDepartment(), e.getBasicSalary(), e.getRole()});
    }

    private void startBonusProcessor() {
        if (bonusProcessor == null) {
            bonusProcessor = new BonusProcessor(dao);
            bonusProcessor.start();
            TransactionLogger.log("Started bonus processor (UI)");
            JOptionPane.showMessageDialog(this, "Bonus processor started.");
        }
    }

    private void stopBonusProcessor() {
        if (bonusProcessor != null) {
            bonusProcessor.shutdown();
            bonusProcessor = null;
            TransactionLogger.log("Stopped bonus processor (UI)");
            JOptionPane.showMessageDialog(this, "Bonus processor stopped.");
        }
    }
}
