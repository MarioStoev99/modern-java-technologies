package uni.nbu.logistic.company.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import uni.nbu.logistic.company.model.Office;

public interface OfficeRepository extends JpaRepository<Office, UUID> {

}
