package uni.nbu.logistic.company.service.employee;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.model.Employee;
import uni.nbu.logistic.company.model.UserRole;
import uni.nbu.logistic.company.repository.EmployeeRepository;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public Employee create(Employee employee) {
        if (!UserRole.OFFICE_EMPLOYEE.equals(employee.getRole()) && !!UserRole.COURIER.equals(employee.getRole())) {
            throw new IllegalStateException("Invalid user role!");
        }
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public List<Employee> getAll() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee update(UUID id, Employee employee) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalStateException("The provided id does not exist!");
        }
        return employeeRepository.saveAndFlush(employee);
    }

    @Override
    public void delete(UUID id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalStateException("User not exist");
        }
        employeeRepository.deleteById(id);
    }

    @Override
    public void saveAll(List<Employee> employees) {
        employeeRepository.saveAll(employees);
    }
}
