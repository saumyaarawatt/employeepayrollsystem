package payroll.service;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;
import payroll.util.TransactionLogger;

import java.util.List;

public class BonusProcessor extends Thread {

    private final EmployeeDAO dao;
    private final PayrollService payrollService;
    private volatile boolean running = true;

    public BonusProcessor(EmployeeDAO dao) {
        this.dao = dao;
        this.payrollService = new PayrollService();
        setName("BonusProcessor-Thread");
    }

    @Override
    public void run() {
        while (running) {
            try {
                List<Employee> employees = dao.getAllEmployees();
                for (Employee e : employees) {
                    // simple rule: if attendance > 90% give 5% bonus of basic
                    double attendPct = (double) e.getDaysPresent() / e.getWorkingDays();
                    double bonus = e.getBonus();
                    if (attendPct >= 0.9) {
                        bonus += e.getBasicSalary() * 0.05;
                    }
                    // example overtime rule: if department == "IT" give a small incentive
                    if ("IT".equalsIgnoreCase(e.getDepartment())) {
                        bonus += 1000; // fixed incentive
                    }
                    e.setBonus(bonus);
                    dao.updateEmployee(e);
                    TransactionLogger.log("Applied bonus to employee id=" + e.getId() + " newBonus=" + bonus);
                }
                Thread.sleep(30_000); // wait 30s between runs (adjust as needed)
            } catch (InterruptedException ex) {
                running = false;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void shutdown() {
        running = false;
        interrupt();
    }
}
