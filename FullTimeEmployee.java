package payroll.model;

import payroll.exception.InvalidSalaryException;

public class FullTimeEmployee extends Employee {
    public FullTimeEmployee(int id, String name, String username, String password,
                            double basicSalary, double allowance, double deduction,
                            int daysPresent, int workingDays, String department, double bonus)
            throws InvalidSalaryException {
        super(id, name, username, password, basicSalary, allowance, deduction,
                daysPresent, workingDays, department, bonus);
    }

    @Override
    public double getAllowanceExtra() {
        return allowance;
    }

    @Override
    public double getDeductionExtra() {
        return deduction;
    }
}
