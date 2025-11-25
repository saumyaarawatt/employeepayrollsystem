package payroll.service;

import payroll.db.EmployeeDAO;
import payroll.model.Employee;

public class AuthService {
    private final EmployeeDAO dao = new EmployeeDAO();

    /**
     * Returns the Employee object if credentials match; otherwise null.
     */
    public Employee authenticate(String username, String password) {
        Employee e = dao.getEmployeeByUsername(username);
        if (e == null) return null;
        if (e.getPassword() == null) return null;
        // simple plain text check
        if (e.getPassword().equals(password)) {
            return e;
        }
        return null;
    }
}
