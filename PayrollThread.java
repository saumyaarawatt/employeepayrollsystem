package payroll.service;

import payroll.model.Employee;

public class PayrollThread extends Thread {

    private final Employee emp;
    private final PayrollService service = new PayrollService();

    public PayrollThread(Employee emp) {
        this.emp = emp;
    }

    @Override
    public void run() {
        System.out.println("Processing salary for: " + emp.getName());
        System.out.println(service.generatePaySlip(emp));
    }
}
