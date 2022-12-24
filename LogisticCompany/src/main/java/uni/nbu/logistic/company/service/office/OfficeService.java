package uni.nbu.logistic.company.service.office;

import java.util.List;
import java.util.UUID;

import uni.nbu.logistic.company.model.Office;

public interface OfficeService {

    Office create(Office office);

    List<Office> getAll();

    Office update(UUID id, Office office);

    void delete(UUID id);
}
