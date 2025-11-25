package payroll.model;

import payroll.exception.InvalidSalaryException;

public class PartTimeEmployee extends Employee {
    private double hoursWorked;
    private double hourlyRate;

    public PartTimeEmployee(int id, String name, String username, String password,
                            double hoursWorked, double hourlyRate,
                            int daysPresent, int workingDays, String department, double bonus)
            throws InvalidSalaryException {
        super(id, name, username, password, hoursWorked * hourlyRate, 0, 0,
                daysPresent, workingDays, department, bonus);
        this.hoursWorked = hoursWorked;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public double getAllowanceExtra() {
        return 0;
    }

    @Override
    public double getDeductionExtra() {
        return 0;
    }
}
