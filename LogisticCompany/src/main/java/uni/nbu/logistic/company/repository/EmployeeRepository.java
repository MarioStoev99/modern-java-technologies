package uni.nbu.logistic.company.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.nbu.logistic.company.model.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

}
