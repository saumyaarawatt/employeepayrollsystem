package payroll.service;

import payroll.model.Employee;

public interface IPayroll {
    double calculateTax(Employee e);
    double calculateNetSalary(Employee e);
    String generatePaySlip(Employee e);
}
