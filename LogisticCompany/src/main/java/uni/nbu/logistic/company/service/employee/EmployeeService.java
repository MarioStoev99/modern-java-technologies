package uni.nbu.logistic.company.service.employee;

import java.util.List;
import java.util.UUID;

import uni.nbu.logistic.company.model.Employee;

public interface EmployeeService {

    Employee create(Employee user);

    List<Employee> getAll();

    Employee update(UUID id, Employee user);

    void delete(UUID id);

    void saveAll(List<Employee> employees);
}
