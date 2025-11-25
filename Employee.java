package payroll.model;

import payroll.exception.InvalidSalaryException;
public abstract class  Employee {

    protected int id;
    protected String name;
    protected String username;
    protected String password;

    protected double basicSalary;
    protected double allowance;
    protected double deduction;

    protected int daysPresent;
    protected int workingDays;

    protected double overtimeHours;
    protected double overtimeRate;

    protected int casualLeave;
    protected int sickLeave;
    protected int unpaidLeave;

    protected String department;
    protected double bonus;

    protected boolean salaryLocked;

    // IMPORTANT: your role field
    protected String role = "EMPLOYEE";   // default role

    // ====================== CONSTRUCTOR =======================
    public Employee(int id, String name, String username, String password,
                    double basicSalary, double allowance, double deduction,
                    int daysPresent, int workingDays,
                    String department, double bonus)
            throws InvalidSalaryException {

        if (basicSalary < 0)
            throw new InvalidSalaryException("Basic salary cannot be negative");

        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.basicSalary = basicSalary;
        this.allowance = allowance;
        this.deduction = deduction;
        this.daysPresent = daysPresent;
        this.workingDays = (workingDays == 0 ? 1 : workingDays);
        this.department = department;
        this.bonus = bonus;

        this.overtimeHours = 0;
        this.overtimeRate = 0;

        this.casualLeave = 0;
        this.sickLeave = 0;
        this.unpaidLeave = 0;

        this.salaryLocked = false;
        this.role = "EMPLOYEE";
    }

    // ========================= GETTERS ==========================
    public int getId() { return id; }
    public String getName() { return name; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }

    public double getBasicSalary() { return basicSalary; }
    public double getAllowance() { return allowance; }
    public double getDeduction() { return deduction; }

    public int getDaysPresent() { return daysPresent; }
    public int getWorkingDays() { return workingDays; }

    public double getOvertimeHours() { return overtimeHours; }
    public double getOvertimeRate() { return overtimeRate; }

    public int getCasualLeave() { return casualLeave; }
    public int getSickLeave() { return sickLeave; }
    public int getUnpaidLeave() { return unpaidLeave; }

    public String getDepartment() { return department; }
    public double getBonus() { return bonus; }

    public boolean isSalaryLocked() { return salaryLocked; }

    // IMPORTANT: Role getter
    public String getRole() { return role; }

    // ========================= SETTERS ==========================
    public void setName(String name) { this.name = name; }
    public void setUsername(String username) { this.username = username; }
    public void setPassword(String password) { this.password = password; }

    public void setBasicSalary(double basicSalary) { this.basicSalary = basicSalary; }
    public void setAllowance(double allowance) { this.allowance = allowance; }
    public void setDeduction(double deduction) { this.deduction = deduction; }

    public void setDaysPresent(int daysPresent) { this.daysPresent = daysPresent; }
    public void setWorkingDays(int workingDays) { this.workingDays = (workingDays == 0 ? 1 : workingDays); }

    public void setOvertimeHours(double overtimeHours) { this.overtimeHours = overtimeHours; }
    public void setOvertimeRate(double overtimeRate) { this.overtimeRate = overtimeRate; }

    public void setCasualLeave(int casualLeave) { this.casualLeave = casualLeave; }
    public void setSickLeave(int sickLeave) { this.sickLeave = sickLeave; }
    public void setUnpaidLeave(int unpaidLeave) { this.unpaidLeave = unpaidLeave; }

    public void setDepartment(String department) { this.department = department; }
    public void setBonus(double bonus) { this.bonus = bonus; }

    public void setSalaryLocked(boolean salaryLocked) { this.salaryLocked = salaryLocked; }

    // IMPORTANT: role setter
    public void setRole(String role) { this.role = role; }

    // ======================= HELPERS ========================
    public double getAttendanceAdjustedSalary() {
        return basicSalary * ((double) daysPresent / workingDays);
    }

    public double getOvertimePay() {
        return overtimeHours * overtimeRate;
    }

    public double getUnpaidLeaveDeduction() {
        if (unpaidLeave <= 0) return 0;
        return unpaidLeave * (basicSalary / workingDays);
    }

    // abstract methods
    public abstract double getAllowanceExtra();
    public abstract double getDeductionExtra();

    @Override
    public String toString() {
        return id + " | " + name + " | " + department + " | Role: " + role + " | Basic: " + basicSalary;
    }
}
