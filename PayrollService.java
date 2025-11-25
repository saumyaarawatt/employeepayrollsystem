package payroll.service;

import payroll.model.Employee;
import payroll.service.IPayroll;
import java.text.DecimalFormat;

/**
 * Updated PayrollService with:
 * - Attendance percentage & grade
 * - Attendance bonus
 * - Overtime calculation
 * - Unpaid leave deduction
 * - Progressive tax
 */
public class PayrollService implements IPayroll {

    private static final DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Attendance grade based on percentage.
     */
    public String getAttendanceGrade(Employee e) {
        double pct = ((double) e.getDaysPresent() / e.getWorkingDays()) * 100;
        if (pct >= 95) return "A";
        if (pct >= 90) return "B";
        if (pct >= 80) return "C";
        return "D";
    }

    /**
     * Attendance bonus based on grade.
     */
    public double getAttendanceBonus(Employee e) {
        double pct = ((double) e.getDaysPresent() / e.getWorkingDays()) * 100;
        double basic = e.getBasicSalary();

        if (pct >= 95) return basic * 0.07;
        if (pct >= 90) return basic * 0.05;
        if (pct >= 80) return basic * 0.02;
        return 0.0;
    }

    /** Overtime Pay */
    public double getOvertimePay(Employee e) {
        return e.getOvertimePay();
    }

    /** Unpaid Leave Deduction */
    public double getUnpaidLeaveDeduction(Employee e) {
        return e.getUnpaidLeaveDeduction();
    }

    /** Tax calculation on gross income */
    @Override
    public double calculateTax(Employee e) {
        double gross = e.getAttendanceAdjustedSalary()
                + e.getAllowance()
                + e.getBonus()
                + getAttendanceBonus(e)
                + getOvertimePay(e);

        if (gross <= 20000) return gross * 0.05;
        if (gross <= 40000) return gross * 0.10;
        return gross * 0.15;
    }

    /** Net salary after all additions & deductions */
    @Override
    public double calculateNetSalary(Employee e) {
        double gross = e.getAttendanceAdjustedSalary()
                + e.getAllowance()
                + e.getBonus()
                + getAttendanceBonus(e)
                + getOvertimePay(e);

        double net = gross
                - e.getDeduction()
                - calculateTax(e)
                - getUnpaidLeaveDeduction(e);

        return Double.parseDouble(df.format(net));
    }

    /** Generate formatted payslip */
    @Override
    public String generatePaySlip(Employee e) {
        double basicAdj = e.getAttendanceAdjustedSalary();
        double overtime = getOvertimePay(e);
        double attendanceBonus = getAttendanceBonus(e);
        double unpaidDeduction = getUnpaidLeaveDeduction(e);
        double tax = calculateTax(e);
        double net = calculateNetSalary(e);
        String grade = getAttendanceGrade(e);

        return "\n========== PAYSLIP ==========" +
                "\nEmployee ID      : " + e.getId() +
                "\nName             : " + e.getName() +
                "\nDepartment       : " + e.getDepartment() +
                "\nBasic Salary     : " + df.format(e.getBasicSalary()) +
                "\nAttendance Pay   : " + df.format(basicAdj) + " (" + e.getDaysPresent() + "/" + e.getWorkingDays() + ")" +
                "\nAttendance Grade : " + grade +
                "\nAllowance        : " + df.format(e.getAllowance()) +
                "\nExisting Bonus   : " + df.format(e.getBonus()) +
                "\nAttendance Bonus : " + df.format(attendanceBonus) +
                "\nOvertime Pay     : " + df.format(overtime) +
                "\nUnpaid Deduction : " + df.format(unpaidDeduction) +
                "\nTax              : " + df.format(tax) +
                "\n--------------------------------" +
                "\nNET SALARY       : " + df.format(net) +
                "\n================================\n";
    }
}