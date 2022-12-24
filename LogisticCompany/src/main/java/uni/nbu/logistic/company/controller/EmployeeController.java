package uni.nbu.logistic.company.controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.constants.Constants;
import uni.nbu.logistic.company.model.Employee;
import uni.nbu.logistic.company.service.employee.EmployeeService;

@AllArgsConstructor
@RequestMapping(Constants.EMPLOYEE_API)
public class EmployeeController {

    private final EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<Employee> create(@Valid @RequestBody Employee employee) {
        return ResponseEntity.of(Optional.of(employeeService.create(employee)));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAll() {
        return ResponseEntity.ok(employeeService.getAll());
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Employee> update(@NotNull UUID id, @Valid @RequestBody Employee employee) {
        return ResponseEntity.of(Optional.of(employeeService.update(id, employee)));
    }

    @DeleteMapping
    public ResponseEntity<HttpStatus> delete(@NotNull UUID id) {
        employeeService.delete(id);
        return ResponseEntity.of(Optional.of(HttpStatus.OK));
    }
}
