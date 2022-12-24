package uni.nbu.logistic.company.model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.AllArgsConstructor;
import uni.nbu.logistic.company.service.employee.EmployeeService;


@AllArgsConstructor
public class Company {

//    private final String companyName;
//    private final EmployeeService employeeService;
//    private final List<Employee> employees = new ArrayList();

//    @Async
//    @Scheduled(fixedRateString = "${register.employee.rate}")
//    public void registerEmployee() {
//        List<Employee> dbEmployees = employeeService.getEmployeesByCompanyNameAndIsWorking(companyName, false);
//        employees.addAll(dbEmployees);
//        for(Employee employee : dbEmployees) {
//            employee.start();
//            employee(true);
//        }
//        employeeService.saveAll(dbEmployees);
//    }
}
