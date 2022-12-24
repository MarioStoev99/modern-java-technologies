package uni.nbu.logistic.company.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uni.nbu.logistic.company.model.Parcel;

@Repository
public interface ParcelRepository extends JpaRepository<Parcel, UUID> {

}
