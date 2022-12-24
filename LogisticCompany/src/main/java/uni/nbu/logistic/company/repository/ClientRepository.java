package uni.nbu.logistic.company.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.nbu.logistic.company.model.User;

@Repository
public interface ClientRepository extends JpaRepository<User, UUID> {

    Optional<User> getByUsername(String username);

}
