package payroll.db;

import payroll.model.Employee;
import payroll.model.FullTimeEmployee;
import payroll.exception.InvalidSalaryException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    // Insert employee (ensure 'role' handled)
    public boolean insertEmployee(Employee e) {
        String sql = "INSERT INTO employees (id, name, username, password, basic_salary, allowance, deduction, " +
                "days_present, working_days, department, bonus, overtime_hours, overtime_rate, casual_leave, sick_leave, unpaid_leave, salary_locked, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, e.getId());
            ps.setString(2, e.getName());
            ps.setString(3, e.getUsername());
            ps.setString(4, e.getPassword());
            ps.setDouble(5, e.getBasicSalary());
            ps.setDouble(6, e.getAllowance());
            ps.setDouble(7, e.getDeduction());
            ps.setInt(8, e.getDaysPresent());
            ps.setInt(9, e.getWorkingDays());
            ps.setString(10, e.getDepartment());
            ps.setDouble(11, e.getBonus());
            ps.setDouble(12, e.getOvertimeHours());
            ps.setDouble(13, e.getOvertimeRate());
            ps.setInt(14, e.getCasualLeave());
            ps.setInt(15, e.getSickLeave());
            ps.setInt(16, e.getUnpaidLeave());
            ps.setBoolean(17, e.isSalaryLocked());
            ps.setString(18, e.getRole() == null ? "EMPLOYEE" : e.getRole());

            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Get employee by id
    public Employee getEmployee(int id) {
        String sql = "SELECT * FROM employees WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FullTimeEmployee e = new FullTimeEmployee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getDouble("basic_salary"),
                            rs.getDouble("allowance"),
                            rs.getDouble("deduction"),
                            rs.getInt("days_present"),
                            rs.getInt("working_days"),
                            rs.getString("department"),
                            rs.getDouble("bonus")
                    );
                    e.setOvertimeHours(rs.getDouble("overtime_hours"));
                    e.setOvertimeRate(rs.getDouble("overtime_rate"));
                    e.setCasualLeave(rs.getInt("casual_leave"));
                    e.setSickLeave(rs.getInt("sick_leave"));
                    e.setUnpaidLeave(rs.getInt("unpaid_leave"));
                    e.setSalaryLocked(rs.getBoolean("salary_locked"));
                    e.setRole(rs.getString("role"));
                    return e;
                }
            }
        } catch (SQLException | InvalidSalaryException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Get employee by username (used by AuthService)
    public Employee getEmployeeByUsername(String username) {
        String sql = "SELECT * FROM employees WHERE username = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FullTimeEmployee e = new FullTimeEmployee(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getDouble("basic_salary"),
                            rs.getDouble("allowance"),
                            rs.getDouble("deduction"),
                            rs.getInt("days_present"),
                            rs.getInt("working_days"),
                            rs.getString("department"),
                            rs.getDouble("bonus")
                    );
                    e.setOvertimeHours(rs.getDouble("overtime_hours"));
                    e.setOvertimeRate(rs.getDouble("overtime_rate"));
                    e.setCasualLeave(rs.getInt("casual_leave"));
                    e.setSickLeave(rs.getInt("sick_leave"));
                    e.setUnpaidLeave(rs.getInt("unpaid_leave"));
                    e.setSalaryLocked(rs.getBoolean("salary_locked"));
                    e.setRole(rs.getString("role"));
                    return e;
                }
            }
        } catch (SQLException | InvalidSalaryException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    // Get all employees
    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM employees";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                FullTimeEmployee e = new FullTimeEmployee(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getDouble("basic_salary"),
                        rs.getDouble("allowance"),
                        rs.getDouble("deduction"),
                        rs.getInt("days_present"),
                        rs.getInt("working_days"),
                        rs.getString("department"),
                        rs.getDouble("bonus")
                );
                e.setOvertimeHours(rs.getDouble("overtime_hours"));
                e.setOvertimeRate(rs.getDouble("overtime_rate"));
                e.setCasualLeave(rs.getInt("casual_leave"));
                e.setSickLeave(rs.getInt("sick_leave"));
                e.setUnpaidLeave(rs.getInt("unpaid_leave"));
                e.setSalaryLocked(rs.getBoolean("salary_locked"));
                e.setRole(rs.getString("role"));
                list.add(e);
            }
        } catch (SQLException | InvalidSalaryException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // helper: insert salary history
    private void insertSalaryHistory(Connection con, int empId, double oldSalary, double newSalary, String changedBy, String reason) throws SQLException {
        String sql = "INSERT INTO salary_history (employee_id, old_salary, new_salary, changed_by, reason) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setDouble(2, oldSalary);
            ps.setDouble(3, newSalary);
            ps.setString(4, changedBy);
            ps.setString(5, reason);
            ps.executeUpdate();
        }
    }

    // update with salary history & lock checks
    public boolean updateEmployee(Employee e) {
        String sql = "UPDATE employees SET name=?, username=?, password=?, basic_salary=?, allowance=?, deduction=?, " +
                "days_present=?, working_days=?, department=?, bonus=?, overtime_hours=?, overtime_rate=?, casual_leave=?, sick_leave=?, unpaid_leave=?, salary_locked=?, role=? " +
                "WHERE id=?";
        try (Connection con = DBConnection.getConnection()) {

            double oldSalary = 0.0;
            try (PreparedStatement pOld = con.prepareStatement("SELECT basic_salary, salary_locked FROM employees WHERE id = ?")) {
                pOld.setInt(1, e.getId());
                try (ResultSet rs = pOld.executeQuery()) {
                    if (rs.next()) {
                        oldSalary = rs.getDouble("basic_salary");
                        boolean locked = rs.getBoolean("salary_locked");
                        if (locked && Double.compare(oldSalary, e.getBasicSalary()) != 0) {
                            System.out.println("Cannot change basic salary: salary is locked for employee id=" + e.getId());
                            return false;
                        }
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, e.getName());
                ps.setString(2, e.getUsername());
                ps.setString(3, e.getPassword());
                ps.setDouble(4, e.getBasicSalary());
                ps.setDouble(5, e.getAllowance());
                ps.setDouble(6, e.getDeduction());
                ps.setInt(7, e.getDaysPresent());
                ps.setInt(8, e.getWorkingDays());
                ps.setString(9, e.getDepartment());
                ps.setDouble(10, e.getBonus());
                ps.setDouble(11, e.getOvertimeHours());
                ps.setDouble(12, e.getOvertimeRate());
                ps.setInt(13, e.getCasualLeave());
                ps.setInt(14, e.getSickLeave());
                ps.setInt(15, e.getUnpaidLeave());
                ps.setBoolean(16, e.isSalaryLocked());
                ps.setString(17, e.getRole() == null ? "EMPLOYEE" : e.getRole());
                ps.setInt(18, e.getId());

                boolean updated = ps.executeUpdate() == 1;
                if (updated && Double.compare(oldSalary, e.getBasicSalary()) != 0) {
                    insertSalaryHistory(con, e.getId(), oldSalary, e.getBasicSalary(), "system", "salary update");
                }
                return updated;
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // delete
    public boolean deleteEmployee(int id) {
        String sql = "DELETE FROM employees WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() == 1;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // salary history reader
    public List<SalaryHistoryRecord> getSalaryHistory(int empId) {
        List<SalaryHistoryRecord> list = new ArrayList<>();
        String sql = "SELECT id, old_salary, new_salary, changed_by, changed_at, reason FROM salary_history WHERE employee_id = ? ORDER BY changed_at DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SalaryHistoryRecord r = new SalaryHistoryRecord(
                            rs.getInt("id"),
                            empId,
                            rs.getDouble("old_salary"),
                            rs.getDouble("new_salary"),
                            rs.getString("changed_by"),
                            rs.getTimestamp("changed_at"),
                            rs.getString("reason")
                    );
                    list.add(r);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return list;
    }

    // SalaryHistoryRecord inner class
    public static class SalaryHistoryRecord {
        private final int id;
        private final int employeeId;
        private final double oldSalary;
        private final double newSalary;
        private final String changedBy;
        private final Timestamp changedAt;
        private final String reason;

        public SalaryHistoryRecord(int id, int employeeId, double oldSalary, double newSalary, String changedBy, Timestamp changedAt, String reason) {
            this.id = id;
            this.employeeId = employeeId;
            this.oldSalary = oldSalary;
            this.newSalary = newSalary;
            this.changedBy = changedBy;
            this.changedAt = changedAt;
            this.reason = reason;
        }

        public int getId() { return id; }
        public int getEmployeeId() { return employeeId; }
        public double getOldSalary() { return oldSalary; }
        public double getNewSalary() { return newSalary; }
        public String getChangedBy() { return changedBy; }
        public Timestamp getChangedAt() { return changedAt; }
        public String getReason() { return reason; }
    }
}
