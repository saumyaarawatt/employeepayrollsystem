package payroll.main;

import payroll.db.EmployeeDAO;
import payroll.db.EmployeeDAO.SalaryHistoryRecord;
import payroll.exception.InvalidSalaryException;
import payroll.model.Employee;
import payroll.model.FullTimeEmployee;
import payroll.service.AuthService;
import payroll.service.BonusProcessor;
import payroll.service.PayrollService;
import payroll.util.TransactionLogger;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final EmployeeDAO dao = new EmployeeDAO();
    private static final PayrollService payroll = new PayrollService();
    private static final AuthService auth = new AuthService();
    private static BonusProcessor bonusThread;

    public static void main(String[] args) {
        runLoginLoop();
    }

    private static void startBonusProcessor() {
        if (bonusThread == null) {
            bonusThread = new BonusProcessor(dao);
            bonusThread.start();
        }
    }

    private static void stopBonusProcessor() {
        if (bonusThread != null) {
            bonusThread.shutdown();
            bonusThread = null;
        }
    }

    private static void runLoginLoop() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n====== LOGIN ======");
            System.out.print("Username: ");
            String user = sc.nextLine();
            System.out.print("Password: ");
            String pass = sc.nextLine();

            Employee logged = auth.authenticate(user, pass);
            if (logged == null) {
                System.out.println("Invalid credentials. Try again.");
                continue;
            }

            System.out.println("Welcome, " + logged.getName() + " [" + logged.getRole() + "]");
            if ("ADMIN".equalsIgnoreCase(logged.getRole()) || "ADMINISTRATOR".equalsIgnoreCase(logged.getRole())) {
                // Start admin environment (bonus thread + admin menu)
                startBonusProcessor();
                runAdminMenu(sc, logged);
            } else {
                // Employee environment (limited)
                runEmployeeMenu(sc, logged);
            }
        }
    }

    // ============== ADMIN MENU (full access) ==============
    private static void runAdminMenu(Scanner sc, Employee admin) {
        while (true) {
            System.out.println("\n====== ADMIN MENU ======");
            System.out.println("1. Add Employee");
            System.out.println("2. Search Employee by ID");
            System.out.println("3. Update Employee");
            System.out.println("4. Delete Employee");
            System.out.println("5. List All Employees");
            System.out.println("6. Generate Payslip");
            System.out.println("7. Save Payslip to File");
            System.out.println("8. Mark Leave");
            System.out.println("9. Add Overtime");
            System.out.println("10. Freeze/Unfreeze Salary");
            System.out.println("11. View Salary History");
            System.out.println("12. Stop Bonus Processor");
            System.out.println("13. Logout");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine().trim());
            switch (choice) {
                case 1 -> addEmployee(sc);
                case 2 -> searchEmployee(sc);
                case 3 -> updateEmployee(sc);
                case 4 -> deleteEmployee(sc);
                case 5 -> listEmployees();
                case 6 -> generatePayslip(sc, false);
                case 7 -> generatePayslip(sc, true);
                case 8 -> markLeave(sc);
                case 9 -> addOvertime(sc);
                case 10 -> toggleFreeze(sc);
                case 11 -> viewSalaryHistory(sc);
                case 12 -> { stopBonusProcessor(); System.out.println("Bonus processor stopped."); }
                case 13 -> { System.out.println("Logging out..."); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ============== EMPLOYEE MENU (limited access) ==============
    private static void runEmployeeMenu(Scanner sc, Employee emp) {
        while (true) {
            System.out.println("\n====== EMPLOYEE MENU ======");
            System.out.println("1. View My Details");
            System.out.println("2. View My Payslip");
            System.out.println("3. Change Password");
            System.out.println("4. Logout");
            System.out.print("Enter choice: ");

            int choice = Integer.parseInt(sc.nextLine().trim());
            switch (choice) {
                case 1 -> showEmployeeDetails(emp);
                case 2 -> {
                    String slip = payroll.generatePaySlip(emp);
                    System.out.println(slip);
                }
                case 3 -> changePassword(sc, emp);
                case 4 -> { System.out.println("Logging out..."); return; }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // ============ Admin methods (reuse existing implementations) ============
    private static void addEmployee(Scanner sc) {
        try {
            System.out.print("Employee ID: ");
            int id = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Name: ");
            String name = sc.nextLine();

            System.out.print("Username: ");
            String username = sc.nextLine();

            System.out.print("Password: ");
            String password = sc.nextLine();

            System.out.print("Basic Salary: ");
            double basic = Double.parseDouble(sc.nextLine().trim());

            System.out.print("Allowance: ");
            double allowance = Double.parseDouble(sc.nextLine().trim());

            System.out.print("Deduction: ");
            double deduction = Double.parseDouble(sc.nextLine().trim());

            System.out.print("Days Present: ");
            int days = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Working Days: ");
            int working = Integer.parseInt(sc.nextLine().trim());

            System.out.print("Department: ");
            String dept = sc.nextLine();

            System.out.print("Role (ADMIN / EMPLOYEE): ");
            String role = sc.nextLine().trim();

            Employee e = new FullTimeEmployee(
                    id, name, username, password,
                    basic, allowance, deduction,
                    days, working, dept, 0
            );
            e.setRole(role);

            boolean ok = dao.insertEmployee(e);
            System.out.println(ok ? "Employee Added Successfully!" : "Failed to add employee!");
            TransactionLogger.log("Admin added employee ID=" + id);

        } catch (InvalidSalaryException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
    }

    private static void searchEmployee(Scanner sc) {
        System.out.print("Enter Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Employee e = dao.getEmployee(id);

        if (e == null) System.out.println("Employee not found!");
        else System.out.println("FOUND: " + e.getName() + " | Department: " + e.getDepartment() + " | Role: " + e.getRole());
    }

    private static void updateEmployee(Scanner sc) {
        System.out.print("Enter employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Employee e = dao.getEmployee(id);
        if (e == null) { System.out.println("Not found!"); return; }

        System.out.println("Leave blank to keep same value.");

        System.out.print("New Name (" + e.getName() + "): ");
        String name = sc.nextLine();
        if (!name.isEmpty()) e.setName(name);

        System.out.print("New Basic Salary (" + e.getBasicSalary() + "): ");
        String b = sc.nextLine();
        if (!b.isEmpty()) e.setBasicSalary(Double.parseDouble(b));

        System.out.print("New Allowance (" + e.getAllowance() + "): ");
        String a = sc.nextLine();
        if (!a.isEmpty()) e.setAllowance(Double.parseDouble(a));

        System.out.print("New Deduction (" + e.getDeduction() + "): ");
        String d = sc.nextLine();
        if (!d.isEmpty()) e.setDeduction(Double.parseDouble(d));

        System.out.print("New Role (" + e.getRole() + "): ");
        String role = sc.nextLine();
        if (!role.isEmpty()) e.setRole(role);

        boolean ok = dao.updateEmployee(e);
        System.out.println(ok ? "Updated successfully!" : "Update failed!");
    }

    private static void deleteEmployee(Scanner sc) {
        System.out.print("Enter ID to delete: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        boolean ok = dao.deleteEmployee(id);
        System.out.println(ok ? "Deleted!" : "Delete failed!");
    }

    private static void listEmployees() {
        List<Employee> list = dao.getAllEmployees();
        if (list.isEmpty()) { System.out.println("No employees."); return; }
        for (Employee e : list) System.out.println(e);
    }

    private static void generatePayslip(Scanner sc, boolean save) {
        System.out.print("Enter ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());

        Employee e = dao.getEmployee(id);
        if (e == null) { System.out.println("Not found!"); return; }

        String slip = payroll.generatePaySlip(e);
        System.out.println(slip);

        if (save) {
            try (FileWriter fw = new FileWriter("payslip_" + id + ".txt");
                 PrintWriter pw = new PrintWriter(fw)) {
                pw.print(slip);
                System.out.println("Saved payslip_" + id + ".txt");
            } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    private static void markLeave(Scanner sc) {
        System.out.print("Enter Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Employee e = dao.getEmployee(id);
        if (e == null) { System.out.println("Employee not found!"); return; }

        System.out.println("Leave Type: 1=Casual  2=Sick  3=Unpaid");
        int type = Integer.parseInt(sc.nextLine().trim());
        System.out.print("Number of days: ");
        int days = Integer.parseInt(sc.nextLine().trim());

        switch (type) {
            case 1 -> e.setCasualLeave(e.getCasualLeave() + days);
            case 2 -> e.setSickLeave(e.getSickLeave() + days);
            case 3 -> e.setUnpaidLeave(e.getUnpaidLeave() + days);
            default -> System.out.println("Invalid leave type!");
        }

        boolean ok = dao.updateEmployee(e);
        System.out.println(ok ? "Leave updated!" : "Failed to update leave.");
    }

    private static void addOvertime(Scanner sc) {
        System.out.print("Enter Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Employee e = dao.getEmployee(id);
        if (e == null) { System.out.println("Employee not found!"); return; }

        System.out.print("Enter overtime hours: ");
        double hours = Double.parseDouble(sc.nextLine().trim());
        System.out.print("Enter overtime rate per hour: ");
        double rate = Double.parseDouble(sc.nextLine().trim());

        e.setOvertimeHours(e.getOvertimeHours() + hours);
        e.setOvertimeRate(rate);

        boolean ok = dao.updateEmployee(e);
        System.out.println(ok ? "Updated overtime!" : "Failed to update.");
    }

    private static void toggleFreeze(Scanner sc) {
        System.out.print("Enter Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        Employee e = dao.getEmployee(id);
        if (e == null) { System.out.println("Employee not found!"); return; }

        boolean newState = !e.isSalaryLocked();
        e.setSalaryLocked(newState);
        boolean ok = dao.updateEmployee(e);
        System.out.println(ok ? (newState ? "Salary Locked!" : "Salary Unlocked!") : "Failed to update lock status.");
    }

    private static void viewSalaryHistory(Scanner sc) {
        System.out.print("Enter Employee ID: ");
        int id = Integer.parseInt(sc.nextLine().trim());
        List<SalaryHistoryRecord> list = dao.getSalaryHistory(id);
        if (list.isEmpty()) { System.out.println("No salary history found."); return; }
        for (SalaryHistoryRecord r : list) {
            System.out.println(r.getChangedAt() + " | Old: " + r.getOldSalary() + " | New: " + r.getNewSalary() + " | By: " + r.getChangedBy() + " | Reason: " + r.getReason());
        }
    }

    // ============== Employee helpers ==============
    private static void showEmployeeDetails(Employee emp) {
        System.out.println("\n--- My Details ---");
        System.out.println("ID: " + emp.getId());
        System.out.println("Name: " + emp.getName());
        System.out.println("Department: " + emp.getDepartment());
        System.out.println("Basic Salary: " + emp.getBasicSalary());
        System.out.println("Allowance: " + emp.getAllowance());
        System.out.println("Deduction: " + emp.getDeduction());
        System.out.println("Days Present: " + emp.getDaysPresent() + "/" + emp.getWorkingDays());
    }

    private static void changePassword(Scanner sc, Employee emp) {
        System.out.print("Enter new password: ");
        String np = sc.nextLine();
        emp.setPassword(np);
        boolean ok = dao.updateEmployee(emp);
        System.out.println(ok ? "Password changed successfully." : "Failed to change password.");
    }
}
