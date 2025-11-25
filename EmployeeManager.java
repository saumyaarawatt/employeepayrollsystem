package payroll.service;

import payroll.model.Employee;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EmployeeManager {

    private final List<Employee> employees =
            Collections.synchronizedList(new ArrayList<>());

    public void addEmployee(Employee e) {
        employees.add(e);
    }

    public Employee findById(int id) {
        synchronized (employees) {
            for (Employee e : employees) {
                if (e.getId() == id) {
                    return e;
                }
            }
        }
        return null;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees);  // return a copy
    }
}
